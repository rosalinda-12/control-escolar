package servicio;

import doa.DAOTrayectoriaAcademica;
import modelo.Alumno;
import modelo.TrayectoriaAcademica;
import modelo.Usuario;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import util.EmailUtil;

public class ServicioTrayectoria
{
    private final DAOTrayectoriaAcademica daoTrayectoria;
    private final ServicioAlumno servicioAlumno;
    private final ServicioBitacora servicioBitacora;

    public ServicioTrayectoria()
    {
        this.daoTrayectoria = new DAOTrayectoriaAcademica();
        this.servicioAlumno = new ServicioAlumno();
        this.servicioBitacora = new ServicioBitacora();
    }



    public ArrayList<TrayectoriaAcademica> listar()
    {
        return daoTrayectoria.listar();
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

    public TrayectoriaAcademica buscarPorTexto(String busqueda)
    {
        return daoTrayectoria.buscarPorTexto(busqueda);
    }

    public String obtenerAvisoSiguienteNivel(int idTrayectoria)
    {
        if (daoTrayectoria.contarMateriasPendientes(idTrayectoria) > 0)
        {
            return null;
        }

        String siguienteNivel = daoTrayectoria.obtenerSiguienteNivel(idTrayectoria);
        return siguienteNivel == null ? null : "Nivel concluido. El alumno puede continuar con " + siguienteNivel + ".";
    }

    public String sugerirMatricula(int idPlan)
    {
        return daoTrayectoria.sugerirMatricula(idPlan);
    }

    public ArrayList<TrayectoriaAcademica> listarListasParaAvance()
    {
        ArrayList<TrayectoriaAcademica> listas = new ArrayList<>();
        for (TrayectoriaAcademica trayectoria : daoTrayectoria.listar())
        {
            if ("ACTIVA".equals(trayectoria.getEstado())
                    && daoTrayectoria.contarMateriasPendientes(trayectoria.getIdTrayectoria()) == 0
                    && daoTrayectoria.tieneSiguienteNivel(trayectoria.getIdPlan()))
            {
                listas.add(trayectoria);
            }
        }
        return listas;
    }

    public ResultadoSimple avanzarEnLote(List<Integer> idsTrayectoria, Usuario responsable)
    {
        int creadas = 0;
        for (Integer idTrayectoria : idsTrayectoria)
        {
            TrayectoriaAcademica anterior = daoTrayectoria.buscarPorId(idTrayectoria);
            if (anterior == null || !"ACTIVA".equals(anterior.getEstado())
                    || daoTrayectoria.contarMateriasPendientes(idTrayectoria) > 0
                    || !daoTrayectoria.tieneSiguienteNivel(anterior.getIdPlan())) continue;

            TrayectoriaAcademica siguiente = new TrayectoriaAcademica();
            siguiente.setIdAlumno(anterior.getIdAlumno());
            siguiente.setIdPlan(anterior.getIdPlan());
            String matriculaBase = anterior.getMatricula().split("_")[0];
            siguiente.setMatricula(matriculaBase + "_i");
            siguiente.setIdTrayectoriaAnterior(anterior.getIdTrayectoria());
            ResultadoSimple resultado = agregar(siguiente, responsable);
            if (resultado.isExito()) creadas++;
        }
        return ResultadoSimple.exito(creadas);
    }



    public ResultadoSimple agregar(TrayectoriaAcademica trayectoria, Usuario responsable)
    {
        Alumno alumno = servicioAlumno.buscarPorId(trayectoria.getIdAlumno());

        if (alumno != null && "BajaDefinitiva".equals(alumno.getEstatus()))
        {
            return ResultadoSimple.fallo("Este alumno tiene baja definitiva; ya no puede registrar nuevas trayectorias. "
                    + "Si fue un error, primero revierte su baja definitiva.");
        }

        if (daoTrayectoria.existeMatricula(trayectoria.getMatricula()))
        {
            return ResultadoSimple.fallo("Ya existe una trayectoria con esa matrícula.");
        }

        if (trayectoria.getIdTrayectoriaAnterior() != null)
        {
            TrayectoriaAcademica anterior = daoTrayectoria.buscarPorId(trayectoria.getIdTrayectoriaAnterior());

            if (anterior == null)
            {
                return ResultadoSimple.fallo("La trayectoria anterior indicada no existe.");
            }

            if (!"ACTIVA".equals(anterior.getEstado()))
            {
                return ResultadoSimple.fallo("La trayectoria anterior ya no está activa; no se puede encadenar un avance de nivel desde ella.");
            }

            int pendientes = daoTrayectoria.contarMateriasPendientes(trayectoria.getIdTrayectoriaAnterior());

            if (pendientes > 0)
            {
                return ResultadoSimple.fallo("El alumno no puede avanzar de nivel: le faltan " + pendientes
                        + " materia(s) por aprobar en su plan actual (" + anterior.getNombrePlan() + ").");
            }
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



    public void bajaTemporal(int idTrayectoria, Usuario responsable)
    {
        daoTrayectoria.actualizarEstado(idTrayectoria, "BAJA_TEMPORAL", false);
        notificarCambioEstado(idTrayectoria, "Baja temporal");
        servicioBitacora.registrarBaja(responsable, "trayectorias_academicas", idTrayectoria, "Dio de baja temporal la trayectoria");
    }

    public void reanudar(int idTrayectoria, Usuario responsable)
    {
        daoTrayectoria.actualizarEstado(idTrayectoria, "ACTIVA", false);
        servicioBitacora.registrarAlta(responsable, "trayectorias_academicas", idTrayectoria, "Reanudó la trayectoria");
    }



    public void bajaDefinitiva(int idTrayectoria, Usuario responsable)
    {
        daoTrayectoria.actualizarEstado(idTrayectoria, "BAJA_DEFINITIVA", true);
        notificarCambioEstado(idTrayectoria, "Baja definitiva");
        servicioBitacora.registrarBaja(responsable, "trayectorias_academicas", idTrayectoria,
                "Dio de baja definitiva la trayectoria; ya no podrá inscribirse en ella");
    }



    public void revertirBajaDefinitiva(int idTrayectoria, Usuario responsable)
    {
        TrayectoriaAcademica trayectoria = daoTrayectoria.buscarPorId(idTrayectoria);

        if (trayectoria == null || !"BAJA_DEFINITIVA".equals(trayectoria.getEstado()))
        {
            return;
        }

        daoTrayectoria.revertirAActiva(idTrayectoria);
        servicioBitacora.registrarAlta(responsable, "trayectorias_academicas", idTrayectoria,
                "Revirtió la baja definitiva de la trayectoria (corrección de error)");
    }

    private void notificarCambioEstado(int idTrayectoria, String estado)
    {
        TrayectoriaAcademica trayectoria = daoTrayectoria.buscarPorId(idTrayectoria);
        if (trayectoria == null) return;
        Alumno alumno = servicioAlumno.buscarPorId(trayectoria.getIdAlumno());
        if (alumno != null) EmailUtil.enviarCambioTrayectoria(alumno.getCorreo(), alumno.getNombreCompleto(), estado);
    }
}
