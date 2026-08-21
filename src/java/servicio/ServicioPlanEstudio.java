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

    public ResultadoSimple actualizar(PlanEstudio plan, Usuario responsable)
    {
        PlanEstudio anterior = daoPlanEstudio.buscarPorId(plan.getIdPlan());
        if (anterior == null)
        {
            return ResultadoSimple.fallo("El plan ya no existe.");
        }
        if (daoPlanEstudio.tieneGruposAsociados(plan.getIdPlan()))
        {
            return ResultadoSimple.fallo("Este plan ya tiene grupos asociados y no puede editarse. Crea una nueva versión para conservar el historial.");
        }
        if ((anterior.getIdCarrera() != plan.getIdCarrera() || !anterior.getVersion().equals(plan.getVersion()))
                && daoPlanEstudio.existeVersion(plan.getIdCarrera(), plan.getVersion()))
        {
            return ResultadoSimple.fallo("Esa carrera ya tiene un plan con esa versión.");
        }
        daoPlanEstudio.actualizar(plan);
        servicioBitacora.registrarAlta(responsable, "planes_estudio", plan.getIdPlan(), "Actualizó el plan " + plan.getNombrePlan());
        return ResultadoSimple.exito(plan.getIdPlan());
    }



    public ResultadoSimple agregar(PlanEstudio plan, int[] idsNivel, String[] titulosEgreso, int[] cuatrimestresInicio, int[] cuatrimestresFin, Usuario responsable)
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

            if (titulosEgreso == null || titulosEgreso[i] == null || titulosEgreso[i].isBlank())
            {
                return ResultadoSimple.fallo("Cada tramo debe indicar el título que se otorga al terminarlo (por ejemplo, "
                        + "\"TSU en Tecnologías de la Información\" o \"Ingeniería en Desarrollo de Software Multiplataforma\"). "
                        + "Si el título es el mismo en todos los tramos, repítelo.");
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
                    daoPlanNivel.agregar(conexion, idPlan, idsNivel[i], titulosEgreso[i].trim(), cuatrimestresInicio[i], cuatrimestresFin[i]);
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
