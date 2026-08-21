<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Grupo"%>
<%@page import="modelo.GrupoMateria"%>
<%@page import="modelo.Docente"%>
<%@page import="java.util.ArrayList"%>
<%
    Grupo grupo = (Grupo) request.getAttribute("grupo");
    ArrayList<GrupoMateria> materiasDelGrupo = (ArrayList<GrupoMateria>) request.getAttribute("materiasDelGrupo");
    ArrayList<Docente> docentes = (ArrayList<Docente>) request.getAttribute("docentes");
    boolean puedeGestionar = Boolean.TRUE.equals(request.getAttribute("puedeGestionar"));
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
        <%@ include file="menu_subdirector.jsp" %>

        <div class="container">
            <header class="asignaciones-hero asignaciones-hero-subdirector">
                <div>
                    <span class="asignaciones-eyebrow"><i class="bi bi-person-workspace"></i> Asignación académica</span>
                    <h1>Docentes del grupo <strong><%= grupo.getNombreGrupo()%></strong></h1>
                    <p class="asignaciones-carrera"><i class="bi bi-mortarboard-fill"></i><%= grupo.getNombreCarrera()%></p>
                </div>
                <span class="asignaciones-grupo-badge"><i class="bi bi-people-fill"></i> <%= grupo.getNombreGrupo()%></span>
            </header>
            <div class="asignaciones-contexto">
                <div><span>Carrera a cargo</span><strong><%= grupo.getNombreCarrera()%></strong></div>
                <div><span>Plan de estudios</span><strong><%= grupo.getNombrePlan()%></strong></div>
                <div><span>Cuatrimestre</span><strong><%= grupo.getNumeroCuatrimestre()%>°</strong></div>
                <div><span>Periodo</span><strong><%= grupo.getNombrePeriodo()%></strong></div>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (!puedeGestionar) { %>
            <div class="mensaje-exito"><i class="bi bi-info-circle me-1"></i>Consulta de asignaciones. Tu cuenta no tiene permiso para modificarlas.</div>
            <% } %>

            <div class="barra-filtros" data-filtros-tabla="#tbodyAsignacionesSubdirector">
                <div class="campo-filtro">
                    <label for="filtroAsignacionSubdirector">Asignación</label>
                    <select id="filtroAsignacionSubdirector" class="form-select form-select-sm" data-filtro-campo="asignacion">
                        <option value="" selected>Todas</option>
                        <option value="Asignado">Con docente</option>
                        <option value="Sin asignar">Sin docente</option>
                    </select>
                </div>
                <div class="campo-filtro campo-filtro-texto">
                    <label for="filtroTextoAsignacionesSubdirector">Buscar</label>
                    <input type="text" id="filtroTextoAsignacionesSubdirector" class="form-control form-control-sm" data-filtro-texto placeholder="Materia o docente...">
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Materia</th>
                            <th>Docente asignado</th>
                            <% if (puedeGestionar) { %><th></th><% } %>
                        </tr>
                    </thead>
                    <tbody id="tbodyAsignacionesSubdirector">
                        <% for (GrupoMateria gm : materiasDelGrupo) { %>
                        <tr data-fila-filtrable data-asignacion="<%= gm.isTieneDocenteAsignado() ? "Asignado" : "Sin asignar"%>">
                            <td><%= gm.getNombreMateria()%></td>
                            <td>
                                <% if (gm.isTieneDocenteAsignado()) { %>
                                <%= gm.getNombreDocente()%>
                                <% } else { %>
                                <span class="text-muted small">Sin asignar</span>
                                <% } %>
                            </td>
                            <% if (puedeGestionar) { %>
                            <td class="text-end">
                                <% if (gm.isTieneDocenteAsignado()) { %>
                                <% if (new servicio.ServicioAutorizacion().tienePermiso((modelo.Usuario) session.getAttribute("usuario"), "asignaciones.editar")) { %>
                                <form method="post" action="SAsignaciones" class="d-inline">
                                    <input type="hidden" name="accion" value="Desactivar">
                                    <input type="hidden" name="idGrupo" value="<%= grupo.getIdGrupo()%>">
                                    <input type="hidden" name="idGrupoMateria" value="<%= gm.getIdGrupoMateria()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal btn-icon-formal" title="Desactivar asignación" aria-label="Desactivar asignación"><i class="bi bi-person-dash"></i></button>
                                </form>
                                <% } %>
                                <% } else if (!docentes.isEmpty()) { %>
                                <form method="post" action="SAsignaciones" class="d-flex gap-2 justify-content-end">
                                    <input type="hidden" name="accion" value="Asignar">
                                    <input type="hidden" name="idGrupo" value="<%= grupo.getIdGrupo()%>">
                                    <input type="hidden" name="idGrupoMateria" value="<%= gm.getIdGrupoMateria()%>">
                                    <input type="hidden" name="selDocente" class="docente-seleccionado-id">
                                    <div class="docente-picker">
                                        <input type="search" class="form-control form-control-sm asignacion-docente-select" placeholder="Buscar docente..." autocomplete="off" required aria-label="Buscar docente para <%= gm.getNombreMateria()%>">
                                        <div class="docente-picker-options">
                                            <% for (Docente docente : docentes) { %><button type="button" class="docente-picker-option" data-id-docente="<%= docente.getIdDocente()%>" data-nombre-docente="<%= docente.getNombreCompleto()%>"><%= docente.getNombreCompleto()%></button><% } %>
                                        </div>
                                    </div>
                                    <button type="submit" class="btn btn-sm btn-primary-formal">Asignar</button>
                                </form>
                                <% } %>
                            </td>
                            <% } %>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <div class="mensaje-exito mt-3" data-filtro-vacio style="display:none;">Ninguna asignación coincide con los filtros seleccionados.</div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
            </main>
    </div>
</div>
    </body>
</html>
