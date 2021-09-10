import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
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
	
	/**
	 * Metodo che calcola la robustezza della password inserita in fase di registrazione
	 * verificando che soddisfi le seguenti regole:
	 * Accettare solo password il cui ‘score robustezza’ è superiore/uguale a 6:
	 * Se la lunghezza password è minore di 7, lo score è sempre 0
	 * Se la lunghezza password è maggiore di 10, score +2
	 * Se la password contiene almeno 1 numero, score +2
	 * Se la password contiene almeno 1 carattere minuscolo, score +2
	 * Se la password contiene almeno 1 carattere maiuscolo, score +2
	 * Se la password contiene almeno 1 carattere tra “~!@#$%^&*()_-”, score +2
	 * @param password inserita dall'utente
	 * @return true se il conteggio supera strettamente il valore 5
	 */
    public boolean isStrongPass(String password){
        
    	// Utilizzo le espressioni regolari
    	// punteggio della password
        int strongCont = 0;
        
        // regola sulla lunghezza minima
        if (password.length() < 7)
            return false;
        
        // regola sulla lunghezza >= 10
        if (password.length() > 10 )
        	strongCont += 2;
        
        // regola sul contenere una cifra
        if (password.matches("(?=.*\\d).*"))
        	strongCont += 2;
        
        // regola sul contenere una lettera minuscola
        if (password.matches("(?=.*[a-z]).*")) 
        	strongCont += 2;
        
        // regola sul contenere una lettera maiuscola
        if (password.matches("(?=.*[A-Z]).*"))
        	strongCont += 2;    
        
        // regola sul contenere un carattere speciale
        if (password.matches("(?=.*[~!@#$%^&*()_-]).*"))
        	strongCont += 2;
        
        return strongCont >= 6;        
    }

	public boolean isCommonPsw(String password) {
		return commonPsw.contains(password);
	}
	
	/**
	 * Metodo che genera un salt per la password inserita dal nuovo utente
	 * @return
	 */
	public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[16];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
	
	/**
	 * Metodo che genera la hash password usando la password inserita dall'utente
	 * e il salt assegnato in fase di registrazione
	 * @param StringToHash
	 * @param salt
	 * @return
	 */
	public String getHashSHA512(String StringToHash, String salt){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(StringToHash.getBytes(StandardCharsets.UTF_8));
            generatedPassword = Base64.getEncoder().encodeToString(bytes);
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return generatedPassword;
    }

	public static void handleMainLoop(LoginTable loginTable) throws SQLException, FileNotFoundException {

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
					String password = new String(console.readPassword("Password: "));

					if (azione.equals("1"))
						loginTable.login(username, password);
					
					else if (azione.equals("2")) {

						String password2 = new String(console.readPassword("Conferma la password: "));

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

}