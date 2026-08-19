package servicio;

import doa.DAOUsuario;
import modelo.Usuario;
import util.PasswordUtil;

public class ServicioAutenticacion
{
    private final DAOUsuario daoUsuario;

    public ServicioAutenticacion()
    {
        this.daoUsuario = new DAOUsuario();
    }

    public ResultadoAutenticacion iniciarSesion(String correo, String contrasenaPlana)
    {
        Usuario usuario = daoUsuario.buscarPorCorreo(correo);

        if (usuario == null || !PasswordUtil.verificar(contrasenaPlana, usuario.getContrasena()))
        {
            return ResultadoAutenticacion.credencialesInvalidas();
        }

        if (!usuario.isCorreoVerificado())
        {
            return ResultadoAutenticacion.correoNoVerificado();
        }

        if ("Inactivo".equals(usuario.getEstatusRegistro()))
        {
            return ResultadoAutenticacion.cuentaDesactivada();
        }

        if (!"Aprobado".equals(usuario.getEstatusRegistro()))
        {
            return ResultadoAutenticacion.registroNoAprobado();
        }

        return ResultadoAutenticacion.exitoso(usuario);
    }

    public static class ResultadoAutenticacion
    {
        private final boolean exitoso;
        private final String mensajeError;
        private final Usuario usuario;

        private ResultadoAutenticacion(boolean exitoso, String mensajeError, Usuario usuario)
        {
            this.exitoso = exitoso;
            this.mensajeError = mensajeError;
            this.usuario = usuario;
        }

        public static ResultadoAutenticacion exitoso(Usuario usuario)
        {
            return new ResultadoAutenticacion(true, null, usuario);
        }

        public static ResultadoAutenticacion credencialesInvalidas()
        {
            return new ResultadoAutenticacion(false, "Correo o contraseña incorrectos.", null);
        }

        public static ResultadoAutenticacion correoNoVerificado()
        {
            return new ResultadoAutenticacion(false, "Todavía no verificas tu correo. Revisa tu bandeja de entrada.", null);
        }

        public static ResultadoAutenticacion registroNoAprobado()
        {
            return new ResultadoAutenticacion(false, "Tu registro está pendiente de aprobación por el Administrador.", null);
        }

        public static ResultadoAutenticacion cuentaDesactivada()
        {
            return new ResultadoAutenticacion(false, "Esta cuenta fue desactivada. Contacta al Administrador.", null);
        }

        public boolean isExitoso()
        {
            return exitoso;
        }

        public String getMensajeError()
        {
            return mensajeError;
        }

        public Usuario getUsuario()
        {
            return usuario;
        }
    }
}
