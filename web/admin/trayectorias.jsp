<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Alumno"%>
<%@page import="modelo.TrayectoriaAcademica"%>
<%@page import="modelo.PlanEstudio"%>
<%@page import="modelo.BoletaCuatrimestre"%>
<%@page import="modelo.Calificacion"%>
<%@page import="java.util.ArrayList"%>
<%
    Alumno alumno = (Alumno) request.getAttribute("alumno");
    ArrayList<TrayectoriaAcademica> trayectorias = (ArrayList<TrayectoriaAcademica>) request.getAttribute("trayectorias");
    ArrayList<PlanEstudio> planes = (ArrayList<PlanEstudio>) request.getAttribute("planes");
    ArrayList<BoletaCuatrimestre> boleta = (ArrayList<BoletaCuatrimestre>) request.getAttribute("boleta");
    int cuatrimestresCursados = boleta == null ? 0 : boleta.size();
    int materiasCursadas = 0;
    double sumaPromedios = 0;
    int promediosRegistrados = 0;
    if (boleta != null) {
        for (BoletaCuatrimestre cuatrimestre : boleta) {
            materiasCursadas += cuatrimestre.getMaterias().size();
            for (Calificacion materia : cuatrimestre.getMaterias()) {
                if (materia.getPromedioFinal() != null) {
                    sumaPromedios += materia.getPromedioFinal();
                    promediosRegistrados++;
                }
            }
        }
    }
    String promedioGeneral = promediosRegistrados == 0 ? "—" : String.format(java.util.Locale.US, "%.1f", sumaPromedios / promediosRegistrados);
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Trayectorias</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <main class="container trayectoria-page">
            <header class="trayectoria-header">
                <div class="trayectoria-header-main">
                    <a href="SAlumnos" class="trayectoria-back"><i class="bi bi-arrow-left"></i> Alumnos</a>
                    <span class="trayectoria-eyebrow"><i class="bi bi-person-vcard"></i> Expediente académico</span>
                    <h1><%= alumno.getNombreCompleto()%></h1>
                    <div class="trayectoria-header-meta">
                        <span><i class="bi bi-upc-scan"></i> Matrícula del alumno</span>
                        <span class="badge text-bg-secondary"><%= trayectorias.isEmpty() ? "Sin trayectoria" : trayectorias.get(0).getMatricula()%></span>
                        <span class="student-status-inline <%= "Activo".equals(alumno.getEstatus()) ? "is-active" : "is-inactive" %>"><i class="bi <%= "Activo".equals(alumno.getEstatus()) ? "bi-check-circle-fill" : "bi-exclamation-circle-fill" %>"></i><%= alumno.getEstatus()%></span>
                    </div>
                </div>
                <div class="trayectoria-header-actions">
                    <button class="btn btn-outline-formal" data-bs-toggle="modal" data-bs-target="#modalNuevaCarrera"><i class="bi bi-signpost-split me-1"></i>Agregar carrera</button>
                    <% if (!planes.isEmpty()) { %>
                    <button class="btn btn-primary-formal" data-bs-toggle="modal" data-bs-target="#modalTrayectoria"><i class="bi bi-arrow-left-right me-1"></i>Registrar cambio</button>
                    <% } %>
                </div>
            </header>

            <section class="trayectoria-summary" aria-label="Resumen académico">
                <div class="summary-heading"><span class="trayectoria-eyebrow"><i class="bi bi-bar-chart-line"></i> Resumen académico</span><span class="summary-caption">Información acumulada</span></div>
                <div class="summary-metrics">
                    <div class="summary-metric"><i class="bi bi-calendar2-week"></i><div><strong><%= cuatrimestresCursados%></strong><span>Cuatrimestres cursados</span></div></div>
                    <div class="summary-metric"><i class="bi bi-journal-bookmark"></i><div><strong><%= materiasCursadas%></strong><span>Materias cursadas</span></div></div>
                    <div class="summary-metric"><i class="bi bi-star"></i><div><strong><%= promedioGeneral%></strong><span>Promedio general</span></div></div>
                    <div class="summary-metric"><i class="bi bi-person-check"></i><div><strong><%= alumno.getEstatus()%></strong><span>Estado actual</span></div></div>
                </div>
            </section>
            <% if (planes.isEmpty()) { %>
            <p class="texto-info mt-2">Todavía no hay ningún plan de estudios vigente. Usa "Agregar carrera" y luego da de alta su plan de estudios (con al menos un nivel académico) para poder registrar una trayectoria.</p>
            <% } %>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (trayectorias.isEmpty()) { %>
            <div class="mensaje-exito mt-4">Este alumno todavía no tiene ninguna trayectoria registrada.</div>
            <% } else { %>
            <section class="trayectoria-section">
                <div class="section-heading-row">
                    <div><span class="trayectoria-eyebrow"><i class="bi bi-signpost-split"></i> Historial de programas</span><h2>Trayectoria académica</h2><p>Las carreras del alumno y su evolución dentro del sistema.</p></div>
                    <div class="barra-filtros trayectoria-filter" data-filtros-tabla="#contenedorTrayectorias">
                        <div class="campo-filtro"><label for="filtroEstadoTrayectorias">Estado</label><select id="filtroEstadoTrayectorias" class="form-select form-select-sm" data-filtro-campo="estado"><option value="" selected>Todas</option><option value="ACTIVA">Activas</option><option value="EGRESADA">Egresadas</option><option value="BAJA_TEMPORAL">Baja temporal</option><option value="BAJA_DEFINITIVA">Baja definitiva</option><option value="CAMBIO">Cambió de trayectoria</option></select></div>
                        <span class="filtro-contador" data-filtro-contador></span>
                    </div>
                </div>
                <div class="trayectoria-list" id="contenedorTrayectorias">
                    <% for (TrayectoriaAcademica trayectoria : trayectorias) { %>
                    <article class="trayectoria-card" data-fila-filtrable data-estado="<%= trayectoria.getEstado()%>">
                        <div class="trayectoria-card-content">
                            <div class="trayectoria-card-icon"><i class="bi bi-mortarboard-fill"></i></div>
                            <div class="trayectoria-card-copy"><span class="card-label">Carrera</span><h3><%= trayectoria.getNombreCarrera()%></h3><p class="trayectoria-specialty"><i class="bi bi-bookmark"></i> <%= trayectoria.getNombrePlan()%></p><div class="trayectoria-card-details"><span><i class="bi bi-upc-scan"></i> <%= trayectoria.getMatricula()%></span><span><i class="bi bi-calendar3"></i> Inicio <%= trayectoria.getFechaInicio()%></span><% if (trayectoria.getFechaFin() != null) { %><span><i class="bi bi-calendar-check"></i> Fin <%= trayectoria.getFechaFin()%></span><% } %></div></div>
                            <div class="trayectoria-card-actions"><span class="trajectory-status status-<%= trayectoria.getEstado().toLowerCase()%>"><i class="bi <%= "ACTIVA".equals(trayectoria.getEstado()) ? "bi-check-circle-fill" : "EGRESADA".equals(trayectoria.getEstado()) ? "bi-mortarboard-fill" : "bi-info-circle-fill" %>"></i><%= "ACTIVA".equals(trayectoria.getEstado()) ? "Activa" : "EGRESADA".equals(trayectoria.getEstado()) ? "Egresada" : "CAMBIO".equals(trayectoria.getEstado()) ? "Continuó en otra trayectoria" : "BAJA_TEMPORAL".equals(trayectoria.getEstado()) ? "Baja temporal" : "Baja definitiva" %></span><a href="#historialAcademico" class="btn btn-primary-formal"><i class="bi bi-journal-text me-1"></i>Ver cuatrimestres</a><% if ("ACTIVA".equals(trayectoria.getEstado()) || "BAJA_TEMPORAL".equals(trayectoria.getEstado()) || "BAJA_DEFINITIVA".equals(trayectoria.getEstado())) { %><div class="dropdown"><button class="btn btn-icon-formal" type="button" data-bs-toggle="dropdown" aria-expanded="false" aria-label="Acciones de trayectoria"><i class="bi bi-three-dots-vertical"></i></button><ul class="dropdown-menu dropdown-menu-end">
                                <% if ("ACTIVA".equals(trayectoria.getEstado())) { %><li><form method="post" action="STrayectorias"><input type="hidden" name="accion" value="BajaTemporal"><input type="hidden" name="idAlumno" value="<%= alumno.getIdAlumno()%>"><input type="hidden" name="idTrayectoria" value="<%= trayectoria.getIdTrayectoria()%>"><button type="submit" class="dropdown-item"><i class="bi bi-pause-circle me-2"></i>Baja temporal</button></form></li><li><form method="post" action="STrayectorias" onsubmit="return confirm('¿Dar de baja definitiva esta trayectoria? Ya no se podrá inscribir en ella salvo que se revierta por error.');"><input type="hidden" name="accion" value="BajaDefinitiva"><input type="hidden" name="idAlumno" value="<%= alumno.getIdAlumno()%>"><input type="hidden" name="idTrayectoria" value="<%= trayectoria.getIdTrayectoria()%>"><button type="submit" class="dropdown-item text-danger"><i class="bi bi-trash3 me-2"></i>Baja definitiva</button></form></li><% } else if ("BAJA_TEMPORAL".equals(trayectoria.getEstado())) { %><li><form method="post" action="STrayectorias"><input type="hidden" name="accion" value="Reanudar"><input type="hidden" name="idAlumno" value="<%= alumno.getIdAlumno()%>"><input type="hidden" name="idTrayectoria" value="<%= trayectoria.getIdTrayectoria()%>"><button type="submit" class="dropdown-item"><i class="bi bi-play-circle me-2"></i>Reanudar trayectoria</button></form></li><% } else if ("BAJA_DEFINITIVA".equals(trayectoria.getEstado())) { %><li><form method="post" action="STrayectorias" onsubmit="return confirm('¿Revertir la baja definitiva? Úsalo solo para corregir un error de captura.');"><input type="hidden" name="accion" value="RevertirBaja"><input type="hidden" name="idAlumno" value="<%= alumno.getIdAlumno()%>"><input type="hidden" name="idTrayectoria" value="<%= trayectoria.getIdTrayectoria()%>"><button type="submit" class="dropdown-item"><i class="bi bi-arrow-counterclockwise me-2"></i>Revertir baja</button></form></li><% } else { %><li><span class="dropdown-item-text text-muted">Sin acciones disponibles</span></li><% } %>
                            </ul></div><% } %></div>
                        </div>
                    </article>
                    <% } %>
                </div>
                <div class="mensaje-exito mt-3" data-filtro-vacio style="display:none;">Ningún registro coincide con el filtro seleccionado.</div>
            </section>
                            <% } %>

                            <section class="trayectoria-section historial-section" id="historialAcademico">
                                <div class="section-heading-row">
                                    <div><span class="trayectoria-eyebrow"><i class="bi bi-clock-history"></i> Rendimiento</span><h2>Historial académico por nivel</h2><p>Consulta TSU e Ingeniería por separado y abre sus calificaciones cuando las necesites.</p></div>
                                </div>
                                <% if (boleta == null || boleta.isEmpty()) { %>
                                <div class="mensaje-exito">Todavía no hay materias cursadas o calificaciones registradas.</div>
                                <% } else { %>
                                <div class="historial-grid">
                                <% for (BoletaCuatrimestre cuatrimestre : boleta) { %>
                                <article class="historial-card">
                                    <div class="historial-card-top"><div><span class="card-label"><%= cuatrimestre.getNombreNivel()%> · Periodo académico</span><h3>Cuatrimestre <%= cuatrimestre.getNumeroCuatrimestre()%></h3></div><span class="historial-number"><%= cuatrimestre.getMaterias().size()%> materias</span></div>
                                    <div class="historial-card-data"><span><i class="bi bi-calendar3"></i><%= cuatrimestre.getNombrePeriodo()%></span><span><i class="bi bi-people"></i>Grupo <%= cuatrimestre.getNombreGrupo()%></span></div>
                                    <div class="historial-card-footer"><div><span class="card-label">Promedio</span><strong><%= cuatrimestre.getPromedioCuatrimestre() == null ? "—" : cuatrimestre.getPromedioCuatrimestre()%></strong><small><%= cuatrimestre.isCompleto() ? "Concluido" : "En curso"%></small></div><button type="button" class="btn btn-outline-formal" data-bs-toggle="collapse" data-bs-target="#materiasCuatrimestre<%= cuatrimestre.getIdTrayectoria()%>_<%= cuatrimestre.getNumeroCuatrimestre()%>" aria-expanded="false" aria-controls="materiasCuatrimestre<%= cuatrimestre.getIdTrayectoria()%>_<%= cuatrimestre.getNumeroCuatrimestre()%>"><i class="bi bi-table me-1"></i>Ver materias</button></div>
                                    <div class="collapse materias-panel" id="materiasCuatrimestre<%= cuatrimestre.getIdTrayectoria()%>_<%= cuatrimestre.getNumeroCuatrimestre()%>">
                                        <div class="table-responsive"><table class="table table-formal align-middle mb-0"><thead><tr><th>Materia</th><th class="text-center">P1</th><th class="text-center">P2</th><th class="text-center">P3</th><th class="text-center">Final</th><th>Estado</th></tr></thead><tbody>
                                        <% for (Calificacion materia : cuatrimestre.getMaterias()) { %><tr><td><strong><%= materia.getNombreMateria()%></strong></td><td class="text-center"><%= materia.getParcial1() == null ? "—" : materia.getParcial1()%></td><td class="text-center"><%= materia.getParcial2() == null ? "—" : materia.getParcial2()%></td><td class="text-center"><%= materia.getParcial3() == null ? "—" : materia.getParcial3()%></td><td class="text-center fw-semibold"><%= materia.getPromedioFinal() == null ? "—" : materia.getPromedioFinal()%></td><td><% if ("Aprobada".equals(materia.getEstadoMateria())) { %><span class="badge text-bg-success">Aprobada</span><% } else if ("Reprobada".equals(materia.getEstadoMateria())) { %><span class="badge text-bg-danger">Reprobada</span><% } else { %><span class="badge text-bg-secondary">En curso</span><% } %></td></tr><% } %>
                                        </tbody></table></div>
                                        <% if (cuatrimestre.getPromedioCuatrimestre() != null && !cuatrimestre.isCompleto()) { %><p class="texto-info mb-0 mt-2"><i class="bi bi-info-circle me-1"></i>Promedio preliminar</p><% } %>
                                    </div>
                                </article>
                                <% } %>
                                </div>
                                <% } %>
                            </section>
        <div class="modal fade modal-formal" id="modalTrayectoria" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="STrayectorias">
                        <div class="modal-header">
                            <h5 class="modal-title">Registrar cambio de carrera/plan</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="Agregar">
                            <input type="hidden" name="idAlumno" value="<%= alumno.getIdAlumno()%>">
                            <div class="mb-3">
                                <label class="form-label">Carrera / Plan</label>
                                <select name="selPlan" class="form-select" required>
                                    <% for (PlanEstudio plan : planes) { %>
                                    <option value="<%= plan.getIdPlan()%>"><%= plan.getNombreCarrera()%> — <%= plan.getNombrePlan()%> (v<%= plan.getVersion()%>)</option>
                                    <% } %>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Matrícula</label>
                                <input type="text" name="tfMatricula" class="form-control" placeholder="123456" required>
                                <div class="form-text">Convención sugerida: TSU sin sufijo, Ingeniería "_i", Licenciatura "_l" (ej. 123456_i).</div>
                            </div>
                            <% if (!trayectorias.isEmpty()) { %>
                            <div class="mb-3">
                                <label class="form-label">¿Continúa una trayectoria anterior?</label>
                                <select name="selTrayectoriaAnterior" class="form-select">
                                    <option value="">No, es independiente</option>
                                    <% for (TrayectoriaAcademica trayectoria : trayectorias) { %>
                                    <% if ("ACTIVA".equals(trayectoria.getEstado()) || "BAJA_TEMPORAL".equals(trayectoria.getEstado())) { %>
                                    <option value="<%= trayectoria.getIdTrayectoria()%>"><%= trayectoria.getMatricula()%> — <%= trayectoria.getNombreCarrera()%></option>
                                    <% } %>
                                    <% } %>
                                </select>
                                <div class="form-text">Si la eliges, esa trayectoria se cerrará automáticamente (ej. pasó de TSU a Ingeniería).</div>
                            </div>
                            <% } %>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary-formal">Guardar</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>


        <div class="modal fade modal-formal" id="modalNuevaCarrera" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SCarreras">
                        <div class="modal-header">
                            <h5 class="modal-title">Nueva carrera</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="Agregar">
                            <input type="hidden" name="retorno" value="STrayectorias?idAlumno=<%= alumno.getIdAlumno()%>">
                            <div class="mb-3">
                                <label class="form-label">Nombre de la carrera</label>
                                <input type="text" name="tfNombreCarrera" class="form-control" placeholder="Ingeniería en Desarrollo de Software" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Clave</label>
                                <input type="text" name="tfClaveCarrera" class="form-control text-uppercase" placeholder="IDS" maxlength="20" required>
                                <div class="form-text">Se usa como identificador corto, por ejemplo en matrículas y reportes.</div>
                            </div>
                            <div class="mensaje-exito mb-0">
                                <i class="bi bi-info-circle me-1"></i>
                                Después de crearla, ve a Académico &gt; Planes de estudio para darle un plan con al menos un
                                nivel académico: sin eso, todavía no podrás registrarla aquí en Trayectoria.
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary-formal">Guardar</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
            </main>
    </div>
</div>
    </body>
</html>
