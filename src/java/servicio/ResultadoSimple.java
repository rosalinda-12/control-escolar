package servicio;



public class ResultadoSimple
{
    private final boolean exito;
    private final String mensajeError;
    private final int idGenerado;

    private ResultadoSimple(boolean exito, String mensajeError, int idGenerado)
    {
        this.exito = exito;
        this.mensajeError = mensajeError;
        this.idGenerado = idGenerado;
    }

    public static ResultadoSimple exito(int idGenerado)
    {
        return new ResultadoSimple(true, null, idGenerado);
    }

    public static ResultadoSimple fallo(String mensajeError)
    {
        return new ResultadoSimple(false, mensajeError, 0);
    }

    public boolean isExito()
    {
        return exito;
    }

    public String getMensajeError()
    {
        return mensajeError;
    }

    public int getIdGenerado()
    {
        return idGenerado;
    }
}
