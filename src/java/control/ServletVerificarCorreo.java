package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servicio.ServicioVerificacionCorreo;
import java.io.IOException;

@WebServlet("/SVerificarCorreo")
public class ServletVerificarCorreo extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        int idUsuario = Integer.parseInt(solicitud.getParameter("idUsuario"));
        solicitud.setAttribute("idUsuario", idUsuario);
        solicitud.getServletContext().getRequestDispatcher("/verificar.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        int idUsuario = Integer.parseInt(solicitud.getParameter("idUsuario"));
        String accion = solicitud.getParameter("accion");

        ServicioVerificacionCorreo servicioVerificacion = new ServicioVerificacionCorreo();

        if ("reenviar".equals(accion))
        {
            servicioVerificacion.reenviarCodigo(idUsuario);
            solicitud.setAttribute("mensaje", "Te enviamos un código nuevo.");
            solicitud.setAttribute("idUsuario", idUsuario);
            solicitud.getServletContext().getRequestDispatcher("/verificar.jsp").forward(solicitud, respuesta);
            return;
        }

        String codigo = solicitud.getParameter("tfCodigo");
        ServicioVerificacionCorreo.ResultadoVerificacion resultado = servicioVerificacion.verificar(idUsuario, codigo);

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.setAttribute("idUsuario", idUsuario);
            solicitud.getServletContext().getRequestDispatcher("/verificar.jsp").forward(solicitud, respuesta);
            return;
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/SLogin?verificado=1");
    }
}
