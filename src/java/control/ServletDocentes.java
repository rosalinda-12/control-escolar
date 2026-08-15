package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Persona;
import modelo.Usuario;
import servicio.ResultadoSimple;
import servicio.ServicioDocente;
import java.io.IOException;

@WebServlet("/admin/SDocentes")
public class ServletDocentes extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        ServicioDocente servicioDocente = new ServicioDocente();
        solicitud.setAttribute("docentes", servicioDocente.listar());
        solicitud.getServletContext().getRequestDispatcher("/admin/docentes.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        String accion = solicitud.getParameter("accion");
        ServicioDocente servicioDocente = new ServicioDocente();

        if ("Desactivar".equals(accion))
        {
            ServicioDocente.ResultadoDesactivable resultado = servicioDocente.desactivar(
                    Integer.parseInt(solicitud.getParameter("idDocente")), responsable);

            if (!resultado.isExito())
            {
                solicitud.setAttribute("error", resultado.getMensajeError());
            }

            solicitud.setAttribute("docentes", servicioDocente.listar());
            solicitud.getServletContext().getRequestDispatcher("/admin/docentes.jsp").forward(solicitud, respuesta);
            return;
        }

        Persona persona = new Persona();
        persona.setNombres(solicitud.getParameter("tfNombres"));
        persona.setApellidoPaterno(solicitud.getParameter("tfApellidoPaterno"));
        persona.setApellidoMaterno(solicitud.getParameter("tfApellidoMaterno"));
        persona.setCorreo(solicitud.getParameter("tfCorreo"));

        ResultadoSimple resultado = servicioDocente.agregar(persona, responsable);

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.setAttribute("docentes", servicioDocente.listar());
            solicitud.getServletContext().getRequestDispatcher("/admin/docentes.jsp").forward(solicitud, respuesta);
            return;
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SDocentes");
    }
}
