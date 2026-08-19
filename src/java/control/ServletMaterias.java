package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Materia;
import modelo.Usuario;
import servicio.ResultadoSimple;
import servicio.ServicioAutorizacion;
import servicio.ServicioCurriculo;
import servicio.ServicioMateria;
import java.io.IOException;

@WebServlet("/admin/SMaterias")
public class ServletMaterias extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");

        if (!new ServicioAutorizacion().autorizarOResponder403(respuesta, usuarioSesion, "materias.ver"))
        {
            return;
        }

        ServicioMateria servicioMateria = new ServicioMateria();
        solicitud.setAttribute("materias", servicioMateria.listar());
        solicitud.setAttribute("cuatrimestres", new ServicioCurriculo().listarCuatrimestresDePlanesVigentes());

        String idMateriaEditar = solicitud.getParameter("editar");
        if (idMateriaEditar != null)
        {
            solicitud.setAttribute("materiaEditar", servicioMateria.buscarPorId(Integer.parseInt(idMateriaEditar)));
        }

        // Permite llegar aquí desde Currículo con el cuatrimestre ya elegido.
        String idCuatrimestrePreseleccionado = solicitud.getParameter("cuatrimestre");
        if (idCuatrimestrePreseleccionado != null)
        {
            solicitud.setAttribute("idCuatrimestrePreseleccionado", idCuatrimestrePreseleccionado);
        }

        solicitud.getServletContext().getRequestDispatcher("/admin/materias.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        String accion = solicitud.getParameter("accion");
        ServicioMateria servicioMateria = new ServicioMateria();
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();

        if ("Eliminar".equals(accion))
        {
            if (!autorizacion.autorizarOResponder403(respuesta, responsable, "materias.eliminar"))
            {
                return;
            }

            ServicioMateria.ResultadoDesactivable resultado = servicioMateria.eliminarODesactivar(
                    Integer.parseInt(solicitud.getParameter("idMateria")), responsable);

            if (!resultado.isExito())
            {
                solicitud.setAttribute("error", resultado.getMensajeError());
            }
            else if (resultado.isDesactivadaEnLugarDeEliminada())
            {
                solicitud.setAttribute("aviso", "Esa materia ya forma parte de un grupo, así que no se puede "
                        + "eliminar sin afectar ese historial. Se desactivó en su lugar.");
            }

            solicitud.setAttribute("materias", servicioMateria.listar());
            solicitud.setAttribute("cuatrimestres", new ServicioCurriculo().listarCuatrimestresDePlanesVigentes());
            solicitud.getServletContext().getRequestDispatcher("/admin/materias.jsp").forward(solicitud, respuesta);
            return;
        }

        Materia materia = new Materia();
        materia.setNombreMateria(solicitud.getParameter("tfNombreMateria"));

        if (!autorizacion.autorizarOResponder403(respuesta, responsable, "materias.gestionar"))
        {
            return;
        }

        ResultadoSimple resultado;

        if ("Modificar".equals(accion))
        {
            materia.setIdMateria(Integer.parseInt(solicitud.getParameter("idMateria")));
            materia.setEstatus(solicitud.getParameter("selEstatus"));
            resultado = servicioMateria.modificar(materia, responsable);
        }
        else
        {
            String idPlanCuatrimestreTexto = solicitud.getParameter("selPlanCuatrimestre");
            materia.setIdPlanCuatrimestre(idPlanCuatrimestreTexto != null && !idPlanCuatrimestreTexto.isEmpty()
                    ? Integer.parseInt(idPlanCuatrimestreTexto) : 0);
            resultado = servicioMateria.agregar(materia, responsable);
        }

        if (!resultado.isExito())
        {
            solicitud.setAttribute("error", resultado.getMensajeError());
            solicitud.setAttribute("materias", servicioMateria.listar());
            solicitud.setAttribute("cuatrimestres", new ServicioCurriculo().listarCuatrimestresDePlanesVigentes());
            solicitud.getServletContext().getRequestDispatcher("/admin/materias.jsp").forward(solicitud, respuesta);
            return;
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SMaterias");
    }
}
