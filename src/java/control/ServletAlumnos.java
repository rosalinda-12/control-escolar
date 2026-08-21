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
import servicio.ServicioAlumno;
import servicio.ServicioPlanEstudio;
import java.io.IOException;

@WebServlet("/admin/SAlumnos")
public class ServletAlumnos extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        ServicioAlumno servicioAlumno = new ServicioAlumno();
        String idEditar = solicitud.getParameter("editar");
        solicitud.setAttribute("alumnoEditar", idEditar == null || idEditar.isEmpty() ? null : servicioAlumno.buscarPorId(Integer.parseInt(idEditar)));
        solicitud.setAttribute("alumnos", servicioAlumno.listar());
        solicitud.setAttribute("planes", new ServicioPlanEstudio().listarVigentes());
        solicitud.getServletContext().getRequestDispatcher("/admin/alumnos.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        String accion = solicitud.getParameter("accion");
        ServicioAlumno servicioAlumno = new ServicioAlumno();

        if ("BajaTemporal".equals(accion))
        {
            servicioAlumno.bajaTemporal(Integer.parseInt(solicitud.getParameter("idAlumno")), responsable);
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SAlumnos");
            return;
        }

        if ("BajaDefinitiva".equals(accion))
        {
            servicioAlumno.bajaDefinitiva(Integer.parseInt(solicitud.getParameter("idAlumno")), responsable);
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SAlumnos");
            return;
        }

        if ("Reactivar".equals(accion))
        {
            servicioAlumno.reactivar(Integer.parseInt(solicitud.getParameter("idAlumno")), responsable);
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SAlumnos");
            return;
        }

        if ("RevertirBaja".equals(accion))
        {
            servicioAlumno.revertirBajaDefinitiva(Integer.parseInt(solicitud.getParameter("idAlumno")), responsable);
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SAlumnos");
            return;
        }

        if ("Modificar".equals(accion))
        {
            modelo.Alumno alumno = new modelo.Alumno();
            alumno.setIdAlumno(Integer.parseInt(solicitud.getParameter("idAlumno")));
            alumno.setIdPersona(Integer.parseInt(solicitud.getParameter("idPersona")));
            alumno.setNombres(solicitud.getParameter("tfNombres"));
            alumno.setApellidoPaterno(solicitud.getParameter("tfApellidoPaterno"));
            alumno.setApellidoMaterno(solicitud.getParameter("tfApellidoMaterno"));
            alumno.setCorreo(solicitud.getParameter("tfCorreo"));
            ResultadoSimple resultado = servicioAlumno.actualizar(alumno, responsable);
            if (!resultado.isExito()) solicitud.setAttribute("error", resultado.getMensajeError());
            else
            { respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SAlumnos"); return; }
            solicitud.setAttribute("alumnos", servicioAlumno.listar());
            solicitud.setAttribute("planes", new ServicioPlanEstudio().listarVigentes());
            solicitud.setAttribute("alumnoEditar", alumno);
            solicitud.getServletContext().getRequestDispatcher("/admin/alumnos.jsp").forward(solicitud, respuesta);
            return;
        }

        Persona persona = new Persona();
        persona.setNombres(solicitud.getParameter("tfNombres"));
        persona.setApellidoPaterno(solicitud.getParameter("tfApellidoPaterno"));
        persona.setApellidoMaterno(solicitud.getParameter("tfApellidoMaterno"));
        persona.setCorreo(solicitud.getParameter("tfCorreo"));

        int idPlan = Integer.parseInt(solicitud.getParameter("selPlan"));
        String matricula = solicitud.getParameter("tfMatricula");

        ResultadoSimple resultado = servicioAlumno.agregarConTrayectoria(persona, idPlan, matricula, responsable);

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.setAttribute("alumnos", servicioAlumno.listar());
            solicitud.setAttribute("planes", new ServicioPlanEstudio().listarVigentes());
            solicitud.getServletContext().getRequestDispatcher("/admin/alumnos.jsp").forward(solicitud, respuesta);
            return;
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/STrayectorias?idAlumno=" + resultado.getIdGenerado());
    }
}
