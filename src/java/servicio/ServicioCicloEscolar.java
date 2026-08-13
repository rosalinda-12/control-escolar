package servicio;

import doa.DAOCicloEscolar;
import modelo.CicloEscolar;
import modelo.Usuario;
import java.util.ArrayList;

public class ServicioCicloEscolar
{
    private final DAOCicloEscolar daoCicloEscolar;
    private final ServicioBitacora servicioBitacora;

    public ServicioCicloEscolar()
    {
        this.daoCicloEscolar = new DAOCicloEscolar();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<CicloEscolar> listar()
    {
        return daoCicloEscolar.listar();
    }

    public CicloEscolar buscarPorId(int idCiclo)
    {
        return daoCicloEscolar.buscarPorId(idCiclo);
    }

    public ResultadoSimple agregar(CicloEscolar ciclo, Usuario responsable)
    {
        if (daoCicloEscolar.existeNombre(ciclo.getNombreCiclo()))
        {
            return ResultadoSimple.fallo("Ya existe un ciclo escolar con ese nombre.");
        }

        int idCiclo = daoCicloEscolar.agregar(ciclo);
        servicioBitacora.registrarAlta(responsable, "ciclos_escolares", idCiclo,
                "Alta de ciclo escolar " + ciclo.getNombreCiclo());

        return ResultadoSimple.exito(idCiclo);
    }

    public void eliminar(int idCiclo, Usuario responsable)
    {
        CicloEscolar ciclo = daoCicloEscolar.buscarPorId(idCiclo);
        daoCicloEscolar.eliminar(idCiclo);
        servicioBitacora.registrarBaja(responsable, "ciclos_escolares", idCiclo,
                "Baja de ciclo escolar " + (ciclo != null ? ciclo.getNombreCiclo() : idCiclo));
    }
}
