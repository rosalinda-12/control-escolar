package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import servicio.ServicioRecuperacionContrasena;
import java.io.IOException;

@WebServlet("/SVerificarCodigoRecuperacion")
public class ServletVerificarCodigoRecuperacion extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        int idUsuario = Integer.parseInt(solicitud.getParameter("idUsuario"));
        solicitud.setAttribute("idUsuario", idUsuario);
        solicitud.getServletContext().getRequestDispatcher("/verificar_codigo_recuperacion.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        int idUsuario = Integer.parseInt(solicitud.getParameter("idUsuario"));
        String accion = solicitud.getParameter("accion");

        ServicioRecuperacionContrasena servicioRecuperacion = new ServicioRecuperacionContrasena();

        if ("reenviar".equals(accion))
        {
            servicioRecuperacion.reenviarCodigo(idUsuario);
            solicitud.setAttribute("mensaje", "Te enviamos un código nuevo.");
            solicitud.setAttribute("idUsuario", idUsuario);
            solicitud.getServletContext().getRequestDispatcher("/verificar_codigo_recuperacion.jsp").forward(solicitud, respuesta);
            return;
        }

        String codigo = solicitud.getParameter("tfCodigo");
        ServicioRecuperacionContrasena.ResultadoVerificacion resultado = servicioRecuperacion.verificarCodigo(idUsuario, codigo);

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.setAttribute("idUsuario", idUsuario);
            solicitud.getServletContext().getRequestDispatcher("/verificar_codigo_recuperacion.jsp").forward(solicitud, respuesta);
            return;
        }

        // Marca en sesión que este usuario ya pasó la verificación del
        // código, para que SNuevaContrasena no se pueda abrir directamente
        // sin haber capturado un código válido primero.
        HttpSession sesion = solicitud.getSession(true);
        sesion.setAttribute("idUsuarioRecuperacionVerificado", idUsuario);

        respuesta.sendRedirect(solicitud.getContextPath() + "/SNuevaContrasena?idUsuario=" + idUsuario);
    }
}
