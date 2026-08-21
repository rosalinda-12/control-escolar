package servicio;

import doa.DAOGrupo;
import doa.DAOGrupoMateria;
import doa.DAOMateria;
import modelo.Grupo;
import modelo.Usuario;
import java.util.ArrayList;

public class ServicioGrupo
{
    private final DAOGrupo daoGrupo;
    private final DAOGrupoMateria daoGrupoMateria;
    private final DAOMateria daoMateria;
    private final ServicioBitacora servicioBitacora;

    public ServicioGrupo()
    {
        this.daoGrupo = new DAOGrupo();
        this.daoGrupoMateria = new DAOGrupoMateria();
        this.daoMateria = new DAOMateria();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<Grupo> listar()
    {
        return daoGrupo.listar();
    }

    public ArrayList<Grupo> listarActivosParaInscripcion()
    {
        return daoGrupo.listarActivosParaInscripcion();
    }

    public Grupo buscarPorId(int idGrupo)
    {
        return daoGrupo.buscarPorId(idGrupo);
    }

    public ResultadoSimple actualizar(Grupo grupo, Usuario responsable)
    {
        Grupo anterior = daoGrupo.buscarPorId(grupo.getIdGrupo());
        if (anterior == null) return ResultadoSimple.fallo("El grupo ya no existe.");
        if (!anterior.getNombreGrupo().equals(grupo.getNombreGrupo()) && daoGrupo.existeNombreEnPeriodo(grupo.getNombreGrupo(), grupo.getIdPeriodo()))
        {
            return ResultadoSimple.fallo("Ya existe un grupo con ese nombre en ese periodo.");
        }
        daoGrupo.actualizar(grupo);
        servicioBitacora.registrarAlta(responsable, "grupos", grupo.getIdGrupo(), "Actualizó el grupo " + grupo.getNombreGrupo());
        return ResultadoSimple.exito(grupo.getIdGrupo());
    }

    public ResultadoSimple agregar(Grupo grupo, Usuario responsable)
    {
        if (daoGrupo.existeNombreEnPeriodo(grupo.getNombreGrupo(), grupo.getIdPeriodo()))
        {
            return ResultadoSimple.fallo("Ya existe un grupo con ese nombre en ese periodo.");
        }

        if (daoMateria.listarPorPlanCuatrimestre(grupo.getIdPlanCuatrimestre()).isEmpty())
        {
            return ResultadoSimple.fallo("Ese cuatrimestre del plan todavía no tiene materias dadas de alta. "
                    + "Créalas primero en Materias para poder crear el grupo con ellas.");
        }

        int idGrupo = daoGrupo.agregar(grupo);
        daoGrupoMateria.copiarDesdeCurriculo(idGrupo, grupo.getIdPlanCuatrimestre());

        servicioBitacora.registrarAlta(responsable, "grupos", idGrupo,
                "Alta de grupo " + grupo.getNombreGrupo() + " con " + daoGrupoMateria.contarMaterias(idGrupo) + " materias del plan");

        return ResultadoSimple.exito(idGrupo);
    }

    public void cerrar(int idGrupo, Usuario responsable)
    {
        Grupo grupo = daoGrupo.buscarPorId(idGrupo);
        daoGrupo.actualizarEstatus(idGrupo, "Cerrado");
        servicioBitacora.registrarBaja(responsable, "grupos", idGrupo,
                "Cerró el grupo " + (grupo != null ? grupo.getNombreGrupo() : idGrupo));
    }
}
