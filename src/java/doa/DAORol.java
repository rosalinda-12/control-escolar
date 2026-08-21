package doa;

import conexion.ConexionMySQL;
import modelo.Rol;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAORol
{
    private static final String SELECCION_BASE =
            "SELECT id_rol, nombre_rol, descripcion, es_administrador_principal FROM roles ";

    public ArrayList<Rol> listar()
    {
        ArrayList<Rol> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "ORDER BY id_rol";

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
        String sql = SELECCION_BASE + "WHERE nombre_rol = ?";

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
        String sql = SELECCION_BASE + "WHERE id_rol = ?";

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

    public boolean existeNombre(String nombreRol)
    {
        String sql = "SELECT COUNT(*) FROM roles WHERE nombre_rol = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, nombreRol);

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

    public int agregar(Rol rol)
    {
        String sql = "INSERT INTO roles (nombre_rol, descripcion, es_administrador_principal) VALUES (?, ?, 0)";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            sentencia.setString(1, rol.getNombreRol());
            sentencia.setString(2, rol.getDescripcion());
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

    private Rol construirRol(ResultSet resultado) throws SQLException
    {
        Rol rol = new Rol();
        rol.setIdRol(resultado.getInt("id_rol"));
        rol.setNombreRol(resultado.getString("nombre_rol"));
        rol.setDescripcion(resultado.getString("descripcion"));
        rol.setEsAdministradorPrincipal(resultado.getBoolean("es_administrador_principal"));
        return rol;
    }
}
