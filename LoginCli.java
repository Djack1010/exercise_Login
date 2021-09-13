import java.io.BufferedReader;
import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.SQLException;
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
	public static String DB_PASS = props.getProperty("db.passwd");
	
	public static int FailCount = 0;
	
//  ----------------------------------------------------------
		
	
	public static void main(String[] args) throws IOException {		
		LoginTable loginTable = null;
		Scanner input = new Scanner(System.in);
		
		String choice = "";
		
		try {

			loginTable = new LoginTable(DB_URL, DB_NAME, DB_USER,DB_PASS);
			
			while(!choice.startsWith("0")) {
				System.out.println("Operazioni disponibili: ");
				System.out.println("0 - Esci dal programma ");
				System.out.println("1 - Login ");
				System.out.println("2 - Registrazione ");
				
				choice = input.nextLine();
				if ((choice.startsWith("1")) && (FailCount > 2)) {
					System.out.println("Tentativi massimi di login raggiunti");
				}				
				
				else if ((choice.startsWith("1")) && (FailCount < 3)) {
					System.out.println("Login Username: ");
					String id = input.nextLine();
					System.out.println("Login Password: ");
					String pw = input.nextLine();
					
					if (loginTable.login(id, pw)) {
						System.out.println("Login eseguito con successo");
					} else {
						System.out.println("Id o password non esatta");
						FailCount++;
					}
					
				} else if (choice.startsWith("2")) {
					System.out.println("Registrazione Username: ");
					String id = input.nextLine();
					System.out.println("Registrazione Password: ");
					String pw = input.nextLine();
					
					if (loginTable.registrazione(id, pw)) {
						System.out.println("Registrazione eseguita con successo");
					} else {
						System.out.println("Errore in registrazione");
					}
					
				}
			}
			
			// esco dal loop while
			System.out.println();
			System.out.println("Uscita dal programma: ciao !");
			input.close();
			
			
		} catch (SQLException e) {
			System.out.println("ERRORE DATABASE: " + e.toString());	
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
