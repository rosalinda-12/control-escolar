package servicio;

import doa.DAODocenteAsignacion;
import doa.DAOGrupo;
import doa.DAOPermiso;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Grupo;
import modelo.Usuario;
import java.io.IOException;
import java.util.Set;

public class ServicioAutorizacion
{
    private final DAOPermiso daoPermiso;
    private final DAOGrupo daoGrupo;
    private final DAODocenteAsignacion daoDocenteAsignacion;

    public ServicioAutorizacion()
    {
        this.daoPermiso = new DAOPermiso();
        this.daoGrupo = new DAOGrupo();
        this.daoDocenteAsignacion = new DAODocenteAsignacion();
    }

    public boolean tienePermiso(Usuario usuario, String clavePermiso)
    {
        if (usuario == null)
        {
            return false;
        }

        if (usuario.esControlEscolar()
                && !clavePermiso.endsWith(".ver")
                && !"usuarios.aprobar_registro".equals(clavePermiso))
        {
            return false;
        }

        if (usuario.isAdministradorPrincipal())
        {
            return true;
        }

        Set<String> activos = daoPermiso.listarClavesActivasPorRol(usuario.getIdRol());
        return activos.contains(clavePermiso);
    }

    public boolean puedeGestionarAutomatizaciones(Usuario usuario)
    {
        return usuario != null && (usuario.isAdministradorPrincipal()
                || usuario.esControlEscolar()
                || tienePermiso(usuario, "grupos.gestionar"));
    }

    public boolean puedeOperarEnCarreraDelGrupo(Usuario usuario, int idGrupo)
    {
        if (usuario.isAdministradorPrincipal())
        {
            return true;
        }

        if (!usuario.esSubdirector())
        {
            return false;
        }

        if (usuario.getIdCarrera() == null)
        {
            return false;
        }

        Grupo grupo = daoGrupo.buscarPorId(idGrupo);
        return grupo != null && usuario.tieneCarrera(grupo.getIdCarrera());
    }

    public boolean puedeOperarEnCarrera(Usuario usuario, int idCarrera)
    {
        if (usuario.isAdministradorPrincipal())
        {
            return true;
        }

        return usuario.esSubdirector() && usuario.tieneCarrera(idCarrera);
    }

    public boolean puedeOperarSobreGrupoMateria(Usuario usuario, int idGrupoMateria)
    {
        if (usuario.isAdministradorPrincipal())
        {
            return true;
        }

        if (usuario.esMaestro())
        {
            return usuario.getIdProfesor() != null
                    && daoDocenteAsignacion.perteneceADocente(idGrupoMateria, usuario.getIdProfesor());
        }

        return false;
    }

    public boolean autorizarOResponder403(HttpServletResponse respuesta, Usuario usuario, String clavePermiso) throws IOException
    {
        if (tienePermiso(usuario, clavePermiso))
        {
            return true;
        }

        respuesta.sendError(HttpServletResponse.SC_FORBIDDEN,
                "No tienes el permiso '" + clavePermiso + "' para realizar esta acción.");
        return false;
    }

    public boolean autorizarCarreraOResponder403(HttpServletResponse respuesta, Usuario usuario, int idGrupo) throws IOException
    {
        if (puedeOperarEnCarreraDelGrupo(usuario, idGrupo))
        {
            return true;
        }

        respuesta.sendError(HttpServletResponse.SC_FORBIDDEN,
                "Ese grupo no pertenece a la carrera que tienes asignada.");
        return false;
    }
}
