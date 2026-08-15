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
        String matricula = solicitud.getParameter("matricula");
        boolean verTodas = "1".equals(solicitud.getParameter("todas"));

        if (matricula != null && !matricula.isEmpty())
        {
            solicitud.setAttribute("inscripciones", servicioInscripcion.listarPorMatricula(matricula));
        }
        else if (verTodas)
        {
            solicitud.setAttribute("inscripciones", servicioInscripcion.listar());
            solicitud.setAttribute("verTodas", true);
        }
        else
        {
            solicitud.setAttribute("inscripciones", servicioInscripcion.listarActivas());
        }

        solicitud.setAttribute("grupos", new ServicioGrupo().listar());

        HttpSession sesion = solicitud.getSession(false);
        if (sesion != null && sesion.getAttribute("errorInscripcion") != null)
        {
            solicitud.setAttribute("error", sesion.getAttribute("errorInscripcion"));
            sesion.removeAttribute("errorInscripcion");
        }

        if (matricula != null && !matricula.isEmpty())
        {
            TrayectoriaAcademica trayectoria = new ServicioTrayectoria().buscarPorMatricula(matricula);
            solicitud.setAttribute("trayectoriaEncontrada", trayectoria);
            solicitud.setAttribute("matriculaBuscada", matricula);

            if (trayectoria == null)
            {
                solicitud.setAttribute("error", "No se encontró ninguna trayectoria con esa matrícula.");
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

        int idTrayectoria = Integer.parseInt(solicitud.getParameter("idTrayectoria"));
        int idGrupo = Integer.parseInt(solicitud.getParameter("idGrupo"));
        int idPeriodo = new ServicioGrupo().buscarPorId(idGrupo).getIdPeriodo();

        ResultadoSimple resultado = servicioInscripcion.inscribir(idTrayectoria, idGrupo, idPeriodo, responsable);

        if (!resultado.isExito())
        {
            solicitud.getSession().setAttribute("errorInscripcion", resultado.getMensajeError());
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SInscripciones");
    }
}