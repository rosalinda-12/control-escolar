package doa;

import conexion.ConexionMySQL;
import modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

public class DAOUsuario
{
    private static final String SELECCION_BASE =
            "SELECT u.id_usuario, u.nombres, u.apellido_paterno, u.apellido_materno, u.correo, u.contrasena, "
            + "u.id_rol, r.nombre_rol, u.id_profesor, u.id_alumno, u.codigo_verificacion, u.expiracion_codigo, "
            + "u.correo_verificado, u.estatus_registro, u.requiere_cambio_contrasena, u.fecha_creacion "
            + "FROM usuarios u JOIN roles r ON u.id_rol = r.id_rol ";

    public boolean existeAdministrador()
    {
        String sql = "SELECT COUNT(*) FROM usuarios u JOIN roles r ON u.id_rol = r.id_rol WHERE r.nombre_rol = 'Administrador'";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            if (resultado.next())
            {
                return resultado.getInt(1) > 0;
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return false;
    }

    public Usuario buscarPorCorreo(String correo)
    {
        String sql = SELECCION_BASE + "WHERE u.correo = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, correo);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirUsuario(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public Usuario buscarPorId(int idUsuario)
    {
        String sql = SELECCION_BASE + "WHERE u.id_usuario = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idUsuario);

            try (ResultSet resultado = sentencia.executeQuery())
            {
                if (resultado.next())
                {
                    return construirUsuario(resultado);
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return null;
    }

    public ArrayList<Usuario> listarSolicitudesPendientes()
    {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "WHERE u.estatus_registro = 'Pendiente' ORDER BY u.fecha_creacion";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                lista.add(construirUsuario(resultado));
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    public int agregar(Usuario usuario)
    {
        String sql = "INSERT INTO usuarios (nombres, apellido_paterno, apellido_materno, correo, contrasena, id_rol, "
                + "id_profesor, id_alumno, codigo_verificacion, expiracion_codigo, correo_verificado, estatus_registro, "
                + "requiere_cambio_contrasena) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            sentencia.setString(1, usuario.getNombres());
            sentencia.setString(2, usuario.getApellidoPaterno());
            sentencia.setString(3, usuario.getApellidoMaterno());
            sentencia.setString(4, usuario.getCorreo());
            sentencia.setString(5, usuario.getContrasena());
            sentencia.setInt(6, usuario.getIdRol());
            establecerEnteroONulo(sentencia, 7, usuario.getIdProfesor());
            establecerEnteroONulo(sentencia, 8, usuario.getIdAlumno());
            sentencia.setString(9, usuario.getCodigoVerificacion());
            sentencia.setTimestamp(10, Timestamp.valueOf(usuario.getExpiracionCodigo()));
            sentencia.setBoolean(11, usuario.isCorreoVerificado());
            sentencia.setString(12, usuario.getEstatusRegistro());
            sentencia.setBoolean(13, usuario.isRequiereCambioContrasena());
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

    public void actualizarCodigoVerificacion(int idUsuario, String codigo, java.time.LocalDateTime expiracion)
    {
        String sql = "UPDATE usuarios SET codigo_verificacion = ?, expiracion_codigo = ? WHERE id_usuario = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, codigo);
            sentencia.setTimestamp(2, Timestamp.valueOf(expiracion));
            sentencia.setInt(3, idUsuario);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void marcarCorreoVerificado(int idUsuario)
    {
        String sql = "UPDATE usuarios SET correo_verificado = TRUE, codigo_verificacion = NULL WHERE id_usuario = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idUsuario);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void actualizarEstatusRegistro(int idUsuario, String estatusRegistro)
    {
        String sql = "UPDATE usuarios SET estatus_registro = ? WHERE id_usuario = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, estatusRegistro);
            sentencia.setInt(2, idUsuario);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void actualizarContrasena(int idUsuario, String contrasenaNueva)
    {
        String sql = "UPDATE usuarios SET contrasena = ?, requiere_cambio_contrasena = FALSE WHERE id_usuario = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, contrasenaNueva);
            sentencia.setInt(2, idUsuario);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public void marcarPrimerInicioSesionCompletado(int idUsuario)
    {
        String sql = "UPDATE usuarios SET requiere_cambio_contrasena = FALSE WHERE id_usuario = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idUsuario);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    private void establecerEnteroONulo(PreparedStatement sentencia, int posicion, Integer valor) throws SQLException
    {
        if (valor == null)
        {
            sentencia.setNull(posicion, java.sql.Types.INTEGER);
        }
        else
        {
            sentencia.setInt(posicion, valor);
        }
    }

    private Usuario construirUsuario(ResultSet resultado) throws SQLException
    {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(resultado.getInt("id_usuario"));
        usuario.setNombres(resultado.getString("nombres"));
        usuario.setApellidoPaterno(resultado.getString("apellido_paterno"));
        usuario.setApellidoMaterno(resultado.getString("apellido_materno"));
        usuario.setCorreo(resultado.getString("correo"));
        usuario.setContrasena(resultado.getString("contrasena"));
        usuario.setIdRol(resultado.getInt("id_rol"));
        usuario.setNombreRol(resultado.getString("nombre_rol"));

        int idProfesor = resultado.getInt("id_profesor");
        usuario.setIdProfesor(resultado.wasNull() ? null : idProfesor);

        int idAlumno = resultado.getInt("id_alumno");
        usuario.setIdAlumno(resultado.wasNull() ? null : idAlumno);

        usuario.setCodigoVerificacion(resultado.getString("codigo_verificacion"));

        Timestamp expiracion = resultado.getTimestamp("expiracion_codigo");
        usuario.setExpiracionCodigo(expiracion == null ? null : expiracion.toLocalDateTime());

        usuario.setCorreoVerificado(resultado.getBoolean("correo_verificado"));
        usuario.setEstatusRegistro(resultado.getString("estatus_registro"));
        usuario.setRequiereCambioContrasena(resultado.getBoolean("requiere_cambio_contrasena"));

        Timestamp fechaCreacion = resultado.getTimestamp("fecha_creacion");
        usuario.setFechaCreacion(fechaCreacion == null ? null : fechaCreacion.toLocalDateTime());

        return usuario;
    }
}
