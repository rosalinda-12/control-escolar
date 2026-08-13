package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import servicio.ServicioAprobacionRegistro;
import java.io.IOException;

@WebServlet("/admin/SSolicitudesRegistro")
public class ServletSolicitudesRegistro extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        ServicioAprobacionRegistro servicioAprobacionRegistro = new ServicioAprobacionRegistro();
        solicitud.setAttribute("solicitudes", servicioAprobacionRegistro.listarPendientes());
        solicitud.getServletContext().getRequestDispatcher("/admin/solicitudes_registro.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");

        int idUsuarioSolicitante = Integer.parseInt(solicitud.getParameter("idUsuario"));
        String accion = solicitud.getParameter("accion");

        ServicioAprobacionRegistro servicioAprobacionRegistro = new ServicioAprobacionRegistro();

        if ("Aprobar".equals(accion))
        {
            servicioAprobacionRegistro.aprobar(idUsuarioSolicitante, responsable);
        }
        else
        {
            servicioAprobacionRegistro.rechazar(idUsuarioSolicitante, responsable);
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SSolicitudesRegistro");
    }
}
