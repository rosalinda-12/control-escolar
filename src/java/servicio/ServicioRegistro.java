package servicio;

import doa.DAOAlumno;
import doa.DAODocente;
import doa.DAORol;
import doa.DAOTrayectoriaAcademica;
import doa.DAOUsuario;
import modelo.Alumno;
import modelo.Docente;
import modelo.Rol;
import modelo.TrayectoriaAcademica;
import modelo.Usuario;
import util.CodigoUtil;
import util.EmailUtil;
import util.PasswordUtil;

public class ServicioRegistro
{
    private final DAOUsuario daoUsuario;
    private final DAOAlumno daoAlumno;
    private final DAOTrayectoriaAcademica daoTrayectoria;
    private final DAODocente daoDocente;
    private final DAORol daoRol;

    public ServicioRegistro()
    {
        this.daoUsuario = new DAOUsuario();
        this.daoAlumno = new DAOAlumno();
        this.daoTrayectoria = new DAOTrayectoriaAcademica();
        this.daoDocente = new DAODocente();
        this.daoRol = new DAORol();
    }

    public boolean existeAdministrador()
    {
        return daoUsuario.existeAdministrador();
    }

    public ResultadoRegistro registrarAdministrador(String nombres, String apellidoPaterno, String apellidoMaterno, String correo, String contrasenaPlana)
    {
        if (existeAdministrador())
        {
            return ResultadoRegistro.fallo("Ya existe un Administrador registrado en el sistema.");
        }

        if (daoUsuario.buscarPorCorreo(correo) != null)
        {
            return ResultadoRegistro.fallo("Ese correo ya tiene una cuenta registrada.");
        }

        Rol rolAdministrador = daoRol.buscarPorNombre("Administrador");

        Usuario usuario = new Usuario();
        usuario.setNombres(nombres);
        usuario.setApellidoPaterno(apellidoPaterno);
        usuario.setApellidoMaterno(apellidoMaterno);
        usuario.setCorreo(correo);
        usuario.setContrasena(PasswordUtil.generarHash(contrasenaPlana));
        usuario.setIdRol(rolAdministrador.getIdRol());
        usuario.setCodigoVerificacion(CodigoUtil.generarCodigo());
        usuario.setExpiracionCodigo(CodigoUtil.calcularExpiracion());
        usuario.setCorreoVerificado(false);
        usuario.setEstatusRegistro("Aprobado");
        usuario.setRequiereCambioContrasena(false);

        int idUsuario = daoUsuario.agregar(usuario);
        EmailUtil.enviarCodigoVerificacion(correo, nombres, usuario.getCodigoVerificacion());

        return ResultadoRegistro.exito(idUsuario);
    }

    /**
     * La matrícula ahora vive en la trayectoria académica (un alumno puede
     * tener varias a lo largo del tiempo: TSU, Ingeniería, etc.), así que el
     * registro busca la trayectoria por matrícula y valida que el correo
     * coincida con el de esa persona antes de crear la cuenta.
     */
    public ResultadoRegistro registrarAlumno(String matricula, String correo, String contrasenaPlana)
    {
        TrayectoriaAcademica trayectoria = daoTrayectoria.buscarPorMatricula(matricula);

        if (trayectoria == null)
        {
            return ResultadoRegistro.fallo("Esa matrícula no coincide con ninguna trayectoria dada de alta. Pide al Administrador que verifique tu registro.");
        }

        Alumno alumno = daoAlumno.buscarPorId(trayectoria.getIdAlumno());

        if (alumno == null || !alumno.getCorreo().equalsIgnoreCase(correo))
        {
            return ResultadoRegistro.fallo("La matrícula y el correo no coinciden con ningún alumno dado de alta. Pide al Administrador que verifique tu registro.");
        }

        if (daoUsuario.buscarPorCorreo(correo) != null)
        {
            return ResultadoRegistro.fallo("Ese correo ya tiene una cuenta registrada.");
        }

        Rol rolAlumno = daoRol.buscarPorNombre("Alumno");

        Usuario usuario = new Usuario();
        usuario.setNombres(alumno.getNombres());
        usuario.setApellidoPaterno(alumno.getApellidoPaterno());
        usuario.setApellidoMaterno(alumno.getApellidoMaterno());
        usuario.setCorreo(correo);
        usuario.setContrasena(PasswordUtil.generarHash(contrasenaPlana));
        usuario.setIdRol(rolAlumno.getIdRol());
        usuario.setIdAlumno(alumno.getIdAlumno());
        usuario.setCodigoVerificacion(CodigoUtil.generarCodigo());
        usuario.setExpiracionCodigo(CodigoUtil.calcularExpiracion());
        usuario.setCorreoVerificado(false);
        usuario.setEstatusRegistro("Pendiente");
        usuario.setRequiereCambioContrasena(true);

        int idUsuario = daoUsuario.agregar(usuario);
        EmailUtil.enviarCodigoVerificacion(correo, alumno.getNombres(), usuario.getCodigoVerificacion());

        return ResultadoRegistro.exito(idUsuario);
    }

    public ResultadoRegistro registrarMaestro(String correo, String contrasenaPlana)
    {
        Docente docente = daoDocente.buscarPorCorreo(correo);

        if (docente == null)
        {
            return ResultadoRegistro.fallo("Ese correo no coincide con ningún docente dado de alta. Pide al Administrador que verifique tu registro.");
        }

        if (daoUsuario.buscarPorCorreo(correo) != null)
        {
            return ResultadoRegistro.fallo("Ese correo ya tiene una cuenta registrada.");
        }

        Rol rolMaestro = daoRol.buscarPorNombre("Maestro");

        Usuario usuario = new Usuario();
        usuario.setNombres(docente.getNombres());
        usuario.setApellidoPaterno(docente.getApellidoPaterno());
        usuario.setApellidoMaterno(docente.getApellidoMaterno());
        usuario.setCorreo(correo);
        usuario.setContrasena(PasswordUtil.generarHash(contrasenaPlana));
        usuario.setIdRol(rolMaestro.getIdRol());
        usuario.setIdProfesor(docente.getIdDocente());
        usuario.setCodigoVerificacion(CodigoUtil.generarCodigo());
        usuario.setExpiracionCodigo(CodigoUtil.calcularExpiracion());
        usuario.setCorreoVerificado(false);
        usuario.setEstatusRegistro("Pendiente");
        usuario.setRequiereCambioContrasena(true);

        int idUsuario = daoUsuario.agregar(usuario);
        EmailUtil.enviarCodigoVerificacion(correo, docente.getNombres(), usuario.getCodigoVerificacion());

        return ResultadoRegistro.exito(idUsuario);
    }

    public static class ResultadoRegistro
    {
        private final boolean exito;
        private final String mensajeError;
        private final int idUsuario;

        private ResultadoRegistro(boolean exito, String mensajeError, int idUsuario)
        {
            this.exito = exito;
            this.mensajeError = mensajeError;
            this.idUsuario = idUsuario;
        }

        public static ResultadoRegistro exito(int idUsuario)
        {
            return new ResultadoRegistro(true, null, idUsuario);
        }

        public static ResultadoRegistro fallo(String mensajeError)
        {
            return new ResultadoRegistro(false, mensajeError, 0);
        }

        public boolean isExito()
        {
            return exito;
        }

        public String getMensajeError()
        {
            return mensajeError;
        }

        public int getIdUsuario()
        {
            return idUsuario;
        }
    }
}
