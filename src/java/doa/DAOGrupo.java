package doa;

import conexion.ConexionMySQL;
import modelo.Grupo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOGrupo
{
    private static final String SELECCION_BASE =
            "SELECT g.id_grupo, g.id_plan_cuatrimestre, pc.numero_cuatrimestre, pc.id_plan, pe.nombre_plan, c.nombre_carrera, "
            + "g.nombre_grupo, g.id_generacion, ge.nombre_generacion, g.id_periodo, pr.nombre_periodo, g.estatus "
            + "FROM grupos g "
            + "JOIN plan_cuatrimestres pc ON g.id_plan_cuatrimestre = pc.id_plan_cuatrimestre "
            + "JOIN planes_estudio pe ON pc.id_plan = pe.id_plan "
            + "JOIN carreras c ON pe.id_carrera = c.id_carrera "
            + "JOIN generaciones ge ON g.id_generacion = ge.id_generacion "
            + "JOIN periodos_escolares pr ON g.id_periodo = pr.id_periodo ";

    public ArrayList<Grupo> listar()
    {
        ArrayList<Grupo> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "ORDER BY pr.fecha_inicio DESC, c.nombre_carrera, g.nombre_grupo";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirGrupo(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public Grupo buscarPorId(int idGrupo)
    {
        String sql = SELECCION_BASE + "WHERE g.id_grupo = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idGrupo);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirGrupo(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public boolean existeNombreEnPeriodo(String nombreGrupo, int idPeriodo)
    {
        String sql = "SELECT COUNT(*) FROM grupos WHERE nombre_grupo = ? AND id_periodo = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, nombreGrupo);
            sentencia.setInt(2, idPeriodo);

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

    public int agregar(Grupo grupo)
    {
        String sql = "INSERT INTO grupos (id_plan_cuatrimestre, nombre_grupo, id_generacion, id_periodo, estatus) VALUES (?, ?, ?, ?, 'Activo')";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            sentencia.setInt(1, grupo.getIdPlanCuatrimestre());
            sentencia.setString(2, grupo.getNombreGrupo());
            sentencia.setInt(3, grupo.getIdGeneracion());
            sentencia.setInt(4, grupo.getIdPeriodo());
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

    public void actualizarEstatus(int idGrupo, String estatus)
    {
        String sql = "UPDATE grupos SET estatus = ? WHERE id_grupo = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, estatus);
            sentencia.setInt(2, idGrupo);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    private Grupo construirGrupo(ResultSet resultado) throws SQLException
    {
        Grupo grupo = new Grupo();
        grupo.setIdGrupo(resultado.getInt("id_grupo"));
        grupo.setIdPlanCuatrimestre(resultado.getInt("id_plan_cuatrimestre"));
        grupo.setNumeroCuatrimestre(resultado.getInt("numero_cuatrimestre"));
        grupo.setIdPlan(resultado.getInt("id_plan"));
        grupo.setNombrePlan(resultado.getString("nombre_plan"));
        grupo.setNombreCarrera(resultado.getString("nombre_carrera"));
        grupo.setNombreGrupo(resultado.getString("nombre_grupo"));
        grupo.setIdGeneracion(resultado.getInt("id_generacion"));
        grupo.setNombreGeneracion(resultado.getString("nombre_generacion"));
        grupo.setIdPeriodo(resultado.getInt("id_periodo"));
        grupo.setNombrePeriodo(resultado.getString("nombre_periodo"));
        grupo.setEstatus(resultado.getString("estatus"));
        return grupo;
    }
}
