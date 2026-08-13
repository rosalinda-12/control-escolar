package servicio;

import doa.DAOAlumno;
import doa.DAOPersona;
import doa.DAOTrayectoriaAcademica;
import modelo.Alumno;
import modelo.Persona;
import modelo.TrayectoriaAcademica;
import modelo.Usuario;
import java.time.LocalDate;
import java.util.ArrayList;

public class ServicioAlumno
{
    private final DAOAlumno daoAlumno;
    private final DAOPersona daoPersona;
    private final DAOTrayectoriaAcademica daoTrayectoria;
    private final ServicioBitacora servicioBitacora;

    public ServicioAlumno()
    {
        this.daoAlumno = new DAOAlumno();
        this.daoPersona = new DAOPersona();
        this.daoTrayectoria = new DAOTrayectoriaAcademica();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<Alumno> listar()
    {
        return daoAlumno.listar();
    }

    public Alumno buscarPorId(int idAlumno)
    {
        return daoAlumno.buscarPorId(idAlumno);
    }

    /**
     * Da de alta al alumno (persona + alumno) y, en el mismo formulario, su
     * primera trayectoria (carrera/plan + matrícula). La matrícula es
     * obligatoria desde el inicio: ya no queda un alumno "a medias" sin
     * trayectoria. Si más adelante el alumno cambia de carrera o continúa
     * de TSU a Ingeniería, esas trayectorias adicionales se siguen dando de
     * alta por separado en Trayectorias.
     */
    public ResultadoSimple agregarConTrayectoria(Persona persona, int idPlan, String matricula, Usuario responsable)
    {
        if (daoPersona.existeCorreo(persona.getCorreo()))
        {
            return ResultadoSimple.fallo("Ya existe una persona registrada con ese correo.");
        }

        if (daoTrayectoria.existeMatricula(matricula))
        {
            return ResultadoSimple.fallo("Ya existe una trayectoria con esa matrícula.");
        }

        int idPersona = daoPersona.agregar(persona);
        int idAlumno = daoAlumno.agregar(idPersona);

        TrayectoriaAcademica trayectoria = new TrayectoriaAcademica();
        trayectoria.setIdAlumno(idAlumno);
        trayectoria.setIdPlan(idPlan);
        trayectoria.setMatricula(matricula);
        trayectoria.setFechaInicio(LocalDate.now());
        daoTrayectoria.agregar(trayectoria);

        servicioBitacora.registrarAlta(responsable, "alumnos", idAlumno,
                "Alta de alumno " + persona.getNombres() + " " + persona.getApellidoPaterno()
                + " con matrícula " + matricula);

        return ResultadoSimple.exito(idAlumno);
    }

    public void actualizarEstatus(int idAlumno, String estatus, Usuario responsable)
    {
        Alumno alumno = daoAlumno.buscarPorId(idAlumno);
        daoAlumno.actualizarEstatus(idAlumno, estatus);
        servicioBitacora.registrarAlta(responsable, "alumnos", idAlumno,
                "Cambió el estatus de " + (alumno != null ? alumno.getNombreCompleto() : idAlumno) + " a " + estatus);
    }
}
