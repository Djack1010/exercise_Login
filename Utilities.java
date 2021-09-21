import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.Console;

public class Utilities {

	public ArrayList<String> commonPsw;
	private static Utilities single_instance = null;

	private Utilities() {
		
		this.commonPsw = new ArrayList<String>();

		try(Scanner s = new Scanner(new File("resources/MostCommonPsw.txt"))) {
			
			while (s.hasNext()){
				commonPsw.add((String) s.next());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/* Versione completa per evitare 'race condition' (accessi simultanei) 
	
		public static Utilities getInstance() {
		// thread safe, synchronized block
		if (single_instance == null) {
			synchronized (Utilities.class) {
				if (single_instance == null) {
					single_instance = new Utilities();
				}
			}
		}
		return single_instance;
	}	
	
	*/

	// NOT SAFE for race condition
	public static Utilities getInstance() {
		// thread safe, synchronized block
		if (single_instance == null) {
			single_instance = new Utilities();
		}
		return single_instance;
	}

	public boolean isCommonPsw(String password) {

		return commonPsw.contains(password);

	}

	public static void handleMainLoop(LoginTable loginTable) throws SQLException, FileNotFoundException, NoSuchAlgorithmException {

		Boolean continua = true;
		String azione = null;
		Scanner input = new Scanner(System.in);

		while (continua) {

			System.out.print("Che operazione vuoi effettuare? (0 per uscire, 1 per login, 2 per registrazione): ");

			azione = input.nextLine();
			continua = ! azione.equals("0");
				
			if (continua) {

				if (azione.equals("1") || azione.equals("2")) {

					System.out.print("Username: ");
					String username = input.nextLine();

					Console console = System.console();

					// Utilizzo Console.readPassword per celare la passowrd inserita 
					//  in scrittura da terminale
					//String password = new String(console.readPassword("Password: "));
					// sostituisco perché con Eclipse non è possibile
					System.out.print("Password: ");
					String password = input.nextLine();

					if (azione.equals("1"))
						loginTable.login(username, password);
					
					else if (azione.equals("2")) {
						
						// sostituisco perché con Eclipse non è possibile
						//String password2 = new String(console.readPassword("Conferma la password: "));
						System.out.print("Inserisci nuovamente la Password: ");
						String password2 = input.nextLine();

						if (password.equals(password2))
							loginTable.registrazione(username, password);
						else
							System.out.println("Errore, le password non combaciano!");
					}

				} else
					System.out.println("Input non valido...");
				
			} 	

		}	

		System.out.println();
		System.out.println("Uscita dal programma: ciao !");
		input.close();

	}

	public static boolean isStrongPassword (String password) {
		int score=0;
		boolean isLong11OK = false;
		boolean isNumberOK = false;
		boolean isLowerCaseOK = false;
		boolean isUpperCaseOK = false;
		boolean isSymbolOK = false;
		String specialChars = "~!@#$%^&*()_-";
		char current;
		
		for (int i=0; i<password.length(); i++) {
			current = password.charAt(i);
			if (score < 6) {
				if (!isLong11OK && i>=10) {
					isLong11OK = true;
					score+=2;
				}
				if (!isNumberOK && Character.isDigit(current)) {
					isNumberOK = true;
					score+=2;
				}
				if (!isLowerCaseOK && Character.isLetter(current) && Character.isLowerCase(current)) {
					isLowerCaseOK = true;
					score+=2;
				}
				if (!isUpperCaseOK && Character.isLetter(current) && Character.isUpperCase(current)) {
					isUpperCaseOK = true;
					score+=2;
				}
				if (!isSymbolOK && specialChars.contains(Character.toString(current))) {
					isSymbolOK = true;
					score+=2;
				}
			} else
				return true;
		}
		
		return false;
	}

	public static String createSalt(String pass) {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		
		return salt.toString();
	}

	public static String createHash(String salt, String pass) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(salt.getBytes());
		byte[] hashedPassword = md.digest(pass.getBytes(StandardCharsets.UTF_8));
		
		return hashedPassword.toString();
	}

}