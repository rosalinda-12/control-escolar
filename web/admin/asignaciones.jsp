<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Grupo"%>
<%@page import="modelo.GrupoMateria"%>
<%@page import="modelo.Docente"%>
<%@page import="java.util.ArrayList"%>
<%
    Grupo grupo = (Grupo) request.getAttribute("grupo");
    ArrayList<GrupoMateria> materiasDelGrupo = (ArrayList<GrupoMateria>) request.getAttribute("materiasDelGrupo");
    ArrayList<Docente> docentes = (ArrayList<Docente>) request.getAttribute("docentes");
    ArrayList<Grupo> gruposParaElegir = (ArrayList<Grupo>) request.getAttribute("grupos");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Asignación de docentes</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <% if (grupo == null) { %>
        <div class="container">
            <h2 class="mt-4">Asignaciones de docentes</h2>
            <p class="texto-info">Elige un grupo para ver y editar sus docentes asignados por materia.</p>

            <% if (gruposParaElegir == null || gruposParaElegir.isEmpty()) { %>
            <div class="mensaje-exito">No hay grupos registrados todavía. Crea uno primero en Grupos.</div>
            <% } else { %>
            <div class="barra-filtros" data-filtros-tabla="#tbodyGruposAsignaciones">
                <div class="campo-filtro">
                    <label for="filtroEstatusGruposAsignaciones">Estatus</label>
                    <select id="filtroEstatusGruposAsignaciones" class="form-select form-select-sm" data-filtro-campo="estatus">
                        <option value="Activo" selected>Activos (actuales)</option>
                        <option value="">Todos</option>
                        <option value="Cerrado">Cerrados</option>
                    </select>
                </div>
                <div class="campo-filtro">
                    <label for="filtroCarreraGruposAsignaciones">Carrera</label>
                    <select id="filtroCarreraGruposAsignaciones" class="form-select form-select-sm" data-filtro-campo="carrera">
                        <option value="" selected>Todas</option>
                        <% java.util.LinkedHashSet<String> carrerasGruposAsig = new java.util.LinkedHashSet<String>();
                           for (Grupo g : gruposParaElegir) { carrerasGruposAsig.add(g.getNombreCarrera()); }
                           for (String nombreCarrera : carrerasGruposAsig) { %>
                        <option value="<%= nombreCarrera%>"><%= nombreCarrera%></option>
                        <% } %>
                    </select>
                </div>
                <div class="campo-filtro campo-filtro-texto">
                    <label for="filtroTextoGruposAsignaciones">Buscar</label>
                    <input type="text" id="filtroTextoGruposAsignaciones" class="form-control form-control-sm" data-filtro-texto placeholder="Grupo, generación...">
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
                            <th>Periodo</th>
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="tbodyGruposAsignaciones">
                        <% for (Grupo g : gruposParaElegir) { %>
                        <tr data-fila-filtrable data-estatus="<%= g.getEstatus()%>" data-carrera="<%= g.getNombreCarrera()%>">
                            <td><%= g.getNombreGrupo()%></td>
                            <td><%= g.getNombreCarrera()%></td>
                            <td><%= g.getNumeroCuatrimestre()%>°</td>
                            <td><%= g.getNombrePeriodo()%></td>
                            <td>
                                <% if ("Activo".equals(g.getEstatus())) { %>
                                <span class="badge text-bg-success">Activo</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary">Cerrado</span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <a href="SAsignaciones?idGrupo=<%= g.getIdGrupo()%>" class="btn btn-sm btn-icon-formal" title="Ver asignaciones" aria-label="Ver asignaciones"><i class="bi bi-person-lines-fill"></i></a>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <div class="mensaje-exito mt-3" data-filtro-vacio style="display:none;">Ningún registro coincide con los filtros seleccionados.</div>
            </div>
            <% } %>
        </div>
        <% } else { %>

        <div class="container">
            <a href="SAsignaciones" class="texto-info d-inline-block mb-2"><i class="bi bi-arrow-left me-1"></i>Todos los grupos</a>
            <header class="asignaciones-hero">
                <div>
                    <span class="asignaciones-eyebrow"><i class="bi bi-person-workspace"></i> Asignación académica</span>
                    <h1>Docentes del grupo <strong><%= grupo.getNombreGrupo()%></strong></h1>
                    <p class="asignaciones-carrera"><i class="bi bi-mortarboard-fill"></i><%= grupo.getNombreCarrera()%></p>
                </div>
                <span class="asignaciones-grupo-badge"><i class="bi bi-people-fill"></i> <%= grupo.getNombreGrupo()%></span>
            </header>
            <div class="asignaciones-contexto">
                <div><span>Carrera</span><strong><%= grupo.getNombreCarrera()%></strong></div>
                <div><span>Plan de estudios</span><strong><%= grupo.getNombrePlan()%></strong></div>
                <div><span>Cuatrimestre</span><strong><%= grupo.getNumeroCuatrimestre()%>°</strong></div>
                <div><span>Periodo</span><strong><%= grupo.getNombrePeriodo()%></strong></div>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (docentes.isEmpty()) { %>
            <div class="mensaje-exito"><a href="SDocentes">Da de alta al menos un docente</a> antes de asignar materias.</div>
            <% } %>

            <div class="barra-filtros" data-filtros-tabla="#tbodyAsignaciones">
                <div class="campo-filtro">
                    <label for="filtroAsignadoAsignaciones">Asignación</label>
                    <select id="filtroAsignadoAsignaciones" class="form-select form-select-sm" data-filtro-campo="asignado">
                        <option value="" selected>Todas</option>
                        <option value="Si">Con docente</option>
                        <option value="No">Sin asignar</option>
                    </select>
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Materia</th>
                            <th>Docente asignado</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="tbodyAsignaciones">
                        <% for (GrupoMateria gm : materiasDelGrupo) { %>
                        <tr data-fila-filtrable data-asignado="<%= gm.isTieneDocenteAsignado() ? "Si" : "No"%>">
                            <td><%= gm.getNombreMateria()%></td>
                            <td>
                                <% if (gm.isTieneDocenteAsignado()) { %>
                                <%= gm.getNombreDocente()%>
                                <% } else { %>
                                <span class="text-muted small">Sin asignar</span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <% if (gm.isTieneDocenteAsignado()) { %>
                                <form method="post" action="SAsignaciones" class="d-inline">
                                    <input type="hidden" name="accion" value="Desactivar">
                                    <input type="hidden" name="idGrupo" value="<%= grupo.getIdGrupo()%>">
                                    <input type="hidden" name="idGrupoMateria" value="<%= gm.getIdGrupoMateria()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal btn-icon-formal" title="Desactivar asignación" aria-label="Desactivar asignación"><i class="bi bi-person-dash"></i></button>
                                </form>
                                <% } else if (!docentes.isEmpty()) { %>
                                <form method="post" action="SAsignaciones" class="d-flex gap-2 justify-content-end">
                                    <input type="hidden" name="accion" value="Asignar">
                                    <input type="hidden" name="idGrupo" value="<%= grupo.getIdGrupo()%>">
                                    <input type="hidden" name="idGrupoMateria" value="<%= gm.getIdGrupoMateria()%>">
                                    <input type="hidden" name="selDocente" class="docente-seleccionado-id">
                                    <div class="docente-picker">
                                        <input type="search" class="form-control form-control-sm asignacion-docente-select" placeholder="Buscar docente..." autocomplete="off" required aria-label="Buscar docente para <%= gm.getNombreMateria()%>">
                                        <div class="docente-picker-options">
                                        <% for (Docente docente : docentes) { %>
                                        <button type="button" class="docente-picker-option" data-id-docente="<%= docente.getIdDocente()%>" data-nombre-docente="<%= docente.getNombreCompleto()%>"><%= docente.getNombreCompleto()%></button>
                                        <% } %>
                                        </div>
                                    </div>
                                    <button type="submit" class="btn btn-sm btn-primary-formal">Asignar</button>
                                </form>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <div class="mensaje-exito mt-3" data-filtro-vacio style="display:none;">Ningún registro coincide con el filtro seleccionado.</div>
            </div>
        </div>
        <% } %>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
            </main>
    </div>
</div>
    </body>
</html>
