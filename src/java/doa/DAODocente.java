package doa;

import conexion.ConexionMySQL;
import modelo.Docente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAODocente
{
    private static final String SELECCION_BASE =
            "SELECT d.id_docente, d.id_persona, p.nombres, p.apellido_paterno, p.apellido_materno, p.correo, d.estatus "
            + "FROM docentes d JOIN personas p ON d.id_persona = p.id_persona ";

    public ArrayList<Docente> listar()
    {
        ArrayList<Docente> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "ORDER BY p.apellido_paterno, p.nombres";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirDocente(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public ArrayList<Docente> listarActivos()
    {
        ArrayList<Docente> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "WHERE d.estatus = 'Activo' ORDER BY p.apellido_paterno, p.nombres";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirDocente(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public Docente buscarPorCorreo(String correo)
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
                    return construirDocente(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public Docente buscarPorId(int idDocente)
    {
        String sql = SELECCION_BASE + "WHERE d.id_docente = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idDocente);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirDocente(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public int agregar(int idPersona)
    {
        String sql = "INSERT INTO docentes (id_persona, estatus) VALUES (?, 'Activo')";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            sentencia.setInt(1, idPersona);
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

    public void actualizarEstatus(int idDocente, String estatus)
    {
        String sql = "UPDATE docentes SET estatus = ? WHERE id_docente = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, estatus);
            sentencia.setInt(2, idDocente);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public boolean tieneAsignacionesActivas(int idDocente)
    {
        String sql = "SELECT COUNT(*) FROM docentes_asignaciones WHERE id_docente = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idDocente);

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

    private Docente construirDocente(ResultSet resultado) throws SQLException
    {
        Docente docente = new Docente();
        docente.setIdDocente(resultado.getInt("id_docente"));
        docente.setIdPersona(resultado.getInt("id_persona"));
        docente.setNombres(resultado.getString("nombres"));
        docente.setApellidoPaterno(resultado.getString("apellido_paterno"));
        docente.setApellidoMaterno(resultado.getString("apellido_materno"));
        docente.setCorreo(resultado.getString("correo"));
        docente.setEstatus(resultado.getString("estatus"));
        return docente;
    }
}
