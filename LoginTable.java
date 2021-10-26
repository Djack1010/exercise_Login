package LoginGiacomo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginTable {
	private Connection conn = null;
//	private PreparedStatement stmtIsActive = null;	
//	private PreparedStatement stmtOttieniPassword = null;
	private PreparedStatement stmtInserisci = null;
	private PreparedStatement stmtBloccareUtente = null;
	private PreparedStatement stmtOttieniSalt = null;
	private PreparedStatement stmtLeggiTutto = null;

//	private final static String SQL_READ_ACTIVE = "SELECT active FROM login WHERE username=?";
//	private final static String SQL_READ_PSW = "SELECT password FROM login WHERE username=?";

	private final static String SQL_INSERT = "INSERT INTO login (username, password, salt) VALUES (?, ?, ?)";
	private final static String SQL_BlOCKED = "UPDATE login SET active = ? WHERE username = ?";
	private final static String SQL_READ_SALT = "SELECT salt FROM login WHERE username=?";
	private final static String SQL_READ_ALL = "SELECT * FROM login WHERE username=?";
	
	public LoginTable(String serverAddr, String dbName, String username, String password) throws SQLException {
		String url = "jdbc:mysql://" + serverAddr + "/" + dbName;

		// Avvio la connessione con il database
		conn = DriverManager.getConnection(url, username, password);		
		// Definisco tutti gli statements necessari
//		stmtIsActive = conn.prepareStatement(SQL_READ_ACTIVE);		
//		stmtOttieniPassword = conn.prepareStatement(SQL_READ_PSW);
		stmtInserisci = conn.prepareStatement(SQL_INSERT);
		stmtBloccareUtente = conn.prepareStatement(SQL_BlOCKED);
		stmtOttieniSalt = conn.prepareStatement(SQL_READ_SALT);
		stmtLeggiTutto = conn.prepareStatement(SQL_READ_ALL);		
	}
	
	public void close() throws SQLException {
		
		if (stmtInserisci != null)
			stmtInserisci.close();
		
		if (stmtBloccareUtente != null)
			stmtBloccareUtente.close();
		
/*		if (stmtOttieniPassword != null)
			stmtOttieniPassword.close();*/

		if (stmtOttieniSalt != null)
			stmtOttieniSalt.close();
		
		if (stmtLeggiTutto != null)
			stmtLeggiTutto.close();
				
		if (conn != null)
			conn.close();
	}
	
	public boolean login(String username, String password) throws SQLException {
		boolean result = true;
		stmtLeggiTutto.setString(1, username);
		ResultSet rs = stmtLeggiTutto.executeQuery();

		if (!rs.next()) {

			System.out.println("ERRORE: Username o Password errati...");		
		} else {
			if (rs.getString("active").equals("0"))
				System.out.println("Account bloccato...");
			else if (rs.getString("password").equals(password))
				System.out.println("Login eseguito con successo!");
			else {
				System.out.println("ERRORE: Username o Password errati...");
				result=false;
			}
		}
		return result;
	}
	
	public byte[] logUser(String username) throws SQLException {
		byte[] salt = new byte[16];;	
		stmtOttieniSalt.setString(1, username);
		ResultSet rs = stmtOttieniSalt.executeQuery();
		if (rs.next()) 
			salt = rs.getBytes("salt");
		return salt;
		}

	public void registrazione(String username, String password, byte[] salt) throws SQLException {

		if (Utilities.getInstance().isCommonPsw(password)) {

			System.out.println("Password troppo semplice...");
			return;
		}


		stmtLeggiTutto.setString(1, username);
		ResultSet rs = stmtLeggiTutto.executeQuery();

      	if (!rs.next()) {
			stmtInserisci.setString(1, username); // giacomo"; -- DROP TABLE login;
			stmtInserisci.setString(2, password);
			stmtInserisci.setBytes(3, salt);
			// Eseguo la query
			stmtInserisci.executeUpdate();
			System.out.println("Nuovo Utente Registrato!");
		
		} else
			System.out.println("Username non disponibile...");		
	}
	
	public boolean regUser(String username) throws SQLException {

		stmtLeggiTutto.setString(1, username);
		ResultSet rs = stmtLeggiTutto.executeQuery();

      	if (rs.next()) {
			System.out.println("Username non disponibile...");
		return false;
	}
		return true;
	}
	
	
	public void bloccare(String username) throws SQLException {
		stmtBloccareUtente.setInt(1, 0);
		stmtBloccareUtente.setString(2, username);

		stmtBloccareUtente.executeUpdate();
		System.out.println("Utente è bloccato, hai fatto tanti tentativi");
	}
      	
}
