package doa;

import conexion.ConexionMySQL;
import modelo.Periodo;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOPeriodo
{
    private static final String SELECCION_BASE =
            "SELECT p.id_periodo, p.id_ciclo, p.nombre_periodo, p.fecha_inicio, p.fecha_fin, p.estatus, c.nombre_ciclo "
            + "FROM periodos_escolares p JOIN ciclos_escolares c ON p.id_ciclo = c.id_ciclo ";

    public ArrayList<Periodo> listar()
    {
        ArrayList<Periodo> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "ORDER BY p.fecha_inicio DESC";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirPeriodo(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public Periodo buscarPorId(int idPeriodo)
    {
        String sql = SELECCION_BASE + "WHERE p.id_periodo = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPeriodo);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirPeriodo(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public boolean existeNombre(String nombrePeriodo, Integer idPeriodoExcluir)
    {
        String sql = "SELECT COUNT(*) FROM periodos_escolares WHERE nombre_periodo = ?"
                + (idPeriodoExcluir != null ? " AND id_periodo <> ?" : "");

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, nombrePeriodo);
            if (idPeriodoExcluir != null)
            {
                sentencia.setInt(2, idPeriodoExcluir);
            }

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return resultado.getInt(1) > 0;
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return false;
    }

    public int agregar(Periodo periodo)
    {
        String sql = "INSERT INTO periodos_escolares (id_ciclo, nombre_periodo, fecha_inicio, fecha_fin, estatus) VALUES (?, ?, ?, ?, 'Activo')";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            sentencia.setInt(1, periodo.getIdCiclo());
            sentencia.setString(2, periodo.getNombrePeriodo());
            sentencia.setDate(3, Date.valueOf(periodo.getFechaInicio()));
            sentencia.setDate(4, Date.valueOf(periodo.getFechaFin()));
            sentencia.executeUpdate();

            try (ResultSet llaves = sentencia.getGeneratedKeys())
            {
                if (llaves.next())
                {
                    return llaves.getInt(1);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return 0;
    }

    public void modificar(Periodo periodo)
    {
        String sql = "UPDATE periodos_escolares SET id_ciclo = ?, nombre_periodo = ?, fecha_inicio = ?, fecha_fin = ?, estatus = ? WHERE id_periodo = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, periodo.getIdCiclo());
            sentencia.setString(2, periodo.getNombrePeriodo());
            sentencia.setDate(3, Date.valueOf(periodo.getFechaInicio()));
            sentencia.setDate(4, Date.valueOf(periodo.getFechaFin()));
            sentencia.setString(5, periodo.getEstatus());
            sentencia.setInt(6, periodo.getIdPeriodo());
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void eliminar(int idPeriodo)
    {
        String sql = "DELETE FROM periodos_escolares WHERE id_periodo = ?";

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

    private Periodo construirPeriodo(ResultSet resultado) throws SQLException
    {
        Periodo periodo = new Periodo();
        periodo.setIdPeriodo(resultado.getInt("id_periodo"));
        periodo.setIdCiclo(resultado.getInt("id_ciclo"));
        periodo.setNombreCiclo(resultado.getString("nombre_ciclo"));
        periodo.setNombrePeriodo(resultado.getString("nombre_periodo"));
        periodo.setFechaInicio(resultado.getDate("fecha_inicio").toLocalDate());
        periodo.setFechaFin(resultado.getDate("fecha_fin").toLocalDate());
        periodo.setEstatus(resultado.getString("estatus"));
        return periodo;
    }
}
