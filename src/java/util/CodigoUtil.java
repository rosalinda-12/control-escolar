package util;

import java.security.SecureRandom;
import java.time.LocalDateTime;

public class CodigoUtil
{
    public static final int SEGUNDOS_EXPIRACION = 120;

    public static String generarCodigo()
    {
        SecureRandom generador = new SecureRandom();
        int numero = generador.nextInt(1000000);
        return String.format("%06d", numero);
    }

    public static LocalDateTime calcularExpiracion()
    {
        return LocalDateTime.now().plusSeconds(SEGUNDOS_EXPIRACION);
    }

    public static boolean expirado(LocalDateTime expiracion)
    {
        return expiracion == null || LocalDateTime.now().isAfter(expiracion);
    }
}
