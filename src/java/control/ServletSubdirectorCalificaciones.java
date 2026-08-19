package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import servicio.ServicioAutorizacion;
import servicio.ServicioCalificacion;
import java.io.IOException;

/**
 * Calificaciones para el Subdirector: SOLO CONSULTA (no puede editar ni
 * eliminar) y SOLO de la carrera que tiene asignada. El id_carrera nunca
 * se toma de un parámetro de la petición, siempre de usuarioSesion, para
 * que no sea posible ver otra carrera manipulando la URL.
 */
@WebServlet("/subdirector/SCalificaciones")
public class ServletSubdirectorCalificaciones extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();

        if (!autorizacion.autorizarOResponder403(respuesta, usuarioSesion, "calificaciones.ver"))
        {
            return;
        }

        if (usuarioSesion.getIdCarrera() == null)
        {
            solicitud.setAttribute("error", "Tu cuenta de Subdirector no tiene una carrera asignada. Pide al Administrador que la configure.");
            solicitud.setAttribute("calificaciones", new java.util.ArrayList<>());
            solicitud.getServletContext().getRequestDispatcher("/subdirector/calificaciones.jsp").forward(solicitud, respuesta);
            return;
        }

        solicitud.setAttribute("calificaciones",
                new ServicioCalificacion().listarParaAdmin(usuarioSesion.getIdCarrera()));
        solicitud.getServletContext().getRequestDispatcher("/subdirector/calificaciones.jsp").forward(solicitud, respuesta);
    }
}
