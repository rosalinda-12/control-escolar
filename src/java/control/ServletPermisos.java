package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;
import servicio.ResultadoSimple;
import servicio.ServicioAutorizacion;
import servicio.ServicioPermiso;
import servicio.ServicioRol;
import java.io.IOException;

@WebServlet("/admin/SPermisos")
public class ServletPermisos extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();

        if (!autorizacion.autorizarOResponder403(respuesta, usuarioSesion, "permisos.gestionar"))
        {
            return;
        }

        ServicioRol servicioRol = new ServicioRol();
        java.util.ArrayList<modelo.Rol> roles = servicioRol.listar();
        solicitud.setAttribute("roles", roles);

        int idRolPorDefecto = roles.stream()
                .filter(r -> !r.isEsAdministradorPrincipal())
                .findFirst()
                .map(modelo.Rol::getIdRol)
                .orElse(roles.get(0).getIdRol());

        String parametroRol = solicitud.getParameter("idRol");
        int idRolSeleccionado = parametroRol != null ? Integer.parseInt(parametroRol) : idRolPorDefecto;

        solicitud.setAttribute("rolSeleccionado", servicioRol.buscarPorId(idRolSeleccionado));
        solicitud.setAttribute("permisos", new ServicioPermiso().listarConEstatusPorRol(idRolSeleccionado));

        if (sesion.getAttribute("errorPermiso") != null)
        {
            solicitud.setAttribute("error", sesion.getAttribute("errorPermiso"));
            sesion.removeAttribute("errorPermiso");
        }

        solicitud.getServletContext().getRequestDispatcher("/admin/permisos.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario responsable = (Usuario) sesion.getAttribute("usuario");
        ServicioAutorizacion autorizacion = new ServicioAutorizacion();

        if (!autorizacion.autorizarOResponder403(respuesta, responsable, "permisos.gestionar"))
        {
            return;
        }

        int idRol = Integer.parseInt(solicitud.getParameter("idRol"));
        int idPermiso = Integer.parseInt(solicitud.getParameter("idPermiso"));
        boolean activo = "1".equals(solicitud.getParameter("activo"));

        ResultadoSimple resultado = new ServicioPermiso().establecer(idRol, idPermiso, activo, responsable);

        if (!resultado.isExito())
        {
            sesion.setAttribute("errorPermiso", resultado.getMensajeError());
        }

        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SPermisos?idRol=" + idRol);
    }
}
