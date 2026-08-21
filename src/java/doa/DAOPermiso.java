package doa;

import conexion.ConexionMySQL;
import modelo.Permiso;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DAOPermiso
{
    public ArrayList<Permiso> listarCatalogo()
    {
        ArrayList<Permiso> lista = new ArrayList<>();
        String sql = "SELECT id_permiso, clave, modulo, descripcion FROM permisos ORDER BY modulo, clave";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirPermiso(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public ArrayList<Permiso> listarConEstatusPorRol(int idRol)
    {
        ArrayList<Permiso> catalogo = listarCatalogo();
        Set<Integer> activos = listarIdsActivosPorRol(idRol);

        for (Permiso permiso : catalogo)
        {
            permiso.setActivo(activos.contains(permiso.getIdPermiso()));
        }

        return catalogo;
    }

    private Set<Integer> listarIdsActivosPorRol(int idRol)
    {
        Set<Integer> activos = new HashSet<>();
        String sql = "SELECT id_permiso FROM rol_permisos WHERE id_rol = ? AND activo = 1";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idRol);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                while (resultado.next())
                {
                    activos.add(resultado.getInt("id_permiso"));
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return activos;
    }

    public Set<String> listarClavesActivasPorRol(int idRol)
    {
        Set<String> claves = new HashSet<>();
        String sql = "SELECT p.clave FROM rol_permisos rp "
                + "JOIN permisos p ON rp.id_permiso = p.id_permiso "
                + "WHERE rp.id_rol = ? AND rp.activo = 1";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idRol);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                while (resultado.next())
                {
                    claves.add(resultado.getString("clave"));
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return claves;
    }

    public void establecer(int idRol, int idPermiso, boolean activo)
    {
        String sql = "INSERT INTO rol_permisos (id_rol, id_permiso, activo) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE activo = VALUES(activo)";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idRol);
            sentencia.setInt(2, idPermiso);
            sentencia.setBoolean(3, activo);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    private Permiso construirPermiso(ResultSet resultado) throws SQLException
    {
        Permiso permiso = new Permiso();
        permiso.setIdPermiso(resultado.getInt("id_permiso"));
        permiso.setClave(resultado.getString("clave"));
        permiso.setModulo(resultado.getString("modulo"));
        permiso.setDescripcion(resultado.getString("descripcion"));
        return permiso;
    }
}
