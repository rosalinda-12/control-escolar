package doa;

import conexion.ConexionMySQL;
import modelo.ConfiguracionParcial;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class DAOConfiguracionParcial
{
    private static final String SELECCION_BASE =
            "SELECT c.id_configuracion, c.id_periodo, c.parcial_activo, c.fecha_actualizacion, p.nombre_periodo "
            + "FROM configuracion_parciales c JOIN periodos_escolares p ON c.id_periodo = p.id_periodo ";

    public ArrayList<ConfiguracionParcial> listar()
    {
        ArrayList<ConfiguracionParcial> lista = new ArrayList<>();
        crearParaPeriodosSinConfiguracion();
        String sql = SELECCION_BASE + "ORDER BY p.fecha_inicio DESC";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirConfiguracion(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public ConfiguracionParcial buscarPorPeriodo(int idPeriodo)
    {
        String sql = SELECCION_BASE + "WHERE c.id_periodo = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPeriodo);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirConfiguracion(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public void crearParaPeriodo(int idPeriodo)
    {
        String sql = "INSERT INTO configuracion_parciales (id_periodo, parcial_activo) VALUES (?, 1)";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPeriodo);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void actualizarParcialActivo(int idPeriodo, int parcialActivo)
    {
        String sql = "INSERT INTO configuracion_parciales (id_periodo, parcial_activo, fecha_actualizacion) VALUES (?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE parcial_activo = VALUES(parcial_activo), fecha_actualizacion = VALUES(fecha_actualizacion)";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPeriodo);
            sentencia.setInt(2, parcialActivo);
            sentencia.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    private void crearParaPeriodosSinConfiguracion()
    {
        String sql = "INSERT INTO configuracion_parciales (id_periodo, parcial_activo) "
                + "SELECT p.id_periodo, 1 FROM periodos_escolares p "
                + "LEFT JOIN configuracion_parciales c ON c.id_periodo = p.id_periodo "
                + "WHERE c.id_periodo IS NULL";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    private ConfiguracionParcial construirConfiguracion(ResultSet resultado) throws SQLException
    {
        ConfiguracionParcial configuracion = new ConfiguracionParcial();
        configuracion.setIdConfiguracion(resultado.getInt("id_configuracion"));
        configuracion.setIdPeriodo(resultado.getInt("id_periodo"));
        configuracion.setParcialActivo(resultado.getInt("parcial_activo"));
        configuracion.setFechaActualizacion(resultado.getTimestamp("fecha_actualizacion").toLocalDateTime());
        configuracion.setNombrePeriodo(resultado.getString("nombre_periodo"));
        return configuracion;
    }
}
