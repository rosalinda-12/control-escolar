package servicio;

import conexion.ConexionMySQL;
import doa.DAOPlanCuatrimestre;
import doa.DAOPlanEstudio;
import doa.DAOPlanNivel;
import modelo.PlanEstudio;
import modelo.PlanNivel;
import modelo.Usuario;
import java.sql.Connection;
import java.sql.SQLException;
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
     *
     * "Niveles dentro del plan" es un paso obligatorio, no una sugerencia:
     * el plan se crea en 'Borrador' y solo se marca 'Vigente' después de
     * registrar al menos un nivel, todo dentro de la misma transacción. Si
     * falta el nivel, o si algo falla a mitad de camino, no queda un plan
     * a medias: se revierte todo. La base de datos refuerza esta misma
     * regla con triggers (trg_planes_bloquear_vigente_directo y
     * trg_planes_bloquear_vigente_sin_niveles), así que ni siquiera un
     * script que hable directo con la BD puede saltársela.
     */
    public ResultadoSimple agregar(PlanEstudio plan, int[] idsNivel, int[] cuatrimestresInicio, int[] cuatrimestresFin, Usuario responsable)
    {
        if (daoPlanEstudio.existeVersion(plan.getIdCarrera(), plan.getVersion()))
        {
            return ResultadoSimple.fallo("Esa carrera ya tiene un plan con esa versión.");
        }

        if (idsNivel == null || idsNivel.length == 0)
        {
            return ResultadoSimple.fallo("Agrega al menos un nivel dentro del plan (por ejemplo, TSU del cuatrimestre 1 al 6). Este paso es obligatorio: un plan no puede quedar Vigente sin niveles.");
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

        try (Connection conexion = ConexionMySQL.obtenerConexion())
        {
            conexion.setAutoCommit(false);

            try
            {
                int idPlan = daoPlanEstudio.agregar(conexion, plan);

                for (int i = 0; i < idsNivel.length; i++)
                {
                    daoPlanNivel.agregar(conexion, idPlan, idsNivel[i], cuatrimestresInicio[i], cuatrimestresFin[i]);
                }

                daoPlanCuatrimestre.generarParaPlan(conexion, idPlan, plan.getDuracionCuatrimestres());
                daoPlanEstudio.marcarVigente(conexion, idPlan);

                conexion.commit();

                servicioBitacora.registrarAlta(responsable, "planes_estudio", idPlan,
                        "Alta de plan de estudios " + plan.getNombrePlan() + " versión " + plan.getVersion());

                return ResultadoSimple.exito(idPlan);
            }
            catch (SQLException excepcion)
            {
                conexion.rollback();
                throw new RuntimeException(excepcion);
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
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
