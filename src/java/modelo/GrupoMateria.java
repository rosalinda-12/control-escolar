package modelo;

public class GrupoMateria
{
    private int idGrupoMateria;
    private int idGrupo;
    private int idMateria;
    private String nombreMateria;
    private boolean tieneDocenteAsignado;
    private String nombreDocente;

    public int getIdGrupoMateria()
    {
        return idGrupoMateria;
    }

    public void setIdGrupoMateria(int idGrupoMateria)
    {
        this.idGrupoMateria = idGrupoMateria;
    }

    public int getIdGrupo()
    {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo)
    {
        this.idGrupo = idGrupo;
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

    public boolean isTieneDocenteAsignado()
    {
        return tieneDocenteAsignado;
    }

    public void setTieneDocenteAsignado(boolean tieneDocenteAsignado)
    {
        this.tieneDocenteAsignado = tieneDocenteAsignado;
    }

    public String getNombreDocente()
    {
        return nombreDocente;
    }

    public void setNombreDocente(String nombreDocente)
    {
        this.nombreDocente = nombreDocente;
    }
}
