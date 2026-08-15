package doa;

import conexion.ConexionMySQL;
import modelo.GrupoMateria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAOGrupoMateria
{
    public ArrayList<GrupoMateria> listarPorGrupo(int idGrupo)
    {
        ArrayList<GrupoMateria> lista = new ArrayList<>();
        String sql = "SELECT gm.id_grupo_materia, gm.id_grupo, gm.id_materia, m.nombre_materia, "
                + "a.id_asignacion, p.nombres, p.apellido_paterno "
                + "FROM grupo_materias gm "
                + "JOIN materias m ON gm.id_materia = m.id_materia "
                + "LEFT JOIN docentes_asignaciones a ON a.id_grupo_materia = gm.id_grupo_materia "
                + "LEFT JOIN docentes d ON a.id_docente = d.id_docente "
                + "LEFT JOIN personas p ON d.id_persona = p.id_persona "
                + "WHERE gm.id_grupo = ? ORDER BY m.nombre_materia";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idGrupo);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                while (resultado.next())
                {
                    GrupoMateria grupoMateria = new GrupoMateria();
                    grupoMateria.setIdGrupoMateria(resultado.getInt("id_grupo_materia"));
                    grupoMateria.setIdGrupo(resultado.getInt("id_grupo"));
                    grupoMateria.setIdMateria(resultado.getInt("id_materia"));
                    grupoMateria.setNombreMateria(resultado.getString("nombre_materia"));
                    grupoMateria.setTieneDocenteAsignado(resultado.getObject("id_asignacion") != null);
                    if (grupoMateria.isTieneDocenteAsignado())
                    {
                        grupoMateria.setNombreDocente(resultado.getString("nombres") + " " + resultado.getString("apellido_paterno"));
                    }
                    lista.add(grupoMateria);
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
     * Copia las materias ya ligadas a ese cuatrimestre del plan hacia el
     * grupo recién creado, para que el admin no tenga que asignar materia
     * por materia manualmente.
     */
    public void copiarDesdeCurriculo(int idGrupo, int idPlanCuatrimestre)
    {
        String sql = "INSERT INTO grupo_materias (id_grupo, id_materia) "
                + "SELECT ?, id_materia FROM materias WHERE id_plan_cuatrimestre = ? AND estatus = 'Activa'";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idGrupo);
            sentencia.setInt(2, idPlanCuatrimestre);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    /**
     * Se usa para decidir si una materia se puede eliminar por completo o si
     * ya se copió a algún grupo y por lo tanto solo se puede desactivar.
     */
    public boolean materiaEstaEnAlgunGrupo(int idMateria)
    {
        String sql = "SELECT COUNT(*) FROM grupo_materias WHERE id_materia = ?";

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

    public int contarMaterias(int idGrupo)
    {
        String sql = "SELECT COUNT(*) FROM grupo_materias WHERE id_grupo = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idGrupo);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return resultado.getInt(1);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return 0;
    }
}
