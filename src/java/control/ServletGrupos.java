package control;

import doa.DAOPlanCuatrimestre;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Grupo;
import modelo.Usuario;
import servicio.ResultadoSimple;
import servicio.ServicioAutorizacion;
import servicio.ServicioGeneracion;
import servicio.ServicioGrupo;
import servicio.ServicioPeriodo;
import java.io.IOException;

@WebServlet("/admin/SGrupos")
public class ServletGrupos extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");

        if (!new ServicioAutorizacion().autorizarOResponder403(respuesta, usuarioSesion, "grupos.ver"))
        {
            return;
        }

        ServicioGrupo servicioGrupo = new ServicioGrupo();
        solicitud.setAttribute("grupos", servicioGrupo.listar());
        String idEditar = solicitud.getParameter("editar");
        if (idEditar != null && !idEditar.isEmpty()) solicitud.setAttribute("grupoEditar", servicioGrupo.buscarPorId(Integer.parseInt(idEditar)));
        solicitud.setAttribute("cuatrimestresDisponibles", new DAOPlanCuatrimestre().listarDePlanesVigentes());
        solicitud.setAttribute("generaciones", new ServicioGeneracion().listar());
        solicitud.setAttribute("periodos", new ServicioPeriodo().listar());
        solicitud.getServletContext().getRequestDispatcher("/admin/grupos.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        String accion = solicitud.getParameter("accion");
        ServicioGrupo servicioGrupo = new ServicioGrupo();

        if (!new ServicioAutorizacion().autorizarOResponder403(respuesta, responsable, "grupos.gestionar"))
        {
            return;
        }

        if ("Cerrar".equals(accion))
        {
            servicioGrupo.cerrar(Integer.parseInt(solicitud.getParameter("idGrupo")), responsable);
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SGrupos");
            return;
        }

        if ("Modificar".equals(accion))
        {
            Grupo grupo = new Grupo();
            grupo.setIdGrupo(Integer.parseInt(solicitud.getParameter("idGrupo")));
            grupo.setNombreGrupo(solicitud.getParameter("tfNombreGrupo"));
            grupo.setIdGeneracion(Integer.parseInt(solicitud.getParameter("selGeneracion")));
            grupo.setIdPeriodo(Integer.parseInt(solicitud.getParameter("selPeriodo")));
            ResultadoSimple resultado = servicioGrupo.actualizar(grupo, responsable);
            if (!resultado.isExito())
            {
                solicitud.setAttribute("error", resultado.getMensajeError());
                solicitud.setAttribute("grupoEditar", grupo);
                solicitud.setAttribute("grupos", servicioGrupo.listar());
                solicitud.setAttribute("cuatrimestresDisponibles", new DAOPlanCuatrimestre().listarDePlanesVigentes());
                solicitud.setAttribute("generaciones", new ServicioGeneracion().listar());
                solicitud.setAttribute("periodos", new ServicioPeriodo().listar());
                solicitud.getServletContext().getRequestDispatcher("/admin/grupos.jsp").forward(solicitud, respuesta);
                return;
            }
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SGrupos");
            return;
        }

        Grupo grupo = new Grupo();
        grupo.setIdPlanCuatrimestre(Integer.parseInt(solicitud.getParameter("selCuatrimestre")));
        grupo.setNombreGrupo(solicitud.getParameter("tfNombreGrupo"));
        grupo.setIdGeneracion(Integer.parseInt(solicitud.getParameter("selGeneracion")));
        grupo.setIdPeriodo(Integer.parseInt(solicitud.getParameter("selPeriodo")));

        ResultadoSimple resultado = servicioGrupo.agregar(grupo, responsable);

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.setAttribute("grupos", servicioGrupo.listar());
            solicitud.setAttribute("cuatrimestresDisponibles", new DAOPlanCuatrimestre().listarDePlanesVigentes());
            solicitud.setAttribute("generaciones", new ServicioGeneracion().listar());
            solicitud.setAttribute("periodos", new ServicioPeriodo().listar());
            solicitud.getServletContext().getRequestDispatcher("/admin/grupos.jsp").forward(solicitud, respuesta);
            return;
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SGrupos");
    }
}
