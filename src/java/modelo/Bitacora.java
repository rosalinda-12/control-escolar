package modelo;

import java.time.LocalDateTime;

public class Bitacora
{
    private int idBitacora;
    private int idUsuario;
    private String tipoAccion;
    private String entidad;
    private int idRegistroAfectado;
    private String descripcion;
    private LocalDateTime fechaAccion;
    private String correoUsuario;

    public int getIdBitacora()
    {
        return idBitacora;
    }

    public void setIdBitacora(int idBitacora)
    {
        this.idBitacora = idBitacora;
    }

    public int getIdUsuario()
    {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario)
    {
        this.idUsuario = idUsuario;
    }

    public String getTipoAccion()
    {
        return tipoAccion;
    }

    public void setTipoAccion(String tipoAccion)
    {
        this.tipoAccion = tipoAccion;
    }

    public String getEntidad()
    {
        return entidad;
    }

    public void setEntidad(String entidad)
    {
        this.entidad = entidad;
    }

    public int getIdRegistroAfectado()
    {
        return idRegistroAfectado;
    }

    public void setIdRegistroAfectado(int idRegistroAfectado)
    {
        this.idRegistroAfectado = idRegistroAfectado;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public void setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaAccion()
    {
        return fechaAccion;
    }

    public void setFechaAccion(LocalDateTime fechaAccion)
    {
        this.fechaAccion = fechaAccion;
    }

    public String getCorreoUsuario()
    {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario)
    {
        this.correoUsuario = correoUsuario;
    }
}
