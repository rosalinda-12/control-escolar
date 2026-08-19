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
import servicio.ServicioAutorizacion;
import servicio.ServicioCarrera;
import servicio.ServicioSubdirector;
import java.io.IOException;

@WebServlet("/admin/SSubdirectores")
public class ServletSubdirectores extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");

        if (!new ServicioAutorizacion().autorizarOResponder403(respuesta, usuarioSesion, "subdirectores.ver"))
        {
            return;
        }

        cargarDatosFormulario(solicitud);
        solicitud.getServletContext().getRequestDispatcher("/admin/subdirectores.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        String accion = solicitud.getParameter("accion");
        ServicioSubdirector servicioSubdirector = new ServicioSubdirector();

        if (!new ServicioAutorizacion().autorizarOResponder403(respuesta, responsable, "subdirectores.gestionar"))
        {
            return;
        }

        if ("Desactivar".equals(accion))
        {
            ResultadoSimple resultado = servicioSubdirector.desactivar(
                    Integer.parseInt(solicitud.getParameter("idSubdirector")), responsable);

            if (!resultado.isExito())
            {
                solicitud.setAttribute("error", resultado.getMensajeError());
            }

            cargarDatosFormulario(solicitud);
            solicitud.getServletContext().getRequestDispatcher("/admin/subdirectores.jsp").forward(solicitud, respuesta);
            return;
        }

        Persona persona = new Persona();
        persona.setNombres(solicitud.getParameter("tfNombres"));
        persona.setApellidoPaterno(solicitud.getParameter("tfApellidoPaterno"));
        persona.setApellidoMaterno(solicitud.getParameter("tfApellidoMaterno"));
        persona.setCorreo(solicitud.getParameter("tfCorreo"));

        int idCarrera = Integer.parseInt(solicitud.getParameter("selCarrera"));

        ResultadoSimple resultado = servicioSubdirector.agregar(persona, idCarrera, responsable);

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            cargarDatosFormulario(solicitud);
            solicitud.getServletContext().getRequestDispatcher("/admin/subdirectores.jsp").forward(solicitud, respuesta);
            return;
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SSubdirectores");
    }

    private void cargarDatosFormulario(HttpServletRequest solicitud)
    {
        solicitud.setAttribute("subdirectores", new ServicioSubdirector().listar());
        solicitud.setAttribute("carreras", new ServicioCarrera().listarActivas());
    }
}
