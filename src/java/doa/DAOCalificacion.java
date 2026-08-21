package doa;

import conexion.ConexionMySQL;
import modelo.Calificacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAOCalificacion
{


    public ArrayList<Calificacion> listarPorGrupoMateria(int idGrupoMateria)
    {
        ArrayList<Calificacion> lista = new ArrayList<>();
        String sql = "SELECT im.id_inscripcion_materia, im.intento, im.estado AS estado_materia, "
                + "t.id_alumno, t.matricula, CONCAT(p.nombres, ' ', p.apellido_paterno, ' ', p.apellido_materno) AS nombre_alumno, "
                + "cal.id_calificacion, cal.parcial_1, cal.parcial_2, cal.parcial_3, cal.promedio_final "
                + "FROM inscripcion_materias im "
                + "JOIN inscripciones i ON im.id_inscripcion = i.id_inscripcion "
                + "JOIN trayectorias_academicas t ON i.id_trayectoria = t.id_trayectoria "
                + "JOIN alumnos a ON t.id_alumno = a.id_alumno "
                + "JOIN personas p ON a.id_persona = p.id_persona "
                + "LEFT JOIN calificaciones cal ON cal.id_inscripcion_materia = im.id_inscripcion_materia "
                + "WHERE im.id_grupo_materia = ? AND i.estado = 'Activa' AND im.estado <> 'Baja' "
                + "ORDER BY p.apellido_paterno, p.apellido_materno, p.nombres";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idGrupoMateria);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                while (resultado.next())
                {
                    lista.add(construirCalificacion(resultado));
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }



    public ArrayList<Calificacion> listarPorAlumno(int idAlumno)
    {
        ArrayList<Calificacion> lista = new ArrayList<>();
        String sql = "SELECT im.id_inscripcion_materia, im.intento, im.estado AS estado_materia, "
                + "mat.nombre_materia, pc.numero_cuatrimestre, per.nombre_periodo, g.nombre_grupo, "
                + "t.id_trayectoria, n.nombre_nivel, "
                + "cal.id_calificacion, cal.parcial_1, cal.parcial_2, cal.parcial_3, cal.promedio_final "
                + "FROM inscripcion_materias im "
                + "JOIN inscripciones i ON im.id_inscripcion = i.id_inscripcion "
                + "JOIN trayectorias_academicas t ON i.id_trayectoria = t.id_trayectoria "
                + "JOIN grupo_materias gm ON im.id_grupo_materia = gm.id_grupo_materia "
                + "JOIN materias mat ON gm.id_materia = mat.id_materia "
                + "JOIN grupos g ON i.id_grupo = g.id_grupo "
                + "JOIN plan_cuatrimestres pc ON g.id_plan_cuatrimestre = pc.id_plan_cuatrimestre "
                + "JOIN plan_niveles pn ON pn.id_plan = pc.id_plan AND pc.numero_cuatrimestre BETWEEN pn.cuatrimestre_inicio AND pn.cuatrimestre_fin "
                + "JOIN niveles_academicos n ON pn.id_nivel = n.id_nivel "
                + "JOIN periodos_escolares per ON i.id_periodo = per.id_periodo "
                + "LEFT JOIN calificaciones cal ON cal.id_inscripcion_materia = im.id_inscripcion_materia "
                + "WHERE t.id_alumno = ? AND im.estado <> 'Baja' "
                + "ORDER BY t.id_trayectoria, pc.numero_cuatrimestre, mat.nombre_materia";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idAlumno);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                while (resultado.next())
                {
                    Calificacion calificacion = new Calificacion();
                    calificacion.setIdInscripcionMateria(resultado.getInt("id_inscripcion_materia"));
                    calificacion.setIntento(resultado.getInt("intento"));
                    calificacion.setEstadoMateria(resultado.getString("estado_materia"));
                    calificacion.setIdCalificacion(resultado.getInt("id_calificacion"));
                    calificacion.setParcial1(obtenerDoubleONull(resultado, "parcial_1"));
                    calificacion.setParcial2(obtenerDoubleONull(resultado, "parcial_2"));
                    calificacion.setParcial3(obtenerDoubleONull(resultado, "parcial_3"));
                    calificacion.setPromedioFinal(obtenerDoubleONull(resultado, "promedio_final"));
                    calificacion.setNombreMateria(resultado.getString("nombre_materia"));
                    calificacion.setNumeroCuatrimestre(resultado.getInt("numero_cuatrimestre"));
                    calificacion.setNombrePeriodo(resultado.getString("nombre_periodo"));
                    calificacion.setNombreGrupo(resultado.getString("nombre_grupo"));
                    calificacion.setIdTrayectoria(resultado.getInt("id_trayectoria"));
                    calificacion.setNombreNivel(resultado.getString("nombre_nivel"));
                    lista.add(calificacion);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }



    public ArrayList<Calificacion> listarPorCarrera(Integer idCarrera)
    {
        ArrayList<Calificacion> lista = new ArrayList<>();
        String sql = "SELECT im.id_inscripcion_materia, im.intento, im.estado AS estado_materia, "
                + "t.id_alumno, t.matricula, CONCAT(p.nombres, ' ', p.apellido_paterno, ' ', p.apellido_materno) AS nombre_alumno, "
                + "car.id_carrera, car.nombre_carrera, "
                + "mat.nombre_materia, pc.numero_cuatrimestre, per.nombre_periodo, g.nombre_grupo, "
                + "cal.id_calificacion, cal.parcial_1, cal.parcial_2, cal.parcial_3, cal.promedio_final "
                + "FROM inscripcion_materias im "
                + "JOIN inscripciones i ON im.id_inscripcion = i.id_inscripcion "
                + "JOIN trayectorias_academicas t ON i.id_trayectoria = t.id_trayectoria "
                + "JOIN alumnos a ON t.id_alumno = a.id_alumno "
                + "JOIN personas p ON a.id_persona = p.id_persona "
                + "JOIN grupo_materias gm ON im.id_grupo_materia = gm.id_grupo_materia "
                + "JOIN materias mat ON gm.id_materia = mat.id_materia "
                + "JOIN grupos g ON i.id_grupo = g.id_grupo "
                + "JOIN plan_cuatrimestres pc ON g.id_plan_cuatrimestre = pc.id_plan_cuatrimestre "
                + "JOIN planes_estudio pe ON pc.id_plan = pe.id_plan "
                + "JOIN carreras car ON pe.id_carrera = car.id_carrera "
                + "JOIN periodos_escolares per ON i.id_periodo = per.id_periodo "
                + "LEFT JOIN calificaciones cal ON cal.id_inscripcion_materia = im.id_inscripcion_materia "
                + "WHERE im.estado <> 'Baja' "
                + (idCarrera != null ? "AND car.id_carrera = ? " : "")
                + "ORDER BY car.nombre_carrera, p.apellido_paterno, p.apellido_materno, p.nombres, pc.numero_cuatrimestre, mat.nombre_materia";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            if (idCarrera != null)
            {
                sentencia.setInt(1, idCarrera);
            }

            try (ResultSet resultado = sentencia.executeQuery())
            {
                while (resultado.next())
                {
                    Calificacion calificacion = construirCalificacion(resultado);
                    calificacion.setIdAlumno(resultado.getInt("id_alumno"));
                    calificacion.setIdCarrera(resultado.getInt("id_carrera"));
                    calificacion.setNombreCarrera(resultado.getString("nombre_carrera"));
                    calificacion.setNombreMateria(resultado.getString("nombre_materia"));
                    calificacion.setNumeroCuatrimestre(resultado.getInt("numero_cuatrimestre"));
                    calificacion.setNombrePeriodo(resultado.getString("nombre_periodo"));
                    calificacion.setNombreGrupo(resultado.getString("nombre_grupo"));
                    lista.add(calificacion);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public ArrayList<Calificacion> listarPorCarreras(List<Integer> idsCarrera)
    {
        if (idsCarrera == null || idsCarrera.isEmpty()) return new ArrayList<>();
        String marcadores = String.join(",", java.util.Collections.nCopies(idsCarrera.size(), "?"));
        ArrayList<Calificacion> lista = new ArrayList<>();
        String sql = "SELECT im.id_inscripcion_materia, im.intento, im.estado AS estado_materia, t.id_alumno, t.matricula, "
                + "CONCAT(p.nombres, ' ', p.apellido_paterno, ' ', p.apellido_materno) AS nombre_alumno, car.id_carrera, car.nombre_carrera, "
                + "mat.nombre_materia, pc.numero_cuatrimestre, per.nombre_periodo, g.nombre_grupo, cal.id_calificacion, cal.parcial_1, cal.parcial_2, cal.parcial_3, cal.promedio_final "
                + "FROM inscripcion_materias im JOIN inscripciones i ON im.id_inscripcion=i.id_inscripcion JOIN trayectorias_academicas t ON i.id_trayectoria=t.id_trayectoria "
                + "JOIN alumnos a ON t.id_alumno=a.id_alumno JOIN personas p ON a.id_persona=p.id_persona JOIN grupo_materias gm ON im.id_grupo_materia=gm.id_grupo_materia "
                + "JOIN materias mat ON gm.id_materia=mat.id_materia JOIN grupos g ON i.id_grupo=g.id_grupo JOIN plan_cuatrimestres pc ON g.id_plan_cuatrimestre=pc.id_plan_cuatrimestre "
                + "JOIN planes_estudio pe ON pc.id_plan=pe.id_plan JOIN carreras car ON pe.id_carrera=car.id_carrera JOIN periodos_escolares per ON i.id_periodo=per.id_periodo "
                + "LEFT JOIN calificaciones cal ON cal.id_inscripcion_materia=im.id_inscripcion_materia WHERE im.estado <> 'Baja' AND car.id_carrera IN (" + marcadores + ") "
                + "ORDER BY car.nombre_carrera, p.apellido_paterno, p.nombres, pc.numero_cuatrimestre, mat.nombre_materia";
        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            for (int indice = 0; indice < idsCarrera.size(); indice++) sentencia.setInt(indice + 1, idsCarrera.get(indice));
            try (ResultSet resultado = sentencia.executeQuery())
            {
                while (resultado.next())
                { Calificacion calificacion = construirCalificacion(resultado); calificacion.setIdAlumno(resultado.getInt("id_alumno")); calificacion.setIdCarrera(resultado.getInt("id_carrera")); calificacion.setNombreCarrera(resultado.getString("nombre_carrera")); calificacion.setNombreMateria(resultado.getString("nombre_materia")); calificacion.setNumeroCuatrimestre(resultado.getInt("numero_cuatrimestre")); calificacion.setNombrePeriodo(resultado.getString("nombre_periodo")); calificacion.setNombreGrupo(resultado.getString("nombre_grupo")); lista.add(calificacion); }
            }
        }
        catch (SQLException excepcion)
        { throw new RuntimeException(excepcion); }
        return lista;
    }



    public Calificacion buscarPorInscripcionMateria(int idInscripcionMateria)
    {
        String sql = "SELECT im.id_inscripcion_materia, im.intento, im.estado AS estado_materia, "
                + "t.matricula, CONCAT(p.nombres, ' ', p.apellido_paterno, ' ', p.apellido_materno) AS nombre_alumno, "
                + "car.id_carrera, car.nombre_carrera, "
                + "mat.nombre_materia, pc.numero_cuatrimestre, per.nombre_periodo, g.nombre_grupo, "
                + "cal.id_calificacion, cal.parcial_1, cal.parcial_2, cal.parcial_3, cal.promedio_final "
                + "FROM inscripcion_materias im "
                + "JOIN inscripciones i ON im.id_inscripcion = i.id_inscripcion "
                + "JOIN trayectorias_academicas t ON i.id_trayectoria = t.id_trayectoria "
                + "JOIN alumnos a ON t.id_alumno = a.id_alumno "
                + "JOIN personas p ON a.id_persona = p.id_persona "
                + "JOIN grupo_materias gm ON im.id_grupo_materia = gm.id_grupo_materia "
                + "JOIN materias mat ON gm.id_materia = mat.id_materia "
                + "JOIN grupos g ON i.id_grupo = g.id_grupo "
                + "JOIN plan_cuatrimestres pc ON g.id_plan_cuatrimestre = pc.id_plan_cuatrimestre "
                + "JOIN planes_estudio pe ON pc.id_plan = pe.id_plan "
                + "JOIN carreras car ON pe.id_carrera = car.id_carrera "
                + "JOIN periodos_escolares per ON i.id_periodo = per.id_periodo "
                + "LEFT JOIN calificaciones cal ON cal.id_inscripcion_materia = im.id_inscripcion_materia "
                + "WHERE im.id_inscripcion_materia = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idInscripcionMateria);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    Calificacion calificacion = new Calificacion();
                    calificacion.setIdInscripcionMateria(resultado.getInt("id_inscripcion_materia"));
                    calificacion.setIntento(resultado.getInt("intento"));
                    calificacion.setEstadoMateria(resultado.getString("estado_materia"));
                    calificacion.setMatricula(resultado.getString("matricula"));
                    calificacion.setNombreAlumno(resultado.getString("nombre_alumno"));
                    calificacion.setIdCarrera(resultado.getInt("id_carrera"));
                    calificacion.setNombreCarrera(resultado.getString("nombre_carrera"));
                    calificacion.setNombreMateria(resultado.getString("nombre_materia"));
                    calificacion.setNumeroCuatrimestre(resultado.getInt("numero_cuatrimestre"));
                    calificacion.setNombrePeriodo(resultado.getString("nombre_periodo"));
                    calificacion.setNombreGrupo(resultado.getString("nombre_grupo"));
                    calificacion.setIdCalificacion(resultado.getInt("id_calificacion"));
                    calificacion.setParcial1(obtenerDoubleONull(resultado, "parcial_1"));
                    calificacion.setParcial2(obtenerDoubleONull(resultado, "parcial_2"));
                    calificacion.setParcial3(obtenerDoubleONull(resultado, "parcial_3"));
                    calificacion.setPromedioFinal(obtenerDoubleONull(resultado, "promedio_final"));
                    return calificacion;
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public int buscarIdAlumnoPorInscripcionMateria(int idInscripcionMateria)
    {
        String sql = "SELECT t.id_alumno FROM inscripcion_materias im JOIN inscripciones i ON im.id_inscripcion = i.id_inscripcion JOIN trayectorias_academicas t ON i.id_trayectoria = t.id_trayectoria WHERE im.id_inscripcion_materia = ?";
        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idInscripcionMateria);
            try (ResultSet resultado = sentencia.executeQuery())
            { return resultado.next() ? resultado.getInt(1) : 0; }
        }
        catch (SQLException excepcion)
        { throw new RuntimeException(excepcion); }
    }



    public boolean perteneceAGrupoMateria(int idInscripcionMateria, int idGrupoMateria)
    {
        String sql = "SELECT COUNT(*) FROM inscripcion_materias WHERE id_inscripcion_materia = ? AND id_grupo_materia = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idInscripcionMateria);
            sentencia.setInt(2, idGrupoMateria);

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



    public void actualizarParcial(int idInscripcionMateria, int numeroParcial, Double valor)
    {
        String columna = columnaParcial(numeroParcial);
        String sql = "UPDATE calificaciones SET " + columna + " = ? WHERE id_inscripcion_materia = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion())
        {
            int filasAfectadas = ejecutarActualizacionParcial(conexion, sql, valor, idInscripcionMateria);

            if (filasAfectadas == 0)
            {
                crearVacia(conexion, idInscripcionMateria);
                filasAfectadas = ejecutarActualizacionParcial(conexion, sql, valor, idInscripcionMateria);

                if (filasAfectadas == 0)
                {
                    throw new RuntimeException(
                            "No se pudo crear ni actualizar la fila de calificaciones para id_inscripcion_materia = "
                                    + idInscripcionMateria);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void actualizarPromedioFinal(int idInscripcionMateria, Double promedioFinal)
    {
        String sql = "UPDATE calificaciones SET promedio_final = ? WHERE id_inscripcion_materia = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            if (promedioFinal == null)
            {
                sentencia.setNull(1, java.sql.Types.DECIMAL);
            }
            else
            {
                sentencia.setDouble(1, promedioFinal);
            }
            sentencia.setInt(2, idInscripcionMateria);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void crearVacia(int idInscripcionMateria)
    {
        try (Connection conexion = ConexionMySQL.obtenerConexion())
        {
            crearVacia(conexion, idInscripcionMateria);
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void eliminarPorInscripcionMateria(int idInscripcionMateria)
    {
        String sql = "DELETE FROM calificaciones WHERE id_inscripcion_materia = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idInscripcionMateria);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    private String columnaParcial(int numeroParcial)
    {
        switch (numeroParcial)
        {
            case 1:
                return "parcial_1";
            case 2:
                return "parcial_2";
            case 3:
                return "parcial_3";
            default:
                throw new IllegalArgumentException("Número de parcial inválido: " + numeroParcial);
        }
    }



    private int ejecutarActualizacionParcial(Connection conexion, String sql, Double valor, int idInscripcionMateria)
            throws SQLException
    {
        try (PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            if (valor == null)
            {
                sentencia.setNull(1, java.sql.Types.DECIMAL);
            }
            else
            {
                sentencia.setDouble(1, valor);
            }
            sentencia.setInt(2, idInscripcionMateria);
            return sentencia.executeUpdate();
        }
    }



    private void crearVacia(Connection conexion, int idInscripcionMateria) throws SQLException
    {
        String sql = "INSERT INTO calificaciones (id_inscripcion_materia) VALUES (?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idInscripcionMateria);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {

            if (!esErrorDeDuplicado(excepcion))
            {
                throw excepcion;
            }
        }
    }

    private boolean esErrorDeDuplicado(SQLException excepcion)
    {
        String estado = excepcion.getSQLState();
        return estado != null && estado.startsWith("23");
    }

    private Calificacion construirCalificacion(ResultSet resultado) throws SQLException
    {
        Calificacion calificacion = new Calificacion();
        calificacion.setIdInscripcionMateria(resultado.getInt("id_inscripcion_materia"));
        calificacion.setIntento(resultado.getInt("intento"));
        calificacion.setEstadoMateria(resultado.getString("estado_materia"));
        calificacion.setMatricula(resultado.getString("matricula"));
        calificacion.setNombreAlumno(resultado.getString("nombre_alumno"));
        calificacion.setIdCalificacion(resultado.getInt("id_calificacion"));

        calificacion.setParcial1(obtenerDoubleONull(resultado, "parcial_1"));
        calificacion.setParcial2(obtenerDoubleONull(resultado, "parcial_2"));
        calificacion.setParcial3(obtenerDoubleONull(resultado, "parcial_3"));
        calificacion.setPromedioFinal(obtenerDoubleONull(resultado, "promedio_final"));

        return calificacion;
    }

    private Double obtenerDoubleONull(ResultSet resultado, String columna) throws SQLException
    {
        double valor = resultado.getDouble(columna);
        return resultado.wasNull() ? null : valor;
    }
}