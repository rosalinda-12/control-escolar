package doa;

import conexion.ConexionMySQL;
import modelo.Nivel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAONivel
{
    public ArrayList<Nivel> listar()
    {
        ArrayList<Nivel> lista = new ArrayList<>();
        String sql = "SELECT id_nivel, nombre_nivel, cuatrimestre_min, cuatrimestre_max FROM niveles ORDER BY id_nivel";

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

    public Nivel buscarPorId(int idNivel)
    {
        String sql = "SELECT id_nivel, nombre_nivel, cuatrimestre_min, cuatrimestre_max FROM niveles WHERE id_nivel = ?";

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

    private Nivel construirNivel(ResultSet resultado) throws SQLException
    {
        Nivel nivel = new Nivel();
        nivel.setIdNivel(resultado.getInt("id_nivel"));
        nivel.setNombreNivel(resultado.getString("nombre_nivel"));
        nivel.setCuatrimestreMinimo(resultado.getInt("cuatrimestre_min"));
        nivel.setCuatrimestreMaximo(resultado.getInt("cuatrimestre_max"));
        return nivel;
    }
}
