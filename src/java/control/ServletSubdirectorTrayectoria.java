package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Alumno;
import modelo.BoletaCuatrimestre;
import modelo.PlanEstudio;
import modelo.TrayectoriaAcademica;
import modelo.Usuario;
import servicio.ServicioAlumno;
import servicio.ServicioAutorizacion;
import servicio.ServicioCalificacion;
import servicio.ServicioPlanEstudio;
import servicio.ServicioTrayectoria;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@WebServlet("/subdirector/STrayectoria")
public class ServletSubdirectorTrayectoria extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");
        if (usuarioSesion.getIdsCarrera().isEmpty())
        {
            respuesta.sendError(HttpServletResponse.SC_FORBIDDEN, "Tu cuenta no tiene una carrera asignada.");
            return;
        }

        int idAlumno = Integer.parseInt(solicitud.getParameter("idAlumno"));
        ServicioTrayectoria servicioTrayectoria = new ServicioTrayectoria();
        ArrayList<TrayectoriaAcademica> trayectorias = servicioTrayectoria.listarPorAlumno(idAlumno);
        ServicioPlanEstudio servicioPlan = new ServicioPlanEstudio();
        ArrayList<TrayectoriaAcademica> trayectoriasDeCarrera = new ArrayList<>();
        for (TrayectoriaAcademica trayectoria : trayectorias)
        {
            PlanEstudio plan = servicioPlan.buscarPorId(trayectoria.getIdPlan());
            if (plan != null && usuarioSesion.tieneCarrera(plan.getIdCarrera()))
            {
                trayectoriasDeCarrera.add(trayectoria);
            }
        }

        if (trayectoriasDeCarrera.isEmpty())
        {
            respuesta.sendError(HttpServletResponse.SC_FORBIDDEN, "El alumno no pertenece a tu carrera.");
            return;
        }

        Alumno alumno = new ServicioAlumno().buscarPorId(idAlumno);
        solicitud.setAttribute("alumno", alumno);
        solicitud.setAttribute("trayectorias", trayectoriasDeCarrera);
        Set<Integer> idsTrayectoriasAutorizadas = new HashSet<>();
        for (TrayectoriaAcademica trayectoria : trayectoriasDeCarrera)
        {
            idsTrayectoriasAutorizadas.add(trayectoria.getIdTrayectoria());
        }
        ArrayList<BoletaCuatrimestre> boletaAutorizada = new ArrayList<>();
        for (BoletaCuatrimestre cuatrimestre : new ServicioCalificacion().obtenerBoletaAlumno(idAlumno))
        {
            if (idsTrayectoriasAutorizadas.contains(cuatrimestre.getIdTrayectoria()))
            {
                boletaAutorizada.add(cuatrimestre);
            }
        }
        solicitud.setAttribute("boleta", boletaAutorizada);
        solicitud.getServletContext().getRequestDispatcher("/subdirector/trayectoria.jsp").forward(solicitud, respuesta);
    }
}
