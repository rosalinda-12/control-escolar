package modelo;

public class Carrera
{
    private int idCarrera;
    private String nombreCarrera;
    private String claveCarrera;
    private String estatus;

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

    public String getClaveCarrera()
    {
        return claveCarrera;
    }

    public void setClaveCarrera(String claveCarrera)
    {
        this.claveCarrera = claveCarrera;
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
