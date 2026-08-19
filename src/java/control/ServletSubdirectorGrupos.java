package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import doa.DAOGrupo;
import java.io.IOException;

/**
 * Grupos de la carrera del Subdirector en sesión (solo consulta; el
 * Subdirector no da de alta grupos, solo asigna docentes desde aquí,
 * ver ServletSubdirectorAsignaciones).
 */
@WebServlet("/subdirector/SGrupos")
public class ServletSubdirectorGrupos extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");

        if (usuarioSesion.getIdCarrera() == null)
        {
            solicitud.setAttribute("error", "Tu cuenta de Subdirector no tiene una carrera asignada. Pide al Administrador que la configure.");
            solicitud.setAttribute("grupos", new java.util.ArrayList<>());
            solicitud.getServletContext().getRequestDispatcher("/subdirector/grupos.jsp").forward(solicitud, respuesta);
            return;
        }

        solicitud.setAttribute("grupos", new DAOGrupo().listarPorCarrera(usuarioSesion.getIdCarrera()));
        solicitud.getServletContext().getRequestDispatcher("/subdirector/grupos.jsp").forward(solicitud, respuesta);
    }
}
