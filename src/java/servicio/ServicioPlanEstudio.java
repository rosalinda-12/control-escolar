package servicio;

import doa.DAOPlanCuatrimestre;
import doa.DAOPlanEstudio;
import doa.DAOPlanNivel;
import modelo.PlanEstudio;
import modelo.PlanNivel;
import modelo.Usuario;
import java.util.ArrayList;

public class ServicioPlanEstudio
{
    private final DAOPlanEstudio daoPlanEstudio;
    private final DAOPlanNivel daoPlanNivel;
    private final DAOPlanCuatrimestre daoPlanCuatrimestre;
    private final ServicioBitacora servicioBitacora;

    public ServicioPlanEstudio()
    {
        this.daoPlanEstudio = new DAOPlanEstudio();
        this.daoPlanNivel = new DAOPlanNivel();
        this.daoPlanCuatrimestre = new DAOPlanCuatrimestre();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<PlanEstudio> listar()
    {
        return daoPlanEstudio.listar();
    }

    public ArrayList<PlanEstudio> listarVigentes()
    {
        return daoPlanEstudio.listarVigentes();
    }

    public PlanEstudio buscarPorId(int idPlan)
    {
        return daoPlanEstudio.buscarPorId(idPlan);
    }

    public ArrayList<PlanNivel> listarNivelesDelPlan(int idPlan)
    {
        return daoPlanNivel.listarPorPlan(idPlan);
    }

    /**
     * Crea el plan, registra sus tramos de nivel (por ejemplo TSU 1-6,
     * Ingeniería 7-11) y genera automáticamente los N cuatrimestres según
     * la duración indicada, para que el admin no tenga que darlos de alta
     * uno por uno.
     */
    public ResultadoSimple agregar(PlanEstudio plan, int[] idsNivel, int[] cuatrimestresInicio, int[] cuatrimestresFin, Usuario responsable)
    {
        if (daoPlanEstudio.existeVersion(plan.getIdCarrera(), plan.getVersion()))
        {
            return ResultadoSimple.fallo("Esa carrera ya tiene un plan con esa versión.");
        }

        if (idsNivel == null || idsNivel.length == 0)
        {
            return ResultadoSimple.fallo("Agrega al menos un tramo de nivel académico (por ejemplo, TSU del cuatrimestre 1 al 6).");
        }

        for (int i = 0; i < idsNivel.length; i++)
        {
            if (cuatrimestresInicio[i] < 1 || cuatrimestresFin[i] > plan.getDuracionCuatrimestres()
                    || cuatrimestresInicio[i] > cuatrimestresFin[i])
            {
                return ResultadoSimple.fallo("Los tramos de nivel deben estar dentro de la duración del plan (1 a "
                        + plan.getDuracionCuatrimestres() + ").");
            }
        }

        int idPlan = daoPlanEstudio.agregar(plan);

        for (int i = 0; i < idsNivel.length; i++)
        {
            daoPlanNivel.agregar(idPlan, idsNivel[i], cuatrimestresInicio[i], cuatrimestresFin[i]);
        }

        daoPlanCuatrimestre.generarParaPlan(idPlan, plan.getDuracionCuatrimestres());

        servicioBitacora.registrarAlta(responsable, "planes_estudio", idPlan,
                "Alta de plan de estudios " + plan.getNombrePlan() + " versión " + plan.getVersion());

        return ResultadoSimple.exito(idPlan);
    }

    /**
     * Un plan histórico nunca se edita para cambiar su significado (regla de
     * negocio del documento de diseño). Lo único que el admin puede hacer
     * después de creado es cerrarlo o reemplazarlo por una nueva versión.
     */
    public ResultadoSimple cerrar(int idPlan, Usuario responsable)
    {
        PlanEstudio plan = daoPlanEstudio.buscarPorId(idPlan);

        if (plan == null)
        {
            return ResultadoSimple.fallo("El plan ya no existe.");
        }

        daoPlanEstudio.actualizarEstatus(idPlan, "Cerrado");
        servicioBitacora.registrarBaja(responsable, "planes_estudio", idPlan,
                "Cerró el plan de estudios " + plan.getNombrePlan() + " versión " + plan.getVersion());

        return ResultadoSimple.exito(idPlan);
    }
}
