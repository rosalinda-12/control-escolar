package servicio;

import doa.DAODocenteAsignacion;
import doa.DAODocente;
import modelo.Usuario;

public class ServicioAsignacionDocente
{
    private final DAODocenteAsignacion daoAsignacion;
    private final DAODocente daoDocente;
    private final ServicioBitacora servicioBitacora;

    public ServicioAsignacionDocente()
    {
        this.daoAsignacion = new DAODocenteAsignacion();
        this.daoDocente = new DAODocente();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ResultadoSimple asignar(int idDocente, int idGrupoMateria, Usuario responsable)
    {
        modelo.Docente docente = daoDocente.buscarPorId(idDocente);
        if (docente == null || !"Activo".equals(docente.getEstatus()))
        {
            return ResultadoSimple.fallo("El docente seleccionado ya no está activo.");
        }

        if (daoAsignacion.existeParaGrupoMateria(idGrupoMateria))
        {
            return ResultadoSimple.fallo("Esa materia del grupo ya tiene un docente asignado. Quítalo primero si quieres cambiarlo.");
        }

        daoAsignacion.asignar(idDocente, idGrupoMateria);
        servicioBitacora.registrarAlta(responsable, "docentes_asignaciones", idGrupoMateria,
                "Asignó un docente a una materia de grupo");

        return ResultadoSimple.exito(idGrupoMateria);
    }

    public void desactivar(int idGrupoMateria, Usuario responsable)
    {
        daoAsignacion.desactivarPorGrupoMateria(idGrupoMateria);
        servicioBitacora.registrarBaja(responsable, "docentes_asignaciones", idGrupoMateria,
                "Desactivó la asignación de docente de una materia de grupo");
    }
}
