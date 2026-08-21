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
import java.util.List;

public class DAOUsuario
{
    private static final String SELECCION_BASE =
            "SELECT u.id_usuario, u.nombres, u.apellido_paterno, u.apellido_materno, u.correo, u.contrasena, "
            + "u.id_rol, r.nombre_rol, r.es_administrador_principal, u.id_profesor, u.id_alumno, u.id_carrera, "
            + "u.codigo_verificacion, u.expiracion_codigo, "
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
                    Usuario usuario = construirUsuario(resultado);
                    cargarCarreras(usuario);
                    return usuario;
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
                    Usuario usuario = construirUsuario(resultado);
                    cargarCarreras(usuario);
                    return usuario;
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
                Usuario usuario = construirUsuario(resultado);
                cargarCarreras(usuario);
                lista.add(usuario);
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }



    public ArrayList<Usuario> listarTodos()
    {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = SELECCION_BASE + "ORDER BY r.nombre_rol, u.nombres";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery())
        {
            while (resultado.next())
            {
                Usuario usuario = construirUsuario(resultado);
                cargarCarreras(usuario);
                lista.add(usuario);
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }



    public void actualizarRolYCarrera(int idUsuario, int idRol, Integer idCarrera)
    {
        String sql = "UPDATE usuarios SET id_rol = ?, id_carrera = ? WHERE id_usuario = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idRol);
            establecerEnteroONulo(sentencia, 2, idCarrera);
            sentencia.setInt(3, idUsuario);
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }



    public void desactivar(int idUsuario)
    {
        String sql = "UPDATE usuarios SET estatus_registro = 'Inactivo' WHERE id_usuario = ?";

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

    public boolean tieneRegistrosEnBitacora(int idUsuario)
    {
        String sql = "SELECT COUNT(*) FROM auditoria WHERE id_usuario = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, idUsuario);

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

    public void eliminar(int idUsuario)
    {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";

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

    public int agregar(Usuario usuario)
    {
        String sql = "INSERT INTO usuarios (nombres, apellido_paterno, apellido_materno, correo, contrasena, id_rol, "
                + "id_profesor, id_alumno, id_carrera, codigo_verificacion, expiracion_codigo, correo_verificado, estatus_registro, "
                + "requiere_cambio_contrasena) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            establecerEnteroONulo(sentencia, 9, usuario.getIdCarrera());
            sentencia.setString(10, usuario.getCodigoVerificacion());
            if (usuario.getExpiracionCodigo() != null)
            {
                sentencia.setTimestamp(11, Timestamp.valueOf(usuario.getExpiracionCodigo()));
            }
            else
            {
                sentencia.setNull(11, java.sql.Types.TIMESTAMP);
            }
            sentencia.setBoolean(12, usuario.isCorreoVerificado());
            sentencia.setString(13, usuario.getEstatusRegistro());
            sentencia.setBoolean(14, usuario.isRequiereCambioContrasena());
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



    public void limpiarCodigo(int idUsuario)
    {
        String sql = "UPDATE usuarios SET codigo_verificacion = NULL, expiracion_codigo = NULL WHERE id_usuario = ?";

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
        usuario.setAdministradorPrincipal(resultado.getBoolean("es_administrador_principal"));

        int idProfesor = resultado.getInt("id_profesor");
        usuario.setIdProfesor(resultado.wasNull() ? null : idProfesor);

        int idAlumno = resultado.getInt("id_alumno");
        usuario.setIdAlumno(resultado.wasNull() ? null : idAlumno);

        int idCarrera = resultado.getInt("id_carrera");
        usuario.setIdCarrera(resultado.wasNull() ? null : idCarrera);

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

    private void cargarCarreras(Usuario usuario)
    {
        if (!usuario.esSubdirector()) return;

        ArrayList<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_carrera FROM subdirector_carreras WHERE id_subdirector = "
                + "(SELECT id_subdirector FROM subdirectores WHERE id_persona = "
                + "(SELECT id_persona FROM personas WHERE correo = ?)) ORDER BY id_carrera";
        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setString(1, usuario.getCorreo());
            try (ResultSet resultado = sentencia.executeQuery())
            {
                while (resultado.next()) ids.add(resultado.getInt("id_carrera"));
            }
            if (!ids.isEmpty()) usuario.setIdsCarrera(ids);
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }
}
