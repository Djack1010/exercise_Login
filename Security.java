import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Security {
	
	private static Security single_instance = null;
	
	private Security() {		
	}	
	
	public static Security getInstance() {
		// thread safe, synchronized block
		if (single_instance == null) {
			single_instance = new Security();
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

}
