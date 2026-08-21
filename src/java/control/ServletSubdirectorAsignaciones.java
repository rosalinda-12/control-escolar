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

@WebServlet("/subdirector/SAsignaciones")
public class ServletSubdirectorAsignaciones extends HttpServlet
{
    private static final String ATRIBUTO_ERROR_ASIGNACION = "errorAsignacion";
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();

        int idGrupo = Integer.parseInt(solicitud.getParameter("idGrupo"));

        if (!autorizacion.autorizarCarreraOResponder403(respuesta, usuarioSesion, idGrupo))
        {
            return;
        }

        if (sesion.getAttribute(ATRIBUTO_ERROR_ASIGNACION) != null)
        {
            solicitud.setAttribute("error", sesion.getAttribute(ATRIBUTO_ERROR_ASIGNACION));
            sesion.removeAttribute(ATRIBUTO_ERROR_ASIGNACION);
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
        String permiso = "Desactivar".equals(accion) ? "asignaciones.editar" : "asignaciones.crear";

        if (!autorizacion.autorizarOResponder403(respuesta, usuarioSesion, permiso)
                || !autorizacion.autorizarCarreraOResponder403(respuesta, usuarioSesion, idGrupo))
        {
            return;
        }

        ServicioAsignacionDocente servicio = new ServicioAsignacionDocente();
        if ("Desactivar".equals(accion))
        {
            servicio.desactivar(Integer.parseInt(solicitud.getParameter("idGrupoMateria")), usuarioSesion);
        }
        else
        {
            ResultadoSimple resultado = servicio.asignar(
                    Integer.parseInt(solicitud.getParameter("selDocente")),
                    Integer.parseInt(solicitud.getParameter("idGrupoMateria")), usuarioSesion);
            if (!resultado.isExito())
            {
                sesion.setAttribute(ATRIBUTO_ERROR_ASIGNACION, resultado.getMensajeError());
            }
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/subdirector/SAsignaciones?idGrupo=" + idGrupo);
    }
}
