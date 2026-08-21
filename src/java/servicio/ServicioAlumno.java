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

    public void marcarEgresado(int idAlumno)
    {
        daoAlumno.actualizarEstatus(idAlumno, "Egresado");
    }

    public ResultadoSimple actualizar(Alumno alumno, Usuario responsable)
    {
        Alumno anterior = daoAlumno.buscarPorId(alumno.getIdAlumno());
        if (anterior == null) return ResultadoSimple.fallo("El alumno ya no existe.");
        if (!anterior.getCorreo().equalsIgnoreCase(alumno.getCorreo()))
        {
            Persona persona = daoPersona.buscarPorCorreo(alumno.getCorreo());
            if (persona != null && persona.getIdPersona() != alumno.getIdPersona())
            {
                return ResultadoSimple.fallo("Ya existe otra persona con ese correo.");
            }
        }
        daoAlumno.actualizar(alumno);
        servicioBitacora.registrarAlta(responsable, "alumnos", alumno.getIdAlumno(), "Actualizó datos del alumno " + alumno.getNombreCompleto());
        return ResultadoSimple.exito(alumno.getIdAlumno());
    }



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



    public void bajaTemporal(int idAlumno, Usuario responsable)
    {
        Alumno alumno = daoAlumno.buscarPorId(idAlumno);
        daoAlumno.actualizarEstatus(idAlumno, "BajaTemporal");
        servicioBitacora.registrarBaja(responsable, "alumnos", idAlumno,
                "Dio de baja temporal a " + (alumno != null ? alumno.getNombreCompleto() : idAlumno));
    }

    public void reactivar(int idAlumno, Usuario responsable)
    {
        Alumno alumno = daoAlumno.buscarPorId(idAlumno);
        daoAlumno.actualizarEstatus(idAlumno, "Activo");
        servicioBitacora.registrarAlta(responsable, "alumnos", idAlumno,
                "Reactivó a " + (alumno != null ? alumno.getNombreCompleto() : idAlumno));
    }



    public void bajaDefinitiva(int idAlumno, Usuario responsable)
    {
        Alumno alumno = daoAlumno.buscarPorId(idAlumno);
        daoAlumno.actualizarEstatus(idAlumno, "BajaDefinitiva");
        servicioBitacora.registrarBaja(responsable, "alumnos", idAlumno,
                "Dio de baja definitiva a " + (alumno != null ? alumno.getNombreCompleto() : idAlumno)
                + "; ya no podrá inscribirse nuevamente");
    }



    public void revertirBajaDefinitiva(int idAlumno, Usuario responsable)
    {
        Alumno alumno = daoAlumno.buscarPorId(idAlumno);

        if (alumno == null || !"BajaDefinitiva".equals(alumno.getEstatus()))
        {
            return;
        }

        daoAlumno.actualizarEstatus(idAlumno, "Activo");
        servicioBitacora.registrarAlta(responsable, "alumnos", idAlumno,
                "Revirtió la baja definitiva de " + alumno.getNombreCompleto() + " (corrección de error)");
    }
}
