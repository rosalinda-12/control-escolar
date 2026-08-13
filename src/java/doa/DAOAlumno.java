package doa;

import conexion.ConexionMySQL;
import modelo.Alumno;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOAlumno
{
    private static final String SELECCION_BASE =
            "SELECT a.id_alumno, a.id_persona, p.nombres, p.apellido_paterno, p.apellido_materno, p.correo, a.estatus "
            + "FROM alumnos a JOIN personas p ON a.id_persona = p.id_persona ";

    public ArrayList<Alumno> listar()
    {
        ArrayList<Alumno> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "ORDER BY p.apellido_paterno, p.nombres";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirAlumno(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public Alumno buscarPorId(int idAlumno)
    {
        String sql = SELECCION_BASE + "WHERE a.id_alumno = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idAlumno);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirAlumno(resultado);
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
        String sql = "INSERT INTO alumnos (id_persona, estatus) VALUES (?, 'Activo')";

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

    public void actualizarEstatus(int idAlumno, String estatus)
    {
        String sql = "UPDATE alumnos SET estatus = ? WHERE id_alumno = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, estatus);
            sentencia.setInt(2, idAlumno);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    private Alumno construirAlumno(ResultSet resultado) throws SQLException
    {
        Alumno alumno = new Alumno();
        alumno.setIdAlumno(resultado.getInt("id_alumno"));
        alumno.setIdPersona(resultado.getInt("id_persona"));
        alumno.setNombres(resultado.getString("nombres"));
        alumno.setApellidoPaterno(resultado.getString("apellido_paterno"));
        alumno.setApellidoMaterno(resultado.getString("apellido_materno"));
        alumno.setCorreo(resultado.getString("correo"));
        alumno.setEstatus(resultado.getString("estatus"));
        return alumno;
    }
}
