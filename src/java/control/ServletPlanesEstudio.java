package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.PlanEstudio;
import modelo.Usuario;
import servicio.ResultadoSimple;
import servicio.ServicioCarrera;
import servicio.ServicioNivelAcademico;
import servicio.ServicioPlanEstudio;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/admin/SPlanes")
public class ServletPlanesEstudio extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        ServicioPlanEstudio servicioPlan = new ServicioPlanEstudio();
        solicitud.setAttribute("planes", servicioPlan.listar());
        solicitud.setAttribute("carreras", new ServicioCarrera().listarActivas());
        solicitud.setAttribute("niveles", new ServicioNivelAcademico().listar());
        solicitud.getServletContext().getRequestDispatcher("/admin/planes.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        String accion = solicitud.getParameter("accion");
        ServicioPlanEstudio servicioPlan = new ServicioPlanEstudio();

        if ("Cerrar".equals(accion))
        {
            servicioPlan.cerrar(Integer.parseInt(solicitud.getParameter("idPlan")), responsable);
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SPlanes");
            return;
        }

        PlanEstudio plan = new PlanEstudio();
        plan.setIdCarrera(Integer.parseInt(solicitud.getParameter("selCarrera")));
        plan.setVersion(solicitud.getParameter("tfVersion"));
        plan.setNombrePlan(solicitud.getParameter("tfNombrePlan"));
        plan.setDuracionCuatrimestres(Integer.parseInt(solicitud.getParameter("tfDuracion")));
        plan.setFechaVigenciaInicio(LocalDate.parse(solicitud.getParameter("tfFechaVigencia")));

        String[] nivelesTexto = solicitud.getParameterValues("selNivel[]");
        String[] iniciosTexto = solicitud.getParameterValues("tfInicio[]");
        String[] finesTexto = solicitud.getParameterValues("tfFin[]");

        int[] idsNivel = null;
        int[] inicios = null;
        int[] fines = null;

        if (nivelesTexto != null)
        {
            idsNivel = new int[nivelesTexto.length];
            inicios = new int[nivelesTexto.length];
            fines = new int[nivelesTexto.length];

            for (int i = 0; i < nivelesTexto.length; i++)
            {
                idsNivel[i] = Integer.parseInt(nivelesTexto[i]);
                inicios[i] = Integer.parseInt(iniciosTexto[i]);
                fines[i] = Integer.parseInt(finesTexto[i]);
            }
        }

        ResultadoSimple resultado = servicioPlan.agregar(plan, idsNivel, inicios, fines, responsable);

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.setAttribute("planes", servicioPlan.listar());
            solicitud.setAttribute("carreras", new ServicioCarrera().listarActivas());
            solicitud.setAttribute("niveles", new ServicioNivelAcademico().listar());
            solicitud.getServletContext().getRequestDispatcher("/admin/planes.jsp").forward(solicitud, respuesta);
            return;
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SCurriculo?idPlan=" + resultado.getIdGenerado());
    }
}
