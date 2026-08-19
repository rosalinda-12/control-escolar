package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import servicio.ServicioAutenticacion;
import java.io.IOException;

@WebServlet("/SLogin")
public class ServletLogin extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        solicitud.getServletContext().getRequestDispatcher("/login.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        String correo = solicitud.getParameter("tfCorreo");
        String contrasena = solicitud.getParameter("tfContrasena");

        ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();
        ServicioAutenticacion.ResultadoAutenticacion resultado = servicioAutenticacion.iniciarSesion(correo, contrasena);

        if (!resultado.isExitoso())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.getServletContext().getRequestDispatcher("/login.jsp").forward(solicitud, respuesta);
            return;
        }

        Usuario usuario = resultado.getUsuario();
        HttpSession sesion = solicitud.getSession(true);
        sesion.setAttribute("usuario", usuario);
        sesion.setAttribute("rol", usuario.getNombreRol());

        if (usuario.isRequiereCambioContrasena())
        {
            respuesta.sendRedirect(solicitud.getContextPath() + "/SCambioContrasena");
            return;
        }

        if (usuario.puedeEntrarAAreaAdmin())
        {
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SPanel");
        }
        else if (usuario.esSubdirector())
        {
            respuesta.sendRedirect(solicitud.getContextPath() + "/subdirector/SPanel");
        }
        else if (usuario.esMaestro())
        {
            respuesta.sendRedirect(solicitud.getContextPath() + "/maestro/SPanel");
        }
        else
        {
            respuesta.sendRedirect(solicitud.getContextPath() + "/alumno/SPanel");
        }
    }
}
