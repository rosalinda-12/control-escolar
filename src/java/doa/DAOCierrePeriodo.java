package doa;

import conexion.ConexionMySQL;
import modelo.ResumenCierrePeriodo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DAOCierrePeriodo
{
    public ResumenCierrePeriodo consultar(int idPeriodo)
    {
        try (Connection conexion = ConexionMySQL.obtenerConexion())
        {
            return consultar(conexion, idPeriodo);
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public ResumenCierrePeriodo consultar(Connection conexion, int idPeriodo) throws SQLException
    {
        ResumenCierrePeriodo resumen = new ResumenCierrePeriodo();
        resumen.setIdPeriodo(idPeriodo);
        String periodoSql = "SELECT nombre_periodo, estatus FROM periodos_escolares WHERE id_periodo = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(periodoSql))
        {
            sentencia.setInt(1, idPeriodo);
            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (!resultado.next()) return null;
                resumen.setNombrePeriodo(resultado.getString("nombre_periodo"));
                resumen.setEstatusPeriodo(resultado.getString("estatus"));
            }
        }
        resumen.setGruposActivos(contar(conexion, "SELECT COUNT(*) FROM grupos WHERE id_periodo = ? AND estatus = 'Activo'", idPeriodo));
        resumen.setInscripcionesActivas(contar(conexion, "SELECT COUNT(*) FROM inscripciones WHERE id_periodo = ? AND estado = 'Activa'", idPeriodo));
        resumen.setMateriasPendientes(contar(conexion,
                "SELECT COUNT(*) FROM inscripcion_materias im "
                + "JOIN inscripciones i ON im.id_inscripcion = i.id_inscripcion "
                + "WHERE i.id_periodo = ? AND i.estado = 'Activa' AND im.estado = 'Cursando'", idPeriodo));
        return resumen;
    }

    public void cerrar(Connection conexion, int idPeriodo) throws SQLException
    {
        try (PreparedStatement grupos = conexion.prepareStatement("UPDATE grupos SET estatus = 'Cerrado' WHERE id_periodo = ? AND estatus = 'Activo'"))
        {
            grupos.setInt(1, idPeriodo);
            grupos.executeUpdate();
        }
        try (PreparedStatement periodo = conexion.prepareStatement("UPDATE periodos_escolares SET estatus = 'Cerrado' WHERE id_periodo = ? AND estatus = 'Activo'"))
        {
            periodo.setInt(1, idPeriodo);
            periodo.executeUpdate();
        }
    }

    private int contar(Connection conexion, String sql, int idPeriodo) throws SQLException
    {
        try (PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPeriodo);
            try (ResultSet resultado = sentencia.executeQuery())
            {
                return resultado.next() ? resultado.getInt(1) : 0;
            }
        }
    }
}
