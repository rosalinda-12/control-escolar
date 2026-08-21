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
import servicio.ServicioPrepararCarrera;
import java.io.IOException;

@WebServlet("/admin/SPrepararCarrera")
public class ServletPrepararCarrera extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        if (!autorizar(solicitud)) return;
        solicitud.getServletContext().getRequestDispatcher("/admin/preparar_carrera.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws IOException, ServletException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");
        if (!autorizar(solicitud)) return;

        ResultadoSimple resultado = new ServicioPrepararCarrera().preparar(
                solicitud.getParameter("tfNombreCarrera"), solicitud.getParameter("tfClaveCarrera"),
                solicitud.getParameter("tfNombrePlan"), solicitud.getParameter("tfVersion"),
                Integer.parseInt(solicitud.getParameter("tfDuracion")), usuario);
        if (resultado.isExito())
        {
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SCurriculo?idPlan=" + resultado.getIdGenerado());
            return;
        }
        solicitud.setAttribute("error", resultado.getMensajeError());
        solicitud.getServletContext().getRequestDispatcher("/admin/preparar_carrera.jsp").forward(solicitud, respuesta);
    }

    private boolean autorizar(HttpServletRequest solicitud) throws IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");
        return new ServicioAutorizacion().puedeGestionarAutomatizaciones(usuario);
    }
}
