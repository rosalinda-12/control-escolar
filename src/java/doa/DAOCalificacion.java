package doa;

import conexion.ConexionMySQL;
import modelo.Calificacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAOCalificacion
{
    /**
     * Alumnos inscritos (y no dados de baja) en una materia de grupo, con
     * su calificación actual, para la pantalla de captura del maestro.
     */
    public ArrayList<Calificacion> listarPorGrupoMateria(int idGrupoMateria)
    {
        ArrayList<Calificacion> lista = new ArrayList<>();
        String sql = "SELECT im.id_inscripcion_materia, im.intento, im.estado AS estado_materia, "
                + "t.matricula, CONCAT(p.nombres, ' ', p.apellido_paterno, ' ', p.apellido_materno) AS nombre_alumno, "
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

    /**
     * Todas las materias cursadas por un alumno (en cualquier cuatrimestre
     * y periodo), con su calificación, para armar la boleta que ve el
     * propio alumno. No incluye materias dadas de baja.
     */
    public ArrayList<Calificacion> listarPorAlumno(int idAlumno)
    {
        ArrayList<Calificacion> lista = new ArrayList<>();
        String sql = "SELECT im.id_inscripcion_materia, im.intento, im.estado AS estado_materia, "
                + "mat.nombre_materia, pc.numero_cuatrimestre, per.nombre_periodo, g.nombre_grupo, "
                + "cal.id_calificacion, cal.parcial_1, cal.parcial_2, cal.parcial_3, cal.promedio_final "
                + "FROM inscripcion_materias im "
                + "JOIN inscripciones i ON im.id_inscripcion = i.id_inscripcion "
                + "JOIN trayectorias_academicas t ON i.id_trayectoria = t.id_trayectoria "
                + "JOIN grupo_materias gm ON im.id_grupo_materia = gm.id_grupo_materia "
                + "JOIN materias mat ON gm.id_materia = mat.id_materia "
                + "JOIN grupos g ON i.id_grupo = g.id_grupo "
                + "JOIN plan_cuatrimestres pc ON g.id_plan_cuatrimestre = pc.id_plan_cuatrimestre "
                + "JOIN periodos_escolares per ON i.id_periodo = per.id_periodo "
                + "LEFT JOIN calificaciones cal ON cal.id_inscripcion_materia = im.id_inscripcion_materia "
                + "WHERE t.id_alumno = ? AND i.estado = 'Activa' AND im.estado <> 'Baja' "
                + "ORDER BY pc.numero_cuatrimestre, mat.nombre_materia";

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

    /**
     * Todas las calificaciones del sistema, con carrera/alumno/materia,
     * para la pantalla de Administrador y Control Escolar (que pueden ver
     * todas las carreras) y para el Subdirector (que solo puede ver la
     * suya, pasando su id_carrera). idCarrera == null trae todas.
     */
    public ArrayList<Calificacion> listarPorCarrera(Integer idCarrera)
    {
        ArrayList<Calificacion> lista = new ArrayList<>();
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
                + "WHERE i.estado = 'Activa' AND im.estado <> 'Baja' "
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

    /**
     * Lee una sola fila de calificaciones (sin datos del alumno), útil
     * después de actualizar un parcial para decidir si ya se puede calcular
     * el promedio final.
     */
    public Calificacion buscarPorInscripcionMateria(int idInscripcionMateria)
    {
        String sql = "SELECT id_calificacion, id_inscripcion_materia, parcial_1, parcial_2, parcial_3, promedio_final "
                + "FROM calificaciones WHERE id_inscripcion_materia = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idInscripcionMateria);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    Calificacion calificacion = new Calificacion();
                    calificacion.setIdCalificacion(resultado.getInt("id_calificacion"));
                    calificacion.setIdInscripcionMateria(resultado.getInt("id_inscripcion_materia"));
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

    /**
     * Confirma que la inscripción-materia pertenezca a esa materia de
     * grupo, para no permitir capturar una calificación de otro grupo
     * manipulando el id enviado desde el formulario.
     */
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

    /**
     * Actualiza un parcial. Si la fila en 'calificaciones' aún no existe
     * para esa inscripción-materia (por ejemplo, porque crearVacia nunca
     * se invocó al momento de la inscripción), la crea automáticamente
     * y reintenta el UPDATE dentro de la misma conexión, evitando que la
     * captura del maestro se pierda silenciosamente.
     */
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

    /**
     * Ejecuta el UPDATE de un parcial reutilizando una conexión ya abierta.
     * Devuelve el número de filas afectadas (0 si no existía la fila).
     */
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

    /**
     * Crea la fila vacía de calificaciones reutilizando una conexión ya
     * abierta (usado internamente por actualizarParcial como auto-reparación).
     */
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
            // Si ya existe (p. ej. condición de carrera con otra petición),
            // no es un error real: seguimos y el UPDATE de reintento la encontrará.
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