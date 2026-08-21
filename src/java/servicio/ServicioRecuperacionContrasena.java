package servicio;

import doa.DAOUsuario;
import modelo.Usuario;
import util.CodigoUtil;
import util.EmailUtil;
import util.PasswordUtil;
import java.time.LocalDateTime;



public class ServicioRecuperacionContrasena
{
    private final DAOUsuario daoUsuario;

    public ServicioRecuperacionContrasena()
    {
        this.daoUsuario = new DAOUsuario();
    }



    public ResultadoSolicitud solicitarCodigo(String correo)
    {
        Usuario usuario = daoUsuario.buscarPorCorreo(correo);

        if (usuario == null)
        {
            return ResultadoSolicitud.fallo("No existe una cuenta con ese correo.");
        }

        if (!usuario.isCorreoVerificado())
        {
            return ResultadoSolicitud.fallo("Esa cuenta todavía no verifica su correo. Revisa tu bandeja de entrada.");
        }

        if (!"Aprobado".equals(usuario.getEstatusRegistro()))
        {
            return ResultadoSolicitud.fallo("Tu registro está pendiente de aprobación por el Administrador.");
        }

        enviarCodigoNuevo(usuario);
        return ResultadoSolicitud.exito(usuario.getIdUsuario());
    }



    public void reenviarCodigo(int idUsuario)
    {
        Usuario usuario = daoUsuario.buscarPorId(idUsuario);

        if (usuario == null)
        {
            return;
        }

        enviarCodigoNuevo(usuario);
    }

    private void enviarCodigoNuevo(Usuario usuario)
    {
        String codigoNuevo = CodigoUtil.generarCodigo();
        LocalDateTime expiracionNueva = CodigoUtil.calcularExpiracion();

        daoUsuario.actualizarCodigoVerificacion(usuario.getIdUsuario(), codigoNuevo, expiracionNueva);
        EmailUtil.enviarCodigoRecuperacion(usuario.getCorreo(), usuario.getNombres(), codigoNuevo);
    }



    public ResultadoVerificacion verificarCodigo(int idUsuario, String codigoCapturado)
    {
        Usuario usuario = daoUsuario.buscarPorId(idUsuario);

        if (usuario == null)
        {
            return ResultadoVerificacion.fallo("No se encontró la cuenta.");
        }

        if (CodigoUtil.expirado(usuario.getExpiracionCodigo()))
        {
            return ResultadoVerificacion.fallo("El código ya venció. Pide uno nuevo.");
        }

        if (usuario.getCodigoVerificacion() == null || !usuario.getCodigoVerificacion().equals(codigoCapturado))
        {
            return ResultadoVerificacion.fallo("El código no es correcto.");
        }

        return ResultadoVerificacion.exito();
    }



    public ResultadoSimple restablecerContrasena(int idUsuario, String contrasenaNueva)
    {
        if (contrasenaNueva == null || contrasenaNueva.length() < 8)
        {
            return ResultadoSimple.fallo("La contraseña nueva debe tener al menos 8 caracteres.");
        }

        daoUsuario.actualizarContrasena(idUsuario, PasswordUtil.generarHash(contrasenaNueva));
        daoUsuario.limpiarCodigo(idUsuario);
        return ResultadoSimple.exito(idUsuario);
    }

    public static class ResultadoSolicitud
    {
        private final boolean exito;
        private final String mensajeError;
        private final int idUsuario;

        private ResultadoSolicitud(boolean exito, String mensajeError, int idUsuario)
        {
            this.exito = exito;
            this.mensajeError = mensajeError;
            this.idUsuario = idUsuario;
        }

        public static ResultadoSolicitud exito(int idUsuario)
        {
            return new ResultadoSolicitud(true, null, idUsuario);
        }

        public static ResultadoSolicitud fallo(String mensajeError)
        {
            return new ResultadoSolicitud(false, mensajeError, 0);
        }

        public boolean isExito()
        {
            return exito;
        }

        public String getMensajeError()
        {
            return mensajeError;
        }

        public int getIdUsuario()
        {
            return idUsuario;
        }
    }

    public static class ResultadoVerificacion
    {
        private final boolean exito;
        private final String mensajeError;

        private ResultadoVerificacion(boolean exito, String mensajeError)
        {
            this.exito = exito;
            this.mensajeError = mensajeError;
        }

        public static ResultadoVerificacion exito()
        {
            return new ResultadoVerificacion(true, null);
        }

        public static ResultadoVerificacion fallo(String mensajeError)
        {
            return new ResultadoVerificacion(false, mensajeError);
        }

        public boolean isExito()
        {
            return exito;
        }

        public String getMensajeError()
        {
            return mensajeError;
        }
    }
}
