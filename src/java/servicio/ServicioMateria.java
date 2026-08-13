package servicio;

import doa.DAOMateria;
import doa.DAOGrupoMateria;
import modelo.Materia;
import modelo.Usuario;
import java.util.ArrayList;

public class ServicioMateria
{
    private final DAOMateria daoMateria;
    private final DAOGrupoMateria daoGrupoMateria;
    private final ServicioBitacora servicioBitacora;

    public ServicioMateria()
    {
        this.daoMateria = new DAOMateria();
        this.daoGrupoMateria = new DAOGrupoMateria();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<Materia> listar()
    {
        return daoMateria.listar();
    }

    public ArrayList<Materia> listarActivas()
    {
        return daoMateria.listarActivas();
    }

    public ArrayList<Materia> listarPorPlanCuatrimestre(int idPlanCuatrimestre)
    {
        return daoMateria.listarPorPlanCuatrimestre(idPlanCuatrimestre);
    }

    public Materia buscarPorId(int idMateria)
    {
        return daoMateria.buscarPorId(idMateria);
    }

    /**
     * Da de alta la materia ya ligada a un cuatrimestre concreto de un plan.
     * Ya no es un catálogo reutilizable: si la misma materia se imparte en
     * otro cuatrimestre o carrera, se debe dar de alta de nuevo ahí.
     */
    public ResultadoSimple agregar(Materia materia, Usuario responsable)
    {
        if (materia.getIdPlanCuatrimestre() <= 0)
        {
            return ResultadoSimple.fallo("Elige la carrera, plan y cuatrimestre al que pertenece la materia.");
        }

        if (daoMateria.existeNombreEnCuatrimestre(materia.getNombreMateria(), materia.getIdPlanCuatrimestre(), null))
        {
            return ResultadoSimple.fallo("Ya existe una materia con ese nombre en ese mismo cuatrimestre.");
        }

        int idMateria = daoMateria.agregar(materia);
        servicioBitacora.registrarAlta(responsable, "materias", idMateria,
                "Alta de materia " + materia.getNombreMateria());

        return ResultadoSimple.exito(idMateria);
    }

    /**
     * Solo nombre y estatus se pueden modificar; el cuatrimestre/plan al que
     * quedó ligada la materia se fijó desde el alta.
     */
    public ResultadoSimple modificar(Materia materia, Usuario responsable)
    {
        Materia existente = daoMateria.buscarPorId(materia.getIdMateria());

        if (existente == null)
        {
            return ResultadoSimple.fallo("La materia ya no existe.");
        }

        if (daoMateria.existeNombreEnCuatrimestre(materia.getNombreMateria(), existente.getIdPlanCuatrimestre(), materia.getIdMateria()))
        {
            return ResultadoSimple.fallo("Ya existe otra materia con ese nombre en ese mismo cuatrimestre.");
        }

        materia.setIdPlanCuatrimestre(existente.getIdPlanCuatrimestre());
        daoMateria.modificar(materia);
        servicioBitacora.registrarAlta(responsable, "materias", materia.getIdMateria(),
                "Modificó la materia " + materia.getNombreMateria());

        return ResultadoSimple.exito(materia.getIdMateria());
    }

    /**
     * Si la materia ya se copió a algún grupo (es decir, ya se está
     * impartiendo) no se puede eliminar sin afectar ese historial, así que
     * se desactiva en su lugar.
     */
    public ResultadoDesactivable eliminarODesactivar(int idMateria, Usuario responsable)
    {
        Materia materia = daoMateria.buscarPorId(idMateria);

        if (materia == null)
        {
            return ResultadoDesactivable.fallo("La materia ya no existe.");
        }

        if (daoGrupoMateria.materiaEstaEnAlgunGrupo(idMateria))
        {
            materia.setEstatus("Inactiva");
            daoMateria.modificar(materia);
            servicioBitacora.registrarBaja(responsable, "materias", idMateria,
                    "Desactivó la materia " + materia.getNombreMateria() + " (ya forma parte de un grupo)");
            return ResultadoDesactivable.exitoDesactivada();
        }

        daoMateria.eliminar(idMateria);
        servicioBitacora.registrarBaja(responsable, "materias", idMateria,
                "Eliminó la materia " + materia.getNombreMateria());

        return ResultadoDesactivable.exito();
    }

    public static class ResultadoDesactivable
    {
        private final boolean exito;
        private final boolean desactivadaEnLugarDeEliminada;
        private final String mensajeError;

        private ResultadoDesactivable(boolean exito, boolean desactivadaEnLugarDeEliminada, String mensajeError)
        {
            this.exito = exito;
            this.desactivadaEnLugarDeEliminada = desactivadaEnLugarDeEliminada;
            this.mensajeError = mensajeError;
        }

        public static ResultadoDesactivable exito()
        {
            return new ResultadoDesactivable(true, false, null);
        }

        public static ResultadoDesactivable exitoDesactivada()
        {
            return new ResultadoDesactivable(true, true, null);
        }

        public static ResultadoDesactivable fallo(String mensajeError)
        {
            return new ResultadoDesactivable(false, false, mensajeError);
        }

        public boolean isExito()
        {
            return exito;
        }

        public boolean isDesactivadaEnLugarDeEliminada()
        {
            return desactivadaEnLugarDeEliminada;
        }

        public String getMensajeError()
        {
            return mensajeError;
        }
    }
}
