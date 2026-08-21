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

@WebServlet("/admin/SAsignaciones")
public class ServletAsignaciones extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesionLectura = solicitud.getSession(false);
        Usuario usuarioSesionLectura = (Usuario) sesionLectura.getAttribute("usuario");

        if (!new ServicioAutorizacion().autorizarOResponder403(respuesta, usuarioSesionLectura, "asignaciones.ver"))
        {
            return;
        }

        String parametroIdGrupo = solicitud.getParameter("idGrupo");


        if (parametroIdGrupo == null || parametroIdGrupo.isEmpty())
        {
            solicitud.setAttribute("grupos", new ServicioGrupo().listar());
            solicitud.getServletContext().getRequestDispatcher("/admin/asignaciones.jsp").forward(solicitud, respuesta);
            return;
        }

        int idGrupo = Integer.parseInt(parametroIdGrupo);

        HttpSession sesion = solicitud.getSession(false);
        if (sesion != null && sesion.getAttribute("errorAsignacion") != null)
        {
            solicitud.setAttribute("error", sesion.getAttribute("errorAsignacion"));
            sesion.removeAttribute("errorAsignacion");
        }

        solicitud.setAttribute("grupo", new ServicioGrupo().buscarPorId(idGrupo));
        solicitud.setAttribute("materiasDelGrupo", new DAOGrupoMateria().listarPorGrupo(idGrupo));
        solicitud.setAttribute("docentes", new ServicioDocente().listarActivos());

        solicitud.getServletContext().getRequestDispatcher("/admin/asignaciones.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        String accion = solicitud.getParameter("accion");
        int idGrupo = Integer.parseInt(solicitud.getParameter("idGrupo"));
        ServicioAsignacionDocente servicioAsignacion = new ServicioAsignacionDocente();
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();

        String clavePermiso = "Desactivar".equals(accion) ? "asignaciones.editar" : "asignaciones.crear";

        if (!autorizacion.autorizarOResponder403(respuesta, responsable, clavePermiso))
        {
            return;
        }

        if ("Desactivar".equals(accion))
        {
            servicioAsignacion.desactivar(Integer.parseInt(solicitud.getParameter("idGrupoMateria")), responsable);
        }
        else
        {
            int idGrupoMateria = Integer.parseInt(solicitud.getParameter("idGrupoMateria"));
            int idDocente = Integer.parseInt(solicitud.getParameter("selDocente"));
            ResultadoSimple resultado = servicioAsignacion.asignar(idDocente, idGrupoMateria, responsable);

            if (!resultado.isExito())
            {
                sesion.setAttribute("errorAsignacion", resultado.getMensajeError());
            }
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SAsignaciones?idGrupo=" + idGrupo);
    }
}
