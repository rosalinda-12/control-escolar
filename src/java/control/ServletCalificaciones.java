package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.DocenteAsignacion;
import modelo.Usuario;
import servicio.ServicioAutorizacion;
import servicio.ServicioCalificacion;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/maestro/SCalificaciones")
public class ServletCalificaciones extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");
        int idDocente = usuarioSesion.getIdProfesor();
        ServicioCalificacion servicioCalificacion = new ServicioCalificacion();
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();

        if (!autorizacion.autorizarOResponder403(respuesta, usuarioSesion, "calificaciones.ver"))
        {
            return;
        }

        String parametroGrupoMateria = solicitud.getParameter("idGrupoMateria");

        if (parametroGrupoMateria == null || parametroGrupoMateria.isEmpty())
        {
            solicitud.setAttribute("asignaciones", servicioCalificacion.listarGruposDelDocente(idDocente));
            solicitud.getServletContext().getRequestDispatcher("/maestro/calificaciones.jsp").forward(solicitud, respuesta);
            return;
        }

        int idGrupoMateria = Integer.parseInt(parametroGrupoMateria);

        // Defensa en profundidad: si alguien cambia el id en la URL para
        // apuntar a una materia de grupo que no es suya, se corta aquí
        // mismo con 403 antes de tocar cualquier dato de otro docente.
        if (!autorizacion.puedeOperarSobreGrupoMateria(usuarioSesion, idGrupoMateria))
        {
            respuesta.sendError(HttpServletResponse.SC_FORBIDDEN, "Esa materia de grupo no está a tu cargo.");
            return;
        }

        DocenteAsignacion contexto = servicioCalificacion.obtenerGrupoMateriaDelDocente(idGrupoMateria, idDocente);

        if (contexto == null)
        {
            solicitud.setAttribute("asignaciones", servicioCalificacion.listarGruposDelDocente(idDocente));
            solicitud.setAttribute("error", "Esa materia de grupo no está a tu cargo.");
            solicitud.getServletContext().getRequestDispatcher("/maestro/calificaciones.jsp").forward(solicitud, respuesta);
            return;
        }

        solicitud.setAttribute("contexto", contexto);
        solicitud.setAttribute("alumnos", servicioCalificacion.listarAlumnos(idGrupoMateria));
        solicitud.getServletContext().getRequestDispatcher("/maestro/captura_calificaciones.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");
        int idDocente = usuarioSesion.getIdProfesor();
        ServicioCalificacion servicioCalificacion = new ServicioCalificacion();
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();

        if (!autorizacion.autorizarOResponder403(respuesta, usuarioSesion, "calificaciones.registrar"))
        {
            return;
        }

        int idGrupoMateria = Integer.parseInt(solicitud.getParameter("idGrupoMateria"));

        // Misma validación de alcance que en el GET: el permiso por sí
        // solo no basta, también tiene que ser SU materia de grupo.
        if (!autorizacion.puedeOperarSobreGrupoMateria(usuarioSesion, idGrupoMateria))
        {
            respuesta.sendError(HttpServletResponse.SC_FORBIDDEN, "Esa materia de grupo no está a tu cargo.");
            return;
        }

        int numeroParcial = Integer.parseInt(solicitud.getParameter("numeroParcial"));

        Map<Integer, String> notas = new HashMap<>();

        for (String nombreParametro : java.util.Collections.list(solicitud.getParameterNames()))
        {
            if (nombreParametro.startsWith("nota_"))
            {
                int idInscripcionMateria = Integer.parseInt(nombreParametro.substring("nota_".length()));
                notas.put(idInscripcionMateria, solicitud.getParameter(nombreParametro));
            }
        }

        ServicioCalificacion.ResultadoCaptura resultado =
                servicioCalificacion.capturar(idGrupoMateria, idDocente, numeroParcial, notas, usuarioSesion);

        DocenteAsignacion contexto = servicioCalificacion.obtenerGrupoMateriaDelDocente(idGrupoMateria, idDocente);
        solicitud.setAttribute("contexto", contexto);
        solicitud.setAttribute("alumnos", servicioCalificacion.listarAlumnos(idGrupoMateria));

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
        }
        else
        {
            solicitud.setAttribute("exito", "Se guardaron las calificaciones del parcial " + numeroParcial + ".");
        }

        solicitud.getServletContext().getRequestDispatcher("/maestro/captura_calificaciones.jsp").forward(solicitud, respuesta);
    }
}
