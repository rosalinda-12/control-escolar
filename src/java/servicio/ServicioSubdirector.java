package servicio;

import doa.DAOPersona;
import doa.DAOSubdirector;
import modelo.Persona;
import modelo.Subdirector;
import modelo.Usuario;
import java.util.ArrayList;
import java.util.List;

public class ServicioSubdirector
{
    private final DAOSubdirector daoSubdirector;
    private final DAOPersona daoPersona;
    private final ServicioBitacora servicioBitacora;

    public ServicioSubdirector()
    {
        this.daoSubdirector = new DAOSubdirector();
        this.daoPersona = new DAOPersona();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<Subdirector> listar()
    {
        return daoSubdirector.listar();
    }

    public Subdirector buscarPorId(int idSubdirector)
    {
        return daoSubdirector.buscarPorId(idSubdirector);
    }

    public ResultadoSimple agregar(Persona persona, int idCarrera, Usuario responsable)
    {
        return agregar(persona, java.util.Collections.singletonList(idCarrera), responsable);
    }

    public ResultadoSimple agregar(Persona persona, List<Integer> idsCarrera, Usuario responsable)
    {
        if (idsCarrera == null || idsCarrera.isEmpty()) return ResultadoSimple.fallo("Selecciona al menos una carrera.");
        if (daoPersona.existeCorreo(persona.getCorreo()))
        {
            return ResultadoSimple.fallo("Ya existe una persona registrada con ese correo.");
        }

        int idPersona = daoPersona.agregar(persona);
        int idSubdirector = daoSubdirector.agregar(idPersona, idsCarrera.get(0));
        daoSubdirector.actualizarCarreras(idSubdirector, idsCarrera);

        servicioBitacora.registrarAlta(responsable, "subdirectores", idSubdirector,
                "Alta de subdirector " + persona.getNombres() + " " + persona.getApellidoPaterno()
                + " (correo: " + persona.getCorreo() + "). Debe autoregistrarse con ese correo y verificarlo.");

        return ResultadoSimple.exito(idSubdirector);
    }

    public ResultadoSimple actualizar(Subdirector subdirector, Usuario responsable)
    {
        Subdirector anterior = daoSubdirector.buscarPorId(subdirector.getIdSubdirector());
        if (anterior == null)
        {
            return ResultadoSimple.fallo("El subdirector ya no existe.");
        }
        if (!anterior.getCorreo().equalsIgnoreCase(subdirector.getCorreo()))
        {
            Subdirector conCorreo = daoSubdirector.buscarPorCorreo(subdirector.getCorreo());
            if (conCorreo != null && conCorreo.getIdSubdirector() != subdirector.getIdSubdirector())
            {
                return ResultadoSimple.fallo("Ya existe otro subdirector con ese correo.");
            }
        }
        Persona persona = new Persona();
        persona.setIdPersona(subdirector.getIdPersona());
        persona.setNombres(subdirector.getNombres());
        persona.setApellidoPaterno(subdirector.getApellidoPaterno());
        persona.setApellidoMaterno(subdirector.getApellidoMaterno());
        persona.setCorreo(subdirector.getCorreo());
        daoPersona.modificar(persona);
        daoSubdirector.actualizarCarrera(subdirector.getIdSubdirector(), subdirector.getIdCarrera());
        daoSubdirector.actualizarCarreras(subdirector.getIdSubdirector(), subdirector.getIdsCarrera());
        servicioBitacora.registrarAlta(responsable, "subdirectores", subdirector.getIdSubdirector(), "Actualizó datos del subdirector " + subdirector.getNombreCompleto());
        return ResultadoSimple.exito(subdirector.getIdSubdirector());
    }

    public ResultadoSimple desactivar(int idSubdirector, Usuario responsable)
    {
        Subdirector subdirector = daoSubdirector.buscarPorId(idSubdirector);

        if (subdirector == null)
        {
            return ResultadoSimple.fallo("El subdirector ya no existe.");
        }

        daoSubdirector.actualizarEstatus(idSubdirector, "Inactivo");
        servicioBitacora.registrarBaja(responsable, "subdirectores", idSubdirector,
                "Desactivó al subdirector " + subdirector.getNombreCompleto());

        return ResultadoSimple.exito(idSubdirector);
    }
}
