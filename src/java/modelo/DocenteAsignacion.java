package modelo;

public class DocenteAsignacion
{
    private int idAsignacion;
    private int idDocente;
    private String nombreDocente;
    private int idGrupoMateria;

    public int getIdAsignacion()
    {
        return idAsignacion;
    }

    public void setIdAsignacion(int idAsignacion)
    {
        this.idAsignacion = idAsignacion;
    }

    public int getIdDocente()
    {
        return idDocente;
    }

    public void setIdDocente(int idDocente)
    {
        this.idDocente = idDocente;
    }

    public String getNombreDocente()
    {
        return nombreDocente;
    }

    public void setNombreDocente(String nombreDocente)
    {
        this.nombreDocente = nombreDocente;
    }

    public int getIdGrupoMateria()
    {
        return idGrupoMateria;
    }

    public void setIdGrupoMateria(int idGrupoMateria)
    {
        this.idGrupoMateria = idGrupoMateria;
    }
}
