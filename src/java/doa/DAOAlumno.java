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
            "SELECT a.id_alumno, a.id_persona, p.nombres, p.apellido_paterno, p.apellido_materno, p.correo, a.estatus, "
            + "COALESCE((SELECT c.nombre_carrera FROM trayectorias_academicas t "
            + "JOIN planes_estudio pe ON t.id_plan = pe.id_plan JOIN carreras c ON pe.id_carrera = c.id_carrera "
            + "WHERE t.id_alumno = a.id_alumno ORDER BY (t.estado = 'ACTIVA') DESC, t.fecha_inicio DESC LIMIT 1), 'Sin carrera') AS nombre_carrera, "
            + "COALESCE((SELECT g.nombre_grupo FROM inscripciones i JOIN grupos g ON i.id_grupo = g.id_grupo "
            + "JOIN trayectorias_academicas t ON i.id_trayectoria = t.id_trayectoria "
            + "WHERE t.id_alumno = a.id_alumno AND i.estado = 'Activa' ORDER BY i.fecha_inscripcion DESC LIMIT 1), 'Sin grupo') AS nombre_grupo "
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

    public void actualizar(Alumno alumno)
    {
        String sql = "UPDATE personas SET nombres = ?, apellido_paterno = ?, apellido_materno = ?, correo = ? WHERE id_persona = ?";
        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, alumno.getNombres());
            sentencia.setString(2, alumno.getApellidoPaterno());
            sentencia.setString(3, alumno.getApellidoMaterno());
            sentencia.setString(4, alumno.getCorreo());
            sentencia.setInt(5, alumno.getIdPersona());
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
        alumno.setNombreCarrera(resultado.getString("nombre_carrera"));
        alumno.setNombreGrupo(resultado.getString("nombre_grupo"));
        return alumno;
    }
}
