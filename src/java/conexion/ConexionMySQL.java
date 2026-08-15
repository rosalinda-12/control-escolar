package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionMySQL
{
    private static String servidor;
    private static String nombreBD;
    private static String usuario;
    private static String password;
    private static String puerto;

    public static Connection obtenerConexion() throws SQLException
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException excepcion)
        {
            System.out.println("No se Cargo el Driver");
        }

        servidor = "localhost";
        nombreBD = "c_3";
        usuario = "root";
        password = "";
        puerto = "3306";

        return DriverManager.getConnection("jdbc:mysql://" + servidor + ":" + puerto + "/" + nombreBD, usuario, password);
    }
}
