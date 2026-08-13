package doa;

import conexion.ConexionMySQL;
import modelo.Inscripcion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOInscripcion
{
    private static final String SELECCION_BASE =
            "SELECT i.id_inscripcion, i.id_trayectoria, t.matricula, "
            + "CONCAT(p.nombres, ' ', p.apellido_paterno, ' ', p.apellido_materno) AS nombre_alumno, "
            + "i.id_grupo, g.nombre_grupo, c.nombre_carrera, i.id_periodo, pr.nombre_periodo, i.fecha_inscripcion, i.estado "
            + "FROM inscripciones i "
            + "JOIN trayectorias_academicas t ON i.id_trayectoria = t.id_trayectoria "
            + "JOIN alumnos a ON t.id_alumno = a.id_alumno "
            + "JOIN personas p ON a.id_persona = p.id_persona "
            + "JOIN grupos g ON i.id_grupo = g.id_grupo "
            + "JOIN plan_cuatrimestres pc ON g.id_plan_cuatrimestre = pc.id_plan_cuatrimestre "
            + "JOIN planes_estudio pe ON pc.id_plan = pe.id_plan "
            + "JOIN carreras c ON pe.id_carrera = c.id_carrera "
            + "JOIN periodos_escolares pr ON i.id_periodo = pr.id_periodo ";

    public ArrayList<Inscripcion> listar()
    {
        ArrayList<Inscripcion> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "ORDER BY i.fecha_inscripcion DESC";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construir(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public boolean existeParaTrayectoriaGrupo(int idTrayectoria, int idGrupo)
    {
        String sql = "SELECT COUNT(*) FROM inscripciones WHERE id_trayectoria = ? AND id_grupo = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idTrayectoria);
            sentencia.setInt(2, idGrupo);

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

    public int agregar(int idTrayectoria, int idGrupo, int idPeriodo)
    {
        String sql = "INSERT INTO inscripciones (id_trayectoria, id_grupo, id_periodo, estado) VALUES (?, ?, ?, 'Activa')";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            sentencia.setInt(1, idTrayectoria);
            sentencia.setInt(2, idGrupo);
            sentencia.setInt(3, idPeriodo);
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

    public void actualizarEstado(int idInscripcion, String estado)
    {
        String sql = "UPDATE inscripciones SET estado = ? WHERE id_inscripcion = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, estado);
            sentencia.setInt(2, idInscripcion);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    private Inscripcion construir(ResultSet resultado) throws SQLException
    {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setIdInscripcion(resultado.getInt("id_inscripcion"));
        inscripcion.setIdTrayectoria(resultado.getInt("id_trayectoria"));
        inscripcion.setMatricula(resultado.getString("matricula"));
        inscripcion.setNombreAlumno(resultado.getString("nombre_alumno"));
        inscripcion.setIdGrupo(resultado.getInt("id_grupo"));
        inscripcion.setNombreGrupo(resultado.getString("nombre_grupo"));
        inscripcion.setNombreCarrera(resultado.getString("nombre_carrera"));
        inscripcion.setIdPeriodo(resultado.getInt("id_periodo"));
        inscripcion.setNombrePeriodo(resultado.getString("nombre_periodo"));
        inscripcion.setFechaInscripcion(resultado.getTimestamp("fecha_inscripcion").toLocalDateTime());
        inscripcion.setEstado(resultado.getString("estado"));
        return inscripcion;
    }
}
