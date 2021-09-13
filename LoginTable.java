import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginTable {
	private Connection conn = null;	
	private PreparedStatement stmt = null;
	private ResultSet results = null;
	private String clientPrivateKeyString = "MmL4ZaYP35";
	
	public LoginTable(String serverAddr, String dbName, String username, String password) throws SQLException {
		String url = "jdbc:mysql://" + serverAddr + "/" + dbName;

		// Avvio la connessione con il database
		conn = DriverManager.getConnection(url, username, password);
		
		// Definisco tutti gli statements necessari
		
		try {
			checkPassword("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void close() throws SQLException {
				
		if (conn != null)
			conn.close();
	
	}
	
	public boolean login(String username, String password) throws SQLException {
		try {
			String query = "SELECT * FROM login WHERE username = '" + username + "'";
			stmt = conn.prepareStatement(query);
			
			results = stmt.executeQuery();
			
			while(results.next()) {
				int id = results.getInt("id");
				String user = results.getString("username");
				String pw = results.getString("password");
				String privatekey = results.getString("privatekey");
				
				String decryptedPW = AES.decrypt(pw, clientPrivateKeyString + privatekey);
				
				if (user.equals(username) && decryptedPW.equals(password)) {
					return true;
				} else {
				return false;
				}
			}
			
			
		} catch(SQLException e) {
			System.out.println("!!!! ERRORE DURANTE IL LOGIN !!!!");
			e.printStackTrace();
			
			return false;
		} finally {
			try {
				if (results != null)
					results.close();
				
				if (stmt != null)
					stmt.close();
			} catch(SQLException e2) {
				System.out.println("!!!! ERRORE NELLA CHIUSURA DELLE RISORSE !!!");
			}
		}
		System.out.println("Username o Password errati!");
		return false;
}

	public boolean registrazione(String username, String password) throws SQLException, IOException {
		if (checkPassword(password) == false) {
			System.out.println("Password troppo easy bro! Usata troppe volte!");
			return false;
		}
		
		if (checkPasswordWithScore(password) == false) {
			System.out.println("Password con score troppo basso!");
			return false;
		}
		
		//random string for clientside password
		int leftLimit = 48; // numero 0 ASCII
	    int rightLimit = 122; // lettera z ASCII
	    int targetStringLength = 10;
	    Random random = new Random();

	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();

		String encryptedPW = AES.encrypt(password, clientPrivateKeyString + generatedString);

		
		String sql = "INSERT INTO login (username, password, privatekey) VALUES ('" + username + "', '" + encryptedPW + "', '" + generatedString + "')";
		
		try {
			stmt = conn.prepareStatement(sql);
			
			int affectedRows = stmt.executeUpdate();
			if (affectedRows > 0) {
				System.out.println("Inserimento effettuato con successo!!!");
				return true;
			} else {
				System.out.println("Errore nell'inserimento del record");
				return false;
			}
			
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	private boolean checkPassword(String pw) throws IOException {
		Path fileName = Path.of("resources/passwords.txt");
		String actual = Files.readString(fileName);
		String words[] = actual.split("\r\n"); 

		for (String word : words) {
			if (word.equals(pw)) {
				return false;
			}
		}
			
		return true;
	}
	
	private boolean checkPasswordWithScore(String pw) {
		int score = 0;
		
		if (pw.length() > 10) {
			score += 2;
		}
		
		    char ch;
		    boolean capitalFlag = false;
		    boolean lowerCaseFlag = false;
		    boolean numberFlag = false;
		    for(int i=0;i < pw.length();i++) {
		        ch = pw.charAt(i);
		        
		        if (Character.isDigit(ch)) {
		            numberFlag = true;
		        } else if (Character.isUpperCase(ch)) {
		            capitalFlag = true;
		        } else if (Character.isLowerCase(ch)) {
		            lowerCaseFlag = true;
		        }
		    }
		    
		    
		if (numberFlag) {
			score +=2;
		}
		if (capitalFlag) {
			score +=2;
		}
		if (lowerCaseFlag) {
			score +=2;
		}
		
		Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
		Matcher hasSpecial = special.matcher(pw);
		
		if (hasSpecial.find()) {
			score +=2;
		}
		
		
		if (score < 6) {
			return false;
		} else {
			return true;
		}
	}
}
