package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.BoletaCuatrimestre;
import modelo.Usuario;
import servicio.ServicioCalificacion;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/alumno/SCalificaciones")
public class ServletCalificacionesAlumno extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");

        if (usuario.getIdAlumno() == null)
        {
            solicitud.setAttribute("error", "Tu usuario no tiene un expediente de alumno asociado.");
            solicitud.setAttribute("boleta", new ArrayList<BoletaCuatrimestre>());
            solicitud.getServletContext().getRequestDispatcher("/alumno/calificaciones.jsp").forward(solicitud, respuesta);
            return;
        }

        ServicioCalificacion servicioCalificacion = new ServicioCalificacion();
        ArrayList<BoletaCuatrimestre> boleta = servicioCalificacion.obtenerBoletaAlumno(usuario.getIdAlumno());

        solicitud.setAttribute("boleta", boleta);
        solicitud.getServletContext().getRequestDispatcher("/alumno/calificaciones.jsp").forward(solicitud, respuesta);
    }
}
