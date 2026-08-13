package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servicio.ServicioCurriculo;
import servicio.ServicioPlanEstudio;
import java.io.IOException;

/**
 * Ahora es una vista de solo lectura: las materias quedan ligadas a su
 * cuatrimestre desde que se dan de alta en Materias, así que aquí ya no hay
 * acciones de "asignar" o "quitar", solo el mapa curricular resultante.
 */
@WebServlet("/admin/SCurriculo")
public class ServletCurriculo extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        ServicioPlanEstudio servicioPlan = new ServicioPlanEstudio();
        ServicioCurriculo servicioCurriculo = new ServicioCurriculo();

        solicitud.setAttribute("planes", servicioPlan.listar());

        String idPlanTexto = solicitud.getParameter("idPlan");
        if (idPlanTexto != null && !idPlanTexto.isEmpty())
        {
            int idPlan = Integer.parseInt(idPlanTexto);
            solicitud.setAttribute("planSeleccionado", servicioPlan.buscarPorId(idPlan));
            solicitud.setAttribute("mapaCurricular", servicioCurriculo.mapaCurricular(idPlan));
        }

        solicitud.getServletContext().getRequestDispatcher("/admin/curriculo.jsp").forward(solicitud, respuesta);
    }
}
