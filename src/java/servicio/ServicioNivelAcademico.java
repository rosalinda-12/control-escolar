package servicio;

import doa.DAONivelAcademico;
import modelo.NivelAcademico;
import modelo.Usuario;
import java.util.ArrayList;

public class ServicioNivelAcademico
{
    private final DAONivelAcademico daoNivel;
    private final ServicioBitacora servicioBitacora;

    public ServicioNivelAcademico()
    {
        this.daoNivel = new DAONivelAcademico();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<NivelAcademico> listar()
    {
        return daoNivel.listar();
    }

    public NivelAcademico buscarPorId(int idNivel)
    {
        return daoNivel.buscarPorId(idNivel);
    }

    public ResultadoSimple agregar(NivelAcademico nivel, Usuario responsable)
    {
        if (daoNivel.existeNombre(nivel.getNombreNivel()))
        {
            return ResultadoSimple.fallo("Ya existe un nivel académico con ese nombre.");
        }

        int idNivel = daoNivel.agregar(nivel);
        servicioBitacora.registrarAlta(responsable, "niveles_academicos", idNivel,
                "Alta de nivel académico " + nivel.getNombreNivel());

        return ResultadoSimple.exito(idNivel);
    }

    public void eliminar(int idNivel, Usuario responsable)
    {
        NivelAcademico nivel = daoNivel.buscarPorId(idNivel);
        daoNivel.eliminar(idNivel);
        servicioBitacora.registrarBaja(responsable, "niveles_academicos", idNivel,
                "Baja de nivel académico " + (nivel != null ? nivel.getNombreNivel() : idNivel));
    }
}
