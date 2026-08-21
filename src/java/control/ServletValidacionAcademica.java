package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import servicio.ServicioAutorizacion;
import servicio.ServicioCierrePeriodo;
import servicio.ServicioPeriodo;
import java.io.IOException;

@WebServlet("/admin/SValidacionAcademica")
public class ServletValidacionAcademica extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");
        if (!new ServicioAutorizacion().puedeGestionarAutomatizaciones(usuario)) return;
        solicitud.setAttribute("periodos", new ServicioPeriodo().listar());
        String idPeriodo = solicitud.getParameter("idPeriodo");
        if (idPeriodo != null && !idPeriodo.isEmpty()) solicitud.setAttribute("resumen", new ServicioCierrePeriodo().consultar(Integer.parseInt(idPeriodo)));
        solicitud.getServletContext().getRequestDispatcher("/admin/validacion_academica.jsp").forward(solicitud, respuesta);
    }
}
