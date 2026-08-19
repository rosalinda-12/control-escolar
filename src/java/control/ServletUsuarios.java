package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import servicio.ResultadoSimple;
import servicio.ServicioAutorizacion;
import servicio.ServicioCarrera;
import servicio.ServicioRol;
import servicio.ServicioUsuario;
import java.io.IOException;

@WebServlet("/admin/SUsuarios")
public class ServletUsuarios extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();

        if (!autorizacion.autorizarOResponder403(respuesta, usuarioSesion, "usuarios.ver"))
        {
            return;
        }

        cargarDatosFormulario(solicitud);
        solicitud.getServletContext().getRequestDispatcher("/admin/usuarios.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();
        ServicioUsuario servicioUsuario = new ServicioUsuario();

        String accion = solicitud.getParameter("accion");

        if ("Eliminar".equals(accion))
        {
            if (!autorizacion.autorizarOResponder403(respuesta, responsable, "usuarios.eliminar"))
            {
                return;
            }

            ResultadoSimple resultado = servicioUsuario.eliminarODesactivar(
                    Integer.parseInt(solicitud.getParameter("idUsuario")), responsable);

            if (!resultado.isExito())
            {
                solicitud.setAttribute("error", resultado.getMensajeError());
            }

            cargarDatosFormulario(solicitud);
            solicitud.getServletContext().getRequestDispatcher("/admin/usuarios.jsp").forward(solicitud, respuesta);
            return;
        }

        if ("CambiarRol".equals(accion))
        {
            if (!autorizacion.autorizarOResponder403(respuesta, responsable, "usuarios.editar"))
            {
                return;
            }

            Integer idCarrera = parametroEnteroOpcional(solicitud, "selCarrera");
            ResultadoSimple resultado = servicioUsuario.cambiarRol(
                    Integer.parseInt(solicitud.getParameter("idUsuario")),
                    Integer.parseInt(solicitud.getParameter("selRol")),
                    idCarrera, responsable);

            if (!resultado.isExito())
            {
                solicitud.setAttribute("error", resultado.getMensajeError());
            }

            cargarDatosFormulario(solicitud);
            solicitud.getServletContext().getRequestDispatcher("/admin/usuarios.jsp").forward(solicitud, respuesta);
            return;
        }

        // Ya no existe alta directa de cuentas desde aquí: toda cuenta
        // nueva se crea vía autoregistro (SRegistro) una vez que la
        // persona (Alumno/Docente/Subdirector) fue dada de alta, y queda
        // pendiente de aprobación en SSolicitudesRegistro.
        respuesta.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no reconocida.");
    }

    private void cargarDatosFormulario(HttpServletRequest solicitud)
    {
        solicitud.setAttribute("usuarios", new ServicioUsuario().listarTodos());
        solicitud.setAttribute("roles", new ServicioRol().listar());
        solicitud.setAttribute("carreras", new ServicioCarrera().listarActivas());
    }

    private Integer parametroEnteroOpcional(HttpServletRequest solicitud, String nombre)
    {
        String valor = solicitud.getParameter(nombre);
        return (valor == null || valor.isEmpty()) ? null : Integer.parseInt(valor);
    }
}
