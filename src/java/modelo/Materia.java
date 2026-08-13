package modelo;

public class Materia
{
    private int idMateria;
    private String nombreMateria;
    private String estatus;

    // La materia ahora nace ligada a un único cuatrimestre de un único plan
    // (y por lo tanto a una única carrera). Ya no es un catálogo reutilizable:
    // si la misma materia debe impartirse en otro cuatrimestre o carrera, se
    // da de alta como un registro nuevo.
    private int idPlanCuatrimestre;

    // Campos de solo lectura (vienen del JOIN) para mostrar el contexto de
    // la materia sin tener que ir a buscarlo aparte.
    private int numeroCuatrimestre;
    private String nombrePlan;
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
}
