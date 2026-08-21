package servicio;

import doa.DAOCarrera;
import modelo.Carrera;
import modelo.Usuario;
import java.util.ArrayList;

public class ServicioCarrera
{
    private final DAOCarrera daoCarrera;
    private final ServicioBitacora servicioBitacora;

    public ServicioCarrera()
    {
        this.daoCarrera = new DAOCarrera();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<Carrera> listar()
    {
        return daoCarrera.listar();
    }

    public ArrayList<Carrera> listarActivas()
    {
        return daoCarrera.listarActivas();
    }

    public Carrera buscarPorId(int idCarrera)
    {
        return daoCarrera.buscarPorId(idCarrera);
    }

    public ResultadoCarrera agregar(Carrera carrera, Usuario responsable)
    {
        if (daoCarrera.existeNombreOClave(carrera.getNombreCarrera(), carrera.getClaveCarrera(), null))
        {
            return ResultadoCarrera.fallo("Ya existe una carrera con ese nombre o esa clave.");
        }

        carrera.setEstatus("Activa");
        int idCarrera = daoCarrera.agregar(carrera);
        servicioBitacora.registrarAlta(responsable, "carreras", idCarrera,
                "Alta de carrera " + carrera.getNombreCarrera() + " (" + carrera.getClaveCarrera() + ")");

        return ResultadoCarrera.exito(idCarrera);
    }

    public ResultadoCarrera modificar(Carrera carrera, Usuario responsable)
    {
        if (daoCarrera.existeNombreOClave(carrera.getNombreCarrera(), carrera.getClaveCarrera(), carrera.getIdCarrera()))
        {
            return ResultadoCarrera.fallo("Ya existe otra carrera con ese nombre o esa clave.");
        }

        daoCarrera.modificar(carrera);
        servicioBitacora.registrarAlta(responsable, "carreras", carrera.getIdCarrera(),
                "Modificó la carrera " + carrera.getNombreCarrera());

        return ResultadoCarrera.exito(carrera.getIdCarrera());
    }



    public ResultadoCarrera eliminarODesactivar(int idCarrera, Usuario responsable)
    {
        Carrera carrera = daoCarrera.buscarPorId(idCarrera);

        if (carrera == null)
        {
            return ResultadoCarrera.fallo("La carrera ya no existe.");
        }

        if (daoCarrera.tienePlanesAsociados(idCarrera))
        {
            carrera.setEstatus("Inactiva");
            daoCarrera.modificar(carrera);
            servicioBitacora.registrarBaja(responsable, "carreras", idCarrera,
                    "Desactivó la carrera " + carrera.getNombreCarrera() + " (tiene planes de estudio asociados, no se puede eliminar)");
            return ResultadoCarrera.exitoDesactivada(idCarrera);
        }

        daoCarrera.eliminar(idCarrera);
        servicioBitacora.registrarBaja(responsable, "carreras", idCarrera,
                "Eliminó la carrera " + carrera.getNombreCarrera());

        return ResultadoCarrera.exito(idCarrera);
    }

    public static class ResultadoCarrera
    {
        private final boolean exito;
        private final boolean desactivadaEnLugarDeEliminada;
        private final String mensajeError;
        private final int idCarrera;

        private ResultadoCarrera(boolean exito, boolean desactivadaEnLugarDeEliminada, String mensajeError, int idCarrera)
        {
            this.exito = exito;
            this.desactivadaEnLugarDeEliminada = desactivadaEnLugarDeEliminada;
            this.mensajeError = mensajeError;
            this.idCarrera = idCarrera;
        }

        public static ResultadoCarrera exito(int idCarrera)
        {
            return new ResultadoCarrera(true, false, null, idCarrera);
        }

        public static ResultadoCarrera exitoDesactivada(int idCarrera)
        {
            return new ResultadoCarrera(true, true, null, idCarrera);
        }

        public static ResultadoCarrera fallo(String mensajeError)
        {
            return new ResultadoCarrera(false, false, mensajeError, 0);
        }

        public boolean isExito()
        {
            return exito;
        }

        public boolean isDesactivadaEnLugarDeEliminada()
        {
            return desactivadaEnLugarDeEliminada;
        }

        public String getMensajeError()
        {
            return mensajeError;
        }

        public int getIdCarrera()
        {
            return idCarrera;
        }
    }
}
