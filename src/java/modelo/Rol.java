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

    /**
     * true únicamente para el rol "Administrador" original (columna
     * roles.es_administrador_principal). Ese rol siempre tiene acceso
     * total y no depende de la tabla rol_permisos.
     */
    public boolean isEsAdministradorPrincipal()
    {
        return esAdministradorPrincipal;
    }

    public void setEsAdministradorPrincipal(boolean esAdministradorPrincipal)
    {
        this.esAdministradorPrincipal = esAdministradorPrincipal;
    }
}
