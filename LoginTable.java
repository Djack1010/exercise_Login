import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class LoginTable {
	private Connection conn = null;
	private PreparedStatement stmtInserisci = null;
	private PreparedStatement stmtOttieniPassword = null;
	private PreparedStatement stmtLeggiTutto = null;
	private PreparedStatement stmtIsActive = null;
	private final static String SQL_READ_PSW = "SELECT password FROM login WHERE username=?";
	private final static String SQL_READ_ACTIVE = "SELECT active FROM login WHERE username=?";
	private final static String SQL_READ_ALL = "SELECT * FROM login WHERE username=?";
	private final static String SQL_INSERT = "INSERT INTO login (username, password) VALUES (?, ?)";
	
	
	public LoginTable(String serverAddr, String dbName, String username, String password) throws SQLException {
		String url = "jdbc:mysql://" + serverAddr + "/" + dbName;

		// Avvio la connessione con il database
		conn = DriverManager.getConnection(url, username, password);
		
		// Definisco tutti gli statements necessari
		stmtInserisci = conn.prepareStatement(SQL_INSERT);
		stmtOttieniPassword = conn.prepareStatement(SQL_READ_PSW);
		stmtLeggiTutto = conn.prepareStatement(SQL_READ_ALL);
		stmtIsActive = conn.prepareStatement(SQL_READ_ACTIVE);
		
	}
	
	public void close() throws SQLException {
		
		if (stmtInserisci != null)
			stmtInserisci.close();
		
		if (stmtOttieniPassword != null)
			stmtOttieniPassword.close();
		
		if (stmtLeggiTutto != null)
			stmtLeggiTutto.close();
		
		if (stmtIsActive != null)
			stmtIsActive.close();
		
		if (conn != null)
			conn.close();
	}
	
	public void login(String username, String password) throws SQLException {
		stmtLeggiTutto.setString(1, username);
		ResultSet rs = stmtLeggiTutto.executeQuery();

		if (!rs.next()) {

			System.out.println("ERRORE: Username o Password errati...");
		
		} else {

			if (rs.getString("active").equals("0"))
				System.out.println("Account bloccato...");
			else if (rs.getString("password").equals(password))
				System.out.println("Login eseguito con successo!");
			else
				System.out.println("ERRORE: Username o Password errati...");

		}
	}

	public void registrazione(String username, String password) throws SQLException {

		if (Utilities.getInstance().isCommonPsw(password)) {

			System.out.println("Password troppo semplice...");
			return;

		}
		
		if (!Utilities.isStrongPassword(password)) {

			System.out.println("La password deve contenere almeno un numero, una minuscola, una maiuscola, un simbolo ed essere lunga più di 10 caratteri...");
			return;

		}

		stmtLeggiTutto.setString(1, username);
		ResultSet rs = stmtLeggiTutto.executeQuery();

      	if (!rs.next()) {

			
			stmtInserisci.setString(1, username); // giacomo"; -- DROP TABLE login;
			stmtInserisci.setString(2, password);
			
			// Eseguo la query
			stmtInserisci.executeUpdate();
			System.out.println("Nuovo Utente Registrato!");
		
		} else
			System.out.println("Username non disponibile...");
		
	}
}
