package doa;

import conexion.ConexionMySQL;
import modelo.Persona;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DAOPersona
{
    public boolean existeCorreo(String correo)
    {
        String sql = "SELECT COUNT(*) FROM personas WHERE correo = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, correo);

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

    public int agregar(Persona persona)
    {
        String sql = "INSERT INTO personas (nombres, apellido_paterno, apellido_materno, correo, estatus) VALUES (?, ?, ?, ?, 'Activo')";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            sentencia.setString(1, persona.getNombres());
            sentencia.setString(2, persona.getApellidoPaterno());
            sentencia.setString(3, persona.getApellidoMaterno());
            sentencia.setString(4, persona.getCorreo());
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

    public void modificar(Persona persona)
    {
        String sql = "UPDATE personas SET nombres = ?, apellido_paterno = ?, apellido_materno = ?, correo = ? WHERE id_persona = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, persona.getNombres());
            sentencia.setString(2, persona.getApellidoPaterno());
            sentencia.setString(3, persona.getApellidoMaterno());
            sentencia.setString(4, persona.getCorreo());
            sentencia.setInt(5, persona.getIdPersona());
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void actualizarEstatus(int idPersona, String estatus)
    {
        String sql = "UPDATE personas SET estatus = ? WHERE id_persona = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, estatus);
            sentencia.setInt(2, idPersona);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }
}
