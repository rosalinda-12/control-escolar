package doa;

import conexion.ConexionMySQL;
import modelo.Materia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOMateria
{
    private static final String SELECCION_BASE =
            "SELECT m.id_materia, m.nombre_materia, m.estatus, m.id_plan_cuatrimestre, "
            + "pc.numero_cuatrimestre, pe.nombre_plan, c.nombre_carrera "
            + "FROM materias m "
            + "JOIN plan_cuatrimestres pc ON m.id_plan_cuatrimestre = pc.id_plan_cuatrimestre "
            + "JOIN planes_estudio pe ON pc.id_plan = pe.id_plan "
            + "JOIN carreras c ON pe.id_carrera = c.id_carrera ";

    public ArrayList<Materia> listar()
    {
        ArrayList<Materia> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "ORDER BY c.nombre_carrera, pe.nombre_plan, pc.numero_cuatrimestre, m.nombre_materia";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirMateria(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public ArrayList<Materia> listarActivas()
    {
        ArrayList<Materia> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "WHERE m.estatus = 'Activa' ORDER BY c.nombre_carrera, pe.nombre_plan, pc.numero_cuatrimestre, m.nombre_materia";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirMateria(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    /**
     * Materias ya ligadas a un cuatrimestre concreto de un plan. Sustituye a
     * la antigua tabla puente plan_cuatrimestre_materias: ahora la relación
     * vive directamente en la materia.
     */
    public ArrayList<Materia> listarPorPlanCuatrimestre(int idPlanCuatrimestre)
    {
        ArrayList<Materia> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "WHERE m.id_plan_cuatrimestre = ? ORDER BY m.nombre_materia";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPlanCuatrimestre);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                while (resultado.next())
                {
                    lista.add(construirMateria(resultado));
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public Materia buscarPorId(int idMateria)
    {
        String sql = SELECCION_BASE + "WHERE m.id_materia = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idMateria);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirMateria(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    /**
     * El nombre ya no tiene que ser único en todo el catálogo: como cada
     * materia vive en un solo cuatrimestre, lo único que no debe repetirse
     * es el mismo nombre dos veces dentro de ese mismo cuatrimestre.
     */
    public boolean existeNombreEnCuatrimestre(String nombreMateria, int idPlanCuatrimestre, Integer idMateriaExcluir)
    {
        String sql = "SELECT COUNT(*) FROM materias WHERE nombre_materia = ? AND id_plan_cuatrimestre = ?"
                + (idMateriaExcluir != null ? " AND id_materia <> ?" : "");

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, nombreMateria);
            sentencia.setInt(2, idPlanCuatrimestre);
            if (idMateriaExcluir != null)
            {
                sentencia.setInt(3, idMateriaExcluir);
            }

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

    public int agregar(Materia materia)
    {
        String sql = "INSERT INTO materias (nombre_materia, estatus, id_plan_cuatrimestre) VALUES (?, 'Activa', ?)";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            sentencia.setString(1, materia.getNombreMateria());
            sentencia.setInt(2, materia.getIdPlanCuatrimestre());
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

    /**
     * Solo nombre y estatus son modificables: el cuatrimestre/plan al que
     * quedó ligada la materia se fija desde el alta y ya no cambia.
     */
    public void modificar(Materia materia)
    {
        String sql = "UPDATE materias SET nombre_materia = ?, estatus = ? WHERE id_materia = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, materia.getNombreMateria());
            sentencia.setString(2, materia.getEstatus());
            sentencia.setInt(3, materia.getIdMateria());
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void eliminar(int idMateria)
    {
        String sql = "DELETE FROM materias WHERE id_materia = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idMateria);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    private Materia construirMateria(ResultSet resultado) throws SQLException
    {
        Materia materia = new Materia();
        materia.setIdMateria(resultado.getInt("id_materia"));
        materia.setNombreMateria(resultado.getString("nombre_materia"));
        materia.setEstatus(resultado.getString("estatus"));
        materia.setIdPlanCuatrimestre(resultado.getInt("id_plan_cuatrimestre"));
        materia.setNumeroCuatrimestre(resultado.getInt("numero_cuatrimestre"));
        materia.setNombrePlan(resultado.getString("nombre_plan"));
        materia.setNombreCarrera(resultado.getString("nombre_carrera"));
        return materia;
    }
}
