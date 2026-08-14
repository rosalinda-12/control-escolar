package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servicio.ServicioRecuperacionContrasena;
import java.io.IOException;

@WebServlet("/SOlvideContrasena")
public class ServletOlvideContrasena extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        solicitud.getServletContext().getRequestDispatcher("/olvide_contrasena.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        String correo = solicitud.getParameter("tfCorreo");

        ServicioRecuperacionContrasena servicioRecuperacion = new ServicioRecuperacionContrasena();
        ServicioRecuperacionContrasena.ResultadoSolicitud resultado = servicioRecuperacion.solicitarCodigo(correo);

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.getServletContext().getRequestDispatcher("/olvide_contrasena.jsp").forward(solicitud, respuesta);
            return;
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/SVerificarCodigoRecuperacion?idUsuario=" + resultado.getIdUsuario());
    }
}
