package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil
{
    private static final int LONGITUD_SALT = 16;

    public static String generarHash(String contrasenaPlana)
    {
        byte[] salt = new byte[LONGITUD_SALT];
        new SecureRandom().nextBytes(salt);
        byte[] hash = calcularHash(contrasenaPlana, salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(String contrasenaPlana, String hashAlmacenado)
    {
        String[] partes = hashAlmacenado.split(":");
        if (partes.length != 2)
        {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(partes[0]);
        byte[] hashEsperado = Base64.getDecoder().decode(partes[1]);
        byte[] hashCalculado = calcularHash(contrasenaPlana, salt);

        return MessageDigest.isEqual(hashEsperado, hashCalculado);
    }

    private static byte[] calcularHash(String contrasenaPlana, byte[] salt)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            return digest.digest(contrasenaPlana.getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }
}
