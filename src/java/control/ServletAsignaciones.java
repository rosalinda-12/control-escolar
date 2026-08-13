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
import servicio.ServicioDocente;
import servicio.ServicioGrupo;
import java.io.IOException;

@WebServlet("/admin/SAsignaciones")
public class ServletAsignaciones extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        int idGrupo = Integer.parseInt(solicitud.getParameter("idGrupo"));

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

        if ("Quitar".equals(accion))
        {
            servicioAsignacion.quitar(Integer.parseInt(solicitud.getParameter("idGrupoMateria")), responsable);
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
