package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.NivelAcademico;
import modelo.Usuario;
import servicio.ResultadoSimple;
import servicio.ServicioNivelAcademico;
import java.io.IOException;

@WebServlet("/admin/SNiveles")
public class ServletNivelesAcademicos extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        ServicioNivelAcademico servicioNivel = new ServicioNivelAcademico();
        solicitud.setAttribute("niveles", servicioNivel.listar());
        solicitud.getServletContext().getRequestDispatcher("/admin/niveles.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        String accion = solicitud.getParameter("accion");
        ServicioNivelAcademico servicioNivel = new ServicioNivelAcademico();

        if ("Eliminar".equals(accion))
        {
            servicioNivel.eliminar(Integer.parseInt(solicitud.getParameter("idNivel")), responsable);
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SNiveles");
            return;
        }

        NivelAcademico nivel = new NivelAcademico();
        nivel.setNombreNivel(solicitud.getParameter("tfNombreNivel"));

        ResultadoSimple resultado = servicioNivel.agregar(nivel, responsable);

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.setAttribute("niveles", servicioNivel.listar());
            solicitud.getServletContext().getRequestDispatcher("/admin/niveles.jsp").forward(solicitud, respuesta);
            return;
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SNiveles");
    }
}
