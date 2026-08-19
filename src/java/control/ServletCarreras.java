package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Carrera;
import modelo.Usuario;
import servicio.ServicioAutorizacion;
import servicio.ServicioCarrera;
import java.io.IOException;

@WebServlet("/admin/SCarreras")
public class ServletCarreras extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");

        if (!new ServicioAutorizacion().autorizarOResponder403(respuesta, usuarioSesion, "carreras.ver"))
        {
            return;
        }

        ServicioCarrera servicioCarrera = new ServicioCarrera();
        solicitud.setAttribute("carreras", servicioCarrera.listar());

        String idCarreraEditar = solicitud.getParameter("editar");
        if (idCarreraEditar != null)
        {
            solicitud.setAttribute("carreraEditar", servicioCarrera.buscarPorId(Integer.parseInt(idCarreraEditar)));
        }

        solicitud.getServletContext().getRequestDispatcher("/admin/carreras.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        String accion = solicitud.getParameter("accion");
        ServicioCarrera servicioCarrera = new ServicioCarrera();
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();

        if ("Eliminar".equals(accion))
        {
            if (!autorizacion.autorizarOResponder403(respuesta, responsable, "carreras.eliminar"))
            {
                return;
            }

            ServicioCarrera.ResultadoCarrera resultado = servicioCarrera.eliminarODesactivar(
                    Integer.parseInt(solicitud.getParameter("idCarrera")), responsable);

            if (!resultado.isExito())
            {
                solicitud.setAttribute("error", resultado.getMensajeError());
            }
            else if (resultado.isDesactivadaEnLugarDeEliminada())
            {
                solicitud.setAttribute("aviso", "Esa carrera ya tiene planes de estudio registrados, así que no se puede "
                        + "eliminar sin perder el historial. Se desactivó en su lugar y ya no aparecerá disponible "
                        + "para crear planes, grupos o alumnos nuevos.");
            }

            solicitud.setAttribute("carreras", servicioCarrera.listar());
            solicitud.getServletContext().getRequestDispatcher("/admin/carreras.jsp").forward(solicitud, respuesta);
            return;
        }

        Carrera carrera = new Carrera();
        carrera.setNombreCarrera(solicitud.getParameter("tfNombreCarrera"));
        carrera.setClaveCarrera(solicitud.getParameter("tfClaveCarrera").toUpperCase());

        if (!autorizacion.autorizarOResponder403(respuesta, responsable, "carreras.gestionar"))
        {
            return;
        }

        ServicioCarrera.ResultadoCarrera resultado;

        if ("Modificar".equals(accion))
        {
            carrera.setIdCarrera(Integer.parseInt(solicitud.getParameter("idCarrera")));
            carrera.setEstatus(solicitud.getParameter("selEstatus"));
            resultado = servicioCarrera.modificar(carrera, responsable);
        }
        else
        {
            resultado = servicioCarrera.agregar(carrera, responsable);
        }

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.setAttribute("carreras", servicioCarrera.listar());
            solicitud.getServletContext().getRequestDispatcher("/admin/carreras.jsp").forward(solicitud, respuesta);
            return;
        }

        // Si la carrera se creó desde un modal independiente en otra
        // pantalla (por ejemplo, el botón "Agregar carrera" dentro de
        // Trayectoria), "retorno" permite regresar ahí en lugar del
        // listado de carreras. Solo se acepta una ruta relativa dentro
        // del propio módulo de admin, para no abrir una redirección
        // abierta hacia otro dominio.
        String retorno = solicitud.getParameter("retorno");
        if (retorno != null && retorno.startsWith("STrayectorias?idAlumno="))
        {
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/" + retorno);
            return;
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SCarreras");
    }
}
