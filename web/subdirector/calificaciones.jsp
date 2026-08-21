<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Calificacion"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.LinkedHashSet"%>
<%@page import="java.util.Map"%>
<%
    ArrayList<Calificacion> calificaciones = (ArrayList<Calificacion>) request.getAttribute("calificaciones");
    Map<Integer, Calificacion> alumnosUnicos = new LinkedHashMap<>();
    Map<Integer, LinkedHashSet<String>> gruposPorAlumno = new LinkedHashMap<>();
    Map<Integer, LinkedHashSet<String>> periodosPorAlumno = new LinkedHashMap<>();
    Map<Integer, LinkedHashSet<String>> estadosPorAlumno = new LinkedHashMap<>();
    for (Calificacion calificacion : calificaciones) {
        int idAlumno = calificacion.getIdAlumno();
        alumnosUnicos.putIfAbsent(idAlumno, calificacion);
        gruposPorAlumno.computeIfAbsent(idAlumno, clave -> new LinkedHashSet<>()).add(calificacion.getNombreGrupo());
        periodosPorAlumno.computeIfAbsent(idAlumno, clave -> new LinkedHashSet<>()).add(calificacion.getNombrePeriodo());
        estadosPorAlumno.computeIfAbsent(idAlumno, clave -> new LinkedHashSet<>()).add(calificacion.getEstadoMateria());
    }
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Calificaciones</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_subdirector.jsp" %>

        <div class="container">
            <div class="mt-4">
                <h2>Calificaciones</h2>
                <p class="texto-info mb-0">Consulta de solo lectura de las calificaciones de tu carrera. Para corregirlas, contacta a Control Escolar.</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error mt-3"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>
            <div class="barra-filtros mt-4" data-filtros-tabla="#tbodyCalificacionesSubdirector">
                <div class="campo-filtro">
                    <label for="filtroEstadoCalificacionesSubdirector">Estado</label>
                    <select id="filtroEstadoCalificacionesSubdirector" class="form-select form-select-sm" data-filtro-campo="estado">
                        <option value="" selected>Todos</option>
                        <option value="Cursando">Cursando</option>
                        <option value="Aprobada">Aprobadas</option>
                        <option value="Reprobada">Reprobadas</option>
                    </select>
                </div>
                <div class="campo-filtro">
                    <label for="filtroGrupoCalificacionesSubdirector">Grupo</label>
                    <select id="filtroGrupoCalificacionesSubdirector" class="form-select form-select-sm" data-filtro-campo="grupo">
                        <option value="" selected>Todos</option>
                        <% java.util.LinkedHashSet<String> gruposCalificaciones = new java.util.LinkedHashSet<String>();
                           for (Calificacion c : calificaciones) { gruposCalificaciones.add(c.getNombreGrupo()); }
                           for (String grupo : gruposCalificaciones) { %>
                        <option value="<%= grupo%>"><%= grupo%></option>
                        <% } %>
                    </select>
                </div>
                <div class="campo-filtro">
                    <label for="filtroPeriodoCalificacionesSubdirector">Periodo</label>
                    <select id="filtroPeriodoCalificacionesSubdirector" class="form-select form-select-sm" data-filtro-campo="periodo">
                        <option value="" selected>Todos</option>
                        <% java.util.LinkedHashSet<String> periodosCalificaciones = new java.util.LinkedHashSet<String>();
                           for (Calificacion c : calificaciones) { periodosCalificaciones.add(c.getNombrePeriodo()); }
                           for (String periodo : periodosCalificaciones) { %>
                        <option value="<%= periodo%>"><%= periodo%></option>
                        <% } %>
                    </select>
                </div>
                <div class="campo-filtro campo-filtro-texto">
                    <label for="filtroTextoCalificacionesSubdirector">Buscar</label>
                    <input type="text" id="filtroTextoCalificacionesSubdirector" class="form-control form-control-sm" data-filtro-texto placeholder="Matrícula, alumno, materia...">
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>
            <div class="tabla-formal-wrap mt-4">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Matrícula</th>
                            <th>Alumno</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="tbodyCalificacionesSubdirector">
                        <% for (Map.Entry<Integer, Calificacion> entrada : alumnosUnicos.entrySet()) { Calificacion calificacion = entrada.getValue(); int idAlumno = entrada.getKey(); %>
                        <tr data-fila-filtrable data-estado="<%= String.join("|", estadosPorAlumno.get(idAlumno))%>" data-grupo="<%= String.join("|", gruposPorAlumno.get(idAlumno))%>" data-periodo="<%= String.join("|", periodosPorAlumno.get(idAlumno))%>">
                            <td><%= calificacion.getMatricula()%></td>
                            <td><%= calificacion.getNombreAlumno()%></td>
                            <td class="text-end"><a class="btn btn-sm btn-icon-formal" href="STrayectoria?idAlumno=<%= idAlumno%>" title="Ver trayectoria" aria-label="Ver trayectoria"><i class="bi bi-signpost-split"></i></a></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <div class="mensaje-exito mt-3" data-filtro-vacio style="display:none;">Ninguna calificación coincide con los filtros seleccionados.</div>
            </div>
            <% if (calificaciones.isEmpty()) { %>
            <div class="mensaje-exito mt-4">No hay calificaciones registradas todavía para esta carrera.</div>
            <% } %>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
            </main>
    </div>
</div>
    </body>
</html>
