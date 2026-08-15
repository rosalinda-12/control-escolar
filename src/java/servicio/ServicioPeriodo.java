package servicio;

import doa.DAOConfiguracionParcial;
import doa.DAOPeriodo;
import modelo.Periodo;
import modelo.Usuario;
import java.util.ArrayList;

public class ServicioPeriodo
{
    private final DAOPeriodo daoPeriodo;
    private final DAOConfiguracionParcial daoConfiguracionParcial;
    private final ServicioBitacora servicioBitacora;

    public ServicioPeriodo()
    {
        this.daoPeriodo = new DAOPeriodo();
        this.daoConfiguracionParcial = new DAOConfiguracionParcial();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<Periodo> listar()
    {
        return daoPeriodo.listar();
    }

    public Periodo buscarPorId(int idPeriodo)
    {
        return daoPeriodo.buscarPorId(idPeriodo);
    }

    public ResultadoSimple agregar(Periodo periodo, Usuario responsable)
    {
        if (!periodo.getFechaInicio().isBefore(periodo.getFechaFin()))
        {
            return ResultadoSimple.fallo("La fecha de apertura debe ser anterior a la fecha de cierre.");
        }

        if (daoPeriodo.existeNombre(periodo.getNombrePeriodo(), null))
        {
            return ResultadoSimple.fallo("Ya existe un periodo con ese nombre.");
        }

        int idPeriodo = daoPeriodo.agregar(periodo);
        daoConfiguracionParcial.crearParaPeriodo(idPeriodo);
        servicioBitacora.registrarAlta(responsable, "periodos_escolares", idPeriodo,
                "Alta de periodo " + periodo.getNombrePeriodo());

        return ResultadoSimple.exito(idPeriodo);
    }

    public ResultadoSimple modificar(Periodo periodo, Usuario responsable)
    {
        if (!periodo.getFechaInicio().isBefore(periodo.getFechaFin()))
        {
            return ResultadoSimple.fallo("La fecha de apertura debe ser anterior a la fecha de cierre.");
        }

        if (daoPeriodo.existeNombre(periodo.getNombrePeriodo(), periodo.getIdPeriodo()))
        {
            return ResultadoSimple.fallo("Ya existe otro periodo con ese nombre.");
        }

        daoPeriodo.modificar(periodo);
        servicioBitacora.registrarAlta(responsable, "periodos_escolares", periodo.getIdPeriodo(),
                "Modificó el periodo " + periodo.getNombrePeriodo());

        return ResultadoSimple.exito(periodo.getIdPeriodo());
    }

    public void eliminar(int idPeriodo, Usuario responsable)
    {
        Periodo periodo = daoPeriodo.buscarPorId(idPeriodo);
        daoPeriodo.eliminar(idPeriodo);
        servicioBitacora.registrarBaja(responsable, "periodos_escolares", idPeriodo,
                "Baja de periodo " + (periodo != null ? periodo.getNombrePeriodo() : idPeriodo));
    }
}
