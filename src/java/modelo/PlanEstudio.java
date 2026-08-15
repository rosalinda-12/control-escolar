package modelo;

import java.time.LocalDate;

public class PlanEstudio
{
    private int idPlan;
    private int idCarrera;
    private String nombreCarrera;
    private String version;
    private String nombrePlan;
    private int duracionCuatrimestres;
    private LocalDate fechaVigenciaInicio;
    private String estatus;

    public int getIdPlan()
    {
        return idPlan;
    }

    public void setIdPlan(int idPlan)
    {
        this.idPlan = idPlan;
    }

    public int getIdCarrera()
    {
        return idCarrera;
    }

    public void setIdCarrera(int idCarrera)
    {
        this.idCarrera = idCarrera;
    }

    public String getNombreCarrera()
    {
        return nombreCarrera;
    }

    public void setNombreCarrera(String nombreCarrera)
    {
        this.nombreCarrera = nombreCarrera;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getNombrePlan()
    {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan)
    {
        this.nombrePlan = nombrePlan;
    }

    public int getDuracionCuatrimestres()
    {
        return duracionCuatrimestres;
    }

    public void setDuracionCuatrimestres(int duracionCuatrimestres)
    {
        this.duracionCuatrimestres = duracionCuatrimestres;
    }

    public LocalDate getFechaVigenciaInicio()
    {
        return fechaVigenciaInicio;
    }

    public void setFechaVigenciaInicio(LocalDate fechaVigenciaInicio)
    {
        this.fechaVigenciaInicio = fechaVigenciaInicio;
    }

    public String getEstatus()
    {
        return estatus;
    }

    public void setEstatus(String estatus)
    {
        this.estatus = estatus;
    }
}
