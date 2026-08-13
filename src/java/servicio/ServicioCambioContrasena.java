package servicio;

import doa.DAOUsuario;
import modelo.Usuario;
import util.PasswordUtil;

public class ServicioCambioContrasena
{
    private final DAOUsuario daoUsuario;

    public ServicioCambioContrasena()
    {
        this.daoUsuario = new DAOUsuario();
    }

    public ResultadoCambio cambiar(Usuario usuario, String contrasenaActual, String contrasenaNueva)
    {
        if (!PasswordUtil.verificar(contrasenaActual, usuario.getContrasena()))
        {
            return ResultadoCambio.fallo("La contraseña actual no es correcta.");
        }

        daoUsuario.actualizarContrasena(usuario.getIdUsuario(), PasswordUtil.generarHash(contrasenaNueva));
        return ResultadoCambio.exito();
    }

    public void omitirCambioPrimerInicio(int idUsuario)
    {
        daoUsuario.marcarPrimerInicioSesionCompletado(idUsuario);
    }

    public static class ResultadoCambio
    {
        private final boolean exito;
        private final String mensajeError;

        private ResultadoCambio(boolean exito, String mensajeError)
        {
            this.exito = exito;
            this.mensajeError = mensajeError;
        }

        public static ResultadoCambio exito()
        {
            return new ResultadoCambio(true, null);
        }

        public static ResultadoCambio fallo(String mensajeError)
        {
            return new ResultadoCambio(false, mensajeError);
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
