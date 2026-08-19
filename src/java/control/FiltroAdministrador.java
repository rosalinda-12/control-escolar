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

@WebFilter("/admin/*")
public class FiltroAdministrador implements Filter
{
    /**
     * Este filtro solo verifica que el usuario haya iniciado sesión con
     * un rol que use el área /admin/* (Administrador o Control Escolar).
     * NO decide qué puede hacer cada uno dentro de esa área: eso se
     * valida permiso por permiso dentro de cada Servlet con
     * ServicioAutorizacion (que sí distingue entre ambos roles y aplica
     * rol_permisos), para no tener que duplicar rutas/JSPs por rol.
     */
    @Override
    public void doFilter(ServletRequest solicitud, ServletResponse respuesta, FilterChain cadena) throws IOException, ServletException
    {
        HttpServletRequest solicitudHttp = (HttpServletRequest) solicitud;
        HttpServletResponse respuestaHttp = (HttpServletResponse) respuesta;
        HttpSession sesion = solicitudHttp.getSession(false);

        Object rol = sesion == null ? null : sesion.getAttribute("rol");

        if (!"Administrador".equals(rol) && !"Control Escolar".equals(rol))
        {
            respuestaHttp.sendRedirect(solicitudHttp.getContextPath() + "/SLogin");
            return;
        }

        cadena.doFilter(solicitud, respuesta);
    }
}
