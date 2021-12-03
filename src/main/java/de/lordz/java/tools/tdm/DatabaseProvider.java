package de.lordz.java.tools.tdm;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.google.common.base.Strings;

import de.lordz.java.tools.tdm.common.Logger; 


/**
 * Provides access to the applications database using a EntityManagerFactory.
 * 
 * @author lordz
 *
 */
public final class DatabaseProvider {

	private static Object lockObject = new Object();
	private static AtomicBoolean isOpen = new AtomicBoolean();
	private static EntityManagerFactory factoryInstance;
	
	/**
	 * Opens the specified database.
	 * @param databasePath The full path to the database
	 * @return Returns true on success, otherwise false.
	 */
	public static boolean openDatabase(String databasePath) {
		boolean result = false;
		try {
			if (isOpen.get())
			{
				return result;
			}
			
			synchronized (lockObject) {
				if (!Strings.isNullOrEmpty(databasePath) && factoryInstance == null) {
						final var persistenceMap = new HashMap<String, String>();
						persistenceMap.put("javax.persistence.jdbc.url", "jdbc:sqlite:" + databasePath);
						persistenceMap.put("javax.persistence.jdbc.user", "");
						persistenceMap.put("javax.persistence.jdbc.password", "");
						persistenceMap.put("javax.persistence.jdbc.driver", "org.sqlite.JDBC");
			
						factoryInstance = Persistence.createEntityManagerFactory("TravelDistanceManager", persistenceMap);
		//		    System.out.println("Is opened connection :: "+ factoryInstance.createEntityManager().isOpen());
					if (ensureDatabaseIsCreated()) {
						isOpen.set(true);
						result = true;
						Logger.LogVerbose("Database %s was openend", databasePath);
					} else {
						Logger.LogError("Checking database '%s' failed!", databasePath);
					}
				    
		//		    var entityMananger = factory.createEntityManager();
		//		    if (entityMananger != null) {
				    	//createDatabase(entityMananger);
		//		    	entityMananger.getTransaction().begin();  
		//		    	var customer = new CustomerEntity("Verein1", 10.5f);
		//		    	entityMananger.persist(customer);
		//		    	entityMananger.getTransaction().commit();
		//		    	entityMananger.close();
		//		    	factory.close(); 	
		//		    }
				} else {
					Logger.LogError("No database path provided");
				}
			}
		}
		catch (Exception ex) {
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
	
	public static boolean saveEntity(Object entity) {
		boolean result = false;
		try {
			if (!isOpen.get()) {
				return result;
			}
			
			synchronized(lockObject) {
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
		}
		catch (Exception ex) {
			Logger.Log(ex);
		}
		
		return result;
	}
	
	public static boolean updateEntity(Object entity) {
		boolean result = false;
		try {
			if (!isOpen.get()) {
				return result;
			}
			
			synchronized(lockObject) {
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
		}
		catch (Exception ex) {
			Logger.Log(ex);
		}
		
		return result;
	}
	
	@SafeVarargs
	public static <T> T getEntity(String query, Class<T> entityClass, AbstractMap.SimpleEntry<String, Object>... parameters) {
		T result = null;
		EntityManager manager = null;
		try {
			manager = createEntityManager();
			if (manager != null) {
				TypedQuery<T> typedQuery = manager.createQuery(query, entityClass);
				for (AbstractMap.SimpleEntry<String, Object> parameter : parameters) {
					typedQuery.setParameter(parameter.getKey(), parameter.getValue());
				}
				var resultList = typedQuery.getResultList();
				if (resultList != null && resultList.size() > 0) {
					result = resultList.get(0);
				}
			}
		}
		catch (Exception ex) {
			Logger.Log(ex);
		}
		finally {
			if (manager != null) {
				manager.close();
			}
		}
		
		return result;
	}
	
	public static <T> List<T> getEntities(String query, Class<T> entityClass) {
		List<T> result = null;
		EntityManager manager = null;
		try {
			manager = createEntityManager();
			if (manager != null) {
				TypedQuery<T> typedQuery = manager.createQuery(query, entityClass);
				result = typedQuery.getResultList();
			}
		}
		catch (Exception ex) {
			Logger.Log(ex);
		}
		finally {
			if (manager != null) {
				manager.close();
			}
		}
		
		return result;
	}
	
	public static String getIsoDateTime() {
		var date = new Date(System.currentTimeMillis());
		var dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		return dateFormat.format(date);
	}
	
	public static Date getDateFromIsoDateTime(String timestamp) {
		try {
			var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	        return simpleDateFormat.parse(timestamp);
		} 
		catch (Exception ex) {
			Logger.Log(ex);
		}
		
		return null;
//		var temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(timestamp); 
//	    return Date.from(Instant.from(temporalAccessor));
	}
	
	private static boolean ensureDatabaseIsCreated() {
		boolean result = false;
		try {
			final var manager = createEntityManager();
			if (manager != null) {
				final var transaction = manager.getTransaction();
				transaction.begin();
				try (final InputStream inputStream = DatabaseProvider.class.getClassLoader().getResourceAsStream("META-INF/create.sql")) {
					if (inputStream != null) {
						try (final var reader = new BufferedReader(new InputStreamReader(inputStream))) {
							while(reader.ready()) {
							     var line = reader.readLine();
							     if (!Strings.isNullOrEmpty(line)) {
							    	 var query = manager.createNativeQuery(line);
							    	 query.executeUpdate();
							     }
							}
						}
					}
				}
				
				if (initializeHistoryAndSequence(manager)) {				
					transaction.commit();
					manager.close();
					result = true;
				}
			}
		}	
		catch(Exception ex) {
			Logger.Log(ex);
		}
		
		return result;
	}
	
	private static boolean initializeHistoryAndSequence(final EntityManager manager) {
		boolean result = false;
		try {
			if (manager != null) {
				var query = manager.createNativeQuery("SELECT count(*) FROM tbDatabaseHistory");
				var historyResult = query.getSingleResult();
				if (historyResult != null ) {
					if ((int)historyResult == 0) {
						query = manager.createNativeQuery("INSERT INTO tbDatabaseHistory (coTimestamp, coDescription) VALUES(?1, ?2)");
						query.setParameter(1, getIsoDateTime());
						query.setParameter(2, "Database created");
						query.executeUpdate();
					}
									
					query = manager.createNativeQuery("INSERT INTO sqlite_sequence (name, seq) VALUES(?1, 0)");
					query.setParameter(1, "tbCustomers");
					query.executeUpdate();
					query.setParameter(1, "tbTrip");
					query.executeUpdate();
					result = true;
				}
			}
		}
		catch (Exception ex)
		{
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
	
	public static void closeDatabase() {
		if (isOpen.get()) {
			if (factoryInstance != null) {
				factoryInstance.close();
				factoryInstance = null;
			}
			
			isOpen.set(false);
		}
	}
	
}