package servicio;

import doa.DAOPlanCuatrimestre;
import doa.DAOMateria;
import doa.DAOPlanNivel;
import modelo.Materia;
import modelo.PlanCuatrimestre;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ServicioCurriculo
{
    private final DAOPlanCuatrimestre daoPlanCuatrimestre;
    private final DAOMateria daoMateria;
    private final DAOPlanNivel daoPlanNivel;

    public ServicioCurriculo()
    {
        this.daoPlanCuatrimestre = new DAOPlanCuatrimestre();
        this.daoMateria = new DAOMateria();
        this.daoPlanNivel = new DAOPlanNivel();
    }

    public ArrayList<PlanCuatrimestre> listarCuatrimestresDelPlan(int idPlan)
    {
        return daoPlanCuatrimestre.listarPorPlan(idPlan);
    }

    /**
     * Cuatrimestres de todos los planes vigentes, con su etiqueta de
     * carrera/plan/cuatrimestre ya armada. Se usa en el selector de "Nueva
     * materia" para elegir dónde queda ligada.
     */
    public ArrayList<PlanCuatrimestre> listarCuatrimestresDePlanesVigentes()
    {
        return daoPlanCuatrimestre.listarDePlanesVigentes();
    }

    public String nivelParaCuatrimestre(int idPlan, int numeroCuatrimestre)
    {
        return daoPlanNivel.nivelParaCuatrimestre(idPlan, numeroCuatrimestre);
    }

    /**
     * Regresa, por cada cuatrimestre del plan, la lista de materias que ya
     * quedaron ligadas a él desde su alta. Se usa para pintar de un vistazo
     * todo el mapa curricular. Es solo lectura: las materias ahora se
     * asignan a su cuatrimestre en el momento en que se crean (ver
     * ServicioMateria), no desde aquí.
     */
    public LinkedHashMap<PlanCuatrimestre, ArrayList<Materia>> mapaCurricular(int idPlan)
    {
        LinkedHashMap<PlanCuatrimestre, ArrayList<Materia>> mapa = new LinkedHashMap<>();

        for (PlanCuatrimestre cuatrimestre : daoPlanCuatrimestre.listarPorPlan(idPlan))
        {
            mapa.put(cuatrimestre, daoMateria.listarPorPlanCuatrimestre(cuatrimestre.getIdPlanCuatrimestre()));
        }

        return mapa;
    }
}
