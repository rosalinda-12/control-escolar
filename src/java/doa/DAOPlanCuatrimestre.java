package doa;

import conexion.ConexionMySQL;
import modelo.PlanCuatrimestre;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAOPlanCuatrimestre
{
    public void generarParaPlan(Connection conexion, int idPlan, int duracionCuatrimestres) throws SQLException
    {
        String sql = "INSERT INTO plan_cuatrimestres (id_plan, numero_cuatrimestre) VALUES (?, ?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            for (int numero = 1; numero <= duracionCuatrimestres; numero++)
            {
                sentencia.setInt(1, idPlan);
                sentencia.setInt(2, numero);
                sentencia.addBatch();
            }
            sentencia.executeBatch();
        }
    }

    public ArrayList<PlanCuatrimestre> listarPorPlan(int idPlan)
    {
        ArrayList<PlanCuatrimestre> lista = new ArrayList<>();
        String sql = "SELECT id_plan_cuatrimestre, id_plan, numero_cuatrimestre FROM plan_cuatrimestres WHERE id_plan = ? ORDER BY numero_cuatrimestre";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPlan);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                while (resultado.next())
                {
                    PlanCuatrimestre cuatrimestre = new PlanCuatrimestre();
                    cuatrimestre.setIdPlanCuatrimestre(resultado.getInt("id_plan_cuatrimestre"));
                    cuatrimestre.setIdPlan(resultado.getInt("id_plan"));
                    cuatrimestre.setNumeroCuatrimestre(resultado.getInt("numero_cuatrimestre"));
                    lista.add(cuatrimestre);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    /**
     * Cuatrimestres de todos los planes vigentes, con carrera/plan ya
     * incluidos en la etiqueta, listos para un selector de "crear grupo".
     */
    public ArrayList<PlanCuatrimestre> listarDePlanesVigentes()
    {
        ArrayList<PlanCuatrimestre> lista = new ArrayList<>();
        String sql = "SELECT pc.id_plan_cuatrimestre, pc.id_plan, pc.numero_cuatrimestre, "
                + "CONCAT(c.nombre_carrera, ' — ', pe.nombre_plan, ' — Cuatrimestre ', pc.numero_cuatrimestre) AS etiqueta "
                + "FROM plan_cuatrimestres pc "
                + "JOIN planes_estudio pe ON pc.id_plan = pe.id_plan "
                + "JOIN carreras c ON pe.id_carrera = c.id_carrera "
                + "WHERE pe.estatus = 'Vigente' "
                + "ORDER BY c.nombre_carrera, pe.nombre_plan, pc.numero_cuatrimestre";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                PlanCuatrimestre cuatrimestre = new PlanCuatrimestre();
                cuatrimestre.setIdPlanCuatrimestre(resultado.getInt("id_plan_cuatrimestre"));
                cuatrimestre.setIdPlan(resultado.getInt("id_plan"));
                cuatrimestre.setNumeroCuatrimestre(resultado.getInt("numero_cuatrimestre"));
                cuatrimestre.setEtiqueta(resultado.getString("etiqueta"));
                lista.add(cuatrimestre);
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public PlanCuatrimestre buscarPorId(int idPlanCuatrimestre)
    {
        String sql = "SELECT id_plan_cuatrimestre, id_plan, numero_cuatrimestre FROM plan_cuatrimestres WHERE id_plan_cuatrimestre = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPlanCuatrimestre);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    PlanCuatrimestre cuatrimestre = new PlanCuatrimestre();
                    cuatrimestre.setIdPlanCuatrimestre(resultado.getInt("id_plan_cuatrimestre"));
                    cuatrimestre.setIdPlan(resultado.getInt("id_plan"));
                    cuatrimestre.setNumeroCuatrimestre(resultado.getInt("numero_cuatrimestre"));
                    return cuatrimestre;
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
