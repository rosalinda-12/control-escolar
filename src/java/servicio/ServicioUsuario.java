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
import java.util.ArrayList;

/**
 * Administración de cuentas de usuario ya existentes (permiso usuarios.*):
 * listar, cambiar rol/carrera y eliminar/desactivar.
 *
 * Ya NO se crean cuentas directamente desde aquí ni con contraseña
 * temporal. Toda cuenta nueva (Alumno, Maestro o Subdirector) se crea con
 * este flujo:
 *   1) El Admin o Control Escolar da de alta a la persona (correo, y para
 *      Subdirector también su carrera) en ServicioAlumno/ServicioDocente/
 *      ServicioSubdirector -sin contraseña ni cuenta de acceso todavía-.
 *   2) La propia persona se autoregistra en /registro.jsp con ESE MISMO
 *      correo y elige su propia contraseña (ServicioRegistro). Si el
 *      correo no coincide con el que se dio de alta, no puede registrarse.
 *   3) Verifica su correo con el código que se le envía
 *      (ServicioVerificacionCorreo).
 *   4) El Admin o Control Escolar aprueba la solicitud
 *      (ServicioAprobacionRegistro) para que pueda iniciar sesión.
 */
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

    /**
     * El cambio de rol desde Usuarios solo toca la cuenta de acceso
     * (tabla usuarios), pero las páginas de Docentes y Subdirectores se
     * alimentan de sus propias tablas (docentes/subdirectores), separadas
     * a propósito de las cuentas. Sin esto, promover a alguien a
     * Subdirector no lo hacía aparecer ahí, y quitarle el rol dejaba su
     * fila anterior activa para siempre.
     */
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

    /**
     * Un usuario con historial (bitácora) no se borra físicamente, para
     * no perder trazabilidad de quién hizo qué; se desactiva en su
     * lugar (no podrá volver a iniciar sesión, ver ServicioAutenticacion
     * que exige estatus_registro = 'Aprobado'). Sin historial, sí se
     * elimina.
     */
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