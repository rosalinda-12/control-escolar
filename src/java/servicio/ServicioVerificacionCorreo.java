package servicio;

import doa.DAOUsuario;
import modelo.Usuario;
import util.CodigoUtil;
import util.EmailUtil;

public class ServicioVerificacionCorreo
{
    private final DAOUsuario daoUsuario;

    public ServicioVerificacionCorreo()
    {
        this.daoUsuario = new DAOUsuario();
    }

    public ResultadoVerificacion verificar(int idUsuario, String codigoCapturado)
    {
        Usuario usuario = daoUsuario.buscarPorId(idUsuario);

        if (usuario == null)
        {
            return ResultadoVerificacion.fallo("No se encontró la cuenta.");
        }

        if (usuario.isCorreoVerificado())
        {
            return ResultadoVerificacion.exito();
        }

        if (CodigoUtil.expirado(usuario.getExpiracionCodigo()))
        {
            return ResultadoVerificacion.fallo("El código ya venció. Pide uno nuevo.");
        }

        if (!usuario.getCodigoVerificacion().equals(codigoCapturado))
        {
            return ResultadoVerificacion.fallo("El código no es correcto.");
        }

        daoUsuario.marcarCorreoVerificado(idUsuario);
        return ResultadoVerificacion.exito();
    }

    public void reenviarCodigo(int idUsuario)
    {
        Usuario usuario = daoUsuario.buscarPorId(idUsuario);

        if (usuario == null)
        {
            return;
        }

        String codigoNuevo = CodigoUtil.generarCodigo();
        java.time.LocalDateTime expiracionNueva = CodigoUtil.calcularExpiracion();

        daoUsuario.actualizarCodigoVerificacion(idUsuario, codigoNuevo, expiracionNueva);
        EmailUtil.enviarCodigoVerificacion(usuario.getCorreo(), usuario.getNombres(), codigoNuevo);
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
