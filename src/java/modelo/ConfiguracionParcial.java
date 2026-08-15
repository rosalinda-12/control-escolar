package modelo;

import java.time.LocalDateTime;

public class ConfiguracionParcial
{
    private int idConfiguracion;
    private int idPeriodo;
    private int parcialActivo;
    private LocalDateTime fechaActualizacion;
    private String nombrePeriodo;

    public int getIdConfiguracion()
    {
        return idConfiguracion;
    }

    public void setIdConfiguracion(int idConfiguracion)
    {
        this.idConfiguracion = idConfiguracion;
    }

    public int getIdPeriodo()
    {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo)
    {
        this.idPeriodo = idPeriodo;
    }

    public int getParcialActivo()
    {
        return parcialActivo;
    }

    public void setParcialActivo(int parcialActivo)
    {
        this.parcialActivo = parcialActivo;
    }

    public LocalDateTime getFechaActualizacion()
    {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion)
    {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getNombrePeriodo()
    {
        return nombrePeriodo;
    }

    public void setNombrePeriodo(String nombrePeriodo)
    {
        this.nombrePeriodo = nombrePeriodo;
    }
}
