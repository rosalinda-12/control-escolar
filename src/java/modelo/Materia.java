package modelo;

public class Materia
{
    private int idMateria;
    private String nombreMateria;
    private String estatus;
    private String pdfTemario;


    private int idPlanCuatrimestre;


    private int numeroCuatrimestre;
    private String nombrePlan;
    private int idCarrera;
    private String nombreCarrera;

    public int getIdMateria()
    {
        return idMateria;
    }

    public void setIdMateria(int idMateria)
    {
        this.idMateria = idMateria;
    }

    public String getNombreMateria()
    {
        return nombreMateria;
    }

    public void setNombreMateria(String nombreMateria)
    {
        this.nombreMateria = nombreMateria;
    }

    public String getEstatus()
    {
        return estatus;
    }

    public void setEstatus(String estatus)
    {
        this.estatus = estatus;
    }



    public String getPdfTemario()
    {
        return pdfTemario;
    }

    public void setPdfTemario(String pdfTemario)
    {
        this.pdfTemario = pdfTemario;
    }

    public boolean isTieneTemario()
    {
        return pdfTemario != null && !pdfTemario.isEmpty();
    }

    public int getIdPlanCuatrimestre()
    {
        return idPlanCuatrimestre;
    }

    public void setIdPlanCuatrimestre(int idPlanCuatrimestre)
    {
        this.idPlanCuatrimestre = idPlanCuatrimestre;
    }

    public int getNumeroCuatrimestre()
    {
        return numeroCuatrimestre;
    }

    public void setNumeroCuatrimestre(int numeroCuatrimestre)
    {
        this.numeroCuatrimestre = numeroCuatrimestre;
    }

    public String getNombrePlan()
    {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan)
    {
        this.nombrePlan = nombrePlan;
    }

    public String getNombreCarrera()
    {
        return nombreCarrera;
    }

    public void setNombreCarrera(String nombreCarrera)
    {
        this.nombreCarrera = nombreCarrera;
    }

    public int getIdCarrera()
    {
        return idCarrera;
    }

    public void setIdCarrera(int idCarrera)
    {
        this.idCarrera = idCarrera;
    }
}
