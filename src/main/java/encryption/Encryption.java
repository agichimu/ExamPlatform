package encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class Encryption {

    public static final char[] charKey = {'b', 'r', 'u', 't', 'a', 'l'};
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String ENCRYPTION_MODE = "AES/GCM/NoPadding";
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int KEY_LENGTH = 256;
    private static final int ITERATION_COUNT = 65536;
    private static SecretKey secretKey;
    private static final byte[] iv;

    static {
        String password = Arrays.toString(charKey);
        try {
            generateSecretKey(password);
            iv = generateIV(); // Generate a single IV for all encryption operations
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }

    private static byte[] generateIV() {
        byte[] iv = new byte[12];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }

    public static void generateSecretKey(String password) throws Exception {
        byte[] salt = generateSalt();
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
        byte[] raw = factory.generateSecret(spec).getEncoded();
        secretKey = new SecretKeySpec(raw, ENCRYPTION_ALGORITHM);
    }

    public static String encrypt(String clearText) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_MODE);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
        byte[] encryptedBytes = cipher.doFinal(clearText.getBytes(StandardCharsets.UTF_8));
        byte[] ciphertext = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, ciphertext, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, ciphertext, iv.length, encryptedBytes.length);
        return Base64.getEncoder().encodeToString(ciphertext);
    }

    public static String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_MODE);
        byte[] ciphertext = Base64.getDecoder().decode(encryptedText);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        byte[] decryptedBytes = cipher.doFinal(Arrays.copyOfRange(ciphertext, 12, ciphertext.length));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
//

//    public static void main(String[] args) throws Exception {
//        String plaintextDatabaseName = "exams_platform";
//        String plaintextUsername = "root";
//        String plaintextPassword = "@Alexander!123";
//
//        String encryptedDatabaseName = encrypt(plaintextDatabaseName);
//        String encryptedUsername = encrypt(plaintextUsername);
//        String encryptedPassword = encrypt(plaintextPassword);
//
//        System.out.println("Encrypted Database Name: " + encryptedDatabaseName);
//        System.out.println("Encrypted Username: " + encryptedUsername);
//        System.out.println("Encrypted Password: " + encryptedPassword);
//
//        String decryptedDatabaseName = decrypt(encryptedDatabaseName);
//        String decryptedUsername = decrypt(encryptedUsername);
//        String decryptedPassword = decrypt(encryptedPassword);
//
//        System.out.println("Decrypted Database Name: " + decryptedDatabaseName);
//        System.out.println("Decrypted Username: " + decryptedUsername);
//        System.out.println("Decrypted Password: " + decryptedPassword);
//    }
}
