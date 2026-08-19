package doa;

import conexion.ConexionMySQL;
import modelo.Subdirector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOSubdirector
{
    private static final String SELECCION_BASE =
            "SELECT s.id_subdirector, s.id_persona, p.nombres, p.apellido_paterno, p.apellido_materno, p.correo, "
            + "s.id_carrera, c.nombre_carrera, s.estatus "
            + "FROM subdirectores s "
            + "JOIN personas p ON s.id_persona = p.id_persona "
            + "JOIN carreras c ON s.id_carrera = c.id_carrera ";

    public ArrayList<Subdirector> listar()
    {
        ArrayList<Subdirector> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "ORDER BY p.apellido_paterno, p.nombres";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirSubdirector(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public Subdirector buscarPorCorreo(String correo)
    {
        String sql = SELECCION_BASE + "WHERE p.correo = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, correo);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirSubdirector(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public Subdirector buscarPorId(int idSubdirector)
    {
        String sql = SELECCION_BASE + "WHERE s.id_subdirector = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idSubdirector);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirSubdirector(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public int agregar(int idPersona, int idCarrera)
    {
        String sql = "INSERT INTO subdirectores (id_persona, id_carrera, estatus) VALUES (?, ?, 'Activo')";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            sentencia.setInt(1, idPersona);
            sentencia.setInt(2, idCarrera);
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

    public void actualizarCarrera(int idSubdirector, int idCarrera)
    {
        String sql = "UPDATE subdirectores SET id_carrera = ? WHERE id_subdirector = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idCarrera);
            sentencia.setInt(2, idSubdirector);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void actualizarEstatus(int idSubdirector, String estatus)
    {
        String sql = "UPDATE subdirectores SET estatus = ? WHERE id_subdirector = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, estatus);
            sentencia.setInt(2, idSubdirector);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    private Subdirector construirSubdirector(ResultSet resultado) throws SQLException
    {
        Subdirector subdirector = new Subdirector();
        subdirector.setIdSubdirector(resultado.getInt("id_subdirector"));
        subdirector.setIdPersona(resultado.getInt("id_persona"));
        subdirector.setNombres(resultado.getString("nombres"));
        subdirector.setApellidoPaterno(resultado.getString("apellido_paterno"));
        subdirector.setApellidoMaterno(resultado.getString("apellido_materno"));
        subdirector.setCorreo(resultado.getString("correo"));
        subdirector.setIdCarrera(resultado.getInt("id_carrera"));
        subdirector.setNombreCarrera(resultado.getString("nombre_carrera"));
        subdirector.setEstatus(resultado.getString("estatus"));
        return subdirector;
    }
}