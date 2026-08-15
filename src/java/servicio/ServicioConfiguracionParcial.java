package servicio;

import doa.DAOConfiguracionParcial;
import modelo.ConfiguracionParcial;
import modelo.Usuario;
import java.util.ArrayList;

public class ServicioConfiguracionParcial
{
    private final DAOConfiguracionParcial daoConfiguracionParcial;
    private final ServicioBitacora servicioBitacora;

    public ServicioConfiguracionParcial()
    {
        this.daoConfiguracionParcial = new DAOConfiguracionParcial();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<ConfiguracionParcial> listar()
    {
        return daoConfiguracionParcial.listar();
    }

    public ConfiguracionParcial buscarPorPeriodo(int idPeriodo)
    {
        return daoConfiguracionParcial.buscarPorPeriodo(idPeriodo);
    }

    public ResultadoConfiguracion habilitarParcial(int idPeriodo, int parcialActivo, Usuario responsable)
    {
        if (parcialActivo < 1 || parcialActivo > 3)
        {
            return ResultadoConfiguracion.fallo("El parcial activo debe ser 1, 2 o 3.");
        }

        daoConfiguracionParcial.actualizarParcialActivo(idPeriodo, parcialActivo);
        servicioBitacora.registrarAlta(responsable, "configuracion_parcial", idPeriodo,
                "Activó el parcial " + parcialActivo + " para el periodo " + idPeriodo);

        return ResultadoConfiguracion.exito();
    }

    public static class ResultadoConfiguracion
    {
        private final boolean exito;
        private final String mensajeError;

        private ResultadoConfiguracion(boolean exito, String mensajeError)
        {
            this.exito = exito;
            this.mensajeError = mensajeError;
        }

        public static ResultadoConfiguracion exito()
        {
            return new ResultadoConfiguracion(true, null);
        }

        public static ResultadoConfiguracion fallo(String mensajeError)
        {
            return new ResultadoConfiguracion(false, mensajeError);
        }

        public boolean isExito()
        {
            return exito;
        }

        public String getMensajeError()
        {
            return mensajeError;
        }
    }
}
