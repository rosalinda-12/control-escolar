package control;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class FiltroCache implements Filter
{
    @Override
    public void doFilter(ServletRequest solicitud, ServletResponse respuesta, FilterChain cadena) throws IOException, ServletException
    {
        HttpServletResponse respuestaHttp = (HttpServletResponse) respuesta;
        respuestaHttp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        respuestaHttp.setHeader("Pragma", "no-cache");
        respuestaHttp.setHeader("Expires", "0");
        cadena.doFilter(solicitud, respuesta);
    }
}
