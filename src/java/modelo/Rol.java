package modelo;

public class Rol
{
    private int idRol;
    private String nombreRol;
    private String descripcion;
    private boolean esAdministradorPrincipal;

    public int getIdRol()
    {
        return idRol;
    }

    public void setIdRol(int idRol)
    {
        this.idRol = idRol;
    }

    public String getNombreRol()
    {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol)
    {
        this.nombreRol = nombreRol;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public void setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
    }



    public boolean isEsAdministradorPrincipal()
    {
        return esAdministradorPrincipal;
    }

    public void setEsAdministradorPrincipal(boolean esAdministradorPrincipal)
    {
        this.esAdministradorPrincipal = esAdministradorPrincipal;
    }
}
