package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import servicio.ServicioAutorizacion;
import servicio.ServicioTrayectoria;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@WebServlet("/admin/SAvanceNivel")
public class ServletAvanceNivel extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");
        if (!new ServicioAutorizacion().puedeGestionarAutomatizaciones(usuario)) return;
        solicitud.setAttribute("trayectorias", new ServicioTrayectoria().listarListasParaAvance());
        solicitud.getServletContext().getRequestDispatcher("/admin/avance_nivel.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");
        if (!new ServicioAutorizacion().puedeGestionarAutomatizaciones(usuario)) return;
        String[] ids = solicitud.getParameterValues("idsTrayectoria");
        if (ids == null || ids.length == 0) sesion.setAttribute("errorAvance", "Selecciona al menos un alumno.");
        else
        {
            int creadas = new ServicioTrayectoria().avanzarEnLote(Arrays.stream(ids).map(Integer::parseInt).collect(Collectors.toList()), usuario).getIdGenerado();
            sesion.setAttribute("avisoAvance", "Se crearon " + creadas + " trayectorias del siguiente nivel.");
        }
        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SAvanceNivel");
    }
}
