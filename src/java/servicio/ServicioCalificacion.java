package servicio;

import doa.DAOCalificacion;
import doa.DAODocenteAsignacion;
import doa.DAOInscripcionMateria;
import doa.DAOInscripcion;
import doa.DAOTrayectoriaAcademica;
import modelo.Calificacion;
import modelo.DocenteAsignacion;
import modelo.Usuario;
import modelo.TrayectoriaAcademica;
import modelo.Alumno;
import util.EmailUtil;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class ServicioCalificacion
{


    private static final double NOTA_APROBATORIA = 6.0;

    private final DAODocenteAsignacion daoAsignacion;
    private final DAOCalificacion daoCalificacion;
    private final DAOInscripcionMateria daoInscripcionMateria;
    private final ServicioBitacora servicioBitacora;
    private final ServicioAlumno servicioAlumno;

    public ServicioCalificacion()
    {
        this.daoAsignacion = new DAODocenteAsignacion();
        this.daoCalificacion = new DAOCalificacion();
        this.daoInscripcionMateria = new DAOInscripcionMateria();
        this.servicioBitacora = new ServicioBitacora();
        this.servicioAlumno = new ServicioAlumno();
    }

    public ArrayList<DocenteAsignacion> listarGruposDelDocente(int idDocente)
    {
        return daoAsignacion.listarPorDocente(idDocente);
    }



    public DocenteAsignacion obtenerGrupoMateriaDelDocente(int idGrupoMateria, int idDocente)
    {
        if (!daoAsignacion.perteneceADocente(idGrupoMateria, idDocente))
        {
            return null;
        }

        return daoAsignacion.buscarInfoGrupoMateria(idGrupoMateria);
    }

    public ArrayList<Calificacion> listarAlumnos(int idGrupoMateria)
    {
        return daoCalificacion.listarPorGrupoMateria(idGrupoMateria);
    }



    public ArrayList<Calificacion> listarParaAdmin(Integer idCarrera)
    {
        return daoCalificacion.listarPorCarrera(idCarrera);
    }

    public ArrayList<Calificacion> listarParaCarreras(List<Integer> idsCarrera)
    {
        return daoCalificacion.listarPorCarreras(idsCarrera);
    }

    public Calificacion buscarPorInscripcionMateria(int idInscripcionMateria)
    {
        return daoCalificacion.buscarPorInscripcionMateria(idInscripcionMateria);
    }



    public ResultadoCaptura editarComoAdmin(int idInscripcionMateria, String parcial1Texto, String parcial2Texto,
            String parcial3Texto, Usuario responsable)
    {
        Double parcial1;
        Double parcial2;
        Double parcial3;

        try
        {
            parcial1 = convertirNota(parcial1Texto);
            parcial2 = convertirNota(parcial2Texto);
            parcial3 = convertirNota(parcial3Texto);
        }
        catch (NumberFormatException excepcion)
        {
            return ResultadoCaptura.fallo("Hay una calificación con un valor no numérico.");
        }
        catch (IllegalArgumentException excepcion)
        {
            return ResultadoCaptura.fallo(excepcion.getMessage());
        }

        daoCalificacion.actualizarParcial(idInscripcionMateria, 1, parcial1);
        daoCalificacion.actualizarParcial(idInscripcionMateria, 2, parcial2);
        daoCalificacion.actualizarParcial(idInscripcionMateria, 3, parcial3);
        actualizarPromedioYEstado(idInscripcionMateria);
        int idAlumno = daoCalificacion.buscarIdAlumnoPorInscripcionMateria(idInscripcionMateria);
        finalizarEgresoSiCorresponde(idAlumno);

        servicioBitacora.registrarAlta(responsable, "calificaciones", idInscripcionMateria,
                "Corrigió manualmente la calificación de la inscripción-materia " + idInscripcionMateria);

        return ResultadoCaptura.exito(1);
    }



    public void eliminarComoAdmin(int idInscripcionMateria, Usuario responsable)
    {
        daoCalificacion.eliminarPorInscripcionMateria(idInscripcionMateria);
        daoInscripcionMateria.actualizarEstado(idInscripcionMateria, "Cursando");

        servicioBitacora.registrarBaja(responsable, "calificaciones", idInscripcionMateria,
                "Eliminó la calificación de la inscripción-materia " + idInscripcionMateria);
    }

    private Double convertirNota(String texto)
    {
        String limpio = texto == null ? "" : texto.trim();

        if (limpio.isEmpty())
        {
            return null;
        }

        double nota = Double.parseDouble(limpio.replace(',', '.'));

        if (nota < 0 || nota > 10)
        {
            throw new IllegalArgumentException("Las calificaciones deben estar entre 0 y 10.");
        }

        return Math.round(nota * 10.0) / 10.0;
    }



    public ArrayList<modelo.BoletaCuatrimestre> obtenerBoletaAlumno(int idAlumno)
    {
        ArrayList<Calificacion> materias = daoCalificacion.listarPorAlumno(idAlumno);

        java.util.LinkedHashMap<String, modelo.BoletaCuatrimestre> porCuatrimestre = new java.util.LinkedHashMap<>();

        for (Calificacion materia : materias)
        {
            int numero = materia.getNumeroCuatrimestre();

            String clave = materia.getIdTrayectoria() + "-" + numero;
            modelo.BoletaCuatrimestre boleta = porCuatrimestre.get(clave);

            if (boleta == null)
            {
                boleta = new modelo.BoletaCuatrimestre();
                boleta.setIdTrayectoria(materia.getIdTrayectoria());
                boleta.setNumeroCuatrimestre(numero);
                boleta.setNombrePeriodo(materia.getNombrePeriodo());
                boleta.setNombreGrupo(materia.getNombreGrupo());
                boleta.setNombreNivel(materia.getNombreNivel());
                porCuatrimestre.put(clave, boleta);
            }

            boleta.getMaterias().add(materia);
        }

        ArrayList<modelo.BoletaCuatrimestre> lista = new ArrayList<>(porCuatrimestre.values());
        lista.sort((a, b) -> Integer.compare(a.getNumeroCuatrimestre(), b.getNumeroCuatrimestre()));

        for (modelo.BoletaCuatrimestre boleta : lista)
        {
            double suma = 0;
            int contador = 0;

            for (Calificacion materia : boleta.getMaterias())
            {
                if (materia.getPromedioFinal() != null)
                {
                    suma += materia.getPromedioFinal();
                    contador++;
                }
            }

            boleta.setMateriasConPromedio(contador);

            if (contador > 0)
            {
                double promedio = suma / contador;
                promedio = Math.round(promedio * 10.0) / 10.0;
                boleta.setPromedioCuatrimestre(promedio);
            }
        }

        return lista;
    }



    public ResultadoCaptura capturar(int idGrupoMateria, int idDocente, int numeroParcial,
            Map<Integer, String> notasPorInscripcionMateria, Usuario responsable)
    {
        if (!daoAsignacion.perteneceADocente(idGrupoMateria, idDocente))
        {
            return ResultadoCaptura.fallo("Esa materia de grupo no está a tu cargo.");
        }

        DocenteAsignacion contexto = daoAsignacion.buscarInfoGrupoMateria(idGrupoMateria);

        if (contexto == null)
        {
            return ResultadoCaptura.fallo("La materia de grupo ya no existe.");
        }

        if (contexto.getParcialActivo() == null)
        {
            return ResultadoCaptura.fallo("Todavía no hay un parcial activo configurado para este periodo.");
        }

        if (contexto.getParcialActivo() != numeroParcial)
        {
            return ResultadoCaptura.fallo("El parcial activo para este periodo es el " + contexto.getParcialActivo()
                    + ", no puedes capturar el " + numeroParcial + ".");
        }

        int actualizados = 0;

        for (Map.Entry<Integer, String> entrada : notasPorInscripcionMateria.entrySet())
        {
            int idInscripcionMateria = entrada.getKey();
            String notaTexto = entrada.getValue() == null ? "" : entrada.getValue().trim();

            if (!daoCalificacion.perteneceAGrupoMateria(idInscripcionMateria, idGrupoMateria))
            {
                continue;
            }

            Double nota;

            if (notaTexto.isEmpty())
            {
                nota = null;
            }
            else
            {
                try
                {
                    nota = Double.valueOf(notaTexto.replace(',', '.'));
                }
                catch (NumberFormatException excepcion)
                {
                    return ResultadoCaptura.fallo("Hay una calificación con un valor no numérico.");
                }

                if (nota < 0 || nota > 10)
                {
                    return ResultadoCaptura.fallo("Las calificaciones deben estar entre 0 y 10.");
                }

                nota = Math.round(nota * 10.0) / 10.0;
            }

            daoCalificacion.actualizarParcial(idInscripcionMateria, numeroParcial, nota);
            actualizarPromedioYEstado(idInscripcionMateria);
            Calificacion calificacion = daoCalificacion.buscarPorInscripcionMateria(idInscripcionMateria);
            if (calificacion != null && "Aprobada".equals(calificacion.getEstadoMateria()))
            {
                int idAlumno = daoCalificacion.buscarIdAlumnoPorInscripcionMateria(idInscripcionMateria);
                finalizarEgresoSiCorresponde(idAlumno);
            }
            actualizados++;
        }

        servicioBitacora.registrarAlta(responsable, "calificaciones", idGrupoMateria,
                "Capturó calificaciones del parcial " + numeroParcial + " de " + contexto.getNombreMateria()
                + " en el grupo " + contexto.getNombreGrupo());

        return ResultadoCaptura.exito(actualizados);
    }

    public void finalizarEgresoSiCorresponde(int idAlumno)
    {
        Alumno alumno = servicioAlumno.buscarPorId(idAlumno);
        if (alumno == null || "Egresado".equals(alumno.getEstatus())) return;
        TrayectoriaAcademica trayectoria = new DAOTrayectoriaAcademica().buscarActivaPorAlumno(idAlumno);
        if (trayectoria != null && new DAOTrayectoriaAcademica().estaListaParaEgreso(trayectoria.getIdTrayectoria()))
        {
            new DAOTrayectoriaAcademica().actualizarEstado(trayectoria.getIdTrayectoria(), "EGRESADA", true);
            new DAOInscripcion().finalizarActivasDeTrayectoria(trayectoria.getIdTrayectoria());
            servicioAlumno.marcarEgresado(idAlumno);
            EmailUtil.enviarCambioTrayectoria(alumno.getCorreo(), alumno.getNombreCompleto(), "Egresado");
        }
    }

    private void actualizarPromedioYEstado(int idInscripcionMateria)
    {

        Calificacion calificacion = daoCalificacion.buscarPorInscripcionMateria(idInscripcionMateria);

        if (calificacion == null)
        {
            return;
        }

        if (calificacion.getParcial1() != null && calificacion.getParcial2() != null && calificacion.getParcial3() != null)
        {
            double promedio = (calificacion.getParcial1() + calificacion.getParcial2() + calificacion.getParcial3()) / 3.0;
            promedio = Math.round(promedio * 10.0) / 10.0;
            daoCalificacion.actualizarPromedioFinal(idInscripcionMateria, promedio);

            String estado = promedio >= NOTA_APROBATORIA ? "Aprobada" : "Reprobada";
            daoInscripcionMateria.actualizarEstado(idInscripcionMateria, estado);
        }
        else
        {
            daoCalificacion.actualizarPromedioFinal(idInscripcionMateria, null);
            daoInscripcionMateria.actualizarEstado(idInscripcionMateria, "Cursando");
        }
    }

    public static class ResultadoCaptura
    {
        private final boolean exito;
        private final String mensajeError;
        private final int alumnosActualizados;

        private ResultadoCaptura(boolean exito, String mensajeError, int alumnosActualizados)
        {
            this.exito = exito;
            this.mensajeError = mensajeError;
            this.alumnosActualizados = alumnosActualizados;
        }

        public static ResultadoCaptura exito(int alumnosActualizados)
        {
            return new ResultadoCaptura(true, null, alumnosActualizados);
        }

        public static ResultadoCaptura fallo(String mensajeError)
        {
            return new ResultadoCaptura(false, mensajeError, 0);
        }

        public boolean isExito()
        {
            return exito;
        }

        public String getMensajeError()
        {
            return mensajeError;
        }

        public int getAlumnosActualizados()
        {
            return alumnosActualizados;
        }
    }
}
