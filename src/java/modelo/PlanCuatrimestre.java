package modelo;

public class PlanCuatrimestre
{
    private int idPlanCuatrimestre;
    private int idPlan;
    private int numeroCuatrimestre;
    private String nombreNivel;
    private String etiqueta;

    public int getIdPlanCuatrimestre()
    {
        return idPlanCuatrimestre;
    }

    public void setIdPlanCuatrimestre(int idPlanCuatrimestre)
    {
        this.idPlanCuatrimestre = idPlanCuatrimestre;
    }

    public int getIdPlan()
    {
        return idPlan;
    }

    public void setIdPlan(int idPlan)
    {
        this.idPlan = idPlan;
    }

    public int getNumeroCuatrimestre()
    {
        return numeroCuatrimestre;
    }

    public void setNumeroCuatrimestre(int numeroCuatrimestre)
    {
        this.numeroCuatrimestre = numeroCuatrimestre;
    }

    public String getNombreNivel()
    {
        return nombreNivel;
    }

    public void setNombreNivel(String nombreNivel)
    {
        this.nombreNivel = nombreNivel;
    }

    public String getEtiqueta()
    {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta)
    {
        this.etiqueta = etiqueta;
    }
}
