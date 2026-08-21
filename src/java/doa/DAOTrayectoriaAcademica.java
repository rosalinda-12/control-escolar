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
            + "t.id_plan, pe.nombre_plan, c.id_carrera, c.nombre_carrera, t.matricula, "
            + "(SELECT ip.id_grupo FROM inscripciones ip "
            + "WHERE ip.id_trayectoria = t.id_trayectoria AND ip.estado = 'Activa' "
            + "ORDER BY ip.id_inscripcion DESC LIMIT 1) AS id_grupo_anterior, "
            + "(SELECT CONCAT(gp.nombre_grupo, ' · ', pp.nombre_periodo) FROM inscripciones ip "
            + "JOIN grupos gp ON ip.id_grupo = gp.id_grupo "
            + "JOIN periodos_escolares pp ON ip.id_periodo = pp.id_periodo "
            + "WHERE ip.id_trayectoria = t.id_trayectoria AND ip.estado = 'Activa' "
            + "ORDER BY pp.fecha_inicio DESC LIMIT 1) AS grupo_anterior, "
            + "t.id_trayectoria_anterior, t.estado, t.fecha_inicio, t.fecha_fin "
            + "FROM trayectorias_academicas t "
            + "JOIN alumnos a ON t.id_alumno = a.id_alumno "
            + "JOIN personas p ON a.id_persona = p.id_persona "
            + "JOIN planes_estudio pe ON t.id_plan = pe.id_plan "
            + "JOIN carreras c ON pe.id_carrera = c.id_carrera ";



    public ArrayList<TrayectoriaAcademica> listar()
    {
        ArrayList<TrayectoriaAcademica> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "ORDER BY p.nombres, p.apellido_paterno, p.apellido_materno";

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

    public TrayectoriaAcademica buscarActivaPorAlumno(int idAlumno)
    {
        String sql = SELECCION_BASE + "WHERE t.id_alumno = ? AND t.estado = 'ACTIVA' ORDER BY t.fecha_inicio DESC LIMIT 1";
        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idAlumno);
            try (ResultSet resultado = sentencia.executeQuery())
            { return resultado.next() ? construir(resultado) : null; }
        }
        catch (SQLException excepcion)
        { throw new RuntimeException(excepcion); }
    }

    public ArrayList<Integer> listarAlumnosPorPeriodo(int idPeriodo)
    {
        ArrayList<Integer> ids = new ArrayList<>();
        String sql = "SELECT DISTINCT t.id_alumno FROM trayectorias_academicas t "
                + "JOIN inscripciones i ON i.id_trayectoria = t.id_trayectoria "
                + "WHERE i.id_periodo = ? AND i.estado = 'Activa'";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPeriodo);
            try (ResultSet resultado = sentencia.executeQuery())
            {
                while (resultado.next()) ids.add(resultado.getInt("id_alumno"));
            }
        }
        catch (SQLException excepcion)
        { throw new RuntimeException(excepcion); }

        return ids;
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

    public boolean tieneSiguienteNivel(int idPlan)
    {
        String sql = "SELECT COUNT(*) FROM plan_niveles WHERE id_plan = ? AND cuatrimestre_inicio > 1";
        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPlan);
            try (ResultSet resultado = sentencia.executeQuery())
            { return resultado.next() && resultado.getInt(1) > 0; }
        }
        catch (SQLException excepcion)
        { throw new RuntimeException(excepcion); }
    }

    public boolean estaListaParaEgreso(int idTrayectoria)
    {
        String sql = "SELECT COUNT(*) FROM trayectorias_academicas t "
                + "JOIN planes_estudio pe ON t.id_plan = pe.id_plan "
                + "WHERE t.id_trayectoria = ? "
                + "AND (SELECT MAX(pc.numero_cuatrimestre) FROM inscripciones i JOIN grupos g ON i.id_grupo = g.id_grupo JOIN plan_cuatrimestres pc ON g.id_plan_cuatrimestre = pc.id_plan_cuatrimestre WHERE i.id_trayectoria = t.id_trayectoria) >= pe.duracion_cuatrimestres "
                + "AND NOT EXISTS (SELECT 1 FROM materias m JOIN plan_cuatrimestres pc ON m.id_plan_cuatrimestre = pc.id_plan_cuatrimestre WHERE pc.id_plan = t.id_plan AND m.estatus = 'Activa' AND NOT EXISTS (SELECT 1 FROM inscripcion_materias im JOIN grupo_materias gm ON im.id_grupo_materia = gm.id_grupo_materia JOIN inscripciones i ON im.id_inscripcion = i.id_inscripcion WHERE i.id_trayectoria = t.id_trayectoria AND gm.id_materia = m.id_materia AND im.estado = 'Aprobada'))";
        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idTrayectoria);
            try (ResultSet resultado = sentencia.executeQuery())
            { return resultado.next() && resultado.getInt(1) > 0; }
        }
        catch (SQLException excepcion)
        { throw new RuntimeException(excepcion); }
    }

    public String sugerirMatricula(int idPlan)
    {
        String sql = "SELECT MAX(CAST(SUBSTRING_INDEX(matricula, '_', 1) AS UNSIGNED)) AS ultimo, "
                + "(SELECT n.nombre_nivel FROM plan_niveles pn JOIN niveles_academicos n ON pn.id_nivel = n.id_nivel "
                + "WHERE pn.id_plan = ? ORDER BY pn.cuatrimestre_inicio LIMIT 1) AS nivel "
                + "FROM trayectorias_academicas";
        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idPlan);
            try (ResultSet resultado = sentencia.executeQuery())
            {
                long siguiente = resultado.next() ? resultado.getLong("ultimo") + 1 : 1;
                String nivel = resultado.getString("nivel");
                return String.valueOf(siguiente) + (nivel != null && !"TSU".equalsIgnoreCase(nivel) ? "_i" : "");
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
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

    public TrayectoriaAcademica buscarPorTexto(String busqueda)
    {
        String sql = SELECCION_BASE
                + "WHERE t.matricula LIKE ? OR CONCAT(p.nombres, ' ', p.apellido_paterno, ' ', p.apellido_materno) LIKE ? OR c.nombre_carrera LIKE ? "
                + "ORDER BY t.estado = 'ACTIVA' DESC, t.fecha_inicio DESC";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            String parametro = "%" + busqueda + "%";
            sentencia.setString(1, parametro);
            sentencia.setString(2, parametro);
            sentencia.setString(3, parametro);

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



    public int contarMateriasPendientes(int idTrayectoria)
    {
        String sql =
                "SELECT COUNT(*) FROM materias m "
                + "JOIN plan_cuatrimestres pc ON m.id_plan_cuatrimestre = pc.id_plan_cuatrimestre "
                + "JOIN plan_niveles pn ON pn.id_plan = pc.id_plan "
                + "AND pc.numero_cuatrimestre BETWEEN pn.cuatrimestre_inicio AND pn.cuatrimestre_fin "
                + "WHERE pc.id_plan = (SELECT id_plan FROM trayectorias_academicas WHERE id_trayectoria = ?) "
                + "AND pn.cuatrimestre_inicio = ("
                + "    SELECT MIN(pn2.cuatrimestre_inicio) FROM plan_niveles pn2 "
                + "    WHERE pn2.id_plan = pc.id_plan "
                + "    AND pn2.cuatrimestre_fin >= COALESCE(("
                + "        SELECT MAX(pc2.numero_cuatrimestre) FROM inscripciones i2 "
                + "        JOIN grupos g2 ON i2.id_grupo = g2.id_grupo "
                + "        JOIN plan_cuatrimestres pc2 ON g2.id_plan_cuatrimestre = pc2.id_plan_cuatrimestre "
                + "        WHERE i2.id_trayectoria = ? AND i2.estado = 'Activa'"
                + "    ), 1)"
                + ") "
                + "AND m.estatus = 'Activa' "
                + "AND NOT EXISTS ("
                + "    SELECT 1 FROM inscripcion_materias im "
                + "    JOIN grupo_materias gm ON im.id_grupo_materia = gm.id_grupo_materia "
                + "    JOIN inscripciones i ON im.id_inscripcion = i.id_inscripcion "
                + "    WHERE gm.id_materia = m.id_materia "
                + "      AND i.id_trayectoria = ? "
                + "      AND im.estado = 'Aprobada'"
                + ")";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idTrayectoria);
            sentencia.setInt(2, idTrayectoria);
            sentencia.setInt(3, idTrayectoria);

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

    public String obtenerSiguienteNivel(int idTrayectoria)
    {
        String sql = "SELECT n.nombre_nivel FROM plan_niveles pn "
                + "JOIN niveles_academicos n ON pn.id_nivel = n.id_nivel "
                + "WHERE pn.id_plan = (SELECT id_plan FROM trayectorias_academicas WHERE id_trayectoria = ?) "
                + "AND pn.cuatrimestre_inicio > COALESCE(("
                + "    SELECT MAX(pc.numero_cuatrimestre) FROM inscripciones i "
                + "    JOIN grupos g ON i.id_grupo = g.id_grupo "
                + "    JOIN plan_cuatrimestres pc ON g.id_plan_cuatrimestre = pc.id_plan_cuatrimestre "
                + "    WHERE i.id_trayectoria = ? AND i.estado = 'Activa'"
                + "), 0) ORDER BY pn.cuatrimestre_inicio LIMIT 1";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idTrayectoria);
            sentencia.setInt(2, idTrayectoria);

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



    public void revertirAActiva(int idTrayectoria)
    {
        String sql = "UPDATE trayectorias_academicas SET estado = 'ACTIVA', fecha_fin = NULL WHERE id_trayectoria = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idTrayectoria);
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
        trayectoria.setIdCarrera(resultado.getInt("id_carrera"));
        trayectoria.setNombrePlan(resultado.getString("nombre_plan"));
        trayectoria.setNombreCarrera(resultado.getString("nombre_carrera"));
        trayectoria.setMatricula(resultado.getString("matricula"));
        int idGrupoAnterior = resultado.getInt("id_grupo_anterior");
        trayectoria.setIdGrupoAnterior(resultado.wasNull() ? null : idGrupoAnterior);
        trayectoria.setGrupoAnterior(resultado.getString("grupo_anterior"));
        int idAnterior = resultado.getInt("id_trayectoria_anterior");
        trayectoria.setIdTrayectoriaAnterior(resultado.wasNull() ? null : idAnterior);
        trayectoria.setEstado(resultado.getString("estado"));
        trayectoria.setFechaInicio(resultado.getDate("fecha_inicio").toLocalDate());
        trayectoria.setFechaFin(resultado.getDate("fecha_fin") != null ? resultado.getDate("fecha_fin").toLocalDate() : null);
        return trayectoria;
    }
}
