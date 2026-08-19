package servicio;

import doa.DAOUsuario;
import modelo.Usuario;
import java.util.ArrayList;

public class ServicioAprobacionRegistro
{
    private final DAOUsuario daoUsuario;
    private final ServicioBitacora servicioBitacora;

    public ServicioAprobacionRegistro()
    {
        this.daoUsuario = new DAOUsuario();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<Usuario> listarPendientes()
    {
        return daoUsuario.listarSolicitudesPendientes();
    }

    public void aprobar(int idUsuarioSolicitante, Usuario administrador)
    {
        Usuario solicitante = daoUsuario.buscarPorId(idUsuarioSolicitante);

        if (solicitante == null)
        {
            return;
        }

        // No se puede aprobar antes de que la propia persona haya
        // verificado su correo: el orden siempre es autoregistro ->
        // verificación de correo -> aprobación.
        if (!solicitante.isCorreoVerificado())
        {
            return;
        }

        daoUsuario.actualizarEstatusRegistro(idUsuarioSolicitante, "Aprobado");
        servicioBitacora.registrarAlta(administrador, "usuarios", idUsuarioSolicitante,
                "Aprobó el registro de " + solicitante.getCorreo() + " (" + solicitante.getNombreRol() + ")");
    }

    public void rechazar(int idUsuarioSolicitante, Usuario administrador)
    {
        Usuario solicitante = daoUsuario.buscarPorId(idUsuarioSolicitante);
        daoUsuario.actualizarEstatusRegistro(idUsuarioSolicitante, "Rechazado");
        servicioBitacora.registrarBaja(administrador, "usuarios", idUsuarioSolicitante,
                "Rechazó el registro de " + solicitante.getCorreo() + " (" + solicitante.getNombreRol() + ")");
    }
}
