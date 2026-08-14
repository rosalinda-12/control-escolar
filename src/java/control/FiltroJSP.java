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
import java.util.Set;

@WebFilter("*.jsp")
public class FiltroJSP implements Filter
{
    private static final Set<String> PAGINAS_PUBLICAS = Set.of(
            "/index.jsp",
            "/login.jsp",
            "/registro.jsp",
            "/verificar.jsp",
            "/cambio_contrasena.jsp"
    );

    @Override
    public void doFilter(ServletRequest solicitud, ServletResponse respuesta, FilterChain cadena) throws IOException, ServletException
    {
        HttpServletRequest solicitudHttp = (HttpServletRequest) solicitud;
        HttpServletResponse respuestaHttp = (HttpServletResponse) respuesta;

        String rutaServlet = solicitudHttp.getServletPath();

        if (PAGINAS_PUBLICAS.contains(rutaServlet))
        {
            cadena.doFilter(solicitud, respuesta);
            return;
        }

        Object atributoForward = solicitudHttp.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        if (atributoForward == null)
        {
            respuestaHttp.sendRedirect(solicitudHttp.getContextPath() + "/");
            return;
        }

        cadena.doFilter(solicitud, respuesta);
    }
}