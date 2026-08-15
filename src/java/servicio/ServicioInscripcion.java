package servicio;

import doa.DAOInscripcion;
import doa.DAOInscripcionMateria;
import doa.DAOTrayectoriaAcademica;
import modelo.TrayectoriaAcademica;
import modelo.Usuario;
import java.util.ArrayList;

public class ServicioInscripcion
{
    private final DAOInscripcion daoInscripcion;
    private final DAOInscripcionMateria daoInscripcionMateria;
    private final DAOTrayectoriaAcademica daoTrayectoria;
    private final ServicioBitacora servicioBitacora;

    public ServicioInscripcion()
    {
        this.daoInscripcion = new DAOInscripcion();
        this.daoInscripcionMateria = new DAOInscripcionMateria();
        this.daoTrayectoria = new DAOTrayectoriaAcademica();
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

    public ArrayList<modelo.Inscripcion> listarPorMatricula(String matricula)
    {
        return daoInscripcion.listarPorMatricula(matricula);
    }

    /**
     * Una inscripción nueva (o una reinscripción: es el mismo flujo, solo
     * cambia el grupo/periodo elegido para una trayectoria que ya existía)
     * SIEMPRE genera automáticamente las inscripcion_materias y sus
     * calificaciones vacías a partir de lo que ya está definido en el grupo,
     * así el admin nunca captura materia por materia.
     */
    public ResultadoSimple inscribir(int idTrayectoria, int idGrupo, int idPeriodo, Usuario responsable)
    {
        TrayectoriaAcademica trayectoria = daoTrayectoria.buscarPorId(idTrayectoria);

        if (trayectoria == null)
        {
            return ResultadoSimple.fallo("La trayectoria ya no existe.");
        }

        if (!"ACTIVA".equals(trayectoria.getEstado()))
        {
            return ResultadoSimple.fallo("Esa trayectoria no está activa (está " + trayectoria.getEstado().toLowerCase()
                    + "). Reanúdala antes de inscribirla a un grupo.");
        }

        if (daoInscripcion.existeParaTrayectoriaGrupo(idTrayectoria, idGrupo))
        {
            return ResultadoSimple.fallo("Esa trayectoria ya está inscrita en ese grupo.");
        }

        // Reinscripción: si la trayectoria ya tenía una inscripción activa en
        // otro grupo/periodo, se cierra antes de crear la nueva para que solo
        // quede una activa a la vez.
        daoInscripcion.finalizarActivasDeTrayectoria(idTrayectoria);

        int idInscripcion = daoInscripcion.agregar(idTrayectoria, idGrupo, idPeriodo);
        daoInscripcionMateria.generarParaInscripcion(idInscripcion, idGrupo);

        servicioBitacora.registrarAlta(responsable, "inscripciones", idInscripcion,
                "Inscribió la matrícula " + trayectoria.getMatricula() + " a un grupo con "
                + daoInscripcionMateria.contarMaterias(idInscripcion) + " materias generadas automáticamente");

        return ResultadoSimple.exito(idInscripcion);
    }

    public void darDeBaja(int idInscripcion, Usuario responsable)
    {
        daoInscripcion.actualizarEstado(idInscripcion, "Baja");
        servicioBitacora.registrarBaja(responsable, "inscripciones", idInscripcion, "Dio de baja una inscripción");
    }
}