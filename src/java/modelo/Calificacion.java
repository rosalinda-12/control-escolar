package modelo;

public class Calificacion
{
    private int idCalificacion;
    private int idInscripcionMateria;
    private Double parcial1;
    private Double parcial2;
    private Double parcial3;
    private Double promedioFinal;

    // Campos de solo lectura (vienen del JOIN) para mostrar la lista de
    // alumnos a capturar sin tener que hacer consultas aparte.
    private String matricula;
    private String nombreAlumno;
    private String estadoMateria;
    private int intento;

    // Campos de solo lectura adicionales (vienen del JOIN) para la boleta
    // de calificaciones que ve el propio alumno, agrupada por cuatrimestre.
    private String nombreMateria;
    private int numeroCuatrimestre;
    private String nombrePeriodo;
    private String nombreGrupo;

    // Campos de solo lectura adicionales (vienen del JOIN) para la pantalla
    // de calificaciones de Administrador/Control Escolar/Subdirector, que
    // muestra todas las carreras (o solo la propia, en el caso del
    // Subdirector) en lugar de un solo grupo-materia.
    private int idCarrera;
    private String nombreCarrera;

    public int getIdCalificacion()
    {
        return idCalificacion;
    }

    public void setIdCalificacion(int idCalificacion)
    {
        this.idCalificacion = idCalificacion;
    }

    public int getIdInscripcionMateria()
    {
        return idInscripcionMateria;
    }

    public void setIdInscripcionMateria(int idInscripcionMateria)
    {
        this.idInscripcionMateria = idInscripcionMateria;
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

    public String getEstadoMateria()
    {
        return estadoMateria;
    }

    public void setEstadoMateria(String estadoMateria)
    {
        this.estadoMateria = estadoMateria;
    }

    public int getIntento()
    {
        return intento;
    }

    public void setIntento(int intento)
    {
        this.intento = intento;
    }

    public String getNombreMateria()
    {
        return nombreMateria;
    }

    public void setNombreMateria(String nombreMateria)
    {
        this.nombreMateria = nombreMateria;
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

    /**
     * Devuelve el valor capturado del parcial indicado (1, 2 o 3), o null si
     * ese número no es válido o aún no se ha capturado.
     */
    public Double getParcial(int numero)
    {
        switch (numero)
        {
            case 1:
                return parcial1;
            case 2:
                return parcial2;
            case 3:
                return parcial3;
            default:
                return null;
        }
    }
}
