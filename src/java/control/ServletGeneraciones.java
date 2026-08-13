package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Generacion;
import modelo.Usuario;
import servicio.ServicioGeneracion;
import java.io.IOException;

@WebServlet("/admin/SGeneraciones")
public class ServletGeneraciones extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        ServicioGeneracion servicioGeneracion = new ServicioGeneracion();
        solicitud.setAttribute("generaciones", servicioGeneracion.listar());

        String idGeneracionEditar = solicitud.getParameter("editar");
        if (idGeneracionEditar != null)
        {
            solicitud.setAttribute("generacionEditar", servicioGeneracion.buscarPorId(Integer.parseInt(idGeneracionEditar)));
        }

        solicitud.getServletContext().getRequestDispatcher("/admin/generaciones.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        String accion = solicitud.getParameter("accion");
        ServicioGeneracion servicioGeneracion = new ServicioGeneracion();

        if ("Eliminar".equals(accion))
        {
            servicioGeneracion.eliminar(Integer.parseInt(solicitud.getParameter("idGeneracion")), responsable);
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SGeneraciones");
            return;
        }

        Generacion generacion = new Generacion();
        generacion.setNombreGeneracion(solicitud.getParameter("tfNombreGeneracion"));
        generacion.setEstatus(solicitud.getParameter("tfEstatus"));

        if ("Modificar".equals(accion))
        {
            generacion.setIdGeneracion(Integer.parseInt(solicitud.getParameter("idGeneracion")));
            servicioGeneracion.modificar(generacion);
        }
        else
        {
            servicioGeneracion.agregar(generacion, responsable);
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SGeneraciones");
    }
}
