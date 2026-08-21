package modelo;

import java.util.ArrayList;



public class BoletaCuatrimestre
{
    private int idTrayectoria;
    private int numeroCuatrimestre;
    private String nombrePeriodo;
    private String nombreGrupo;
    private String nombreNivel;
    private ArrayList<Calificacion> materias = new ArrayList<>();
    private Double promedioCuatrimestre;
    private int materiasConPromedio;

    public int getIdTrayectoria()
    {
        return idTrayectoria;
    }

    public void setIdTrayectoria(int idTrayectoria)
    {
        this.idTrayectoria = idTrayectoria;
    }

    public int getNumeroCuatrimestre()
    {
        return numeroCuatrimestre;
    }

    public void setNumeroCuatrimestre(int numeroCuatrimestre)
    {
        this.numeroCuatrimestre = numeroCuatrimestre;
    }

    public String getNombrePeriodo()
    {
        return nombrePeriodo;
    }

    public void setNombrePeriodo(String nombrePeriodo)
    {
        this.nombrePeriodo = nombrePeriodo;
    }

    public String getNombreGrupo()
    {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo)
    {
        this.nombreGrupo = nombreGrupo;
    }

    public String getNombreNivel()
    {
        return nombreNivel;
    }

    public void setNombreNivel(String nombreNivel)
    {
        this.nombreNivel = nombreNivel;
    }

    public ArrayList<Calificacion> getMaterias()
    {
        return materias;
    }

    public void setMaterias(ArrayList<Calificacion> materias)
    {
        this.materias = materias;
    }

    public Double getPromedioCuatrimestre()
    {
        return promedioCuatrimestre;
    }

    public void setPromedioCuatrimestre(Double promedioCuatrimestre)
    {
        this.promedioCuatrimestre = promedioCuatrimestre;
    }

    public int getMateriasConPromedio()
    {
        return materiasConPromedio;
    }

    public void setMateriasConPromedio(int materiasConPromedio)
    {
        this.materiasConPromedio = materiasConPromedio;
    }



    public boolean isCompleto()
    {
        return materiasConPromedio == materias.size();
    }
}
