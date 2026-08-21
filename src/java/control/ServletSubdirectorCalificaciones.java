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

        if (usuarioSesion.getIdsCarrera().isEmpty())
        {
            solicitud.setAttribute("error", "Tu cuenta de Subdirector no tiene una carrera asignada. Pide al Administrador que la configure.");
            solicitud.setAttribute("calificaciones", new java.util.ArrayList<>());
            solicitud.getServletContext().getRequestDispatcher("/subdirector/calificaciones.jsp").forward(solicitud, respuesta);
            return;
        }

        solicitud.setAttribute("calificaciones",
                new ServicioCalificacion().listarParaCarreras(usuarioSesion.getIdsCarrera()));
        solicitud.getServletContext().getRequestDispatcher("/subdirector/calificaciones.jsp").forward(solicitud, respuesta);
    }
}
