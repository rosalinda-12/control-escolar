package servicio;

import doa.DAODocente;
import doa.DAOPersona;
import doa.DAORol;
import doa.DAOSubdirector;
import doa.DAOUsuario;
import modelo.Docente;
import modelo.Persona;
import modelo.Rol;
import modelo.Subdirector;
import modelo.Usuario;
import util.PasswordUtil;
import java.util.ArrayList;

public class ServicioUsuario
{
    private final DAOUsuario daoUsuario;
    private final DAORol daoRol;
    private final DAOPersona daoPersona;
    private final DAODocente daoDocente;
    private final DAOSubdirector daoSubdirector;
    private final ServicioBitacora servicioBitacora;

    public ServicioUsuario()
    {
        this.daoUsuario = new DAOUsuario();
        this.daoRol = new DAORol();
        this.daoPersona = new DAOPersona();
        this.daoDocente = new DAODocente();
        this.daoSubdirector = new DAOSubdirector();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<Usuario> listarTodos()
    {
        return daoUsuario.listarTodos();
    }

    public Usuario buscarPorId(int idUsuario)
    {
        return daoUsuario.buscarPorId(idUsuario);
    }

    public ResultadoSimple agregarCuenta(String nombres, String apellidoPaterno, String apellidoMaterno,
            String correo, String contrasena, int idRol, Integer idCarrera, Usuario responsable)
    {
        Rol rol = daoRol.buscarPorId(idRol);
        if (rol == null || (!"Control Escolar".equals(rol.getNombreRol()) && !"Subdirector".equals(rol.getNombreRol())))
        {
            return ResultadoSimple.fallo("Solo se pueden agregar cuentas de Control Escolar o Subdirector.");
        }
        if (correo == null || correo.trim().isEmpty() || contrasena == null || contrasena.length() < 8)
        {
            return ResultadoSimple.fallo("El correo es obligatorio y la contraseña debe tener al menos 8 caracteres.");
        }
        if (daoUsuario.buscarPorCorreo(correo.trim()) != null)
        {
            return ResultadoSimple.fallo("Ese correo ya tiene una cuenta registrada.");
        }
        if ("Subdirector".equals(rol.getNombreRol()) && idCarrera == null)
        {
            return ResultadoSimple.fallo("Un Subdirector debe tener una carrera asignada.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombres(nombres == null ? "" : nombres.trim());
        usuario.setApellidoPaterno(apellidoPaterno == null ? "" : apellidoPaterno.trim());
        usuario.setApellidoMaterno(apellidoMaterno == null ? "" : apellidoMaterno.trim());
        usuario.setCorreo(correo.trim());
        usuario.setContrasena(PasswordUtil.generarHash(contrasena));
        usuario.setIdRol(idRol);
        usuario.setIdCarrera("Subdirector".equals(rol.getNombreRol()) ? idCarrera : null);
        usuario.setCorreoVerificado(true);
        usuario.setEstatusRegistro("Aprobado");
        usuario.setRequiereCambioContrasena(true);

        int idUsuario = daoUsuario.agregar(usuario);
        servicioBitacora.registrarAlta(responsable, "usuarios", idUsuario,
                "Agregó cuenta de " + rol.getNombreRol() + " para " + usuario.getCorreo());
        if ("Subdirector".equals(rol.getNombreRol()))
        {
            activarComoSubdirector(usuario, idCarrera);
        }
        return ResultadoSimple.exito(idUsuario);
    }

    public ResultadoSimple cambiarRol(int idUsuario, int idRol, Integer idCarrera, Usuario responsable)
    {
        Usuario usuario = daoUsuario.buscarPorId(idUsuario);
        if (usuario == null)
        {
            return ResultadoSimple.fallo("El usuario ya no existe.");
        }

        Rol rol = daoRol.buscarPorId(idRol);
        if (rol == null)
        {
            return ResultadoSimple.fallo("El rol seleccionado ya no existe.");
        }

        if ("Subdirector".equals(rol.getNombreRol()) && idCarrera == null)
        {
            return ResultadoSimple.fallo("Un Subdirector debe tener una carrera asignada.");
        }

        Integer carreraFinal = "Subdirector".equals(rol.getNombreRol()) ? idCarrera : null;
        String rolAnterior = usuario.getNombreRol();
        daoUsuario.actualizarRolYCarrera(idUsuario, idRol, carreraFinal);
        sincronizarDirectorios(usuario, rol.getNombreRol(), rolAnterior, carreraFinal);

        servicioBitacora.registrarAlta(responsable, "usuarios", idUsuario,
                "Cambió el rol de " + usuario.getCorreo() + " a " + rol.getNombreRol());

        return ResultadoSimple.exito(idUsuario);
    }

    private void sincronizarDirectorios(Usuario usuario, String rolNuevo, String rolAnterior, Integer idCarrera)
    {
        if ("Subdirector".equals(rolNuevo))
        {
            activarComoSubdirector(usuario, idCarrera);
        }
        else if ("Subdirector".equals(rolAnterior))
        {
            desactivarSubdirectorSiExiste(usuario.getCorreo());
        }

        if ("Maestro".equals(rolNuevo))
        {
            activarComoDocente(usuario);
        }
        else if ("Maestro".equals(rolAnterior))
        {
            desactivarDocenteSiExiste(usuario.getCorreo());
        }
    }

    private void activarComoSubdirector(Usuario usuario, int idCarrera)
    {
        Subdirector subdirectorExistente = daoSubdirector.buscarPorCorreo(usuario.getCorreo());

        if (subdirectorExistente != null)
        {
            daoSubdirector.actualizarEstatus(subdirectorExistente.getIdSubdirector(), "Activo");
            daoSubdirector.actualizarCarrera(subdirectorExistente.getIdSubdirector(), idCarrera);
            daoSubdirector.actualizarCarreras(subdirectorExistente.getIdSubdirector(), java.util.Collections.singletonList(idCarrera));
            return;
        }

        daoSubdirector.agregar(obtenerOCrearPersona(usuario), idCarrera);
    }

    private void activarComoDocente(Usuario usuario)
    {
        Docente docenteExistente = daoDocente.buscarPorCorreo(usuario.getCorreo());

        if (docenteExistente != null)
        {
            daoDocente.actualizarEstatus(docenteExistente.getIdDocente(), "Activo");
            return;
        }

        daoDocente.agregar(obtenerOCrearPersona(usuario));
    }

    private void desactivarSubdirectorSiExiste(String correo)
    {
        Subdirector subdirector = daoSubdirector.buscarPorCorreo(correo);

        if (subdirector != null)
        {
            daoSubdirector.actualizarEstatus(subdirector.getIdSubdirector(), "Inactivo");
        }
    }

    private void desactivarDocenteSiExiste(String correo)
    {
        Docente docente = daoDocente.buscarPorCorreo(correo);

        if (docente != null)
        {
            daoDocente.actualizarEstatus(docente.getIdDocente(), "Inactivo");
        }
    }

    private int obtenerOCrearPersona(Usuario usuario)
    {
        Persona personaExistente = daoPersona.buscarPorCorreo(usuario.getCorreo());

        if (personaExistente != null)
        {
            return personaExistente.getIdPersona();
        }

        Persona persona = new Persona();
        persona.setNombres(usuario.getNombres());
        persona.setApellidoPaterno(usuario.getApellidoPaterno());
        persona.setApellidoMaterno(usuario.getApellidoMaterno());
        persona.setCorreo(usuario.getCorreo());

        return daoPersona.agregar(persona);
    }

    public ResultadoSimple eliminarODesactivar(int idUsuario, Usuario responsable)
    {
        Usuario usuario = daoUsuario.buscarPorId(idUsuario);
        if (usuario == null)
        {
            return ResultadoSimple.fallo("El usuario ya no existe.");
        }

        if (usuario.isAdministradorPrincipal())
        {
            return ResultadoSimple.fallo("No se puede eliminar ni desactivar al Administrador.");
        }

        if (usuario.getIdUsuario() == responsable.getIdUsuario())
        {
            return ResultadoSimple.fallo("No puedes eliminar tu propia cuenta.");
        }

        if (daoUsuario.tieneRegistrosEnBitacora(idUsuario))
        {
            daoUsuario.desactivar(idUsuario);
            servicioBitacora.registrarBaja(responsable, "usuarios", idUsuario,
                    "Desactivó la cuenta de " + usuario.getCorreo() + " (tiene historial, no se puede eliminar)");
        }
        else
        {
            daoUsuario.eliminar(idUsuario);
            servicioBitacora.registrarBaja(responsable, "usuarios", idUsuario,
                    "Eliminó la cuenta de " + usuario.getCorreo());
        }

        return ResultadoSimple.exito(idUsuario);
    }
}