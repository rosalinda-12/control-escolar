package control;

import doa.DAOGrupoMateria;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import servicio.ResultadoSimple;
import servicio.ServicioAsignacionDocente;
import servicio.ServicioAutorizacion;
import servicio.ServicioDocente;
import servicio.ServicioGrupo;
import java.io.IOException;

/**
 * Misma funcionalidad que /admin/SAsignaciones (asignar/quitar un
 * docente de una materia de grupo), pero para el Subdirector: se valida
 * el permiso "asignaciones.crear"/"asignaciones.editar" y, además, que
 * el grupo pertenezca a la carrera que tiene asignada. Si cualquiera de
 * las dos validaciones falla, se responde HTTP 403 (no basta con que el
 * botón esté oculto en la pantalla).
 */
@WebServlet("/subdirector/SAsignaciones")
public class ServletSubdirectorAsignaciones extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();

        int idGrupo = Integer.parseInt(solicitud.getParameter("idGrupo"));

        if (!autorizacion.autorizarOResponder403(respuesta, usuarioSesion, "asignaciones.ver"))
        {
            return;
        }

        if (!autorizacion.autorizarCarreraOResponder403(respuesta, usuarioSesion, idGrupo))
        {
            return;
        }

        if (sesion.getAttribute("errorAsignacion") != null)
        {
            solicitud.setAttribute("error", sesion.getAttribute("errorAsignacion"));
            sesion.removeAttribute("errorAsignacion");
        }

        solicitud.setAttribute("grupo", new ServicioGrupo().buscarPorId(idGrupo));
        solicitud.setAttribute("materiasDelGrupo", new DAOGrupoMateria().listarPorGrupo(idGrupo));
        solicitud.setAttribute("docentes", new ServicioDocente().listarActivos());
        solicitud.setAttribute("puedeGestionar", autorizacion.tienePermiso(usuarioSesion, "asignaciones.crear"));

        solicitud.getServletContext().getRequestDispatcher("/subdirector/asignaciones.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();

        String accion = solicitud.getParameter("accion");
        int idGrupo = Integer.parseInt(solicitud.getParameter("idGrupo"));

        String clavePermiso = "Quitar".equals(accion) ? "asignaciones.editar" : "asignaciones.crear";

        if (!autorizacion.autorizarOResponder403(respuesta, usuarioSesion, clavePermiso))
        {
            return;
        }

        if (!autorizacion.autorizarCarreraOResponder403(respuesta, usuarioSesion, idGrupo))
        {
            return;
        }

        ServicioAsignacionDocente servicioAsignacion = new ServicioAsignacionDocente();

        if ("Quitar".equals(accion))
        {
            servicioAsignacion.quitar(Integer.parseInt(solicitud.getParameter("idGrupoMateria")), usuarioSesion);
        }
        else
        {
            int idGrupoMateria = Integer.parseInt(solicitud.getParameter("idGrupoMateria"));
            int idDocente = Integer.parseInt(solicitud.getParameter("selDocente"));
            ResultadoSimple resultado = servicioAsignacion.asignar(idDocente, idGrupoMateria, usuarioSesion);

            if (!resultado.isExito())
            {
                sesion.setAttribute("errorAsignacion", resultado.getMensajeError());
            }
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/subdirector/SAsignaciones?idGrupo=" + idGrupo);
    }
}
