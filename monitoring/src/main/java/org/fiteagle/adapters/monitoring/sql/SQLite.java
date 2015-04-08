package org.fiteagle.adapters.monitoring.sql;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class SQLite {
	private static Logger LOGGER = Logger.getLogger(SQLite.class.toString());
	private Preferences preferences = Preferences.userNodeForPackage(getClass());
	
    private  String sDriver = ""; 
    private  String sUrl = null;
    private  int iTimeout = 30;
    private  Connection c = null;
    private  Statement stmt = null;
    private  String sDriverKey = "org.sqlite.JDBC" ;
    private  String sUrlKey = "jdbc:sqlite:" + preferences.get("sqliteDB_path", null) ;
    private	 String tableName = "virtual_physical_map" ;
    
    public SQLite(){
    	try{
		    init(sDriverKey, sUrlKey);
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
    
    public void init(String sDriverVar, String sUrlVar) throws Exception{
        setDriver(sDriverVar);
        setUrl(sUrlVar);
        setConnection();
        setStatement();
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
    		executeStmt("create table IF NOT EXISTS virtual_physical_map (resource_id text primary key, host_name text, collector_uri text)");
    		return true ;	
    	}catch(SQLException e){
    		LOGGER.log(Level.WARNING, "Creating table failed.");
    		return false ;
    	}
	}
	
	public boolean insert(String vm_id, String host, String oml_uri){
		try{
			 if(createTable()){
				 executeStmt("insert into virtual_physical_map (resource_id, host_name, collector_uri) values (\"" + vm_id + "\",\"" + host + "\",\"" + oml_uri + "\")");
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
			executeStmt("delete from virtual_physical_map where resource_id = \"" + vm_id + "\"");
			return true ;
		 }catch(SQLException e){
			 e.getStackTrace();
			 LOGGER.log(Level.WARNING, "Error deleting from database.");
			 return false ;
		 } 
	}
	
}
