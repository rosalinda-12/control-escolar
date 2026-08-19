package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import servicio.ServicioCarrera;
import java.io.IOException;

@WebServlet("/subdirector/SPanel")
public class ServletPanelSubdirector extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");

        if (usuarioSesion.getIdCarrera() != null)
        {
            solicitud.setAttribute("carrera", new ServicioCarrera().buscarPorId(usuarioSesion.getIdCarrera()));
        }

        solicitud.getServletContext().getRequestDispatcher("/subdirector/panel.jsp").forward(solicitud, respuesta);
    }
}
