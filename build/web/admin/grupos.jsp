<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
                    <tbody>
                        <% for (Grupo grupo : grupos) { %>
                        <tr>
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
                                <a href="SAsignaciones?idGrupo=<%= grupo.getIdGrupo()%>" class="btn btn-sm btn-outline-formal">Docentes</a>
                                <% if ("Activo".equals(grupo.getEstatus())) { %>
                                <form method="post" action="SGrupos" class="d-inline">
                                    <input type="hidden" name="accion" value="Cerrar">
                                    <input type="hidden" name="idGrupo" value="<%= grupo.getIdGrupo()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal">Cerrar</button>
                                </form>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% } %>
        </div>

        <div class="modal fade modal-formal" id="modalGrupo" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SGrupos">
                        <div class="modal-header">
                            <h5 class="modal-title">Nuevo grupo</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="Agregar">
                            <div class="mb-3">
                                <label class="form-label">Carrera / Plan / Cuatrimestre</label>
                                <select name="selCuatrimestre" class="form-select" required>
                                    <% for (PlanCuatrimestre cuatrimestre : cuatrimestresDisponibles) { %>
                                    <option value="<%= cuatrimestre.getIdPlanCuatrimestre()%>"><%= cuatrimestre.getEtiqueta()%></option>
                                    <% } %>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Nombre del grupo</label>
                                <input type="text" name="tfNombreGrupo" class="form-control" placeholder="TI-1A" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Generación</label>
                                <select name="selGeneracion" class="form-select" required>
                                    <% for (Generacion generacion : generaciones) { %>
                                    <option value="<%= generacion.getIdGeneracion()%>"><%= generacion.getNombreGeneracion()%></option>
                                    <% } %>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Periodo</label>
                                <select name="selPeriodo" class="form-select" required>
                                    <% for (Periodo periodo : periodos) { %>
                                    <option value="<%= periodo.getIdPeriodo()%>"><%= periodo.getNombrePeriodo()%></option>
                                    <% } %>
                                </select>
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
    </body>
</html>
