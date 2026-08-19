package modelo;

public class PlanCuatrimestreMateria
{
    private int idPlanCuatrimestreMateria;
    private int idPlanCuatrimestre;
    private int idMateria;
    private String nombreMateria;

    public int getIdPlanCuatrimestreMateria()
    {
        return idPlanCuatrimestreMateria;
    }

    public void setIdPlanCuatrimestreMateria(int idPlanCuatrimestreMateria)
    {
        this.idPlanCuatrimestreMateria = idPlanCuatrimestreMateria;
    }

    public int getIdPlanCuatrimestre()
    {
        return idPlanCuatrimestre;
    }

    public void setIdPlanCuatrimestre(int idPlanCuatrimestre)
    {
        this.idPlanCuatrimestre = idPlanCuatrimestre;
    }

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
}
