package modelo;

import java.time.LocalDateTime;

public class Inscripcion
{
    private int idInscripcion;
    private int idTrayectoria;
    private String matricula;
    private String nombreAlumno;
    private int idGrupo;
    private String nombreGrupo;
    private String nombreCarrera;
    private int idPeriodo;
    private String nombrePeriodo;
    private LocalDateTime fechaInscripcion;
    private String estado;

    public int getIdInscripcion()
    {
        return idInscripcion;
    }

    public void setIdInscripcion(int idInscripcion)
    {
        this.idInscripcion = idInscripcion;
    }

    public int getIdTrayectoria()
    {
        return idTrayectoria;
    }

    public void setIdTrayectoria(int idTrayectoria)
    {
        this.idTrayectoria = idTrayectoria;
    }

    public String getMatricula()
    {
        return matricula;
    }

    public void setMatricula(String matricula)
    {
        this.matricula = matricula;
    }

    public String getNombreAlumno()
    {
        return nombreAlumno;
    }

    public void setNombreAlumno(String nombreAlumno)
    {
        this.nombreAlumno = nombreAlumno;
    }

    public int getIdGrupo()
    {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo)
    {
        this.idGrupo = idGrupo;
    }

    public String getNombreGrupo()
    {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo)
    {
        this.nombreGrupo = nombreGrupo;
    }

    public String getNombreCarrera()
    {
        return nombreCarrera;
    }

    public void setNombreCarrera(String nombreCarrera)
    {
        this.nombreCarrera = nombreCarrera;
    }

    public int getIdPeriodo()
    {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo)
    {
        this.idPeriodo = idPeriodo;
    }

    public String getNombrePeriodo()
    {
        return nombrePeriodo;
    }

    public void setNombrePeriodo(String nombrePeriodo)
    {
        this.nombrePeriodo = nombrePeriodo;
    }

    public LocalDateTime getFechaInscripcion()
    {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDateTime fechaInscripcion)
    {
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getEstado()
    {
        return estado;
    }

    public void setEstado(String estado)
    {
        this.estado = estado;
    }
}
