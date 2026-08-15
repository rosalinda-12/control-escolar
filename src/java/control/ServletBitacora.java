package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servicio.ServicioBitacora;
import java.io.IOException;

@WebServlet("/admin/SBitacora")
public class ServletBitacora extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        String entidadFiltro = solicitud.getParameter("tfEntidad");
        String correoFiltro = solicitud.getParameter("tfCorreo");

        ServicioBitacora servicioBitacora = new ServicioBitacora();
        solicitud.setAttribute("movimientos", servicioBitacora.consultar(entidadFiltro, correoFiltro));
        solicitud.setAttribute("entidadFiltro", entidadFiltro);
        solicitud.setAttribute("correoFiltro", correoFiltro);

        solicitud.getServletContext().getRequestDispatcher("/admin/historial.jsp").forward(solicitud, respuesta);
    }
}
