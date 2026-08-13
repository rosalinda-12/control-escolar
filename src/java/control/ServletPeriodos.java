package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Periodo;
import modelo.Usuario;
import servicio.ResultadoSimple;
import servicio.ServicioCicloEscolar;
import servicio.ServicioPeriodo;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/admin/SPeriodos")
public class ServletPeriodos extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        ServicioPeriodo servicioPeriodo = new ServicioPeriodo();
        ServicioCicloEscolar servicioCiclo = new ServicioCicloEscolar();
        solicitud.setAttribute("periodos", servicioPeriodo.listar());
        solicitud.setAttribute("ciclos", servicioCiclo.listar());

        String idPeriodoEditar = solicitud.getParameter("editar");
        if (idPeriodoEditar != null)
        {
            solicitud.setAttribute("periodoEditar", servicioPeriodo.buscarPorId(Integer.parseInt(idPeriodoEditar)));
        }

        solicitud.getServletContext().getRequestDispatcher("/admin/periodos.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        String accion = solicitud.getParameter("accion");
        ServicioPeriodo servicioPeriodo = new ServicioPeriodo();

        if ("Eliminar".equals(accion))
        {
            servicioPeriodo.eliminar(Integer.parseInt(solicitud.getParameter("idPeriodo")), responsable);
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SPeriodos");
            return;
        }

        Periodo periodo = new Periodo();
        periodo.setIdCiclo(Integer.parseInt(solicitud.getParameter("selCiclo")));
        periodo.setNombrePeriodo(solicitud.getParameter("tfNombrePeriodo"));
        periodo.setFechaInicio(LocalDate.parse(solicitud.getParameter("tfFechaInicio")));
        periodo.setFechaFin(LocalDate.parse(solicitud.getParameter("tfFechaFin")));

        ResultadoSimple resultado;

        if ("Modificar".equals(accion))
        {
            periodo.setIdPeriodo(Integer.parseInt(solicitud.getParameter("idPeriodo")));
            periodo.setEstatus(solicitud.getParameter("selEstatus"));
            resultado = servicioPeriodo.modificar(periodo, responsable);
        }
        else
        {
            resultado = servicioPeriodo.agregar(periodo, responsable);
        }

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.setAttribute("periodos", servicioPeriodo.listar());
            solicitud.setAttribute("ciclos", new servicio.ServicioCicloEscolar().listar());
            solicitud.getServletContext().getRequestDispatcher("/admin/periodos.jsp").forward(solicitud, respuesta);
            return;
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SPeriodos");
    }
}
