package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import servicio.ResultadoSimple;
import servicio.ServicioAutorizacion;
import servicio.ServicioCierrePeriodo;
import servicio.ServicioPeriodo;
import servicio.ServicioPrepararCarrera;
import java.io.IOException;

@WebServlet("/admin/SAutomatizaciones")
public class ServletAutomatizaciones extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");
        if (!new ServicioAutorizacion().puedeGestionarAutomatizaciones(usuario))
        {
            respuesta.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para acceder a Automatizaciones.");
            return;
        }

        ServicioPeriodo servicioPeriodo = new ServicioPeriodo();
        java.util.ArrayList<modelo.Periodo> periodos = servicioPeriodo.listar();
        new ServicioCierrePeriodo().cerrarVencidos(usuario, periodos);
        solicitud.setAttribute("periodos", servicioPeriodo.listar());
        String idPeriodo = solicitud.getParameter("idPeriodo");
        if (idPeriodo != null && !idPeriodo.isEmpty())
        {
            solicitud.setAttribute("resumen", new ServicioCierrePeriodo().consultar(Integer.parseInt(idPeriodo)));
        }
        solicitud.setAttribute("proceso", solicitud.getParameter("proceso"));
        solicitud.getServletContext().getRequestDispatcher("/admin/automatizaciones.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws IOException, ServletException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");
        if (!new ServicioAutorizacion().puedeGestionarAutomatizaciones(usuario))
        {
            respuesta.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para acceder a Automatizaciones.");
            return;
        }

        ResultadoSimple resultado;
        if ("PrepararCarrera".equals(solicitud.getParameter("accion")))
        {
            resultado = new ServicioPrepararCarrera().preparar(
                    solicitud.getParameter("tfNombreCarrera"),
                    solicitud.getParameter("tfClaveCarrera"),
                    solicitud.getParameter("tfNombrePlan"),
                    solicitud.getParameter("tfVersion"),
                    Integer.parseInt(solicitud.getParameter("tfDuracion")), usuario);
        }
        else
        {
            int idPeriodo = Integer.parseInt(solicitud.getParameter("idPeriodo"));
            resultado = new ServicioCierrePeriodo().cerrar(idPeriodo, usuario);
        }
        if (!resultado.isExito()) sesion.setAttribute("errorAutomatizacion", resultado.getMensajeError());
        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SAutomatizaciones?proceso="
                + ("PrepararCarrera".equals(solicitud.getParameter("accion")) ? "carrera" : "cierre"));
    }
}
