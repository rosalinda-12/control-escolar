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
import servicio.ServicioAlumno;
import servicio.ServicioAutorizacion;
import servicio.ServicioPlanEstudio;
import servicio.ServicioTrayectoria;
import java.io.IOException;

@WebServlet("/admin/SGenerarMatricula")
public class ServletGenerarMatricula extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");
        if (!new ServicioAutorizacion().puedeGestionarAutomatizaciones(usuario)) return;
        solicitud.setAttribute("alumnos", new ServicioAlumno().listar());
        solicitud.setAttribute("planes", new ServicioPlanEstudio().listarVigentes());
        String idPlan = solicitud.getParameter("idPlan");
        if (idPlan != null && !idPlan.isEmpty()) solicitud.setAttribute("matriculaSugerida", new ServicioTrayectoria().sugerirMatricula(Integer.parseInt(idPlan)));
        solicitud.getServletContext().getRequestDispatcher("/admin/generar_matricula.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws IOException, ServletException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");
        if (!new ServicioAutorizacion().puedeGestionarAutomatizaciones(usuario)) return;
        TrayectoriaAcademica trayectoria = new TrayectoriaAcademica();
        trayectoria.setIdAlumno(Integer.parseInt(solicitud.getParameter("idAlumno")));
        trayectoria.setIdPlan(Integer.parseInt(solicitud.getParameter("idPlan")));
        trayectoria.setMatricula(solicitud.getParameter("matricula"));
        ResultadoSimple resultado = new ServicioTrayectoria().agregar(trayectoria, usuario);
        sesion.setAttribute(resultado.isExito() ? "avisoMatricula" : "errorMatricula", resultado.isExito() ? "Trayectoria creada con matrícula " + trayectoria.getMatricula() : resultado.getMensajeError());
        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SGenerarMatricula");
    }
}
