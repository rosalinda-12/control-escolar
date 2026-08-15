package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import servicio.ServicioConfiguracionParcial;
import java.io.IOException;

@WebServlet("/admin/SConfiguracionParcial")
public class ServletConfiguracionParcial extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        ServicioConfiguracionParcial servicioConfiguracionParcial = new ServicioConfiguracionParcial();
        solicitud.setAttribute("configuraciones", servicioConfiguracionParcial.listar());
        solicitud.getServletContext().getRequestDispatcher("/admin/configuracion_parcial.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");

        int idPeriodo = Integer.parseInt(solicitud.getParameter("tfIdPeriodo"));
        int parcialActivo = Integer.parseInt(solicitud.getParameter("tfParcialActivo"));

        ServicioConfiguracionParcial servicioConfiguracionParcial = new ServicioConfiguracionParcial();
        ServicioConfiguracionParcial.ResultadoConfiguracion resultado = servicioConfiguracionParcial.habilitarParcial(idPeriodo, parcialActivo, responsable);

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.setAttribute("configuraciones", servicioConfiguracionParcial.listar());
            solicitud.getServletContext().getRequestDispatcher("/admin/configuracion_parcial.jsp").forward(solicitud, respuesta);
            return;
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SConfiguracionParcial");
    }
}
