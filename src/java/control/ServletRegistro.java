package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servicio.ServicioRegistro;
import java.io.IOException;

@WebServlet("/SRegistro")
public class ServletRegistro extends HttpServlet
{
    private static final String ADMINISTRADOR = "Administrador";
    private static final String ALUMNO = "Alumno";
    private static final String MAESTRO = "Maestro";
    private static final String SUBDIRECTOR = "Subdirector";

    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        ServicioRegistro servicioRegistro = new ServicioRegistro();
        boolean existeAdministrador = servicioRegistro.existeAdministrador();

        String tipoSolicitado = solicitud.getParameter("tipo");
        String tipoSeleccionado = existeAdministrador ? ALUMNO : ADMINISTRADOR;

        if (existeAdministrador && (MAESTRO.equals(tipoSolicitado) || SUBDIRECTOR.equals(tipoSolicitado)))
        {
            tipoSeleccionado = tipoSolicitado;
        }

        solicitud.setAttribute("existeAdministrador", existeAdministrador);
        solicitud.setAttribute("tipoSeleccionado", tipoSeleccionado);
        solicitud.getServletContext().getRequestDispatcher("/registro.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        ServicioRegistro servicioRegistro = new ServicioRegistro();
        boolean existeAdministrador = servicioRegistro.existeAdministrador();

        String tipo = solicitud.getParameter("tipo");
        if (existeAdministrador && ADMINISTRADOR.equals(tipo))
        {
            tipo = ALUMNO;
        }

        String correo = solicitud.getParameter("tfCorreo");
        String contrasena = solicitud.getParameter("tfContrasena");

        solicitud.setAttribute("existeAdministrador", existeAdministrador);
        solicitud.setAttribute("tipoSeleccionado", tipo);

        ServicioRegistro.ResultadoRegistro resultado;

        if (ADMINISTRADOR.equals(tipo))
        {
            String nombres = solicitud.getParameter("tfNombres");
            String apellidoPaterno = solicitud.getParameter("tfApellidoPaterno");
            String apellidoMaterno = solicitud.getParameter("tfApellidoMaterno");
            resultado = servicioRegistro.registrarAdministrador(nombres, apellidoPaterno, apellidoMaterno, correo, contrasena);
        }
        else if (MAESTRO.equals(tipo))
        {
            resultado = servicioRegistro.registrarMaestro(correo, contrasena);
        }
        else if (SUBDIRECTOR.equals(tipo))
        {
            resultado = servicioRegistro.registrarSubdirector(correo, contrasena);
        }
        else
        {
            String matricula = solicitud.getParameter("tfMatricula");
            resultado = servicioRegistro.registrarAlumno(matricula, correo, contrasena);
        }

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.getServletContext().getRequestDispatcher("/registro.jsp").forward(solicitud, respuesta);
            return;
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/SVerificarCorreo?idUsuario=" + resultado.getIdUsuario());
    }
}
