package modelo;

public class Nivel
{
    private int idNivel;
    private String nombreNivel;
    private int cuatrimestreMinimo;
    private int cuatrimestreMaximo;

    public int getIdNivel()
    {
        return idNivel;
    }

    public void setIdNivel(int idNivel)
    {
        this.idNivel = idNivel;
    }

    public String getNombreNivel()
    {
        return nombreNivel;
    }

    public void setNombreNivel(String nombreNivel)
    {
        this.nombreNivel = nombreNivel;
    }

    public int getCuatrimestreMinimo()
    {
        return cuatrimestreMinimo;
    }

    public void setCuatrimestreMinimo(int cuatrimestreMinimo)
    {
        this.cuatrimestreMinimo = cuatrimestreMinimo;
    }

    public int getCuatrimestreMaximo()
    {
        return cuatrimestreMaximo;
    }

    public void setCuatrimestreMaximo(int cuatrimestreMaximo)
    {
        this.cuatrimestreMaximo = cuatrimestreMaximo;
    }
}
