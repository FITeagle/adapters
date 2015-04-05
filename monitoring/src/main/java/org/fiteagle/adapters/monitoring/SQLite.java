package org.fiteagle.adapters.monitoring;
import java.sql.*;
import java.util.logging.Logger;

public class SQLite {
	private static Logger LOGGER = Logger.getLogger(SQLite.class.toString());
	
    public  String sDriver = ""; 
    public  String sUrl = null;
    public  int iTimeout = 30;
    public  Connection c = null;
    public  Statement stmt = null;
    public  String sDriverKey = "org.sqlite.JDBC" ;
    public  String sUrlKey = "jdbc:sqlite:database.db" ;
    
    public SQLite(){
    	try{
		    init(sDriverKey, sUrlKey);
		    if(c != null){
		    	System.out.println("SQLite: Connected OK using " + sDriverKey + " to " + sUrlKey);
		    }
		    else{
		    	System.out.println("SQLite: Connection failed");
		    }
    	}catch(Exception e){
    		System.out.println("SQLite: Connection failed");
    	}
    }
    
    public void init(String sDriverVar, String sUrlVar) throws Exception{
        setDriver(sDriverVar);
        setUrl(sUrlVar);
        setConnection();
        setStatement();
    }
    
    private void setDriver(String sDriverVar){
        sDriver = sDriverVar;
    }
 
    private void setUrl(String sUrlVar){
        sUrl = sUrlVar;
    }
        
    public Connection closeConnection(){
    	try {
    		  if(c != null || !c.isClosed()){
    			  c.close() ;	
			  }
    		  System.out.println("Closed database successfully");
    	      
    	} catch ( Exception e ) {
    	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    	      System.exit(0);
    	    }
    	return c ;
    }
    
    public  void executeStmt(String instruction) throws SQLException {
        stmt.executeUpdate(instruction);
    }
    
    public ResultSet executeQry(String instruction) throws SQLException {
        return stmt.executeQuery(instruction);
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
	
	public void addToTable(String value, String tablename){
		
	}
	
	public void deleteFromTable(String value, String tablename){
		
	}
	
}
