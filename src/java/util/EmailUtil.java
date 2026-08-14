package util;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil
{
    private static final String CORREO_EMISOR = "rmh121220@gmail.com";
    private static final String CONTRASENA_APLICACION = "rsumcdavwqgdryps";

    public static void enviarCodigoVerificacion(String correoDestino, String nombreDestino, String codigo)
    {
        String cuerpo = "Hola " + nombreDestino + ",\n\nTu codigo de verificacion es: " + codigo
                + "\n\nEste codigo vence en " + CodigoUtil.SEGUNDOS_EXPIRACION + " segundos.";
        enviarCorreo(correoDestino, "Codigo de verificacion - Control Escolar", cuerpo);
    }

    public static void enviarCodigoRecuperacion(String correoDestino, String nombreDestino, String codigo)
    {
        String cuerpo = "Hola " + nombreDestino + ",\n\nRecibimos una solicitud para restablecer tu contraseña.\n\n"
                + "Tu codigo de verificacion es: " + codigo + "\n\nEste codigo vence en "
                + CodigoUtil.SEGUNDOS_EXPIRACION + " segundos.\n\n"
                + "Si tu no pediste este cambio, puedes ignorar este correo.";
        enviarCorreo(correoDestino, "Recuperación de contraseña - Control Escolar", cuerpo);
    }

    private static void enviarCorreo(String correoDestino, String asunto, String cuerpo)
    {
        Properties propiedades = new Properties();
        propiedades.put("mail.smtp.auth", "true");
        propiedades.put("mail.smtp.starttls.enable", "true");
        propiedades.put("mail.smtp.host", "smtp.gmail.com");
        propiedades.put("mail.smtp.port", "587");

        Session sesion = Session.getInstance(propiedades, new jakarta.mail.Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(CORREO_EMISOR, CONTRASENA_APLICACION);
            }
        });

        try
        {
            Message mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(CORREO_EMISOR));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoDestino));
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);
            Transport.send(mensaje);
        }
        catch (MessagingException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }
}
