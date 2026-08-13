package doa;

import conexion.ConexionMySQL;
import modelo.Rol;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAORol
{
    public ArrayList<Rol> listar()
    {
        ArrayList<Rol> lista = new ArrayList<>();
        String sql = "SELECT id_rol, nombre_rol FROM roles ORDER BY id_rol";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirRol(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public Rol buscarPorNombre(String nombreRol)
    {
        String sql = "SELECT id_rol, nombre_rol FROM roles WHERE nombre_rol = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, nombreRol);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirRol(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public Rol buscarPorId(int idRol)
    {
        String sql = "SELECT id_rol, nombre_rol FROM roles WHERE id_rol = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idRol);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirRol(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    private Rol construirRol(ResultSet resultado) throws SQLException
    {
        Rol rol = new Rol();
        rol.setIdRol(resultado.getInt("id_rol"));
        rol.setNombreRol(resultado.getString("nombre_rol"));
        return rol;
    }
}
