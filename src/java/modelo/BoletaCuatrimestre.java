package modelo;

import java.util.ArrayList;

/**
 * Agrupa las materias (y sus calificaciones) que un alumno cursó en un
 * mismo cuatrimestre de su plan de estudio, junto con el promedio final
 * de ese cuatrimestre, para la boleta que ve el propio alumno.
 */
public class BoletaCuatrimestre
{
    private int numeroCuatrimestre;
    private String nombrePeriodo;
    private String nombreGrupo;
    private ArrayList<Calificacion> materias = new ArrayList<>();
    private Double promedioCuatrimestre;
    private int materiasConPromedio;

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

    /**
     * True si todavía falta el promedio final de alguna materia de este
     * cuatrimestre (parciales incompletos), para poder avisar en la vista
     * que el promedio del cuatrimestre es preliminar.
     */
    public boolean isCompleto()
    {
        return materiasConPromedio == materias.size();
    }
}
