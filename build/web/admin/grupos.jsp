<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Grupo"%>
<%@page import="modelo.PlanCuatrimestre"%>
<%@page import="modelo.Generacion"%>
<%@page import="modelo.Periodo"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Grupo> grupos = (ArrayList<Grupo>) request.getAttribute("grupos");
    ArrayList<PlanCuatrimestre> cuatrimestresDisponibles = (ArrayList<PlanCuatrimestre>) request.getAttribute("cuatrimestresDisponibles");
    ArrayList<Generacion> generaciones = (ArrayList<Generacion>) request.getAttribute("generaciones");
    ArrayList<Periodo> periodos = (ArrayList<Periodo>) request.getAttribute("periodos");
    Grupo grupoEditar = (Grupo) request.getAttribute("grupoEditar");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Grupos</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <div class="d-flex justify-content-between align-items-center mt-4">
                <div>
                    <h2>Grupos</h2>
                    <p class="texto-info mb-0">Al crear un grupo, sus materias se copian automáticamente del currículo del cuatrimestre elegido.</p>
                </div>
                <% if (cuatrimestresDisponibles.isEmpty() || generaciones.isEmpty() || periodos.isEmpty()) { %>
                <span class="badge text-bg-secondary">Completa Planes, Generaciones y Periodos primero</span>
                <% } else { %>
                <button class="btn btn-primary-formal" data-bs-toggle="modal" data-bs-target="#modalGrupo">
                    <i class="bi bi-plus-lg me-1"></i>Nuevo grupo
                </button>
                <% } %>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (grupos.isEmpty()) { %>
            <div class="mensaje-exito mt-4">No hay grupos registrados todavía.</div>
            <% } else { %>
            <div class="barra-filtros" data-filtros-tabla="#tbodyGrupos">
                <div class="campo-filtro">
                    <label for="filtroEstatusGrupos">Estatus</label>
                    <select id="filtroEstatusGrupos" class="form-select form-select-sm" data-filtro-campo="estatus">
                        <option value="Activo" selected>Activos (actuales)</option>
                        <option value="">Todos</option>
                        <option value="Cerrado">Cerrados</option>
                    </select>
                </div>
                <div class="campo-filtro">
                    <label for="filtroCarreraGrupos">Carrera</label>
                    <select id="filtroCarreraGrupos" class="form-select form-select-sm" data-filtro-campo="carrera">
                        <option value="" selected>Todas</option>
                        <% java.util.LinkedHashSet<String> carrerasGrupos = new java.util.LinkedHashSet<String>();
                           for (Grupo g : grupos) { carrerasGrupos.add(g.getNombreCarrera()); }
                           for (String nombreCarrera : carrerasGrupos) { %>
                        <option value="<%= nombreCarrera%>"><%= nombreCarrera%></option>
                        <% } %>
                    </select>
                </div>
                <div class="campo-filtro">
                    <label for="filtroPeriodoGrupos">Periodo</label>
                    <select id="filtroPeriodoGrupos" class="form-select form-select-sm" data-filtro-campo="periodo">
                        <option value="" selected>Todos</option>
                        <% java.util.LinkedHashSet<String> periodosGrupos = new java.util.LinkedHashSet<String>();
                           for (Grupo g : grupos) { periodosGrupos.add(g.getNombrePeriodo()); }
                           for (String nombrePeriodo : periodosGrupos) { %>
                        <option value="<%= nombrePeriodo%>"><%= nombrePeriodo%></option>
                        <% } %>
                    </select>
                </div>
                <div class="campo-filtro campo-filtro-texto">
                    <label for="filtroTextoGrupos">Buscar</label>
                    <input type="text" id="filtroTextoGrupos" class="form-control form-control-sm" data-filtro-texto placeholder="Grupo, generación...">
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Grupo</th>
                            <th>Carrera</th>
                            <th>Cuatrimestre</th>
                            <th>Generación</th>
                            <th>Periodo</th>
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="tbodyGrupos">
                        <% for (Grupo grupo : grupos) { %>
                        <tr data-fila-filtrable data-estatus="<%= grupo.getEstatus()%>" data-carrera="<%= grupo.getNombreCarrera()%>" data-periodo="<%= grupo.getNombrePeriodo()%>">
                            <td><%= grupo.getNombreGrupo()%></td>
                            <td><%= grupo.getNombreCarrera()%></td>
                            <td><%= grupo.getNumeroCuatrimestre()%>°</td>
                            <td><%= grupo.getNombreGeneracion()%></td>
                            <td><%= grupo.getNombrePeriodo()%></td>
                            <td>
                                <% if ("Activo".equals(grupo.getEstatus())) { %>
                                <span class="badge text-bg-success">Activo</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary">Cerrado</span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <% if ("Activo".equals(grupo.getEstatus())) { %><a href="SGrupos?editar=<%= grupo.getIdGrupo()%>" class="btn btn-sm btn-icon-formal" title="Editar grupo" aria-label="Editar grupo"><i class="bi bi-pencil-square"></i></a><% } %>
                                <a href="SAsignaciones?idGrupo=<%= grupo.getIdGrupo()%>" class="btn btn-sm btn-icon-formal" title="Ver docentes" aria-label="Ver docentes"><i class="bi bi-person-lines-fill"></i></a>
                                <% if ("Activo".equals(grupo.getEstatus())) { %>
                                <form method="post" action="SGrupos" class="d-inline">
                                    <input type="hidden" name="accion" value="Cerrar">
                                    <input type="hidden" name="idGrupo" value="<%= grupo.getIdGrupo()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal" title="Desactivar grupo">
                                        <i class="bi bi-toggle-off"></i>
                                    </button>
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

        <div class="modal fade modal-formal" id="modalGrupo" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SGrupos">
                        <div class="modal-header">
                            <h5 class="modal-title"><%= grupoEditar == null ? "Nuevo grupo" : "Editar grupo"%></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="<%= grupoEditar == null ? "Agregar" : "Modificar"%>">
                            <% if (grupoEditar != null) { %><input type="hidden" name="idGrupo" value="<%= grupoEditar.getIdGrupo()%>"><% } %>
                            <div class="mb-3">
                                <label class="form-label">Carrera / Plan / Cuatrimestre</label>
                                <select name="selCuatrimestre" class="form-select" required <%= grupoEditar != null ? "disabled" : ""%>>
                                    <% for (PlanCuatrimestre cuatrimestre : cuatrimestresDisponibles) { %>
                                    <option value="<%= cuatrimestre.getIdPlanCuatrimestre()%>" <%= grupoEditar != null && grupoEditar.getIdPlanCuatrimestre() == cuatrimestre.getIdPlanCuatrimestre() ? "selected" : ""%>><%= cuatrimestre.getEtiqueta()%></option>
                                    <% } %>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Nombre del grupo</label>
                                <input type="text" name="tfNombreGrupo" class="form-control" placeholder="TI-1A" value="<%= grupoEditar == null ? "" : grupoEditar.getNombreGrupo()%>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Generación</label>
                                <select name="selGeneracion" class="form-select" required>
                                    <% for (Generacion generacion : generaciones) { %>
                                    <option value="<%= generacion.getIdGeneracion()%>" <%= grupoEditar != null && grupoEditar.getIdGeneracion() == generacion.getIdGeneracion() ? "selected" : ""%>><%= generacion.getNombreGeneracion()%></option>
                                    <% } %>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Periodo</label>
                                <select name="selPeriodo" class="form-select" required>
                                    <% for (Periodo periodo : periodos) { %>
                                    <option value="<%= periodo.getIdPeriodo()%>" <%= grupoEditar != null && grupoEditar.getIdPeriodo() == periodo.getIdPeriodo() ? "selected" : ""%>><%= periodo.getNombrePeriodo()%></option>
                                    <% } %>
                                </select>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary-formal"><%= grupoEditar == null ? "Guardar" : "Actualizar"%></button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
        <% if (grupoEditar != null) { %><script>new bootstrap.Modal(document.getElementById("modalGrupo")).show();</script><% } %>
            </main>
    </div>
</div>
    </body>
</html>
