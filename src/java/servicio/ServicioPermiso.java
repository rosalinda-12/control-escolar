package servicio;

import doa.DAOPermiso;
import doa.DAORol;
import modelo.Permiso;
import modelo.Rol;
import modelo.Usuario;
import java.util.ArrayList;

public class ServicioPermiso
{
    private final DAOPermiso daoPermiso;
    private final DAORol daoRol;
    private final ServicioBitacora servicioBitacora;

    public ServicioPermiso()
    {
        this.daoPermiso = new DAOPermiso();
        this.daoRol = new DAORol();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<Permiso> listarCatalogo()
    {
        return daoPermiso.listarCatalogo();
    }

    public ArrayList<Permiso> listarConEstatusPorRol(int idRol)
    {
        return daoPermiso.listarConEstatusPorRol(idRol);
    }

    /**
     * Activa/desactiva un permiso para un rol. El rol "Administrador"
     * (es_administrador_principal = 1) no depende de esta tabla -su
     * acceso es total por código-, así que no tiene sentido dejar que se
     * le desactive nada; se rechaza explícitamente para que quede claro
     * en la UI por qué esos checks no cambian.
     */
    public ResultadoSimple establecer(int idRol, int idPermiso, boolean activo, Usuario responsable)
    {
        Rol rol = daoRol.buscarPorId(idRol);

        if (rol == null)
        {
            return ResultadoSimple.fallo("El rol ya no existe.");
        }

        if (rol.isEsAdministradorPrincipal())
        {
            return ResultadoSimple.fallo("El rol Administrador siempre tiene acceso total; sus permisos no son editables.");
        }

        daoPermiso.establecer(idRol, idPermiso, activo);
        servicioBitacora.registrarAlta(responsable, "rol_permisos", idPermiso,
                (activo ? "Activó" : "Desactivó") + " un permiso para el rol " + rol.getNombreRol());

        return ResultadoSimple.exito(idPermiso);
    }
}
