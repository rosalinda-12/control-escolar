package doa;

import conexion.ConexionMySQL;
import modelo.CicloEscolar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOCicloEscolar
{
    public ArrayList<CicloEscolar> listar()
    {
        ArrayList<CicloEscolar> lista = new ArrayList<>();
        String sql = "SELECT id_ciclo, nombre_ciclo FROM ciclos_escolares ORDER BY nombre_ciclo DESC";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirCiclo(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public CicloEscolar buscarPorId(int idCiclo)
    {
        String sql = "SELECT id_ciclo, nombre_ciclo FROM ciclos_escolares WHERE id_ciclo = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idCiclo);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirCiclo(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public boolean existeNombre(String nombreCiclo)
    {
        String sql = "SELECT COUNT(*) FROM ciclos_escolares WHERE nombre_ciclo = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, nombreCiclo);

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

    public int agregar(CicloEscolar ciclo)
    {
        String sql = "INSERT INTO ciclos_escolares (nombre_ciclo) VALUES (?)";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            sentencia.setString(1, ciclo.getNombreCiclo());
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

    public void eliminar(int idCiclo)
    {
        String sql = "DELETE FROM ciclos_escolares WHERE id_ciclo = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idCiclo);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    private CicloEscolar construirCiclo(ResultSet resultado) throws SQLException
    {
        CicloEscolar ciclo = new CicloEscolar();
        ciclo.setIdCiclo(resultado.getInt("id_ciclo"));
        ciclo.setNombreCiclo(resultado.getString("nombre_ciclo"));
        return ciclo;
    }
}
