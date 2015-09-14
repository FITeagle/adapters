package org.fiteagle.adapters.monitoring.sql;
import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.fiteagle.api.core.Config;

public class SQLite {
	private static Logger LOGGER = Logger.getLogger(SQLite.class.toString());
	
    private  String sDriver = ""; 
    private  String sUrl = null;
    private  int iTimeout = 30;
    private  Connection c = null;
    private  Statement stmt = null;
    private  String sDriverKey = "org.sqlite.JDBC" ;
    private  String sUrlKey;
    private	 String tableName = "virtual_physical_map" ;
    private String sql_path ;
    private Config config ;
    
    
    public void init() throws SQLException{
    	loadPreferences() ;
        if (sql_path == null)
        {
            LOGGER.log(Level.WARNING, "can't load 'sqliteDB_path' from prefs.");
            sUrlKey = "jdbc:sqlite:/.fiteagle/monitoring_sqlite.db";
        }else
            sUrlKey = "jdbc:sqlite:"+sql_path;
        
    	try{
		    setDriver(sDriverKey);
	        setUrl(sUrlKey);
	        setConnection();
	        setStatement();
		    if(c != null){
		    	System.out.println("SQLite: Connected OK using " + sDriverKey + " to " + sUrlKey);
		    }
		    else{
		    	LOGGER.log(Level.WARNING, "Connection failed");
		    }
    	}catch(Exception e){
    		LOGGER.log(Level.WARNING, "Connection failed");
    	}
    }
    
    public Connection closeConnection(){
    	try {
    		  if(c != null || !c.isClosed()){
    			  c.close() ;	
			  }
    		  System.out.println("Closed database successfully");
    	      
    	} catch (Exception e ) {
    		LOGGER.log(Level.WARNING, "Closing connection failed");
    	}
    	return c ;
    }
    
    private void setDriver(String sDriverVar){
        sDriver = sDriverVar;
    }
 
    private void setUrl(String sUrlVar){
        sUrl = sUrlVar;
    }
    
    private  void setStatement() throws Exception {
        if (c == null) {
            setConnection();
        }
        stmt = c.createStatement();
        stmt.setQueryTimeout(iTimeout);  // set timeout to 30 sec.
    }
 
    private void setConnection() throws Exception {
    	Class.forName(sDriver);
        c = DriverManager.getConnection(sUrl);	
	}

	public  Statement getStatement() {
        return stmt;
    }
        
    public  void executeStmt(String instruction) throws SQLException {
        stmt.executeUpdate(instruction);
    }
    
    public ResultSet executeQry(String instruction) throws SQLException {
        return stmt.executeQuery(instruction);
    } 
    
    public boolean createTable(){
    	try{
    		if(c == null) init() ;
    		executeStmt("create table IF NOT EXISTS virtual_physical_map (resource_id text primary key, host_name text, collector_uri text, vm_uri text)");
    		return true ;	
    	}catch(SQLException e){
    		LOGGER.log(Level.WARNING, "Creating table failed.");
    		return false ;
    	}
	}
	
	public boolean insert(String vm_id, String host, String oml_uri, String vm_uri){
		try{
			if(c == null) init() ;
			 if(createTable()){
				 executeStmt("insert into virtual_physical_map (resource_id, host_name, collector_uri, vm_uri) values (\"" + vm_id + "\",\"" + host + "\",\"" + oml_uri + "\",\"" + vm_uri + "\")");
			 }
			 return true ;
		 }catch(SQLException e){
			 e.getStackTrace();
			 LOGGER.log(Level.WARNING, "Error inserting to database.");
			 return false ;
		 } 
	}
	
	public boolean delete(String vm_id){
		try{
			if(c == null) init() ;
			executeStmt("delete from virtual_physical_map where resource_id = \"" + vm_id + "\"");
			return true ;
		 }catch(SQLException e){
			 e.getStackTrace();
			 LOGGER.log(Level.WARNING, "Error deleting from database.");
			 return false ;
		 } 
	}
	
	public void setConfig(Config config){
		this.config = config ;
	}
	
	private void loadPreferences() {
		String jsonProperties = this.config.readJsonProperties();
        if(!jsonProperties.isEmpty()){
            JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(jsonProperties.getBytes()));

            JsonObject jsonObject = jsonReader.readObject();

            JsonArray adapterInstances = jsonObject.getJsonArray("SQLITE");

            for (int i = 0; i < adapterInstances.size(); i++) {
                JsonObject adapterInstanceObject = adapterInstances.getJsonObject(i);
                sql_path = adapterInstanceObject.getString("sqliteDB_path");
    
            }
        }
	}
	
}
