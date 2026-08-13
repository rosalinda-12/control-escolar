package modelo;

public class PlanNivel
{
    private int idPlanNivel;
    private int idPlan;
    private int idNivel;
    private String nombreNivel;
    private int cuatrimestreInicio;
    private int cuatrimestreFin;

    public int getIdPlanNivel()
    {
        return idPlanNivel;
    }

    public void setIdPlanNivel(int idPlanNivel)
    {
        this.idPlanNivel = idPlanNivel;
    }

    public int getIdPlan()
    {
        return idPlan;
    }

    public void setIdPlan(int idPlan)
    {
        this.idPlan = idPlan;
    }

    public int getIdNivel()
    {
        return idNivel;
    }

    public void setIdNivel(int idNivel)
    {
        this.idNivel = idNivel;
    }

    public String getNombreNivel()
    {
        return nombreNivel;
    }

    public void setNombreNivel(String nombreNivel)
    {
        this.nombreNivel = nombreNivel;
    }

    public int getCuatrimestreInicio()
    {
        return cuatrimestreInicio;
    }

    public void setCuatrimestreInicio(int cuatrimestreInicio)
    {
        this.cuatrimestreInicio = cuatrimestreInicio;
    }

    public int getCuatrimestreFin()
    {
        return cuatrimestreFin;
    }

    public void setCuatrimestreFin(int cuatrimestreFin)
    {
        this.cuatrimestreFin = cuatrimestreFin;
    }
}
