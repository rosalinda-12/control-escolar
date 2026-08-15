package servicio;

import doa.DAOCalificacion;
import doa.DAODocenteAsignacion;
import doa.DAOInscripcionMateria;
import modelo.Calificacion;
import modelo.DocenteAsignacion;
import modelo.Usuario;
import java.util.ArrayList;
import java.util.Map;

public class ServicioCalificacion
{
    /**
     * Nota mínima aprobatoria. Se deja como constante porque el reglamento
     * de la institución podría cambiarla; hoy no hay ningún catálogo en la
     * base de datos que la defina.
     */
    private static final double NOTA_APROBATORIA = 6.0;

    private final DAODocenteAsignacion daoAsignacion;
    private final DAOCalificacion daoCalificacion;
    private final DAOInscripcionMateria daoInscripcionMateria;
    private final ServicioBitacora servicioBitacora;

    public ServicioCalificacion()
    {
        this.daoAsignacion = new DAODocenteAsignacion();
        this.daoCalificacion = new DAOCalificacion();
        this.daoInscripcionMateria = new DAOInscripcionMateria();
        this.servicioBitacora = new ServicioBitacora();
    }

    public ArrayList<DocenteAsignacion> listarGruposDelDocente(int idDocente)
    {
        return daoAsignacion.listarPorDocente(idDocente);
    }

    /**
     * Devuelve el contexto de la materia de grupo (para el encabezado de la
     * pantalla de captura) solo si en verdad pertenece a ese docente.
     */
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

    /**
     * Arma la boleta de calificaciones que ve el propio alumno: sus
     * materias agrupadas por cuatrimestre, con el promedio final de cada
     * materia y el promedio general de cada cuatrimestre (el promedio de
     * los promedios finales de las materias que ya lo tienen calculado).
     */
    public ArrayList<modelo.BoletaCuatrimestre> obtenerBoletaAlumno(int idAlumno)
    {
        ArrayList<Calificacion> materias = daoCalificacion.listarPorAlumno(idAlumno);

        java.util.LinkedHashMap<Integer, modelo.BoletaCuatrimestre> porCuatrimestre = new java.util.LinkedHashMap<>();

        for (Calificacion materia : materias)
        {
            int numero = materia.getNumeroCuatrimestre();

            modelo.BoletaCuatrimestre boleta = porCuatrimestre.get(numero);

            if (boleta == null)
            {
                boleta = new modelo.BoletaCuatrimestre();
                boleta.setNumeroCuatrimestre(numero);
                boleta.setNombrePeriodo(materia.getNombrePeriodo());
                boleta.setNombreGrupo(materia.getNombreGrupo());
                porCuatrimestre.put(numero, boleta);
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

    /**
     * Captura las notas del parcial indicado para varios alumnos a la vez.
     * Solo se permite capturar el parcial que está activo en el periodo del
     * grupo, y solo si la materia de grupo en verdad es del docente. Cuando
     * los tres parciales de un alumno ya están capturados, calcula el
     * promedio final y actualiza el estado de esa materia
     * (Aprobada/Reprobada).
     */
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
            actualizados++;
        }

        servicioBitacora.registrarAlta(responsable, "calificaciones", idGrupoMateria,
                "Capturó calificaciones del parcial " + numeroParcial + " de " + contexto.getNombreMateria()
                + " en el grupo " + contexto.getNombreGrupo());

        return ResultadoCaptura.exito(actualizados);
    }

    private void actualizarPromedioYEstado(int idInscripcionMateria)
    {
        // Vuelve a leer la fila recién actualizada para saber si ya están
        // los tres parciales y poder calcular/limpiar el promedio.
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
