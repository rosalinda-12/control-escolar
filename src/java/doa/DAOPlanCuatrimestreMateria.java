package doa;

import conexion.ConexionMySQL;
import modelo.PlanCuatrimestreMateria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAOPlanCuatrimestreMateria
{
    public ArrayList<PlanCuatrimestreMateria> listarPorCuatrimestre(int idPlanCuatrimestre)
    {
        ArrayList<PlanCuatrimestreMateria> lista = new ArrayList<>();
        String sql = "SELECT pcm.id_plan_cuatrimestre_materia, pcm.id_plan_cuatrimestre, pcm.id_materia, m.nombre_materia "
                + "FROM plan_cuatrimestre_materias pcm JOIN materias m ON pcm.id_materia = m.id_materia "
                + "WHERE pcm.id_plan_cuatrimestre = ? ORDER BY m.nombre_materia";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPlanCuatrimestre);

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

    public boolean existe(int idPlanCuatrimestre, int idMateria)
    {
        String sql = "SELECT COUNT(*) FROM plan_cuatrimestre_materias WHERE id_plan_cuatrimestre = ? AND id_materia = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPlanCuatrimestre);
            sentencia.setInt(2, idMateria);

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

    public boolean materiaEstaEnAlgunPlan(int idMateria)
    {
        String sql = "SELECT COUNT(*) FROM plan_cuatrimestre_materias WHERE id_materia = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idMateria);

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

    public void agregar(int idPlanCuatrimestre, int idMateria)
    {
        String sql = "INSERT INTO plan_cuatrimestre_materias (id_plan_cuatrimestre, id_materia) VALUES (?, ?)";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPlanCuatrimestre);
            sentencia.setInt(2, idMateria);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void eliminar(int idPlanCuatrimestreMateria)
    {
        String sql = "DELETE FROM plan_cuatrimestre_materias WHERE id_plan_cuatrimestre_materia = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPlanCuatrimestreMateria);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public ArrayList<PlanCuatrimestreMateria> listarPorPlan(int idPlan)
    {
        ArrayList<PlanCuatrimestreMateria> lista = new ArrayList<>();
        String sql = "SELECT pcm.id_plan_cuatrimestre_materia, pcm.id_plan_cuatrimestre, pcm.id_materia, m.nombre_materia "
                + "FROM plan_cuatrimestre_materias pcm "
                + "JOIN plan_cuatrimestres pc ON pcm.id_plan_cuatrimestre = pc.id_plan_cuatrimestre "
                + "JOIN materias m ON pcm.id_materia = m.id_materia "
                + "WHERE pc.id_plan = ? ORDER BY pc.numero_cuatrimestre, m.nombre_materia";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPlan);

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

    private PlanCuatrimestreMateria construir(ResultSet resultado) throws SQLException
    {
        PlanCuatrimestreMateria pcm = new PlanCuatrimestreMateria();
        pcm.setIdPlanCuatrimestreMateria(resultado.getInt("id_plan_cuatrimestre_materia"));
        pcm.setIdPlanCuatrimestre(resultado.getInt("id_plan_cuatrimestre"));
        pcm.setIdMateria(resultado.getInt("id_materia"));
        pcm.setNombreMateria(resultado.getString("nombre_materia"));
        return pcm;
    }
}
