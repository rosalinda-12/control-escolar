package servicio;

import doa.DAORol;
import modelo.Rol;
import modelo.Usuario;
import java.util.ArrayList;

public class ServicioRol
{
    private final DAORol daoRol;
    private final ServicioBitacora servicioBitacora;

    public ServicioRol()
    {
        this.daoRol = new DAORol();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<Rol> listar()
    {
        return daoRol.listar();
    }

    public Rol buscarPorId(int idRol)
    {
        return daoRol.buscarPorId(idRol);
    }

    public ResultadoSimple agregar(Rol rol, Usuario responsable)
    {
        if (rol.getNombreRol() == null || rol.getNombreRol().isBlank())
        {
            return ResultadoSimple.fallo("El nombre del rol es obligatorio.");
        }

        if (daoRol.existeNombre(rol.getNombreRol()))
        {
            return ResultadoSimple.fallo("Ya existe un rol con ese nombre.");
        }

        int idRol = daoRol.agregar(rol);
        servicioBitacora.registrarAlta(responsable, "roles", idRol, "Alta de rol " + rol.getNombreRol());

        return ResultadoSimple.exito(idRol);
    }
}
