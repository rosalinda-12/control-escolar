package doa;

import conexion.ConexionMySQL;
import modelo.Bitacora;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class DAOBitacora
{
    public void registrar(Bitacora bitacora)
    {
        String sql = "INSERT INTO auditoria (id_usuario, accion, modulo, tabla_afectada, id_registro_afectado, datos_nuevos) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql))
        {
            sentencia.setInt(1, bitacora.getIdUsuario());
            sentencia.setString(2, bitacora.getTipoAccion());
            sentencia.setString(3, bitacora.getEntidad());
            sentencia.setString(4, bitacora.getEntidad());
            sentencia.setInt(5, bitacora.getIdRegistroAfectado());
            sentencia.setString(6, bitacora.getDescripcion());
            sentencia.executeUpdate();
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }
    }

    public ArrayList<Bitacora> listar(String entidadFiltro, String correoFiltro)
    {
        ArrayList<Bitacora> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT a.id_auditoria, a.id_usuario, a.accion, a.modulo, a.id_registro_afectado, "
                + "a.datos_nuevos, a.fecha_accion, u.correo AS correo_usuario "
                + "FROM auditoria a JOIN usuarios u ON a.id_usuario = u.id_usuario WHERE 1 = 1 ");

        ArrayList<String> parametros = new ArrayList<>();

        if (entidadFiltro != null && !entidadFiltro.isBlank())
        {
            sql.append("AND a.modulo = ? ");
            parametros.add(entidadFiltro);
        }

        if (correoFiltro != null && !correoFiltro.isBlank())
        {
            sql.append("AND u.correo = ? ");
            parametros.add(correoFiltro);
        }

        sql.append("ORDER BY a.fecha_accion DESC");

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql.toString()))
        {
            for (int indice = 0; indice < parametros.size(); indice++)
            {
                sentencia.setString(indice + 1, parametros.get(indice));
            }

            try (ResultSet resultado = sentencia.executeQuery())
            {
                while (resultado.next())
                {
                    lista.add(construirBitacora(resultado));
                }
            }
        }
        catch (SQLException excepcion)
        {
            throw new RuntimeException(excepcion);
        }

        return lista;
    }

    private Bitacora construirBitacora(ResultSet resultado) throws SQLException
    {
        Bitacora bitacora = new Bitacora();
        bitacora.setIdBitacora(resultado.getInt("id_auditoria"));
        bitacora.setIdUsuario(resultado.getInt("id_usuario"));
        bitacora.setTipoAccion(resultado.getString("accion"));
        bitacora.setEntidad(resultado.getString("modulo"));
        bitacora.setIdRegistroAfectado(resultado.getInt("id_registro_afectado"));
        bitacora.setDescripcion(resultado.getString("datos_nuevos"));
        bitacora.setFechaAccion(resultado.getTimestamp("fecha_accion").toLocalDateTime());
        bitacora.setCorreoUsuario(resultado.getString("correo_usuario"));
        return bitacora;
    }
}
