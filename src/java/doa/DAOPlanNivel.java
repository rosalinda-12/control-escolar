package doa;

import conexion.ConexionMySQL;
import modelo.PlanNivel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAOPlanNivel
{
    public ArrayList<PlanNivel> listarPorPlan(int idPlan)
    {
        ArrayList<PlanNivel> lista = new ArrayList<>();
        String sql = "SELECT pn.id_plan_nivel, pn.id_plan, pn.id_nivel, pn.cuatrimestre_inicio, pn.cuatrimestre_fin, n.nombre_nivel "
                + "FROM plan_niveles pn JOIN niveles_academicos n ON pn.id_nivel = n.id_nivel "
                + "WHERE pn.id_plan = ? ORDER BY pn.cuatrimestre_inicio";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPlan);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                while (resultado.next())
                {
                    PlanNivel planNivel = new PlanNivel();
                    planNivel.setIdPlanNivel(resultado.getInt("id_plan_nivel"));
                    planNivel.setIdPlan(resultado.getInt("id_plan"));
                    planNivel.setIdNivel(resultado.getInt("id_nivel"));
                    planNivel.setNombreNivel(resultado.getString("nombre_nivel"));
                    planNivel.setCuatrimestreInicio(resultado.getInt("cuatrimestre_inicio"));
                    planNivel.setCuatrimestreFin(resultado.getInt("cuatrimestre_fin"));
                    lista.add(planNivel);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public void agregar(int idPlan, int idNivel, int cuatrimestreInicio, int cuatrimestreFin)
    {
        String sql = "INSERT INTO plan_niveles (id_plan, id_nivel, cuatrimestre_inicio, cuatrimestre_fin) VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPlan);
            sentencia.setInt(2, idNivel);
            sentencia.setInt(3, cuatrimestreInicio);
            sentencia.setInt(4, cuatrimestreFin);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public String nivelParaCuatrimestre(int idPlan, int numeroCuatrimestre)
    {
        String sql = "SELECT n.nombre_nivel FROM plan_niveles pn JOIN niveles_academicos n ON pn.id_nivel = n.id_nivel "
                + "WHERE pn.id_plan = ? AND ? BETWEEN pn.cuatrimestre_inicio AND pn.cuatrimestre_fin";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPlan);
            sentencia.setInt(2, numeroCuatrimestre);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return resultado.getString("nombre_nivel");
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }
}
