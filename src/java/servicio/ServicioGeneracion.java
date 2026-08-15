package servicio;

import doa.DAOGeneracion;
import modelo.Generacion;
import modelo.Usuario;
import java.util.ArrayList;

public class ServicioGeneracion
{
    private final DAOGeneracion daoGeneracion;
    private final ServicioBitacora servicioBitacora;

    public ServicioGeneracion()
    {
        this.daoGeneracion = new DAOGeneracion();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<Generacion> listar()
    {
        return daoGeneracion.listar();
    }

    public Generacion buscarPorId(int idGeneracion)
    {
        return daoGeneracion.buscarPorId(idGeneracion);
    }

    public void agregar(Generacion generacion, Usuario responsable)
    {
        int idGeneracion = daoGeneracion.agregar(generacion);
        servicioBitacora.registrarAlta(responsable, "generaciones", idGeneracion,
                "Alta de generación " + generacion.getNombreGeneracion());
    }

    public void modificar(Generacion generacion)
    {
        daoGeneracion.modificar(generacion);
    }

    public void eliminar(int idGeneracion, Usuario responsable)
    {
        Generacion generacion = daoGeneracion.buscarPorId(idGeneracion);
        daoGeneracion.eliminar(idGeneracion);
        servicioBitacora.registrarBaja(responsable, "generaciones", idGeneracion,
                "Baja de generación " + generacion.getNombreGeneracion());
    }
}
