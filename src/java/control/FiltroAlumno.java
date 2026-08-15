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
import java.io.IOException;

@WebFilter("/alumno/*")
public class FiltroAlumno implements Filter
{
    @Override
    public void doFilter(ServletRequest solicitud, ServletResponse respuesta, FilterChain cadena) throws IOException, ServletException
    {
        HttpServletRequest solicitudHttp = (HttpServletRequest) solicitud;
        HttpServletResponse respuestaHttp = (HttpServletResponse) respuesta;
        HttpSession sesion = solicitudHttp.getSession(false);

        Object rol = sesion == null ? null : sesion.getAttribute("rol");

        if (!"Alumno".equals(rol))
        {
            respuestaHttp.sendRedirect(solicitudHttp.getContextPath() + "/SLogin");
            return;
        }

        cadena.doFilter(solicitud, respuesta);
    }
}
