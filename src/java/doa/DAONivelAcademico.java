package doa;

import conexion.ConexionMySQL;
import modelo.NivelAcademico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAONivelAcademico
{
    public ArrayList<NivelAcademico> listar()
    {
        ArrayList<NivelAcademico> lista = new ArrayList<>();
        String sql = "SELECT id_nivel, nombre_nivel FROM niveles_academicos ORDER BY id_nivel";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirNivel(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public NivelAcademico buscarPorId(int idNivel)
    {
        String sql = "SELECT id_nivel, nombre_nivel FROM niveles_academicos WHERE id_nivel = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idNivel);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirNivel(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public boolean existeNombre(String nombreNivel)
    {
        String sql = "SELECT COUNT(*) FROM niveles_academicos WHERE nombre_nivel = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, nombreNivel);

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

    public int agregar(NivelAcademico nivel)
    {
        String sql = "INSERT INTO niveles_academicos (nombre_nivel) VALUES (?)";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            sentencia.setString(1, nivel.getNombreNivel());
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

    public void eliminar(int idNivel)
    {
        String sql = "DELETE FROM niveles_academicos WHERE id_nivel = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idNivel);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    private NivelAcademico construirNivel(ResultSet resultado) throws SQLException
    {
        NivelAcademico nivel = new NivelAcademico();
        nivel.setIdNivel(resultado.getInt("id_nivel"));
        nivel.setNombreNivel(resultado.getString("nombre_nivel"));
        return nivel;
    }
}
