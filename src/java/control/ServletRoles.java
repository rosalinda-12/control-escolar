package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Rol;
import modelo.Usuario;
import servicio.ResultadoSimple;
import servicio.ServicioAutorizacion;
import servicio.ServicioRol;
import java.io.IOException;

@WebServlet("/admin/SRoles")
public class ServletRoles extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();

        if (!autorizacion.autorizarOResponder403(respuesta, usuarioSesion, "roles.gestionar"))
        {
            return;
        }

        solicitud.setAttribute("roles", new ServicioRol().listar());
        solicitud.getServletContext().getRequestDispatcher("/admin/roles.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();

        if (!autorizacion.autorizarOResponder403(respuesta, responsable, "roles.gestionar"))
        {
            return;
        }

        Rol rol = new Rol();
        rol.setNombreRol(solicitud.getParameter("tfNombreRol"));
        rol.setDescripcion(solicitud.getParameter("tfDescripcion"));

        ResultadoSimple resultado = new ServicioRol().agregar(rol, responsable);

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.setAttribute("roles", new ServicioRol().listar());
            solicitud.getServletContext().getRequestDispatcher("/admin/roles.jsp").forward(solicitud, respuesta);
            return;
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SRoles");
    }
}
