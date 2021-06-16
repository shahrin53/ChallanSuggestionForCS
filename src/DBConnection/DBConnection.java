/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DBConnection;

import java.sql.*;
import org.apache.log4j.*;

/**
 *
 * @author chhaya
 */
public class DBConnection {

    private Connection myConnection;
    String url, user, pass;
    static final Logger logger = Logger.getLogger(DBConnection.class.getName());

    /** Creates a new instance of MyDBConnection */
    public DBConnection() {
    }

    public void localDatabase() {

        try {

            Class.forName("com.mysql.jdbc.Driver");
            url = "jdbc:mysql://localhost/lereve";
            user = "lereve_local";
            pass = "kWeb018$$k";
            myConnection = DriverManager.getConnection(url, user, pass);
          
        } catch (Exception e) {
            logger.debug(e);
        }
    }

    public void remoteDatabase() {

        try {

            Class.forName("com.mysql.jdbc.Driver");    
                 
           
            url = "jdbc:mysql://localhost/lereve";
            user = "root";
            pass = "";
       
            
            myConnection = DriverManager.getConnection(url, user, pass);
  
 
        } catch (Exception e) {
            logger.debug(e);
        }
    }

    public Connection getConnection() {
        return myConnection;
    }

    public void close(ResultSet rs) {

        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                logger.debug(e);
            }

        }
    }

    public void close(java.sql.Statement stmt) {

        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
                logger.debug(e);
            }

        }
    }
    
    public void close(java.sql.PreparedStatement pstmt) {

        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (Exception e) {
                logger.debug(e);
            }

        }
    }

    public void destroy() {

        if (myConnection != null) {

            try {
                myConnection.close();
            } catch (Exception e) {
                logger.debug(e);
            }


        }
    }
}
