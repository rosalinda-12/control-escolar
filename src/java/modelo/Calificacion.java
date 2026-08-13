package modelo;

public class Calificacion
{
    private int idCalificacion;
    private int idInscripcion;
    private Double parcial1;
    private Double parcial2;
    private Double parcial3;
    private Double promedioFinal;

    public int getIdCalificacion()
    {
        return idCalificacion;
    }

    public void setIdCalificacion(int idCalificacion)
    {
        this.idCalificacion = idCalificacion;
    }

    public int getIdInscripcion()
    {
        return idInscripcion;
    }

    public void setIdInscripcion(int idInscripcion)
    {
        this.idInscripcion = idInscripcion;
    }

    public Double getParcial1()
    {
        return parcial1;
    }

    public void setParcial1(Double parcial1)
    {
        this.parcial1 = parcial1;
    }

    public Double getParcial2()
    {
        return parcial2;
    }

    public void setParcial2(Double parcial2)
    {
        this.parcial2 = parcial2;
    }

    public Double getParcial3()
    {
        return parcial3;
    }

    public void setParcial3(Double parcial3)
    {
        this.parcial3 = parcial3;
    }

    public Double getPromedioFinal()
    {
        return promedioFinal;
    }

    public void setPromedioFinal(Double promedioFinal)
    {
        this.promedioFinal = promedioFinal;
    }
}
