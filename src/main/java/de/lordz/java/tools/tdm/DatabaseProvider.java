package de.lordz.java.tools.tdm;

import java.io.*;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.google.common.base.Strings;

import de.lordz.java.tools.tdm.common.DateTimeHelper;
import de.lordz.java.tools.tdm.common.Logger;
import de.lordz.java.tools.tdm.config.DatabaseConnectionConfiguration;

/**
 * Provides access to the applications database using a EntityManagerFactory.
 * 
 * @author lordzomat
 *
 */
public final class DatabaseProvider {

    private static final int LATEST_DB_BUILD_NUMBER = 1;
    private static Object lockObject = new Object();
    private static AtomicBoolean isOpen = new AtomicBoolean();
    private static AtomicBoolean isSqlite = new AtomicBoolean();
    private static EntityManagerFactory factoryInstance;

    /**
     * Opens the specified database.
     * 
     * @param databasePath The full path to the database
     * @return Returns true on success, otherwise false.
     */
    public static boolean openDatabase(String databasePath) {
        boolean result = false;
        try {
            if (isOpen.get()) {
                return result;
            }

            synchronized (lockObject) {
                if (!Strings.isNullOrEmpty(databasePath) && factoryInstance == null) {
                    final var persistenceMap = new HashMap<String, String>();
                    boolean isSqliteDatabase = !databasePath.endsWith(".pg");
                    isSqlite.set(isSqliteDatabase);
                    if (isSqliteDatabase) {
                        persistenceMap.put("javax.persistence.jdbc.url", "jdbc:sqlite:" + databasePath);
                        persistenceMap.put("javax.persistence.jdbc.user", "");
                        persistenceMap.put("javax.persistence.jdbc.password", "");
                        persistenceMap.put("javax.persistence.jdbc.driver", "org.sqlite.JDBC");
                    } else {
                        var configuration = DatabaseConnectionConfiguration.loadFromFile(Path.of(databasePath));
                        if (configuration != null) {
                            final var server = configuration.Server;
                            final var databaseName = configuration.DatabaseName;
                            if (!Strings.isNullOrEmpty(server) && !Strings.isNullOrEmpty(databaseName)) {
                                final var postgreServerUrl = String.format("jdbc:postgresql://%s/%s", server, databaseName);
                                persistenceMap.put("javax.persistence.jdbc.url", postgreServerUrl);
                                persistenceMap.put("javax.persistence.jdbc.user", configuration.User);
                                persistenceMap.put("javax.persistence.jdbc.password", configuration.Password);
                                persistenceMap.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");                            
                            }
                        }
                    }

                    factoryInstance = Persistence.createEntityManagerFactory("TravelDistanceManager", persistenceMap);
                    if (ensureDatabaseIsCreated(isSqliteDatabase)) {
                        if (isSqliteDatabase) {
                            var manager = createEntityManager();
                            if (manager != null) {
                                var nativeQuery = manager.createNativeQuery("PRAGMA journal_mode=WAL;");
                                if (nativeQuery != null) {
                                    nativeQuery.getSingleResult();
                                }
                                manager.close();
                            
                            }
                        }
                        
                        isOpen.set(true);
                        result = true;
                        Logger.LogVerbose("Database %s was openend", databasePath);
                    } else {
                        factoryInstance.close();
                        factoryInstance = null;
                        Logger.LogError("Checking database '%s' failed!", databasePath);
                    }
                } else {
                    Logger.LogError("No database path provided");
                }
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        return result;
    }

    /**
     * Checks if a database is open.
     * 
     * @return Returns true if a database is open, otherwise false.
     */
    public static boolean getIsOpen() {
        return isOpen.get();
    }

    /**
     * Saves the entity.
     * 
     * @param entity The entity to save.
     * @return Returns true on success, otherwise false.
     */
    public static boolean saveEntity(Object entity) {
        boolean result = false;
        try {
            if (!isOpen.get()) {
                return result;
            }

            synchronized (lockObject) {
                final var manager = createEntityManager();
                if (manager != null) {
                    var transaction = manager.getTransaction();
                    transaction.begin();
                    manager.persist(entity);
                    transaction.commit();
                    manager.close();
                    result = true;
                }
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        return result;
    }

    /**
     * Updates the entity.
     * 
     * @param entity The entity to update.
     * @return Returns true on success, otherwise false.
     */
    public static boolean updateEntity(Object entity) {
        boolean result = false;
        try {
            if (!isOpen.get()) {
                return result;
            }

            synchronized (lockObject) {
                final var manager = createEntityManager();
                if (manager != null) {
                    var transaction = manager.getTransaction();
                    transaction.begin();
                    manager.merge(entity);
                    transaction.commit();
                    manager.close();
                    result = true;
                }
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        return result;
    }

    /**
     * Removes the entity.
     * 
     * @param entity The entity to remove.
     * @return Returns true on success, otherwise false.
     */
    public static boolean removeEntity(Object entity) {
        boolean result = false;
        try {
            if (!isOpen.get()) {
                return result;
            }

            synchronized (lockObject) {
                final var manager = createEntityManager();
                if (manager != null) {
                    var transaction = manager.getTransaction();
                    transaction.begin();
                    manager.remove(manager.merge(entity));
                    transaction.commit();
                    manager.close();
                    result = true;
                }
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        return result;
    }

    /**
     * Retrieves a entity using the specified JPA query.
     * 
     * @param <T>         The type of the query.
     * @param query       The query itself.
     * @param entityClass The entity class.
     * @param parameters  The parameters to pass to the query.
     * @return Returns the entity on success, otherwise null.
     */
    public static <T> T getEntity(String query, Class<T> entityClass,
            List<AbstractMap.SimpleEntry<String, Object>> parameters) {
        T result = null;
        EntityManager manager = null;
        try {
            manager = createEntityManager();
            if (manager != null) {
                TypedQuery<T> typedQuery = manager.createQuery(query, entityClass);
                if (parameters != null) {
                    for (AbstractMap.SimpleEntry<String, Object> parameter : parameters) {
                        typedQuery.setParameter(parameter.getKey(), parameter.getValue());
                    }
                }
                
                var resultList = typedQuery.getResultList();
                if (resultList != null && resultList.size() > 0) {
                    result = resultList.get(0);
                }
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        } finally {
            if (manager != null) {
                manager.close();
            }
        }

        return result;
    }

    /**
     * Retrieves entities using the specified JPA query.
     * 
     * @param <T>         The type of the entity.
     * @param query       The query itself.
     * @param entityClass The entity class.
     * @param parameters  The parameters if any to pass to the query.
     * @return Returns a list of entities on success, otherwise null.
     */
    public static <T> List<T> getEntities(String query, Class<T> entityClass, List<AbstractMap.SimpleEntry<String, Object>> parameters) {
        List<T> result = null;
        EntityManager manager = null;
        try {
            manager = createEntityManager();
            if (manager != null) {
                TypedQuery<T> typedQuery = manager.createQuery(query, entityClass);
                if (parameters != null) {
                    for (AbstractMap.SimpleEntry<String, Object> parameter : parameters) {
                        typedQuery.setParameter(parameter.getKey(), parameter.getValue());
                    }
                }
                
                result = typedQuery.getResultList();
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        } finally {
            if (manager != null) {
                manager.close();
            }
        }

        return result;
    }
    
    /** Retrieves a single result using the specified native query.
     * @param sqlQuery      The query itself.
     * @param parameters    The parameters to use if present.
     * @return              Returns the single result on success.
     */
    public static Object getSqlSingleResult(String sqlQuery, Object... parameters) {
        Object result = null;
        EntityManager manager = null;
        try {
            if (!Strings.isNullOrEmpty(sqlQuery)) {
                manager = createEntityManager();
                if (manager != null) {
                    var nativeQuery = manager.createNativeQuery(sqlQuery);
                    if (nativeQuery != null) {
                        if (parameters != null) {
                            for (int i= 0; i < parameters.length; i++) {
                                nativeQuery.setParameter(i + 1, parameters[i]);
                            }
                        }
                        
                        result = nativeQuery.getSingleResult();
                    }
                }
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }
        
        return result;
    }
    
    /**
     * Closes the database connection if open.
     */
    public static void closeDatabase() {
        if (isOpen.get()) {
            if (factoryInstance != null) {
                factoryInstance.close();
                factoryInstance = null;
            }

            isOpen.set(false);
        }
    }

    private static boolean ensureDatabaseIsCreated(boolean isSqliteDatabase) {
        boolean result = false;
        EntityManager manager = null;
        try {
            manager = createEntityManager();
            if (manager != null) {
                final var transaction = manager.getTransaction();
                transaction.begin();
                if (isSqliteDatabase) {
                    try (final InputStream inputStream = DatabaseProvider.class.getClassLoader()
                            .getResourceAsStream("META-INF/create.sql")) {
                        if (inputStream != null) {
                            try (final var reader = new BufferedReader(new InputStreamReader(inputStream))) {
                                while (reader.ready()) {
                                    var line = reader.readLine();
                                    if (!Strings.isNullOrEmpty(line)) {
                                        var query = manager.createNativeQuery(line);
                                        query.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                }

                if (initializeHistoryAndSequence(manager, isSqliteDatabase)) {
                    transaction.commit();
                    manager.close();
                    manager = null;
                    result = true;
                } else {
                    transaction.rollback();
                }
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        } finally {
            if (manager != null) {
                manager.close();
            }
        }

        return result;
    }

    private static boolean initializeHistoryAndSequence(final EntityManager manager, boolean isSqliteDatabase) {
        boolean result = false;
        try {
            if (manager != null) {
                final var countSelect = isSqliteDatabase ? "count(*)": "cast(count(*) as smallint)";
                var query = manager.createNativeQuery("SELECT " + countSelect + " FROM tbDatabaseHistory");
                var historyResult = query.getSingleResult();
                if (historyResult != null) {
                    int countOfEntries = (int)historyResult;
                    if (countOfEntries == 0) {
                        query = manager.createNativeQuery("INSERT INTO sqlite_sequence (name, seq) VALUES(?1, 0)");
                        insertDatabaseHistoryEntry(manager, LATEST_DB_BUILD_NUMBER, "Database created");
                        query.setParameter(1, "tbCustomers");
                        query.executeUpdate();
                        query.setParameter(1, "tbTravelAllowance");
                        query.executeUpdate();
                        query.setParameter(1, "tbTrip");
                        query.executeUpdate();
                        query.setParameter(1, "tbTripType");
                        query.executeUpdate();
                    } else {
                        if (countOfEntries == 1 && isSqliteDatabase) {
                            query = manager.createNativeQuery("SELECT EXISTS (SELECT 1 FROM pragma_table_info('tbDatabaseHistory') WHERE name='coBuildNumber')");
                            var checkBuildNumberExistsResult = query.getSingleResult();
                            if (checkBuildNumberExistsResult != null && checkBuildNumberExistsResult instanceof Integer) {
                                if ((int)checkBuildNumberExistsResult == 0) {
                                    query = manager.createNativeQuery("ALTER TABLE tbDatabaseHistory ADD COLUMN coBuildNumber INTEGER NOT NULL DEFAULT 0");
                                    query.executeUpdate();
                                }
                            }
                        }
                        
                        query = manager.createNativeQuery("SELECT max(coBuildNumber) FROM tbDatabaseHistory");
                        var buildNumberResult = query.getSingleResult();
                        if (buildNumberResult != null && buildNumberResult instanceof Integer) {
                            final var buildNumber = (int)buildNumberResult;
                            if (buildNumber != LATEST_DB_BUILD_NUMBER) {
                                if (buildNumber == 0) {
                                    query = manager.createNativeQuery("INSERT INTO sqlite_sequence (name, seq) VALUES(?1, 0)");
                                    query.setParameter(1, "tbTravelAllowance");
                                    query.executeUpdate();
                                    insertDatabaseHistoryEntry(manager, 1, "Upgrade 1");
                                }
                            }
                        }
                    }
                    
                    result = true;
                }
            }
        } catch (Exception ex) {
            Logger.Log(ex);
        }

        return result;
    }

    private static final EntityManager createEntityManager() {
        synchronized (lockObject) {
            if (factoryInstance != null) {
                return factoryInstance.createEntityManager();
            }
        }

        return null;
    }
    
    private static void insertDatabaseHistoryEntry(EntityManager manager, int buildNumber, String description) {
        final var query = manager.createNativeQuery(
                "INSERT INTO tbDatabaseHistory (coTimestamp, coBuildNumber, coDescription) VALUES(?1, ?2, ?3)");
        query.setParameter(1, DateTimeHelper.getIsoDateTime());
        query.setParameter(2, buildNumber);
        query.setParameter(3, description);
        query.executeUpdate();
    }
}