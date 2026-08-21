package doa;

import conexion.ConexionMySQL;
import modelo.Subdirector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
                Subdirector subdirector = construirSubdirector(resultado);
                cargarCarreras(subdirector);
                lista.add(subdirector);
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
                    Subdirector subdirector = construirSubdirector(resultado);
                    cargarCarreras(subdirector);
                    return subdirector;
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
                    Subdirector subdirector = construirSubdirector(resultado);
                    cargarCarreras(subdirector);
                    return subdirector;
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

    public void actualizarCarreras(int idSubdirector, List<Integer> idsCarrera)
    {
        String borrar = "DELETE FROM subdirector_carreras WHERE id_subdirector = ?";
        String insertar = "INSERT INTO subdirector_carreras (id_subdirector, id_carrera) VALUES (?, ?)";
        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement eliminar = conexion.prepareStatement(borrar);
             PreparedStatement agregar = conexion.prepareStatement(insertar))
        {
            conexion.setAutoCommit(false);
            eliminar.setInt(1, idSubdirector);
            eliminar.executeUpdate();
            for (Integer idCarrera : idsCarrera)
            {
                agregar.setInt(1, idSubdirector);
                agregar.setInt(2, idCarrera);
                agregar.addBatch();
            }
            agregar.executeBatch();
            conexion.commit();
        }
        catch (SQLException excepcion)
        { throw new RuntimeException(excepcion); }
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

    private void cargarCarreras(Subdirector subdirector)
    {
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<String> nombres = new ArrayList<>();
        String sql = "SELECT sc.id_carrera, c.nombre_carrera FROM subdirector_carreras sc "
                + "JOIN carreras c ON c.id_carrera = sc.id_carrera WHERE sc.id_subdirector = ? ORDER BY c.nombre_carrera";
        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, subdirector.getIdSubdirector());
            try (ResultSet resultado = sentencia.executeQuery())
            {
                while (resultado.next())
                { ids.add(resultado.getInt("id_carrera")); nombres.add(resultado.getString("nombre_carrera")); }
            }
            if (!ids.isEmpty())
            { subdirector.setIdsCarrera(ids); subdirector.setNombresCarrera(nombres); }
        }
        catch (SQLException excepcion)
        { throw new RuntimeException(excepcion); }
    }
}