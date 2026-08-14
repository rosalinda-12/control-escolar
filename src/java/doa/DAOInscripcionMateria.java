package doa;

import conexion.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DAOInscripcionMateria
{
    /**
     * A partir de las materias ya asignadas al grupo (grupo_materias, que a
     * su vez vino del currículo del plan), genera automáticamente una
     * inscripcion_materia por cada una, y su calificación vacía asociada.
     * El admin no tiene que dar de alta materia por materia.
     */
    public void generarParaInscripcion(int idInscripcion, int idGrupo)
    {
        String sqlMaterias = "INSERT INTO inscripcion_materias (id_inscripcion, id_grupo_materia, intento, estado) "
                + "SELECT ?, id_grupo_materia, 1, 'Cursando' FROM grupo_materias WHERE id_grupo = ?";

        String sqlCalificaciones = "INSERT INTO calificaciones (id_inscripcion_materia) "
                + "SELECT id_inscripcion_materia FROM inscripcion_materias WHERE id_inscripcion = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion())
        {
            try (PreparedStatement sentenciaMaterias = conexion.prepareStatement(sqlMaterias))
            {
                sentenciaMaterias.setInt(1, idInscripcion);
                sentenciaMaterias.setInt(2, idGrupo);
                sentenciaMaterias.executeUpdate();
            }

            try (PreparedStatement sentenciaCalificaciones = conexion.prepareStatement(sqlCalificaciones))
            {
                sentenciaCalificaciones.setInt(1, idInscripcion);
                sentenciaCalificaciones.executeUpdate();
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void actualizarEstado(int idInscripcionMateria, String estado)
    {
        String sql = "UPDATE inscripcion_materias SET estado = ? WHERE id_inscripcion_materia = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, estado);
            sentencia.setInt(2, idInscripcionMateria);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public int contarMaterias(int idInscripcion)
    {
        String sql = "SELECT COUNT(*) FROM inscripcion_materias WHERE id_inscripcion = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idInscripcion);

            try (java.sql.ResultSet resultado = sentencia.executeQuery())
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
