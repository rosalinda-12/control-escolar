package doa;

import conexion.ConexionMySQL;
import modelo.TrayectoriaAcademica;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOTrayectoriaAcademica
{
    private static final String SELECCION_BASE =
            "SELECT t.id_trayectoria, t.id_alumno, CONCAT(p.nombres, ' ', p.apellido_paterno, ' ', p.apellido_materno) AS nombre_alumno, "
            + "t.id_plan, pe.nombre_plan, c.nombre_carrera, t.matricula, t.id_trayectoria_anterior, t.estado, t.fecha_inicio, t.fecha_fin "
            + "FROM trayectorias_academicas t "
            + "JOIN alumnos a ON t.id_alumno = a.id_alumno "
            + "JOIN personas p ON a.id_persona = p.id_persona "
            + "JOIN planes_estudio pe ON t.id_plan = pe.id_plan "
            + "JOIN carreras c ON pe.id_carrera = c.id_carrera ";

    public ArrayList<TrayectoriaAcademica> listarPorAlumno(int idAlumno)
    {
        ArrayList<TrayectoriaAcademica> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "WHERE t.id_alumno = ? ORDER BY t.fecha_inicio";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idAlumno);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                while (resultado.next())
                {
                    lista.add(construir(resultado));
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public TrayectoriaAcademica buscarPorId(int idTrayectoria)
    {
        String sql = SELECCION_BASE + "WHERE t.id_trayectoria = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idTrayectoria);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construir(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public boolean existeMatricula(String matricula)
    {
        String sql = "SELECT COUNT(*) FROM trayectorias_academicas WHERE matricula = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, matricula);

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

    public TrayectoriaAcademica buscarPorMatricula(String matricula)
    {
        String sql = SELECCION_BASE + "WHERE t.matricula = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, matricula);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construir(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public int agregar(TrayectoriaAcademica trayectoria)
    {
        String sql = "INSERT INTO trayectorias_academicas (id_alumno, id_plan, matricula, id_trayectoria_anterior, estado, fecha_inicio) "
                + "VALUES (?, ?, ?, ?, 'ACTIVA', ?)";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            sentencia.setInt(1, trayectoria.getIdAlumno());
            sentencia.setInt(2, trayectoria.getIdPlan());
            sentencia.setString(3, trayectoria.getMatricula());
            if (trayectoria.getIdTrayectoriaAnterior() != null)
            {
                sentencia.setInt(4, trayectoria.getIdTrayectoriaAnterior());
            }
            else
            {
                sentencia.setNull(4, java.sql.Types.INTEGER);
            }
            sentencia.setDate(5, Date.valueOf(trayectoria.getFechaInicio()));
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

    public void actualizarEstado(int idTrayectoria, String estado, boolean cerrarFecha)
    {
        String sql = "UPDATE trayectorias_academicas SET estado = ?" + (cerrarFecha ? ", fecha_fin = CURDATE()" : "") + " WHERE id_trayectoria = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, estado);
            sentencia.setInt(2, idTrayectoria);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    private TrayectoriaAcademica construir(ResultSet resultado) throws SQLException
    {
        TrayectoriaAcademica trayectoria = new TrayectoriaAcademica();
        trayectoria.setIdTrayectoria(resultado.getInt("id_trayectoria"));
        trayectoria.setIdAlumno(resultado.getInt("id_alumno"));
        trayectoria.setNombreAlumno(resultado.getString("nombre_alumno"));
        trayectoria.setIdPlan(resultado.getInt("id_plan"));
        trayectoria.setNombrePlan(resultado.getString("nombre_plan"));
        trayectoria.setNombreCarrera(resultado.getString("nombre_carrera"));
        trayectoria.setMatricula(resultado.getString("matricula"));
        int idAnterior = resultado.getInt("id_trayectoria_anterior");
        trayectoria.setIdTrayectoriaAnterior(resultado.wasNull() ? null : idAnterior);
        trayectoria.setEstado(resultado.getString("estado"));
        trayectoria.setFechaInicio(resultado.getDate("fecha_inicio").toLocalDate());
        trayectoria.setFechaFin(resultado.getDate("fecha_fin") != null ? resultado.getDate("fecha_fin").toLocalDate() : null);
        return trayectoria;
    }
}
