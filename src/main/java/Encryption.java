import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

/*@SuppressWarnings("ALL")*/
public class Encryption {

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String ENCRYPTION_MODE = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int KEY_LENGTH = 256;
    private static final int ITERATION_COUNT = 65536;

    private static final char[] charKey = {'b','r','u','t','a','l'};

    private SecretKey secretKey;

    public Encryption() throws Exception {
        generateSecretKey( Arrays.toString ( charKey ) );
    }

    private void generateSecretKey(String password) throws Exception {
        byte[] salt = generateSalt();
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
        byte[] raw = factory.generateSecret(spec).getEncoded();
        secretKey = new SecretKeySpec(raw, ENCRYPTION_ALGORITHM);
    }

    private byte[] generateSalt() {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }

    public String encrypt(String clearText) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(clearText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_MODE);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception {


        Encryption encryptor = new Encryption();

        String plaintextDatabaseName = "exams_platform";
        String plaintextUsername = "root";
        String plaintextPassword = "@Alexander!123";

        String encryptedDatabaseName = encryptor.encrypt(plaintextDatabaseName);
        String encryptedUsername = encryptor.encrypt(plaintextUsername);
        String encryptedPassword = encryptor.encrypt(plaintextPassword);

        System.out.println("Encrypted Database Name: " + encryptedDatabaseName);
        System.out.println("Encrypted Username: " + encryptedUsername);
        System.out.println("Encrypted Password: " + encryptedPassword);

       String decryptedDatabaseName = encryptor.decrypt(encryptedDatabaseName);
        String decryptedUsername = encryptor.decrypt(encryptedUsername);
        String decryptedPassword = encryptor.decrypt(encryptedPassword);

        System.out.println("Decrypted Database Name: " + decryptedDatabaseName);
        System.out.println("Decrypted Username: " + decryptedUsername);
        System.out.println("Decrypted Password: " + decryptedPassword);
    }

    }

