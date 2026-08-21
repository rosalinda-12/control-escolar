package modelo;

public class ResumenCierrePeriodo
{
    private int idPeriodo;
    private String nombrePeriodo;
    private String estatusPeriodo;
    private int gruposActivos;
    private int inscripcionesActivas;
    private int materiasPendientes;

    public int getIdPeriodo()
    { return idPeriodo; }
    public void setIdPeriodo(int idPeriodo)
    { this.idPeriodo = idPeriodo; }
    public String getNombrePeriodo()
    { return nombrePeriodo; }
    public void setNombrePeriodo(String nombrePeriodo)
    { this.nombrePeriodo = nombrePeriodo; }
    public String getEstatusPeriodo()
    { return estatusPeriodo; }
    public void setEstatusPeriodo(String estatusPeriodo)
    { this.estatusPeriodo = estatusPeriodo; }
    public int getGruposActivos()
    { return gruposActivos; }
    public void setGruposActivos(int gruposActivos)
    { this.gruposActivos = gruposActivos; }
    public int getInscripcionesActivas()
    { return inscripcionesActivas; }
    public void setInscripcionesActivas(int inscripcionesActivas)
    { this.inscripcionesActivas = inscripcionesActivas; }
    public int getMateriasPendientes()
    { return materiasPendientes; }
    public void setMateriasPendientes(int materiasPendientes)
    { this.materiasPendientes = materiasPendientes; }
    public boolean isListoParaCerrar()
    { return materiasPendientes == 0; }
}
