package de.lordz.java.tools.tdm;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import com.google.common.base.Strings; 


/**
 * Provides access to the applications database using a EntityManagerFactory.
 * 
 * @author lordz
 *
 */
public final class DatabaseProvider {

	private static AtomicBoolean isOpen = new AtomicBoolean();
	private static EntityManagerFactory factoryInstance;
	
	/**
	 * Opens the specified database.
	 * @param databasePath The full path to the database
	 * @return Returns true on success, otherwise false.
	 */
	public static Boolean openDatabase(String databasePath) {
		Boolean result = false;
		try {
			if (isOpen.get())
			{
				return false;
			}
			
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
		catch (Exception ex) {
			Logger.Log(ex);
		}
		
		return result;
	}
	
	
	private static Boolean ensureDatabaseIsCreated() {
		Boolean result = false;
		try {
			final var manager = factoryInstance.createEntityManager();
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
				transaction.commit();
				result = true;
			}
		}	
		catch(Exception ex) {
			Logger.Log(ex);
		}
		
		return result;
	}
	
	public static void closeDatabase() {
		if (isOpen.get()) {
			if (factoryInstance != null) {
				factoryInstance.close();
			}
			
			isOpen.set(false);
		}
	}
	
}