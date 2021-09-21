import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginCli {

//	----------------------------------------------------------
//	recupero i parametri di connessione al DB 
//	da un file esterno al progetto
	private static Properties getConnectionData() {

        Properties props = new Properties();

        String fileName = "resources/db.properties";

        try (FileInputStream in = new FileInputStream(fileName)) {
            props.load(in);
        } catch (IOException ex) {
            Logger lgr = Logger.getLogger("Lettura dati connessione DB");
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return props;
    }
//	----------------------------------------------------------
	
	
	public static Properties props = getConnectionData();

	public static String DB_URL = props.getProperty("db.url");
	public static String DB_NAME = props.getProperty("db.name");
	public static String DB_USER = props.getProperty("db.user");
	public static String DB_PASS = props.getProperty("db.psw");
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		
		LoginTable loginTable = null;
		
		try {

			loginTable = new LoginTable(DB_URL, DB_NAME, DB_USER,DB_PASS);

			Utilities.getInstance().handleMainLoop(loginTable);			
			
		} catch (SQLException e) {
			System.out.println("ERRORE DATABASE: " + e.toString());	
		} catch (FileNotFoundException e) {
			System.out.println("ERRORE: " + e.toString());	
		} finally {
			if (loginTable != null)
				try {
					loginTable.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}

}
