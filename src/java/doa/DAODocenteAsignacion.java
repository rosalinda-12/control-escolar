package doa;

import conexion.ConexionMySQL;
import modelo.DocenteAsignacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAODocenteAsignacion
{
    /**
     * Materias de grupo que tiene a cargo el docente, con el contexto
     * (grupo, carrera, periodo) y el parcial activo de ese periodo, para
     * el panel del maestro.
     */
    public ArrayList<DocenteAsignacion> listarPorDocente(int idDocente)
    {
        ArrayList<DocenteAsignacion> lista = new ArrayList<>();
        String sql = "SELECT a.id_asignacion, a.id_docente, a.id_grupo_materia, "
                + "gm.id_grupo, m.nombre_materia, g.nombre_grupo, c.nombre_carrera, "
                + "g.id_periodo, pr.nombre_periodo, g.estatus AS estatus_grupo, cp.parcial_activo "
                + "FROM docentes_asignaciones a "
                + "JOIN grupo_materias gm ON a.id_grupo_materia = gm.id_grupo_materia "
                + "JOIN materias m ON gm.id_materia = m.id_materia "
                + "JOIN grupos g ON gm.id_grupo = g.id_grupo "
                + "JOIN plan_cuatrimestres pc ON g.id_plan_cuatrimestre = pc.id_plan_cuatrimestre "
                + "JOIN planes_estudio pe ON pc.id_plan = pe.id_plan "
                + "JOIN carreras c ON pe.id_carrera = c.id_carrera "
                + "JOIN periodos_escolares pr ON g.id_periodo = pr.id_periodo "
                + "LEFT JOIN configuracion_parciales cp ON cp.id_periodo = g.id_periodo "
                + "WHERE a.id_docente = ? "
                + "ORDER BY pr.fecha_inicio DESC, c.nombre_carrera, g.nombre_grupo, m.nombre_materia";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idDocente);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                while (resultado.next())
                {
                    DocenteAsignacion asignacion = new DocenteAsignacion();
                    asignacion.setIdAsignacion(resultado.getInt("id_asignacion"));
                    asignacion.setIdDocente(resultado.getInt("id_docente"));
                    asignacion.setIdGrupoMateria(resultado.getInt("id_grupo_materia"));
                    asignacion.setIdGrupo(resultado.getInt("id_grupo"));
                    asignacion.setNombreMateria(resultado.getString("nombre_materia"));
                    asignacion.setNombreGrupo(resultado.getString("nombre_grupo"));
                    asignacion.setNombreCarrera(resultado.getString("nombre_carrera"));
                    asignacion.setIdPeriodo(resultado.getInt("id_periodo"));
                    asignacion.setNombrePeriodo(resultado.getString("nombre_periodo"));
                    asignacion.setEstatusGrupo(resultado.getString("estatus_grupo"));

                    int parcialActivo = resultado.getInt("parcial_activo");
                    asignacion.setParcialActivo(resultado.wasNull() ? null : parcialActivo);

                    lista.add(asignacion);
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
     * Verifica que la materia de grupo en verdad esté a cargo de ese
     * docente, para que un maestro no pueda capturar calificaciones de un
     * grupo ajeno manipulando el parámetro en la URL.
     */
    public boolean perteneceADocente(int idGrupoMateria, int idDocente)
    {
        String sql = "SELECT COUNT(*) FROM docentes_asignaciones WHERE id_grupo_materia = ? AND id_docente = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idGrupoMateria);
            sentencia.setInt(2, idDocente);

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

    /**
     * Contexto (materia, grupo, carrera, periodo, parcial activo) de una
     * materia de grupo específica, para encabezar la pantalla de captura de
     * calificaciones. No exige que exista una asignación de docente.
     */
    public DocenteAsignacion buscarInfoGrupoMateria(int idGrupoMateria)
    {
        String sql = "SELECT gm.id_grupo_materia, gm.id_grupo, m.nombre_materia, g.nombre_grupo, c.nombre_carrera, "
                + "g.id_periodo, pr.nombre_periodo, g.estatus AS estatus_grupo, cp.parcial_activo "
                + "FROM grupo_materias gm "
                + "JOIN materias m ON gm.id_materia = m.id_materia "
                + "JOIN grupos g ON gm.id_grupo = g.id_grupo "
                + "JOIN plan_cuatrimestres pc ON g.id_plan_cuatrimestre = pc.id_plan_cuatrimestre "
                + "JOIN planes_estudio pe ON pc.id_plan = pe.id_plan "
                + "JOIN carreras c ON pe.id_carrera = c.id_carrera "
                + "JOIN periodos_escolares pr ON g.id_periodo = pr.id_periodo "
                + "LEFT JOIN configuracion_parciales cp ON cp.id_periodo = g.id_periodo "
                + "WHERE gm.id_grupo_materia = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idGrupoMateria);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    DocenteAsignacion asignacion = new DocenteAsignacion();
                    asignacion.setIdGrupoMateria(resultado.getInt("id_grupo_materia"));
                    asignacion.setIdGrupo(resultado.getInt("id_grupo"));
                    asignacion.setNombreMateria(resultado.getString("nombre_materia"));
                    asignacion.setNombreGrupo(resultado.getString("nombre_grupo"));
                    asignacion.setNombreCarrera(resultado.getString("nombre_carrera"));
                    asignacion.setIdPeriodo(resultado.getInt("id_periodo"));
                    asignacion.setNombrePeriodo(resultado.getString("nombre_periodo"));
                    asignacion.setEstatusGrupo(resultado.getString("estatus_grupo"));

                    int parcialActivo = resultado.getInt("parcial_activo");
                    asignacion.setParcialActivo(resultado.wasNull() ? null : parcialActivo);

                    return asignacion;
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public boolean existeParaGrupoMateria(int idGrupoMateria)
    {
        String sql = "SELECT COUNT(*) FROM docentes_asignaciones WHERE id_grupo_materia = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idGrupoMateria);

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

    public void asignar(int idDocente, int idGrupoMateria)
    {
        String sql = "INSERT INTO docentes_asignaciones (id_docente, id_grupo_materia) VALUES (?, ?)";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idDocente);
            sentencia.setInt(2, idGrupoMateria);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void quitarPorGrupoMateria(int idGrupoMateria)
    {
        String sql = "DELETE FROM docentes_asignaciones WHERE id_grupo_materia = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idGrupoMateria);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }
}
