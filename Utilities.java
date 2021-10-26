package LoginGiacomo;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

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

	public static void handleMainLoop(LoginTable loginTable) throws SQLException, 
					FileNotFoundException, NoSuchAlgorithmException {

		Boolean continua = true;
		String azione = null;
		String username="";
		String password = "";
		String password2 = "";
		String securePassword;
		byte[] salt;
		boolean strongPw= false;
		boolean user = false;
		int count =0;
		
		Scanner input = new Scanner(System.in);

		while (continua) {

			System.out.print("Che operazione vuoi effettuare? (0 per uscire, 1 per login, 2 per registrazione): ");
			user = false;
			azione = input.nextLine();
			continua = ! azione.equals("0");
				
			if (continua) {

				if (azione.equals("1") || azione.equals("2")) {
					while (user == false) {
						System.out.print("Username: ");
						username = input.nextLine();
						if (azione.equals("2")) user = loginTable.regUser(username);
						else user =true;
					}
	//				Console console = System.console();
					System.out.print("Password: ");
					password = input.nextLine();
					// Utilizzo Console.readPassword per celare la passowrd inserita 
					//  in scrittura da terminale
	//				String password = new String(console.readPassword("Password: "));*/

					if (azione.equals("1")) {
						salt = loginTable.logUser(username);
						securePassword = get_SHA_512_SecurePassword(password, salt);
						if(loginTable.login(username, securePassword)==false) count++ ;
						if (count>2) loginTable.bloccare(username);
					}
					else if (azione.equals("2")) {
						while (!strongPassw(password)) {
								System.out.println("Il password è debole, reinserisci password");	
								password = input.nextLine();}	
						System.out.print("Confermi Password: ");
						password2 = input.nextLine();
//						String password2 = new String(console.readPassword("Conferma la password: "));

						if (password.equals(password2)) {
							salt = getSalt();
						 	securePassword = get_SHA_512_SecurePassword(password, salt);
							loginTable.registrazione(username, securePassword, salt);
						}
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

	private static String get_SHA_512_SecurePassword(String passwordToHash, byte[] salt)
    {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } 
        catch (NoSuchAlgorithmException e) 
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }
	     
	    //Add salt
	   private static byte[] getSalt() throws NoSuchAlgorithmException
	    {
	        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
	        byte[] salt = new byte[16];
	        sr.nextBytes(salt);
	        return salt;
	    }
	   
	   private static boolean strongPassw(String password) {		   
		   int strPw = 0;
		   if(password.length()<= 7) return false;
		   if (password.matches(".*\\d.*")==true) strPw = strPw + 2;
		   if (password.matches(".*[a-z].*")==true) strPw = strPw + 2;
		   if (password.matches(".*[A-Z].*")==true) strPw = strPw + 2;
		   if (password.matches(".*[~!@#$%^&*()_-].*")==true) strPw = strPw + 2;
		   if (strPw<6) return false;
		return true;
	   }	
}
