package control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import modelo.Materia;
import modelo.Usuario;
import servicio.ResultadoSimple;
import servicio.ServicioAutorizacion;
import servicio.ServicioCurriculo;
import servicio.ServicioMateria;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@WebServlet("/admin/SMaterias")
@MultipartConfig(maxFileSize = 20 * 1024 * 1024)
public class ServletMaterias extends HttpServlet
{

    private static final String CARPETA_TEMARIOS = "/recursos/temarios";

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
        boolean nuevaMateria = "1".equals(solicitud.getParameter("nueva"));
        if (idMateriaEditar != null && !nuevaMateria)
        {
            solicitud.setAttribute("materiaEditar", servicioMateria.buscarPorId(Integer.parseInt(idMateriaEditar)));
        }
        if (nuevaMateria)
        {
            solicitud.setAttribute("nuevaMateria", true);
        }


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

        if ("SubirPdf".equals(accion))
        {
            if (!autorizacion.autorizarOResponder403(respuesta, responsable, "materias.gestionar"))
            {
                return;
            }

            int idMateria = Integer.parseInt(solicitud.getParameter("idMateria"));
            ResultadoSimple resultado = subirPdf(solicitud, idMateria, responsable, servicioMateria);

            if (!resultado.isExito())
            {
                solicitud.setAttribute("error", resultado.getMensajeError());
                solicitud.setAttribute("materias", servicioMateria.listar());
                solicitud.setAttribute("cuatrimestres", new ServicioCurriculo().listarCuatrimestresDePlanesVigentes());
                solicitud.getServletContext().getRequestDispatcher("/admin/materias.jsp").forward(solicitud, respuesta);
                return;
            }

            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SMaterias");
            return;
        }

        if ("QuitarPdf".equals(accion))
        {
            if (!autorizacion.autorizarOResponder403(respuesta, responsable, "materias.gestionar"))
            {
                return;
            }

            int idMateria = Integer.parseInt(solicitud.getParameter("idMateria"));
            Materia materiaActual = servicioMateria.buscarPorId(idMateria);

            if (materiaActual != null && materiaActual.isTieneTemario())
            {
                borrarArchivoTemario(solicitud, materiaActual.getPdfTemario());
            }

            servicioMateria.quitarPdf(idMateria, responsable);
            respuesta.sendRedirect(solicitud.getContextPath() + "/admin/SMaterias");
            return;
        }

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

            Part archivoPdf = solicitud.getPart("archivoPdf");
            if (resultado.isExito() && archivoPdf != null && archivoPdf.getSize() > 0)
            {
                resultado = subirPdf(solicitud, materia.getIdMateria(), responsable, servicioMateria);
            }
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



    private ResultadoSimple subirPdf(HttpServletRequest solicitud, int idMateria, Usuario responsable, ServicioMateria servicioMateria)
            throws IOException, ServletException
    {
        Part archivo = solicitud.getPart("archivoPdf");

        if (archivo == null || archivo.getSize() <= 0)
        {
            return ResultadoSimple.fallo("Elige un archivo PDF antes de subirlo.");
        }

        String nombreOriginal = obtenerNombreArchivo(archivo);
        String tipoContenido = archivo.getContentType();
        boolean pareceExtensionPdf = nombreOriginal != null && nombreOriginal.toLowerCase().endsWith(".pdf");
        boolean pareceTipoPdf = tipoContenido != null && tipoContenido.toLowerCase().contains("pdf");

        if (!pareceExtensionPdf && !pareceTipoPdf)
        {
            return ResultadoSimple.fallo("El temario debe subirse en formato PDF.");
        }

        String carpetaReal = solicitud.getServletContext().getRealPath(CARPETA_TEMARIOS);
        File carpeta = new File(carpetaReal);
        if (!carpeta.exists())
        {
            carpeta.mkdirs();
        }

        String nombreArchivo = "materia_" + idMateria + ".pdf";
        Path destino = new File(carpeta, nombreArchivo).toPath();

        try (InputStream entrada = archivo.getInputStream())
        {
            Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
        }

        return servicioMateria.actualizarPdf(idMateria, nombreArchivo, responsable);
    }

    private String obtenerNombreArchivo(Part parte)
    {
        String cabecera = parte.getHeader("content-disposition");
        if (cabecera == null)
        {
            return null;
        }

        for (String seccion : cabecera.split(";"))
        {
            seccion = seccion.trim();
            if (seccion.startsWith("filename"))
            {
                return seccion.substring(seccion.indexOf('=') + 1).replace("\"", "").trim();
            }
        }

        return null;
    }

    private void borrarArchivoTemario(HttpServletRequest solicitud, String nombreArchivo)
    {
        String carpetaReal = solicitud.getServletContext().getRealPath(CARPETA_TEMARIOS);
        if (carpetaReal == null)
        {
            return;
        }

        File archivo = new File(carpetaReal, nombreArchivo);
        if (archivo.exists())
        {
            archivo.delete();
        }
    }
}
