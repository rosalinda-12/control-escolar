package servicio;

import doa.DAODocente;
import doa.DAOPersona;
import modelo.Docente;
import modelo.Persona;
import modelo.Usuario;
import java.util.ArrayList;

public class ServicioDocente
{
    private final DAODocente daoDocente;
    private final DAOPersona daoPersona;
    private final ServicioBitacora servicioBitacora;

    public ServicioDocente()
    {
        this.daoDocente = new DAODocente();
        this.daoPersona = new DAOPersona();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<Docente> listar()
    {
        return daoDocente.listar();
    }

    public ArrayList<Docente> listarActivos()
    {
        return daoDocente.listarActivos();
    }

    public Docente buscarPorId(int idDocente)
    {
        return daoDocente.buscarPorId(idDocente);
    }

    public ResultadoSimple agregar(Persona persona, Usuario responsable)
    {
        if (daoPersona.existeCorreo(persona.getCorreo()))
        {
            return ResultadoSimple.fallo("Ya existe una persona registrada con ese correo.");
        }

        int idPersona = daoPersona.agregar(persona);
        int idDocente = daoDocente.agregar(idPersona);

        servicioBitacora.registrarAlta(responsable, "docentes", idDocente,
                "Alta de docente " + persona.getNombres() + " " + persona.getApellidoPaterno());

        return ResultadoSimple.exito(idDocente);
    }

    public ResultadoSimple actualizar(Docente docente, Usuario responsable)
    {
        Docente anterior = daoDocente.buscarPorId(docente.getIdDocente());
        if (anterior == null)
        {
            return ResultadoSimple.fallo("El docente ya no existe.");
        }
        if (!anterior.getCorreo().equalsIgnoreCase(docente.getCorreo()))
        {
            Docente conCorreo = daoDocente.buscarPorCorreo(docente.getCorreo());
            if (conCorreo != null && conCorreo.getIdDocente() != docente.getIdDocente())
            {
                return ResultadoSimple.fallo("Ya existe otro docente con ese correo.");
            }
        }
        Persona persona = new Persona();
        persona.setIdPersona(docente.getIdPersona());
        persona.setNombres(docente.getNombres());
        persona.setApellidoPaterno(docente.getApellidoPaterno());
        persona.setApellidoMaterno(docente.getApellidoMaterno());
        persona.setCorreo(docente.getCorreo());
        daoPersona.modificar(persona);
        servicioBitacora.registrarAlta(responsable, "docentes", docente.getIdDocente(), "Actualizó datos del docente " + docente.getNombreCompleto());
        return ResultadoSimple.exito(docente.getIdDocente());
    }



    public ResultadoDesactivable desactivar(int idDocente, Usuario responsable)
    {
        Docente docente = daoDocente.buscarPorId(idDocente);

        if (docente == null)
        {
            return ResultadoDesactivable.fallo("El docente ya no existe.");
        }

        daoDocente.actualizarEstatus(idDocente, "Inactivo");
        daoPersona.actualizarEstatus(docente.getIdPersona(), "Inactivo");
        servicioBitacora.registrarBaja(responsable, "docentes", idDocente,
                "Desactivó al docente " + docente.getNombreCompleto());

        return ResultadoDesactivable.exito();
    }

    public static class ResultadoDesactivable
    {
        private final boolean exito;
        private final String mensajeError;

        private ResultadoDesactivable(boolean exito, String mensajeError)
        {
            this.exito = exito;
            this.mensajeError = mensajeError;
        }

        public static ResultadoDesactivable exito()
        {
            return new ResultadoDesactivable(true, null);
        }

        public static ResultadoDesactivable fallo(String mensajeError)
        {
            return new ResultadoDesactivable(false, mensajeError);
        }

        public boolean isExito()
        {
            return exito;
        }

        public String getMensajeError()
        {
            return mensajeError;
        }
    }
}
