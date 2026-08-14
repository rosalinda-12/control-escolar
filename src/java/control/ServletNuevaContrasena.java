package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import servicio.ServicioRecuperacionContrasena;
import java.io.IOException;

@WebServlet("/SNuevaContrasena")
public class ServletNuevaContrasena extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        int idUsuario = Integer.parseInt(solicitud.getParameter("idUsuario"));

        if (!verificacionVigente(solicitud, idUsuario))
        {
            respuesta.sendRedirect(solicitud.getContextPath() + "/SOlvideContrasena");
            return;
        }

        solicitud.setAttribute("idUsuario", idUsuario);
        solicitud.getServletContext().getRequestDispatcher("/nueva_contrasena.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        int idUsuario = Integer.parseInt(solicitud.getParameter("idUsuario"));

        if (!verificacionVigente(solicitud, idUsuario))
        {
            respuesta.sendRedirect(solicitud.getContextPath() + "/SOlvideContrasena");
            return;
        }

        String contrasenaNueva = solicitud.getParameter("tfContrasenaNueva");
        String confirmacion = solicitud.getParameter("tfConfirmarContrasena");

        if (contrasenaNueva == null || !contrasenaNueva.equals(confirmacion))
        {
            solicitud.setAttribute("error", "Las contraseñas no coinciden.");
            solicitud.setAttribute("idUsuario", idUsuario);
            solicitud.getServletContext().getRequestDispatcher("/nueva_contrasena.jsp").forward(solicitud, respuesta);
            return;
        }

        ServicioRecuperacionContrasena servicioRecuperacion = new ServicioRecuperacionContrasena();
        servicio.ResultadoSimple resultadoCambio = servicioRecuperacion.restablecerContrasena(idUsuario, contrasenaNueva);

        if (!resultadoCambio.isExito())
        {
            solicitud.setAttribute("error", resultadoCambio.getMensajeError());
            solicitud.setAttribute("idUsuario", idUsuario);
            solicitud.getServletContext().getRequestDispatcher("/nueva_contrasena.jsp").forward(solicitud, respuesta);
            return;
        }

        HttpSession sesion = solicitud.getSession(false);
        if (sesion != null)
        {
            sesion.removeAttribute("idUsuarioRecuperacionVerificado");
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/SLogin?recuperada=1");
    }

    private boolean verificacionVigente(HttpServletRequest solicitud, int idUsuario)
    {
        HttpSession sesion = solicitud.getSession(false);
        Object idVerificado = sesion == null ? null : sesion.getAttribute("idUsuarioRecuperacionVerificado");
        return idVerificado != null && idVerificado.equals(idUsuario);
    }
}
