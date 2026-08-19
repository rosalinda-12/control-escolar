package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import servicio.ServicioAutorizacion;
import servicio.ServicioCalificacion;
import servicio.ServicioCarrera;
import java.io.IOException;

/**
 * Pantalla de calificaciones para Administrador y Control Escolar.
 *
 * Alcance (regla de negocio):
 *   - Administrador: puede ver, editar y eliminar calificaciones de
 *     TODAS las carreras (calificaciones.ver / .editar / .eliminar).
 *   - Control Escolar: puede ver y editar calificaciones de TODAS las
 *     carreras, pero NO puede eliminarlas (sin calificaciones.eliminar).
 *
 * El Subdirector tiene su propia pantalla de solo lectura, limitada a su
 * carrera asignada, en ServletSubdirectorCalificaciones.
 */
@WebServlet("/admin/SCalificaciones")
public class ServletCalificacionesAdmin extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();

        if (!autorizacion.autorizarOResponder403(respuesta, usuarioSesion, "calificaciones.ver"))
        {
            return;
        }

        cargarListado(solicitud, usuarioSesion, autorizacion);

        String parametroEditar = solicitud.getParameter("editar");
        if (parametroEditar != null && !parametroEditar.isEmpty())
        {
            solicitud.setAttribute("calificacionEditar",
                    new ServicioCalificacion().buscarPorInscripcionMateria(Integer.parseInt(parametroEditar)));
        }

        solicitud.getServletContext().getRequestDispatcher("/admin/calificaciones.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");
        String accion = solicitud.getParameter("accion");
        ServicioCalificacion servicioCalificacion = new ServicioCalificacion();
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();
        int idInscripcionMateria = Integer.parseInt(solicitud.getParameter("idInscripcionMateria"));
        String idCarreraFiltro = solicitud.getParameter("idCarrera");

        if ("Eliminar".equals(accion))
        {
            if (!autorizacion.autorizarOResponder403(respuesta, usuarioSesion, "calificaciones.eliminar"))
            {
                return;
            }

            servicioCalificacion.eliminarComoAdmin(idInscripcionMateria, usuarioSesion);
        }
        else
        {
            if (!autorizacion.autorizarOResponder403(respuesta, usuarioSesion, "calificaciones.editar"))
            {
                return;
            }

            ServicioCalificacion.ResultadoCaptura resultado = servicioCalificacion.editarComoAdmin(
                    idInscripcionMateria,
                    solicitud.getParameter("tfParcial1"),
                    solicitud.getParameter("tfParcial2"),
                    solicitud.getParameter("tfParcial3"),
                    usuarioSesion);

            if (!resultado.isExito())
            {
                cargarListado(solicitud, usuarioSesion, autorizacion);
                solicitud.setAttribute("error", resultado.getMensajeError());
                solicitud.setAttribute("calificacionEditar",
                        servicioCalificacion.buscarPorInscripcionMateria(idInscripcionMateria));
                solicitud.getServletContext().getRequestDispatcher("/admin/calificaciones.jsp").forward(solicitud, respuesta);
                return;
            }
        }

        String destino = solicitud.getContextPath() + "/admin/SCalificaciones";
        if (idCarreraFiltro != null && !idCarreraFiltro.isEmpty())
        {
            destino += "?idCarrera=" + Integer.parseInt(idCarreraFiltro);
        }
        respuesta.sendRedirect(destino);
    }

    private void cargarListado(HttpServletRequest solicitud, Usuario usuarioSesion, ServicioAutorizacion autorizacion)
    {
        String parametroCarrera = solicitud.getParameter("idCarrera");
        Integer idCarrera = (parametroCarrera != null && !parametroCarrera.isEmpty())
                ? Integer.parseInt(parametroCarrera) : null;

        solicitud.setAttribute("calificaciones", new ServicioCalificacion().listarParaAdmin(idCarrera));
        solicitud.setAttribute("carreras", new ServicioCarrera().listar());
        solicitud.setAttribute("idCarreraSeleccionada", idCarrera);
        solicitud.setAttribute("puedeEditar", autorizacion.tienePermiso(usuarioSesion, "calificaciones.editar"));
        solicitud.setAttribute("puedeEliminar", autorizacion.tienePermiso(usuarioSesion, "calificaciones.eliminar"));
    }
}
