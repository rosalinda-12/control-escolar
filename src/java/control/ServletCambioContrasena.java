package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import servicio.ServicioCambioContrasena;
import java.io.IOException;

@WebServlet("/SCambioContrasena")
public class ServletCambioContrasena extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        solicitud.getServletContext().getRequestDispatcher("/cambio_contrasena.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");

        String accion = solicitud.getParameter("accion");
        ServicioCambioContrasena servicioCambioContrasena = new ServicioCambioContrasena();

        if ("omitir".equals(accion))
        {
            servicioCambioContrasena.omitirCambioPrimerInicio(usuario.getIdUsuario());
            usuario.setRequiereCambioContrasena(false);
            redirigirAPanel(solicitud, respuesta, usuario);
            return;
        }

        String contrasenaActual = solicitud.getParameter("tfContrasenaActual");
        String contrasenaNueva = solicitud.getParameter("tfContrasenaNueva");

        ServicioCambioContrasena.ResultadoCambio resultado = servicioCambioContrasena.cambiar(usuario, contrasenaActual, contrasenaNueva);

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.getServletContext().getRequestDispatcher("/cambio_contrasena.jsp").forward(solicitud, respuesta);
            return;
        }

        usuario.setRequiereCambioContrasena(false);
        redirigirAPanel(solicitud, respuesta, usuario);
    }

    private void redirigirAPanel(HttpServletRequest solicitud, HttpServletResponse respuesta, Usuario usuario) throws IOException
    {
        if (usuario.esAdministrador())
        {
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SPanel");
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
