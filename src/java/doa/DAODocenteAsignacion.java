package doa;

import conexion.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DAODocenteAsignacion
{
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
