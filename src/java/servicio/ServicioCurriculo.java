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



    public ArrayList<PlanCuatrimestre> listarCuatrimestresDePlanesVigentes()
    {
        return daoPlanCuatrimestre.listarDePlanesVigentes();
    }

    public String nivelParaCuatrimestre(int idPlan, int numeroCuatrimestre)
    {
        return daoPlanNivel.nivelParaCuatrimestre(idPlan, numeroCuatrimestre);
    }



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
