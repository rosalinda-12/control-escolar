package servicio;

import doa.DAOInscripcion;
import doa.DAOInscripcionMateria;
import doa.DAOTrayectoriaAcademica;
import modelo.Alumno;
import modelo.TrayectoriaAcademica;
import modelo.Usuario;
import java.util.ArrayList;
import java.util.List;
import modelo.ResultadoLoteInscripcion;

public class ServicioInscripcion
{
    private final DAOInscripcion daoInscripcion;
    private final DAOInscripcionMateria daoInscripcionMateria;
    private final DAOTrayectoriaAcademica daoTrayectoria;
    private final ServicioAlumno servicioAlumno;
    private final ServicioBitacora servicioBitacora;

    public ServicioInscripcion()
    {
        this.daoInscripcion = new DAOInscripcion();
        this.daoInscripcionMateria = new DAOInscripcionMateria();
        this.daoTrayectoria = new DAOTrayectoriaAcademica();
        this.servicioAlumno = new ServicioAlumno();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<modelo.Inscripcion> listar()
    {
        return daoInscripcion.listar();
    }

    public ArrayList<modelo.Inscripcion> listarActivas()
    {
        return daoInscripcion.listarActivas();
    }

    public ArrayList<modelo.Inscripcion> listarPorAlumno(String busqueda)
    {
        return daoInscripcion.listarPorAlumno(busqueda);
    }



    public ResultadoSimple inscribir(int idTrayectoria, int idGrupo, int idPeriodo, Usuario responsable)
    {
        TrayectoriaAcademica trayectoria = daoTrayectoria.buscarPorId(idTrayectoria);

        if (trayectoria == null)
        {
            return ResultadoSimple.fallo("La trayectoria ya no existe.");
        }

        if (!"ACTIVA".equals(trayectoria.getEstado()))
        {
            return ResultadoSimple.fallo(mensajeEstadoTrayectoria(trayectoria.getEstado()));
        }

        Alumno alumno = servicioAlumno.buscarPorId(trayectoria.getIdAlumno());

        if (alumno != null && !"Activo".equals(alumno.getEstatus()))
        {
            if ("Egresado".equals(alumno.getEstatus()))
            {
                return ResultadoSimple.fallo("El alumno ya es egresado; no puede reinscribirse.");
            }
            if ("BajaDefinitiva".equals(alumno.getEstatus()))
            {
                return ResultadoSimple.fallo("El alumno tiene baja definitiva; ya no puede inscribirse.");
            }
            return ResultadoSimple.fallo("El alumno está en baja temporal; reactívalo antes de inscribirlo.");
        }

        if (daoInscripcion.existeParaTrayectoriaGrupo(idTrayectoria, idGrupo))
        {
            return ResultadoSimple.fallo("Esa trayectoria ya está inscrita en ese grupo.");
        }


        daoInscripcion.finalizarActivasDeTrayectoria(idTrayectoria);

        int idInscripcion = daoInscripcion.agregar(idTrayectoria, idGrupo, idPeriodo);
        daoInscripcionMateria.generarParaInscripcion(idInscripcion, idGrupo);

        servicioBitacora.registrarAlta(responsable, "inscripciones", idInscripcion,
                "Inscribió la matrícula " + trayectoria.getMatricula() + " a un grupo con "
                + daoInscripcionMateria.contarMaterias(idInscripcion) + " materias generadas automáticamente");

        return ResultadoSimple.exito(idInscripcion);
    }

    public ResultadoLoteInscripcion inscribirLote(List<Integer> idsTrayectoria, int idGrupo, int idPeriodo, Usuario responsable)
    {
        ResultadoLoteInscripcion resultado = new ResultadoLoteInscripcion();
        for (Integer idTrayectoria : idsTrayectoria)
        {
            ResultadoSimple individual = inscribir(idTrayectoria, idGrupo, idPeriodo, responsable);
            if (individual.isExito()) resultado.registrarExito();
            else resultado.registrarError("Trayectoria " + idTrayectoria + ": " + individual.getMensajeError());
        }
        return resultado;
    }

    public void darDeBaja(int idInscripcion, Usuario responsable)
    {
        daoInscripcion.actualizarEstado(idInscripcion, "Baja");
        servicioBitacora.registrarBaja(responsable, "inscripciones", idInscripcion, "Dio de baja una inscripción");
    }

    private String mensajeEstadoTrayectoria(String estado)
    {
        switch (estado)
        {
            case "BAJA_TEMPORAL":
                return "Esa trayectoria está en baja temporal. Reanúdala antes de inscribirla a un grupo.";
            case "BAJA_DEFINITIVA":
                return "Esa trayectoria tiene baja definitiva y ya no puede inscribirse.";
            case "EGRESADA":
                return "Esa trayectoria ya está egresada y no puede reinscribirse.";
            case "CAMBIO":
                return "Esa trayectoria ya no está activa: el alumno continuó en otra trayectoria.";
            default:
                return "Esa trayectoria no está activa.";
        }
    }
}