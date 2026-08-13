package doa;

import conexion.ConexionMySQL;
import modelo.PlanEstudio;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOPlanEstudio
{
    private static final String SELECCION_BASE =
            "SELECT p.id_plan, p.id_carrera, p.version, p.nombre_plan, p.duracion_cuatrimestres, p.fecha_vigencia_inicio, p.estatus, c.nombre_carrera "
            + "FROM planes_estudio p JOIN carreras c ON p.id_carrera = c.id_carrera ";

    public ArrayList<PlanEstudio> listar()
    {
        ArrayList<PlanEstudio> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "ORDER BY c.nombre_carrera, p.fecha_vigencia_inicio DESC";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirPlan(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public ArrayList<PlanEstudio> listarVigentes()
    {
        ArrayList<PlanEstudio> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "WHERE p.estatus = 'Vigente' ORDER BY c.nombre_carrera";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirPlan(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public PlanEstudio buscarPorId(int idPlan)
    {
        String sql = SELECCION_BASE + "WHERE p.id_plan = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPlan);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirPlan(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public boolean existeVersion(int idCarrera, String version)
    {
        String sql = "SELECT COUNT(*) FROM planes_estudio WHERE id_carrera = ? AND version = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idCarrera);
            sentencia.setString(2, version);

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

    public boolean tieneGruposAsociados(int idPlan)
    {
        String sql = "SELECT COUNT(*) FROM grupos g JOIN plan_cuatrimestres pc ON g.id_plan_cuatrimestre = pc.id_plan_cuatrimestre WHERE pc.id_plan = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPlan);

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

    public int agregar(PlanEstudio plan)
    {
        String sql = "INSERT INTO planes_estudio (id_carrera, version, nombre_plan, duracion_cuatrimestres, fecha_vigencia_inicio, estatus) "
                + "VALUES (?, ?, ?, ?, ?, 'Vigente')";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            sentencia.setInt(1, plan.getIdCarrera());
            sentencia.setString(2, plan.getVersion());
            sentencia.setString(3, plan.getNombrePlan());
            sentencia.setInt(4, plan.getDuracionCuatrimestres());
            sentencia.setDate(5, Date.valueOf(plan.getFechaVigenciaInicio()));
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

    public void actualizarEstatus(int idPlan, String estatus)
    {
        String sql = "UPDATE planes_estudio SET estatus = ? WHERE id_plan = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, estatus);
            sentencia.setInt(2, idPlan);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    private PlanEstudio construirPlan(ResultSet resultado) throws SQLException
    {
        PlanEstudio plan = new PlanEstudio();
        plan.setIdPlan(resultado.getInt("id_plan"));
        plan.setIdCarrera(resultado.getInt("id_carrera"));
        plan.setNombreCarrera(resultado.getString("nombre_carrera"));
        plan.setVersion(resultado.getString("version"));
        plan.setNombrePlan(resultado.getString("nombre_plan"));
        plan.setDuracionCuatrimestres(resultado.getInt("duracion_cuatrimestres"));
        plan.setFechaVigenciaInicio(resultado.getDate("fecha_vigencia_inicio").toLocalDate());
        plan.setEstatus(resultado.getString("estatus"));
        return plan;
    }
}
