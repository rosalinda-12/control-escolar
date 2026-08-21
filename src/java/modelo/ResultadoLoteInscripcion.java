package modelo;

public class ResultadoLoteInscripcion
{
    private int procesados;
    private int exitosos;
    private int rechazados;
    private StringBuilder errores = new StringBuilder();

    public void registrarExito()
    { exitosos++; procesados++; }
    public void registrarError(String mensaje)
    { rechazados++; procesados++; if (errores.length() > 0) errores.append(" | "); errores.append(mensaje); }
    public int getProcesados()
    { return procesados; }
    public int getExitosos()
    { return exitosos; }
    public int getRechazados()
    { return rechazados; }
    public String getErrores()
    { return errores.toString(); }
}
