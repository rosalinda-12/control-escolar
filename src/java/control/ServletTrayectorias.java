package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.TrayectoriaAcademica;
import modelo.Usuario;
import modelo.BoletaCuatrimestre;
import servicio.ResultadoSimple;
import servicio.ServicioAlumno;
import servicio.ServicioPlanEstudio;
import servicio.ServicioTrayectoria;
import servicio.ServicioCalificacion;
import java.util.ArrayList;
import java.io.IOException;

@WebServlet("/admin/STrayectorias")
public class ServletTrayectorias extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        String idAlumnoTexto = solicitud.getParameter("idAlumno");
        if (idAlumnoTexto == null || idAlumnoTexto.isEmpty())
        {
            int idTrayectoria = Integer.parseInt(solicitud.getParameter("idTrayectoria"));
            TrayectoriaAcademica trayectoria = new ServicioTrayectoria().buscarPorId(idTrayectoria);
            idAlumnoTexto = String.valueOf(trayectoria.getIdAlumno());
        }
        int idAlumno = Integer.parseInt(idAlumnoTexto);

        solicitud.setAttribute("alumno", new ServicioAlumno().buscarPorId(idAlumno));
        solicitud.setAttribute("trayectorias", new ServicioTrayectoria().listarPorAlumno(idAlumno));
        solicitud.setAttribute("planes", new ServicioPlanEstudio().listarVigentes());
        ArrayList<BoletaCuatrimestre> boleta = new ServicioCalificacion().obtenerBoletaAlumno(idAlumno);
        solicitud.setAttribute("boleta", boleta);

        solicitud.getServletContext().getRequestDispatcher("/admin/trayectorias.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        String accion = solicitud.getParameter("accion");
        int idAlumno = Integer.parseInt(solicitud.getParameter("idAlumno"));
        ServicioTrayectoria servicioTrayectoria = new ServicioTrayectoria();

        if ("BajaTemporal".equals(accion))
        {
            servicioTrayectoria.bajaTemporal(Integer.parseInt(solicitud.getParameter("idTrayectoria")), responsable);
        }
        else if ("Reanudar".equals(accion))
        {
            servicioTrayectoria.reanudar(Integer.parseInt(solicitud.getParameter("idTrayectoria")), responsable);
        }
        else if ("BajaDefinitiva".equals(accion))
        {
            servicioTrayectoria.bajaDefinitiva(Integer.parseInt(solicitud.getParameter("idTrayectoria")), responsable);
        }
        else if ("RevertirBaja".equals(accion))
        {
            servicioTrayectoria.revertirBajaDefinitiva(Integer.parseInt(solicitud.getParameter("idTrayectoria")), responsable);
        }
        else
        {
            TrayectoriaAcademica trayectoria = new TrayectoriaAcademica();
            trayectoria.setIdAlumno(idAlumno);
            trayectoria.setIdPlan(Integer.parseInt(solicitud.getParameter("selPlan")));
            trayectoria.setMatricula(solicitud.getParameter("tfMatricula"));

            String idAnteriorTexto = solicitud.getParameter("selTrayectoriaAnterior");
            if (idAnteriorTexto != null && !idAnteriorTexto.isEmpty())
            {
                trayectoria.setIdTrayectoriaAnterior(Integer.parseInt(idAnteriorTexto));
            }

            ResultadoSimple resultado = servicioTrayectoria.agregar(trayectoria, responsable);

            if (!resultado.isExito())
            {
                solicitud.setAttribute("error", resultado.getMensajeError());
                solicitud.setAttribute("alumno", new ServicioAlumno().buscarPorId(idAlumno));
                solicitud.setAttribute("trayectorias", servicioTrayectoria.listarPorAlumno(idAlumno));
                solicitud.setAttribute("planes", new ServicioPlanEstudio().listarVigentes());
                solicitud.setAttribute("boleta", new ServicioCalificacion().obtenerBoletaAlumno(idAlumno));
                solicitud.getServletContext().getRequestDispatcher("/admin/trayectorias.jsp").forward(solicitud, respuesta);
                return;
            }
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/STrayectorias?idAlumno=" + idAlumno);
    }
}
