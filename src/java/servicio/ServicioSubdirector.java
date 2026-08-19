package servicio;

import doa.DAOPersona;
import doa.DAOSubdirector;
import modelo.Persona;
import modelo.Subdirector;
import modelo.Usuario;
import java.util.ArrayList;

/**
 * Alta, por parte del Admin o Control Escolar, de la persona que podrá
 * autoregistrarse como Subdirector de una carrera. Igual que con Alumno
 * (ServicioAlumno) y Maestro (ServicioDocente), aquí NO se crea una
 * cuenta de acceso ni contraseña: solo se deja constancia de quién y con
 * qué correo puede autoregistrarse después en /registro.jsp. La cuenta
 * de acceso (tabla usuarios) la crea la propia persona en ServicioRegistro,
 * y queda pendiente de aprobación en ServicioAprobacionRegistro hasta que
 * el Admin/Control Escolar la acepte.
 */
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
        if (daoPersona.existeCorreo(persona.getCorreo()))
        {
            return ResultadoSimple.fallo("Ya existe una persona registrada con ese correo.");
        }

        int idPersona = daoPersona.agregar(persona);
        int idSubdirector = daoSubdirector.agregar(idPersona, idCarrera);

        servicioBitacora.registrarAlta(responsable, "subdirectores", idSubdirector,
                "Alta de subdirector " + persona.getNombres() + " " + persona.getApellidoPaterno()
                + " (correo: " + persona.getCorreo() + "). Debe autoregistrarse con ese correo y verificarlo.");

        return ResultadoSimple.exito(idSubdirector);
    }

    /**
     * Un subdirector con historial no se elimina; se desactiva, igual que
     * con los docentes.
     */
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
