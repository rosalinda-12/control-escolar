package servicio;

import doa.DAODocenteAsignacion;
import doa.DAOGrupo;
import doa.DAOPermiso;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Grupo;
import modelo.Usuario;
import java.io.IOException;
import java.util.Set;

/**
 * Punto único de validación de permisos dinámicos (rol_permisos) y de
 * alcance (carrera del Subdirector, materias/grupos del Maestro).
 *
 * Regla general del sistema:
 *   - Administrador (roles.es_administrador_principal = 1) => acceso
 *     total, sin consultar rol_permisos.
 *   - Cualquier otro rol => se valida la clave de permiso contra
 *     rol_permisos (activo = 1) y, si aplica, el alcance del usuario.
 *
 * Esta clase se usa tanto desde los servlets (para responder HTTP 403 si
 * el usuario no tiene el permiso) como, indirectamente, desde las JSP a
 * través de un atributo de sesión, para poder ocultar botones. Ocultar
 * el botón NUNCA es suficiente por sí solo: la validación real siempre
 * ocurre aquí, en el servidor, antes de ejecutar la acción.
 */
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

    /**
     * true si el usuario puede ejecutar la acción identificada por
     * clavePermiso (p. ej. "usuarios.crear"), sin considerar alcance.
     */
    public boolean tienePermiso(Usuario usuario, String clavePermiso)
    {
        if (usuario == null)
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

    /**
     * Para el Subdirector: además del permiso, el grupo/materia sobre el
     * que quiere operar debe pertenecer a su carrera asignada.
     */
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
        return grupo != null && grupo.getIdCarrera() == usuario.getIdCarrera();
    }

    public boolean puedeOperarEnCarrera(Usuario usuario, int idCarrera)
    {
        if (usuario.isAdministradorPrincipal())
        {
            return true;
        }

        return usuario.esSubdirector() && usuario.getIdCarrera() != null && usuario.getIdCarrera() == idCarrera;
    }

    /**
     * Para el Maestro: la materia de grupo sobre la que quiere
     * registrar/consultar calificaciones debe estarle asignada a él.
     * (Reutiliza la validación que ya existía en ServicioCalificacion,
     * centralizada aquí para que todos los puntos de entrada la usen
     * igual.)
     */
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

    /**
     * Verifica el permiso y, si falta, responde HTTP 403 directamente
     * sobre la respuesta del servlet (no basta con ocultar el botón en
     * el frontend). Devuelve true si el servlet puede continuar.
     */
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
