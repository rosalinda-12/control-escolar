package modelo;

import java.util.ArrayList;
import java.util.List;

public class Subdirector
{
    private int idSubdirector;
    private int idPersona;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correo;
    private int idCarrera;
    private String nombreCarrera;
    private String estatus;
    private List<Integer> idsCarrera = new ArrayList<>();
    private List<String> nombresCarrera = new ArrayList<>();

    public int getIdSubdirector()
    {
        return idSubdirector;
    }

    public void setIdSubdirector(int idSubdirector)
    {
        this.idSubdirector = idSubdirector;
    }

    public int getIdPersona()
    {
        return idPersona;
    }

    public void setIdPersona(int idPersona)
    {
        this.idPersona = idPersona;
    }

    public String getNombres()
    {
        return nombres;
    }

    public void setNombres(String nombres)
    {
        this.nombres = nombres;
    }

    public String getApellidoPaterno()
    {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno)
    {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno()
    {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno)
    {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getCorreo()
    {
        return correo;
    }

    public void setCorreo(String correo)
    {
        this.correo = correo;
    }

    public int getIdCarrera()
    {
        return idCarrera;
    }

    public void setIdCarrera(int idCarrera)
    {
        this.idCarrera = idCarrera;
        if (!idsCarrera.contains(idCarrera)) idsCarrera.add(idCarrera);
    }

    public String getNombreCarrera()
    {
        return nombreCarrera;
    }

    public void setNombreCarrera(String nombreCarrera)
    {
        this.nombreCarrera = nombreCarrera;
    }

    public List<Integer> getIdsCarrera()
    { return idsCarrera; }
    public void setIdsCarrera(List<Integer> idsCarrera)
    { this.idsCarrera = idsCarrera == null ? new ArrayList<>() : new ArrayList<>(idsCarrera); }
    public List<String> getNombresCarrera()
    { return nombresCarrera; }
    public void setNombresCarrera(List<String> nombresCarrera)
    { this.nombresCarrera = nombresCarrera == null ? new ArrayList<>() : new ArrayList<>(nombresCarrera); }

    public String getEstatus()
    {
        return estatus;
    }

    public void setEstatus(String estatus)
    {
        this.estatus = estatus;
    }

    public String getNombreCompleto()
    {
        return nombres + " " + apellidoPaterno + " " + apellidoMaterno;
    }
}
