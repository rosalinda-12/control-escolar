package servicio;

import doa.DAOBitacora;
import modelo.Bitacora;
import modelo.Usuario;
import java.util.ArrayList;

public class ServicioBitacora
{
    private final DAOBitacora daoBitacora;

    public ServicioBitacora()
    {
        this.daoBitacora = new DAOBitacora();
    }

    public void registrarAlta(Usuario usuarioResponsable, String entidad, int idRegistroAfectado, String descripcion)
    {
        registrar(usuarioResponsable, "ALTA", entidad, idRegistroAfectado, descripcion);
    }

    public void registrarBaja(Usuario usuarioResponsable, String entidad, int idRegistroAfectado, String descripcion)
    {
        registrar(usuarioResponsable, "BAJA", entidad, idRegistroAfectado, descripcion);
    }

    public ArrayList<Bitacora> consultar(String entidadFiltro, String correoFiltro)
    {
        return daoBitacora.listar(entidadFiltro, correoFiltro);
    }

    private void registrar(Usuario usuarioResponsable, String tipoAccion, String entidad, int idRegistroAfectado, String descripcion)
    {
        Bitacora bitacora = new Bitacora();
        bitacora.setIdUsuario(usuarioResponsable.getIdUsuario());
        bitacora.setTipoAccion(tipoAccion);
        bitacora.setEntidad(entidad);
        bitacora.setIdRegistroAfectado(idRegistroAfectado);
        bitacora.setDescripcion(descripcion);
        daoBitacora.registrar(bitacora);
    }
}
