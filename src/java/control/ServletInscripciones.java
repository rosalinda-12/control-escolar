package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.TrayectoriaAcademica;
import modelo.Usuario;
import servicio.ResultadoSimple;
import servicio.ServicioGrupo;
import servicio.ServicioInscripcion;
import servicio.ServicioTrayectoria;
import java.io.IOException;

@WebServlet("/admin/SInscripciones")
public class ServletInscripciones extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        ServicioInscripcion servicioInscripcion = new ServicioInscripcion();
        String busquedaAlumno = solicitud.getParameter("busqueda");
        if (busquedaAlumno == null || busquedaAlumno.trim().isEmpty())
        {
            busquedaAlumno = solicitud.getParameter("matricula");
        }
        solicitud.setAttribute("inscripciones", servicioInscripcion.listar());

        solicitud.setAttribute("grupos", new ServicioGrupo().listarActivosParaInscripcion());

        HttpSession sesion = solicitud.getSession(false);
        if (sesion != null && sesion.getAttribute("errorInscripcion") != null)
        {
            solicitud.setAttribute("error", sesion.getAttribute("errorInscripcion"));
            sesion.removeAttribute("errorInscripcion");
        }

        if (busquedaAlumno != null && !busquedaAlumno.trim().isEmpty())
        {
            ServicioTrayectoria servicioTrayectoria = new ServicioTrayectoria();
            TrayectoriaAcademica trayectoria = servicioTrayectoria.buscarPorTexto(busquedaAlumno.trim());
            solicitud.setAttribute("trayectoriaEncontrada", trayectoria);
            solicitud.setAttribute("matriculaBuscada", busquedaAlumno.trim());

            if (trayectoria == null)
            {
                solicitud.setAttribute("error", "No se encontró ningún alumno con esa matrícula, nombre o carrera.");
            }
            else if ("ACTIVA".equals(trayectoria.getEstado()))
            {
                solicitud.setAttribute("avanceNivel", servicioTrayectoria.obtenerAvisoSiguienteNivel(trayectoria.getIdTrayectoria()));
            }
        }

        solicitud.getServletContext().getRequestDispatcher("/admin/inscripciones.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        String accion = solicitud.getParameter("accion");
        ServicioInscripcion servicioInscripcion = new ServicioInscripcion();

        if ("Baja".equals(accion))
        {
            servicioInscripcion.darDeBaja(Integer.parseInt(solicitud.getParameter("idInscripcion")), responsable);
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SInscripciones");
            return;
        }

        String idTrayectoriaTexto = solicitud.getParameter("idTrayectoria");
        String idGrupoTexto = solicitud.getParameter("idGrupo");
        if (idTrayectoriaTexto == null || idGrupoTexto == null || idGrupoTexto.trim().isEmpty())
        {
            solicitud.getSession().setAttribute("errorInscripcion", "Selecciona un grupo disponible antes de confirmar la inscripción.");
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SInscripciones");
            return;
        }

        int idTrayectoria;
        int idGrupo;
        try
        {
            idTrayectoria = Integer.parseInt(idTrayectoriaTexto);
            idGrupo = Integer.parseInt(idGrupoTexto);
        }
        catch (NumberFormatException excepcion)
        {
            solicitud.getSession().setAttribute("errorInscripcion", "La selección de inscripción no es válida.");
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SInscripciones");
            return;
        }
        modelo.Grupo grupoSeleccionado = new ServicioGrupo().buscarPorId(idGrupo);
        if (grupoSeleccionado == null || !"Activo".equals(grupoSeleccionado.getEstatus()))
        {
            solicitud.getSession().setAttribute("errorInscripcion", "El grupo seleccionado ya no está disponible.");
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SInscripciones");
            return;
        }
        int idPeriodo = grupoSeleccionado.getIdPeriodo();

        ResultadoSimple resultado = servicioInscripcion.inscribir(idTrayectoria, idGrupo, idPeriodo, responsable);

        if (!resultado.isExito())
        {
            solicitud.getSession().setAttribute("errorInscripcion", resultado.getMensajeError());
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SInscripciones");
    }
}