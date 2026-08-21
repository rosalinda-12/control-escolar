package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import modelo.Carrera;
import modelo.Grupo;
import doa.DAOGrupo;
import servicio.ServicioCarrera;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/subdirector/SPanel")
public class ServletPanelSubdirector extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");

        ServicioCarrera servicioCarrera = new ServicioCarrera();
        ArrayList<Carrera> carreras = new ArrayList<>();
        for (Integer idCarrera : usuarioSesion.getIdsCarrera())
        {
            Carrera carrera = servicioCarrera.buscarPorId(idCarrera);
            if (carrera != null) carreras.add(carrera);
        }

        ArrayList<Grupo> grupos = new DAOGrupo().listarPorCarreras(usuarioSesion.getIdsCarrera());
        solicitud.setAttribute("carreras", carreras);
        solicitud.setAttribute("grupos", grupos);
        solicitud.setAttribute("carrera", carreras.isEmpty() ? null : carreras.get(0));

        solicitud.getServletContext().getRequestDispatcher("/subdirector/panel.jsp").forward(solicitud, respuesta);
    }
}
