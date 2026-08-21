package servicio;

import conexion.ConexionMySQL;
import doa.DAOCierrePeriodo;
import modelo.ResumenCierrePeriodo;
import modelo.Usuario;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import modelo.Periodo;
import doa.DAOTrayectoriaAcademica;

public class ServicioCierrePeriodo
{
    private final DAOCierrePeriodo daoCierre;
    private final ServicioBitacora bitacora;

    public ServicioCierrePeriodo()
    {
        this.daoCierre = new DAOCierrePeriodo();
        this.bitacora = new ServicioBitacora();
    }

    public ResumenCierrePeriodo consultar(int idPeriodo)
    {
        return daoCierre.consultar(idPeriodo);
    }

    public ResultadoSimple cerrar(int idPeriodo, Usuario responsable)
    {
        try (Connection conexion = ConexionMySQL.obtenerConexion())
        {
            conexion.setAutoCommit(false);
            try
            {
                ResumenCierrePeriodo resumen = daoCierre.consultar(conexion, idPeriodo);
                if (resumen == null) return ResultadoSimple.fallo("El periodo ya no existe.");
                if (!"Activo".equals(resumen.getEstatusPeriodo())) return ResultadoSimple.fallo("El periodo ya está cerrado.");
                if (!resumen.isListoParaCerrar())
                {
                    return ResultadoSimple.fallo("No se puede cerrar: todavía hay " + resumen.getMateriasPendientes() + " materia(s) sin calificación final.");
                }
                daoCierre.cerrar(conexion, idPeriodo);
                conexion.commit();
                ServicioCalificacion servicioCalificacion = new ServicioCalificacion();
                DAOTrayectoriaAcademica daoTrayectoria = new DAOTrayectoriaAcademica();
                for (Integer idAlumno : daoTrayectoria.listarAlumnosPorPeriodo(idPeriodo))
                {
                    servicioCalificacion.finalizarEgresoSiCorresponde(idAlumno);
                }
                for (modelo.TrayectoriaAcademica trayectoria : daoTrayectoria.listar())
                {
                    if ("ACTIVA".equals(trayectoria.getEstado())
                            && daoTrayectoria.estaListaParaEgreso(trayectoria.getIdTrayectoria()))
                    {
                        servicioCalificacion.finalizarEgresoSiCorresponde(trayectoria.getIdAlumno());
                    }
                }
                bitacora.registrarBaja(responsable, "periodos_escolares", idPeriodo,
                        "Cierre automático del periodo " + resumen.getNombrePeriodo()
                        + ": " + resumen.getGruposActivos() + " grupos cerrados");
                return ResultadoSimple.exito(idPeriodo);
            }
            catch (SQLException | RuntimeException excepcion)
            {
                conexion.rollback();
                throw excepcion;
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public int cerrarVencidos(Usuario responsable, ArrayList<Periodo> periodos)
    {
        int cerrados = 0;
        for (Periodo periodo : periodos)
        {
            if ("Activo".equals(periodo.getEstatus()) && periodo.getFechaFin().isBefore(LocalDate.now()))
            {
                ResultadoSimple resultado = cerrar(periodo.getIdPeriodo(), responsable);
                if (resultado.isExito()) cerrados++;
            }
        }
        return cerrados;
    }
}
