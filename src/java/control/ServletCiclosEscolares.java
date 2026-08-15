package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.CicloEscolar;
import modelo.Usuario;
import servicio.ResultadoSimple;
import servicio.ServicioCicloEscolar;
import java.io.IOException;

@WebServlet("/admin/SCiclos")
public class ServletCiclosEscolares extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        ServicioCicloEscolar servicioCiclo = new ServicioCicloEscolar();
        solicitud.setAttribute("ciclos", servicioCiclo.listar());
        solicitud.getServletContext().getRequestDispatcher("/admin/ciclos.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        String accion = solicitud.getParameter("accion");
        ServicioCicloEscolar servicioCiclo = new ServicioCicloEscolar();

        if ("Eliminar".equals(accion))
        {
            servicioCiclo.eliminar(Integer.parseInt(solicitud.getParameter("idCiclo")), responsable);
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SCiclos");
            return;
        }

        CicloEscolar ciclo = new CicloEscolar();
        ciclo.setNombreCiclo(solicitud.getParameter("tfNombreCiclo"));

        ResultadoSimple resultado = servicioCiclo.agregar(ciclo, responsable);

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.setAttribute("ciclos", servicioCiclo.listar());
            solicitud.getServletContext().getRequestDispatcher("/admin/ciclos.jsp").forward(solicitud, respuesta);
            return;
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SCiclos");
    }
}
