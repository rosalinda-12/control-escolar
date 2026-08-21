package modelo;

public class Calificacion
{
    private int idCalificacion;
    private int idInscripcionMateria;
    private Double parcial1;
    private Double parcial2;
    private Double parcial3;
    private Double promedioFinal;


    private String matricula;
    private String nombreAlumno;
    private int idAlumno;
    private String estadoMateria;
    private int intento;


    private String nombreMateria;
    private int numeroCuatrimestre;
    private String nombrePeriodo;
    private String nombreGrupo;
    private int idTrayectoria;
    private String nombreNivel;


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

    public int getIdAlumno()
    {
        return idAlumno;
    }

    public void setIdAlumno(int idAlumno)
    {
        this.idAlumno = idAlumno;
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

    public int getIdTrayectoria()
    {
        return idTrayectoria;
    }

    public void setIdTrayectoria(int idTrayectoria)
    {
        this.idTrayectoria = idTrayectoria;
    }

    public String getNombreNivel()
    {
        return nombreNivel;
    }

    public void setNombreNivel(String nombreNivel)
    {
        this.nombreNivel = nombreNivel;
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
