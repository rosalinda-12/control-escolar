<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Calificacion"%>
<%@page import="modelo.Carrera"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Calificacion> calificaciones = (ArrayList<Calificacion>) request.getAttribute("calificaciones");
    ArrayList<Carrera> carreras = (ArrayList<Carrera>) request.getAttribute("carreras");
    Calificacion calificacionEditar = (Calificacion) request.getAttribute("calificacionEditar");
    Integer idCarreraSeleccionada = (Integer) request.getAttribute("idCarreraSeleccionada");
    Boolean puedeEditar = (Boolean) request.getAttribute("puedeEditar");
    Boolean puedeEliminar = (Boolean) request.getAttribute("puedeEliminar");
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
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <div class="d-flex justify-content-between align-items-center mt-4 flex-wrap">
                <div>
                    <h2>Calificaciones</h2>
                    <p class="texto-info mb-0">Consulta y corrige las calificaciones capturadas por los docentes en cualquier carrera.</p>
                </div>
                <form method="get" action="SCalificaciones" class="d-flex align-items-center gap-2">
                    <select name="idCarrera" class="form-select" onchange="this.form.submit()">
                        <option value="">Todas las carreras</option>
                        <% for (Carrera carrera : carreras) { %>
                        <option value="<%= carrera.getIdCarrera()%>" <%= (idCarreraSeleccionada != null && idCarreraSeleccionada == carrera.getIdCarrera()) ? "selected" : ""%>>
                            <%= carrera.getNombreCarrera()%>
                        </option>
                        <% } %>
                    </select>
                </form>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error mt-3"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (calificaciones.isEmpty()) { %>
            <div class="mensaje-exito mt-4">No hay calificaciones registradas para este filtro.</div>
            <% } else { %>
            <div class="barra-filtros" data-filtros-tabla="#tbodyCalificaciones">
                <div class="campo-filtro">
                    <label for="filtroEstadoCalif">Estado</label>
                    <select id="filtroEstadoCalif" class="form-select form-select-sm" data-filtro-campo="estado">
                        <option value="Cursando" selected>Cursando (actuales)</option>
                        <option value="">Todos</option>
                        <option value="Aprobada">Aprobadas</option>
                        <option value="Reprobada">Reprobadas</option>
                    </select>
                </div>
                <div class="campo-filtro">
                    <label for="filtroGrupoCalif">Grupo</label>
                    <select id="filtroGrupoCalif" class="form-select form-select-sm" data-filtro-campo="grupo">
                        <option value="" selected>Todos</option>
                        <% java.util.LinkedHashSet<String> gruposCalif = new java.util.LinkedHashSet<String>();
                           for (Calificacion c : calificaciones) { gruposCalif.add(c.getNombreGrupo()); }
                           for (String nombreGrupo : gruposCalif) { %>
                        <option value="<%= nombreGrupo%>"><%= nombreGrupo%></option>
                        <% } %>
                    </select>
                </div>
                <div class="campo-filtro campo-filtro-texto">
                    <label for="filtroTextoCalif">Buscar</label>
                    <input type="text" id="filtroTextoCalif" class="form-control form-control-sm" data-filtro-texto placeholder="Matrícula, alumno, materia...">
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>
            <div class="tabla-formal-wrap mt-3">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Matrícula</th>
                            <th>Alumno</th>
                            <th>Carrera</th>
                            <th>Materia</th>
                            <th>Grupo</th>
                            <th class="text-center">Cuat.</th>
                            <th class="text-center">P1</th>
                            <th class="text-center">P2</th>
                            <th class="text-center">P3</th>
                            <th class="text-center">Promedio</th>
                            <th class="text-center">Estado</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="tbodyCalificaciones">
                        <% for (Calificacion calificacion : calificaciones) { %>
                        <tr data-fila-filtrable data-estado="<%= calificacion.getEstadoMateria()%>" data-grupo="<%= calificacion.getNombreGrupo()%>">
                            <td><%= calificacion.getMatricula()%></td>
                            <td><%= calificacion.getNombreAlumno()%></td>
                            <td><%= calificacion.getNombreCarrera()%></td>
                            <td><%= calificacion.getNombreMateria()%></td>
                            <td><%= calificacion.getNombreGrupo()%></td>
                            <td class="text-center"><%= calificacion.getNumeroCuatrimestre()%></td>
                            <td class="text-center"><%= calificacion.getParcial1() == null ? "—" : calificacion.getParcial1()%></td>
                            <td class="text-center"><%= calificacion.getParcial2() == null ? "—" : calificacion.getParcial2()%></td>
                            <td class="text-center"><%= calificacion.getParcial3() == null ? "—" : calificacion.getParcial3()%></td>
                            <td class="text-center fw-semibold"><%= calificacion.getPromedioFinal() == null ? "—" : calificacion.getPromedioFinal()%></td>
                            <td class="text-center">
                                <% if ("Aprobada".equals(calificacion.getEstadoMateria())) { %>
                                <span class="badge text-bg-success">Aprobada</span>
                                <% } else if ("Reprobada".equals(calificacion.getEstadoMateria())) { %>
                                <span class="badge text-bg-danger">Reprobada</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary">Cursando</span>
                                <% } %>
                            </td>
                            <td class="text-end text-nowrap">
                                <% if (Boolean.TRUE.equals(puedeEditar)) { %>
                                          <a class="btn btn-sm btn-icon-formal" title="Editar calificación" aria-label="Editar calificación"
                                              href="SCalificaciones?editar=<%= calificacion.getIdInscripcionMateria()%><%= idCarreraSeleccionada != null ? "&idCarrera=" + idCarreraSeleccionada : ""%>"><i class="bi bi-pencil-square"></i></a>
                                <% } %>
                                <% if (Boolean.TRUE.equals(puedeEliminar)) { %>
                                <form method="post" action="SCalificaciones" class="d-inline"
                                      onsubmit="return confirm('¿Eliminar esta calificación? El alumno quedará como Cursando de nuevo.');">
                                    <input type="hidden" name="accion" value="Eliminar">
                                    <input type="hidden" name="idInscripcionMateria" value="<%= calificacion.getIdInscripcionMateria()%>">
                                    <% if (idCarreraSeleccionada != null) { %>
                                    <input type="hidden" name="idCarrera" value="<%= idCarreraSeleccionada%>">
                                    <% } %>
                                    <button type="submit" class="btn btn-sm btn-danger-formal btn-icon-formal" title="Eliminar calificación" aria-label="Eliminar calificación"><i class="bi bi-trash3"></i></button>
                                </form>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <div class="mensaje-exito mt-3" data-filtro-vacio style="display:none;">Ningún registro coincide con los filtros seleccionados.</div>
            </div>
            <% } %>
        </div>

        <% if (calificacionEditar != null) { %>
        <div class="modal fade modal-formal" id="modalCalificacion" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SCalificaciones">
                        <div class="modal-header">
                            <h5 class="modal-title">Corregir calificación</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" onclick="window.location='SCalificaciones<%= idCarreraSeleccionada != null ? "?idCarrera=" + idCarreraSeleccionada : ""%>'"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="Editar">
                            <input type="hidden" name="idInscripcionMateria" value="<%= calificacionEditar.getIdInscripcionMateria()%>">
                            <% if (idCarreraSeleccionada != null) { %>
                            <input type="hidden" name="idCarrera" value="<%= idCarreraSeleccionada%>">
                            <% } %>
                            <div class="calificacion-alumno-card">
                                <div class="calificacion-alumno-nombre"><%= calificacionEditar.getNombreAlumno()%></div>
                                <div class="row g-2 small">
                                    <div class="col-6"><strong>Matrícula:</strong> <%= calificacionEditar.getMatricula()%></div>
                                    <div class="col-6"><strong>Carrera:</strong> <%= calificacionEditar.getNombreCarrera()%></div>
                                    <div class="col-6"><strong>Materia:</strong> <%= calificacionEditar.getNombreMateria()%></div>
                                    <div class="col-6"><strong>Grupo:</strong> <%= calificacionEditar.getNombreGrupo()%></div>
                                    <div class="col-6"><strong>Cuatrimestre:</strong> <%= calificacionEditar.getNumeroCuatrimestre()%></div>
                                    <div class="col-6"><strong>Estado:</strong> <%= calificacionEditar.getEstadoMateria()%></div>
                                </div>
                            </div>
                            <p class="texto-info">Deja un campo vacío para borrar ese parcial. El promedio y el estado (Aprobada/Reprobada) se recalculan solos.</p>
                            <div class="row">
                                <div class="col-4 mb-3">
                                    <label class="form-label">Parcial 1</label>
                                    <input type="number" step="0.1" min="0" max="10" name="tfParcial1" class="form-control"
                                           value="<%= calificacionEditar.getParcial1() == null ? "" : calificacionEditar.getParcial1()%>">
                                </div>
                                <div class="col-4 mb-3">
                                    <label class="form-label">Parcial 2</label>
                                    <input type="number" step="0.1" min="0" max="10" name="tfParcial2" class="form-control"
                                           value="<%= calificacionEditar.getParcial2() == null ? "" : calificacionEditar.getParcial2()%>">
                                </div>
                                <div class="col-4 mb-3">
                                    <label class="form-label">Parcial 3</label>
                                    <input type="number" step="0.1" min="0" max="10" name="tfParcial3" class="form-control"
                                           value="<%= calificacionEditar.getParcial3() == null ? "" : calificacionEditar.getParcial3()%>">
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary-formal">Guardar</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <% } %>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
        <% if (calificacionEditar != null) { %>
        <script>
            new bootstrap.Modal(document.getElementById("modalCalificacion")).show();
        </script>
        <% } %>
            </main>
    </div>
</div>
    </body>
</html>
