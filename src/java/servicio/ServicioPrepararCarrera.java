package servicio;

import modelo.Carrera;
import modelo.NivelAcademico;
import modelo.PlanEstudio;
import modelo.Usuario;
import java.time.LocalDate;
import java.util.ArrayList;

public class ServicioPrepararCarrera
{
    private final ServicioCarrera servicioCarrera;
    private final ServicioPlanEstudio servicioPlan;
    private final ServicioNivelAcademico servicioNivel;

    public ServicioPrepararCarrera()
    {
        this.servicioCarrera = new ServicioCarrera();
        this.servicioPlan = new ServicioPlanEstudio();
        this.servicioNivel = new ServicioNivelAcademico();
    }

    public ResultadoSimple preparar(String nombreCarrera, String claveCarrera, String nombrePlan,
            String version, int duracion, Usuario responsable)
    {
        if (duracion < 1 || duracion > 20)
        {
            return ResultadoSimple.fallo("La duración debe estar entre 1 y 20 cuatrimestres.");
        }

        Carrera carrera = new Carrera();
        carrera.setNombreCarrera(nombreCarrera.trim());
        carrera.setClaveCarrera(claveCarrera.trim().toUpperCase());
        ServicioCarrera.ResultadoCarrera altaCarrera = servicioCarrera.agregar(carrera, responsable);
        if (!altaCarrera.isExito()) return ResultadoSimple.fallo(altaCarrera.getMensajeError());

        ArrayList<NivelAcademico> niveles = servicioNivel.listar();
        Integer idTsu = null;
        Integer idSuperior = null;
        for (NivelAcademico nivel : niveles)
        {
            if ("TSU".equalsIgnoreCase(nivel.getNombreNivel())) idTsu = nivel.getIdNivel();
            else if (idSuperior == null) idSuperior = nivel.getIdNivel();
        }
        if (idTsu == null)
        {
            servicioCarrera.eliminarODesactivar(altaCarrera.getIdCarrera(), responsable);
            return ResultadoSimple.fallo("La carrera se creó, pero falta registrar el nivel académico TSU.");
        }
        if (duracion > 6 && idSuperior == null)
        {
            servicioCarrera.eliminarODesactivar(altaCarrera.getIdCarrera(), responsable);
            return ResultadoSimple.fallo("La carrera se creó, pero falta registrar un nivel superior para los cuatrimestres 7 en adelante.");
        }

        PlanEstudio plan = new PlanEstudio();
        plan.setIdCarrera(altaCarrera.getIdCarrera());
        plan.setNombrePlan(nombrePlan.trim());
        plan.setVersion(version.trim());
        plan.setDuracionCuatrimestres(duracion);
        plan.setFechaVigenciaInicio(LocalDate.now());

        int cantidadNiveles = duracion > 6 ? 2 : 1;
        int[] idsNivel = new int[cantidadNiveles];
        String[] titulos = new String[cantidadNiveles];
        int[] inicios = new int[cantidadNiveles];
        int[] fines = new int[cantidadNiveles];
        idsNivel[0] = idTsu;
        titulos[0] = "TSU en " + nombreCarrera.trim();
        inicios[0] = 1;
        fines[0] = Math.min(6, duracion);
        if (cantidadNiveles == 2)
        {
            idsNivel[1] = idSuperior;
            titulos[1] = "Ingeniería en " + nombreCarrera.trim();
            inicios[1] = 7;
            fines[1] = duracion;
        }

        ResultadoSimple altaPlan = servicioPlan.agregar(plan, idsNivel, titulos, inicios, fines, responsable);
        if (!altaPlan.isExito())
        {
            servicioCarrera.eliminarODesactivar(altaCarrera.getIdCarrera(), responsable);
            return altaPlan;
        }
        return ResultadoSimple.exito(altaPlan.getIdGenerado());
    }
}
