package servicio;

import doa.DAOTrayectoriaAcademica;
import modelo.TrayectoriaAcademica;
import modelo.Usuario;
import java.time.LocalDate;
import java.util.ArrayList;

public class ServicioTrayectoria
{
    private final DAOTrayectoriaAcademica daoTrayectoria;
    private final ServicioBitacora servicioBitacora;

    public ServicioTrayectoria()
    {
        this.daoTrayectoria = new DAOTrayectoriaAcademica();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<TrayectoriaAcademica> listarPorAlumno(int idAlumno)
    {
        return daoTrayectoria.listarPorAlumno(idAlumno);
    }

    public TrayectoriaAcademica buscarPorId(int idTrayectoria)
    {
        return daoTrayectoria.buscarPorId(idTrayectoria);
    }

    public TrayectoriaAcademica buscarPorMatricula(String matricula)
    {
        return daoTrayectoria.buscarPorMatricula(matricula);
    }

    /**
     * Crea una nueva trayectoria para el alumno. Si se indica una trayectoria
     * anterior (por ejemplo, viene de TSU y ahora continúa en Ingeniería, o
     * cambió de carrera), esa trayectoria previa se cierra automáticamente
     * con estado CAMBIO, pero su historial de inscripciones y calificaciones
     * no se toca.
     */
    public ResultadoSimple agregar(TrayectoriaAcademica trayectoria, Usuario responsable)
    {
        if (daoTrayectoria.existeMatricula(trayectoria.getMatricula()))
        {
            return ResultadoSimple.fallo("Ya existe una trayectoria con esa matrícula.");
        }

        trayectoria.setFechaInicio(LocalDate.now());
        int idTrayectoria = daoTrayectoria.agregar(trayectoria);

        if (trayectoria.getIdTrayectoriaAnterior() != null)
        {
            daoTrayectoria.actualizarEstado(trayectoria.getIdTrayectoriaAnterior(), "CAMBIO", true);
        }

        servicioBitacora.registrarAlta(responsable, "trayectorias_academicas", idTrayectoria,
                "Alta de trayectoria con matrícula " + trayectoria.getMatricula());

        return ResultadoSimple.exito(idTrayectoria);
    }

    public void pausar(int idTrayectoria, Usuario responsable)
    {
        daoTrayectoria.actualizarEstado(idTrayectoria, "PAUSADA", false);
        servicioBitacora.registrarBaja(responsable, "trayectorias_academicas", idTrayectoria, "Pausó la trayectoria");
    }

    public void reanudar(int idTrayectoria, Usuario responsable)
    {
        daoTrayectoria.actualizarEstado(idTrayectoria, "ACTIVA", false);
        servicioBitacora.registrarAlta(responsable, "trayectorias_academicas", idTrayectoria, "Reanudó la trayectoria");
    }

    public void cerrar(int idTrayectoria, Usuario responsable)
    {
        daoTrayectoria.actualizarEstado(idTrayectoria, "CERRADA", true);
        servicioBitacora.registrarBaja(responsable, "trayectorias_academicas", idTrayectoria, "Cerró la trayectoria");
    }
}
