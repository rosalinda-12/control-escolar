package doa;

import conexion.ConexionMySQL;
import modelo.Generacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOGeneracion
{
    public ArrayList<Generacion> listar()
    {
        ArrayList<Generacion> lista = new ArrayList<>();
        String sql = "SELECT id_generacion, nombre_generacion, estatus FROM generaciones ORDER BY nombre_generacion";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirGeneracion(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public Generacion buscarPorId(int idGeneracion)
    {
        String sql = "SELECT id_generacion, nombre_generacion, estatus FROM generaciones WHERE id_generacion = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idGeneracion);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirGeneracion(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public int agregar(Generacion generacion)
    {
        String sql = "INSERT INTO generaciones (nombre_generacion, estatus) VALUES (?, ?)";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            sentencia.setString(1, generacion.getNombreGeneracion());
            sentencia.setString(2, generacion.getEstatus());
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

    public void modificar(Generacion generacion)
    {
        String sql = "UPDATE generaciones SET nombre_generacion = ?, estatus = ? WHERE id_generacion = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, generacion.getNombreGeneracion());
            sentencia.setString(2, generacion.getEstatus());
            sentencia.setInt(3, generacion.getIdGeneracion());
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void eliminar(int idGeneracion)
    {
        String sql = "DELETE FROM generaciones WHERE id_generacion = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idGeneracion);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    private Generacion construirGeneracion(ResultSet resultado) throws SQLException
    {
        Generacion generacion = new Generacion();
        generacion.setIdGeneracion(resultado.getInt("id_generacion"));
        generacion.setNombreGeneracion(resultado.getString("nombre_generacion"));
        generacion.setEstatus(resultado.getString("estatus"));
        return generacion;
    }
}
