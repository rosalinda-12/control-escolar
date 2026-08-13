package modelo;

import java.time.LocalDate;

public class Periodo
{
    private int idPeriodo;
    private int idCiclo;
    private String nombreCiclo;
    private String nombrePeriodo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estatus;

    public int getIdPeriodo()
    {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo)
    {
        this.idPeriodo = idPeriodo;
    }

    public int getIdCiclo()
    {
        return idCiclo;
    }

    public void setIdCiclo(int idCiclo)
    {
        this.idCiclo = idCiclo;
    }

    public String getNombreCiclo()
    {
        return nombreCiclo;
    }

    public void setNombreCiclo(String nombreCiclo)
    {
        this.nombreCiclo = nombreCiclo;
    }

    public String getNombrePeriodo()
    {
        return nombrePeriodo;
    }

    public void setNombrePeriodo(String nombrePeriodo)
    {
        this.nombrePeriodo = nombrePeriodo;
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

    public String getEstatus()
    {
        return estatus;
    }

    public void setEstatus(String estatus)
    {
        this.estatus = estatus;
    }
}
