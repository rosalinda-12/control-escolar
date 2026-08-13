package control;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("*.jsp")
public class FiltroJSP implements Filter
{
    @Override
    public void doFilter(ServletRequest solicitud, ServletResponse respuesta, FilterChain cadena) throws IOException, ServletException
    {
        HttpServletRequest solicitudHttp = (HttpServletRequest) solicitud;
        HttpServletResponse respuestaHttp = (HttpServletResponse) respuesta;

        Object atributoForward = solicitudHttp.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);

        if (atributoForward == null)
        {
            respuestaHttp.sendRedirect(solicitudHttp.getContextPath() + "/");
            return;
        }

        cadena.doFilter(solicitud, respuesta);
    }
}
