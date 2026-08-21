package control;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import java.io.IOException;

@WebFilter("/admin/*")
public class FiltroAdministrador implements Filter
{


    @Override
    public void doFilter(ServletRequest solicitud, ServletResponse respuesta, FilterChain cadena) throws IOException, ServletException
    {
        HttpServletRequest solicitudHttp = (HttpServletRequest) solicitud;
        HttpServletResponse respuestaHttp = (HttpServletResponse) respuesta;
        HttpSession sesion = solicitudHttp.getSession(false);

        Usuario usuario = sesion == null ? null : (Usuario) sesion.getAttribute("usuario");
        String rol = usuario == null ? null : usuario.getNombreRol();

        if (!"Administrador".equals(rol) && !"Control Escolar".equals(rol))
        {
            respuestaHttp.sendRedirect(solicitudHttp.getContextPath() + "/SLogin");
            return;
        }

            if ("Control Escolar".equals(rol)
                && "POST".equalsIgnoreCase(solicitudHttp.getMethod())
                && !solicitudHttp.getServletPath().endsWith("/SSolicitudesRegistro"))
            {
                respuestaHttp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Control Escolar tiene permiso de consulta en esta sección.");
                return;
            }

        cadena.doFilter(solicitud, respuesta);
    }
}
