package modelo;

import java.time.LocalDateTime;

public class Usuario
{
    private int idUsuario;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correo;
    private String contrasena;
    private int idRol;
    private String nombreRol;
    private Integer idProfesor;
    private Integer idAlumno;
    private Integer idCarrera;
    private String codigoVerificacion;
    private LocalDateTime expiracionCodigo;
    private boolean correoVerificado;
    private String estatusRegistro;
    private boolean requiereCambioContrasena;
    private LocalDateTime fechaCreacion;

    // Copiado de roles.es_administrador_principal al cargar el usuario,
    // para que ServicioAutorizacion no tenga que ir a buscar el rol
    // aparte en cada validación de permiso.
    private boolean administradorPrincipal;

    public int getIdUsuario()
    {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario)
    {
        this.idUsuario = idUsuario;
    }

    public String getNombres()
    {
        return nombres;
    }

    public void setNombres(String nombres)
    {
        this.nombres = nombres;
    }

    public String getApellidoPaterno()
    {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno)
    {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno()
    {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno)
    {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getCorreo()
    {
        return correo;
    }

    public void setCorreo(String correo)
    {
        this.correo = correo;
    }

    public String getContrasena()
    {
        return contrasena;
    }

    public void setContrasena(String contrasena)
    {
        this.contrasena = contrasena;
    }

    public int getIdRol()
    {
        return idRol;
    }

    public void setIdRol(int idRol)
    {
        this.idRol = idRol;
    }

    public String getNombreRol()
    {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol)
    {
        this.nombreRol = nombreRol;
    }

    public Integer getIdProfesor()
    {
        return idProfesor;
    }

    public void setIdProfesor(Integer idProfesor)
    {
        this.idProfesor = idProfesor;
    }

    public Integer getIdAlumno()
    {
        return idAlumno;
    }

    public void setIdAlumno(Integer idAlumno)
    {
        this.idAlumno = idAlumno;
    }

    /**
     * Carrera a la que está restringido este usuario. Solo aplica (no
     * nula) cuando nombreRol es "Subdirector"; para el resto de los
     * roles siempre es null.
     */
    public Integer getIdCarrera()
    {
        return idCarrera;
    }

    public void setIdCarrera(Integer idCarrera)
    {
        this.idCarrera = idCarrera;
    }

    public String getCodigoVerificacion()
    {
        return codigoVerificacion;
    }

    public void setCodigoVerificacion(String codigoVerificacion)
    {
        this.codigoVerificacion = codigoVerificacion;
    }

    public LocalDateTime getExpiracionCodigo()
    {
        return expiracionCodigo;
    }

    public void setExpiracionCodigo(LocalDateTime expiracionCodigo)
    {
        this.expiracionCodigo = expiracionCodigo;
    }

    public boolean isCorreoVerificado()
    {
        return correoVerificado;
    }

    public void setCorreoVerificado(boolean correoVerificado)
    {
        this.correoVerificado = correoVerificado;
    }

    public String getEstatusRegistro()
    {
        return estatusRegistro;
    }

    public void setEstatusRegistro(String estatusRegistro)
    {
        this.estatusRegistro = estatusRegistro;
    }

    public boolean isRequiereCambioContrasena()
    {
        return requiereCambioContrasena;
    }

    public void setRequiereCambioContrasena(boolean requiereCambioContrasena)
    {
        this.requiereCambioContrasena = requiereCambioContrasena;
    }

    public LocalDateTime getFechaCreacion()
    {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion)
    {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean esAdministrador()
    {
        return "Administrador".equals(nombreRol);
    }

    public boolean esMaestro()
    {
        return "Maestro".equals(nombreRol);
    }

    public boolean esAlumno()
    {
        return "Alumno".equals(nombreRol);
    }

    public boolean esSubdirector()
    {
        return "Subdirector".equals(nombreRol);
    }

    public boolean esControlEscolar()
    {
        return "Control Escolar".equals(nombreRol);
    }

    /**
     * true para cualquier rol que use el área /admin/* (Administrador y
     * Control Escolar); dentro de esa área cada acción se sigue
     * filtrando con el permiso puntual vía ServicioAutorizacion.
     */
    public boolean puedeEntrarAAreaAdmin()
    {
        return esAdministrador() || esControlEscolar();
    }

    public boolean isAdministradorPrincipal()
    {
        return administradorPrincipal;
    }

    public void setAdministradorPrincipal(boolean administradorPrincipal)
    {
        this.administradorPrincipal = administradorPrincipal;
    }
}
