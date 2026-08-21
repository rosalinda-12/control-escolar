package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.TrayectoriaAcademica;
import modelo.Grupo;
import modelo.Usuario;
import servicio.ServicioAutorizacion;
import servicio.ServicioGrupo;
import servicio.ServicioInscripcion;
import servicio.ServicioTrayectoria;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/admin/SInscripcionMasiva")
public class ServletInscripcionMasiva extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws ServletException, IOException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");
        if (!new ServicioAutorizacion().puedeGestionarAutomatizaciones(usuario)) return;
        solicitud.setAttribute("grupos", new ServicioGrupo().listarActivosParaInscripcion());
        solicitud.setAttribute("trayectorias", new ServicioTrayectoria().listar());
        solicitud.getServletContext().getRequestDispatcher("/admin/inscripcion_masiva.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws IOException, ServletException
    {
        HttpSession sesion = solicitud.getSession(false);
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");
        if (!new ServicioAutorizacion().puedeGestionarAutomatizaciones(usuario)) return;
        int idGrupo = Integer.parseInt(solicitud.getParameter("idGrupo"));
        Grupo grupo = new ServicioGrupo().buscarPorId(idGrupo);
        int idPeriodo = grupo.getIdPeriodo();
        String[] seleccionados = solicitud.getParameterValues("idsTrayectoria");
        String idGrupoAnteriorParametro = solicitud.getParameter("idGrupoAnterior");
        int idGrupoAnterior = idGrupoAnteriorParametro == null || idGrupoAnteriorParametro.isBlank()
            ? 0 : Integer.parseInt(idGrupoAnteriorParametro);
        if (seleccionados == null || seleccionados.length == 0)
        {
            sesion.setAttribute("errorMasivo", "Selecciona al menos un alumno.");
        }
        else
        {
            ServicioTrayectoria servicioTrayectoria = new ServicioTrayectoria();
            List<Integer> ids = Arrays.stream(seleccionados).map(Integer::parseInt).collect(Collectors.toList());
            List<Integer> idsCompatibles = ids.stream()
                    .map(servicioTrayectoria::buscarPorId)
                    .filter(trayectoria -> trayectoria != null && trayectoria.getIdCarrera() == grupo.getIdCarrera()
                        && (idGrupoAnterior == 0 || Integer.valueOf(idGrupoAnterior).equals(trayectoria.getIdGrupoAnterior()))
                            && "ACTIVA".equals(trayectoria.getEstado()))
                    .map(TrayectoriaAcademica::getIdTrayectoria)
                    .collect(Collectors.toList());
            if (idsCompatibles.isEmpty())
            {
                sesion.setAttribute("errorMasivo", idGrupoAnterior == 0
                        ? "Los alumnos seleccionados no pertenecen a la carrera del grupo."
                        : "Los alumnos seleccionados no pertenecen al grupo anterior elegido o a la carrera del grupo destino.");
            }
            else
            {
                var resultado = new ServicioInscripcion().inscribirLote(idsCompatibles, idGrupo, idPeriodo, usuario);
                sesion.setAttribute("avisoMasivo", "Procesados: " + resultado.getProcesados() + ", exitosos: " + resultado.getExitosos() + ", rechazados: " + resultado.getRechazados() + ". " + resultado.getErrores());
            }
        }
        respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SInscripcionMasiva");
    }
}
