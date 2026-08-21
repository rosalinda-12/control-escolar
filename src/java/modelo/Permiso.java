package modelo;

public class Permiso
{
    private int idPermiso;
    private String clave;
    private String modulo;
    private String descripcion;

    private boolean activo;

    public int getIdPermiso()
    {
        return idPermiso;
    }

    public void setIdPermiso(int idPermiso)
    {
        this.idPermiso = idPermiso;
    }

    public String getClave()
    {
        return clave;
    }

    public void setClave(String clave)
    {
        this.clave = clave;
    }

    public String getModulo()
    {
        return modulo;
    }

    public void setModulo(String modulo)
    {
        this.modulo = modulo;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public void setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
    }

    public boolean isActivo()
    {
        return activo;
    }

    public void setActivo(boolean activo)
    {
        this.activo = activo;
    }
}
