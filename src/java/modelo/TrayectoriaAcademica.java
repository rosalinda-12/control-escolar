package modelo;

import java.time.LocalDate;

public class TrayectoriaAcademica
{
    private int idTrayectoria;
    private int idAlumno;
    private String nombreAlumno;
    private int idPlan;
    private int idCarrera;
    private String nombrePlan;
    private String nombreCarrera;
    private String matricula;
    private String grupoAnterior;
    private Integer idGrupoAnterior;
    private Integer idTrayectoriaAnterior;
    private String estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public int getIdTrayectoria()
    {
        return idTrayectoria;
    }

    public void setIdTrayectoria(int idTrayectoria)
    {
        this.idTrayectoria = idTrayectoria;
    }

    public int getIdAlumno()
    {
        return idAlumno;
    }

    public void setIdAlumno(int idAlumno)
    {
        this.idAlumno = idAlumno;
    }

    public String getNombreAlumno()
    {
        return nombreAlumno;
    }

    public void setNombreAlumno(String nombreAlumno)
    {
        this.nombreAlumno = nombreAlumno;
    }

    public int getIdPlan()
    {
        return idPlan;
    }

    public int getIdCarrera()
    {
        return idCarrera;
    }

    public void setIdCarrera(int idCarrera)
    {
        this.idCarrera = idCarrera;
    }

    public void setIdPlan(int idPlan)
    {
        this.idPlan = idPlan;
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

    public String getMatricula()
    {
        return matricula;
    }

    public void setMatricula(String matricula)
    {
        this.matricula = matricula;
    }

    public String getGrupoAnterior()
    {
        return grupoAnterior;
    }

    public void setGrupoAnterior(String grupoAnterior)
    {
        this.grupoAnterior = grupoAnterior;
    }

    public Integer getIdGrupoAnterior()
    {
        return idGrupoAnterior;
    }

    public void setIdGrupoAnterior(Integer idGrupoAnterior)
    {
        this.idGrupoAnterior = idGrupoAnterior;
    }

    public Integer getIdTrayectoriaAnterior()
    {
        return idTrayectoriaAnterior;
    }

    public void setIdTrayectoriaAnterior(Integer idTrayectoriaAnterior)
    {
        this.idTrayectoriaAnterior = idTrayectoriaAnterior;
    }

    public String getEstado()
    {
        return estado;
    }

    public void setEstado(String estado)
    {
        this.estado = estado;
    }

    public LocalDate getFechaInicio()
    {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio)
    {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin()
    {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin)
    {
        this.fechaFin = fechaFin;
    }
}
