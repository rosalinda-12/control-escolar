package doa;

import conexion.ConexionMySQL;
import modelo.Carrera;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOCarrera
{
    public ArrayList<Carrera> listar()
    {
        ArrayList<Carrera> lista = new ArrayList<>();
        String sql = "SELECT id_carrera, nombre_carrera, clave_carrera, estatus FROM carreras ORDER BY nombre_carrera";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirCarrera(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public ArrayList<Carrera> listarActivas()
    {
        ArrayList<Carrera> lista = new ArrayList<>();
        String sql = "SELECT id_carrera, nombre_carrera, clave_carrera, estatus FROM carreras WHERE estatus = 'Activa' ORDER BY nombre_carrera";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirCarrera(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public Carrera buscarPorId(int idCarrera)
    {
        String sql = "SELECT id_carrera, nombre_carrera, clave_carrera, estatus FROM carreras WHERE id_carrera = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idCarrera);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirCarrera(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public boolean existeNombreOClave(String nombreCarrera, String claveCarrera, Integer idCarreraExcluir)
    {
        String sql = "SELECT COUNT(*) FROM carreras WHERE (nombre_carrera = ? OR clave_carrera = ?)"
                + (idCarreraExcluir != null ? " AND id_carrera <> ?" : "");

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, nombreCarrera);
            sentencia.setString(2, claveCarrera);
            if (idCarreraExcluir != null)
            {
                sentencia.setInt(3, idCarreraExcluir);
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

    public boolean tienePlanesAsociados(int idCarrera)
    {
        String sql = "SELECT COUNT(*) FROM planes_estudio WHERE id_carrera = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idCarrera);

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

    public int agregar(Carrera carrera)
    {
        String sql = "INSERT INTO carreras (nombre_carrera, clave_carrera, estatus) VALUES (?, ?, ?)";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            sentencia.setString(1, carrera.getNombreCarrera());
            sentencia.setString(2, carrera.getClaveCarrera());
            sentencia.setString(3, carrera.getEstatus());
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

    public void modificar(Carrera carrera)
    {
        String sql = "UPDATE carreras SET nombre_carrera = ?, clave_carrera = ?, estatus = ? WHERE id_carrera = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, carrera.getNombreCarrera());
            sentencia.setString(2, carrera.getClaveCarrera());
            sentencia.setString(3, carrera.getEstatus());
            sentencia.setInt(4, carrera.getIdCarrera());
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void eliminar(int idCarrera)
    {
        String sql = "DELETE FROM carreras WHERE id_carrera = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idCarrera);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    private Carrera construirCarrera(ResultSet resultado) throws SQLException
    {
        Carrera carrera = new Carrera();
        carrera.setIdCarrera(resultado.getInt("id_carrera"));
        carrera.setNombreCarrera(resultado.getString("nombre_carrera"));
        carrera.setClaveCarrera(resultado.getString("clave_carrera"));
        carrera.setEstatus(resultado.getString("estatus"));
        return carrera;
    }
}
