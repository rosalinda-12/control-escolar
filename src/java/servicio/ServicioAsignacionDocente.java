package servicio;

import doa.DAODocenteAsignacion;
import modelo.Usuario;

public class ServicioAsignacionDocente
{
    private final DAODocenteAsignacion daoAsignacion;
    private final ServicioBitacora servicioBitacora;

    public ServicioAsignacionDocente()
    {
        this.daoAsignacion = new DAODocenteAsignacion();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ResultadoSimple asignar(int idDocente, int idGrupoMateria, Usuario responsable)
    {
        if (daoAsignacion.existeParaGrupoMateria(idGrupoMateria))
        {
            return ResultadoSimple.fallo("Esa materia del grupo ya tiene un docente asignado. Quítalo primero si quieres cambiarlo.");
        }

        daoAsignacion.asignar(idDocente, idGrupoMateria);
        servicioBitacora.registrarAlta(responsable, "docentes_asignaciones", idGrupoMateria,
                "Asignó un docente a una materia de grupo");

        return ResultadoSimple.exito(idGrupoMateria);
    }

    public void quitar(int idGrupoMateria, Usuario responsable)
    {
        daoAsignacion.quitarPorGrupoMateria(idGrupoMateria);
        servicioBitacora.registrarBaja(responsable, "docentes_asignaciones", idGrupoMateria,
                "Quitó la asignación de docente de una materia de grupo");
    }
}
