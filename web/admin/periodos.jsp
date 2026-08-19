<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Periodo"%>
<%@page import="modelo.CicloEscolar"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Periodo> periodos = (ArrayList<Periodo>) request.getAttribute("periodos");
    ArrayList<CicloEscolar> ciclos = (ArrayList<CicloEscolar>) request.getAttribute("ciclos");
    Periodo periodoEditar = (Periodo) request.getAttribute("periodoEditar");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Periodos</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <div class="d-flex justify-content-between align-items-center mt-4">
                <h2>Periodos</h2>
                <% if (ciclos.isEmpty()) { %>
                <a href="SCiclos" class="btn btn-primary-formal"><i class="bi bi-plus-lg me-1"></i>Crea un ciclo escolar primero</a>
                <% } else { %>
                <button class="btn btn-primary-formal" data-bs-toggle="modal" data-bs-target="#modalPeriodo">
                    <i class="bi bi-plus-lg me-1"></i>Nuevo periodo
                </button>
                <% } %>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (ciclos.isEmpty()) { %>
            <div class="mensaje-exito mt-4">
                <i class="bi bi-info-circle me-1"></i>
                Antes de crear un periodo necesitas al menos un <a href="SCiclos">ciclo escolar</a>.
            </div>
            <% } else if (periodos.isEmpty()) { %>
            <div class="mensaje-exito mt-4">No hay periodos registrados todavía.</div>
            <% } else { %>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Ciclo</th>
                            <th>Nombre</th>
                            <th>Apertura</th>
                            <th>Cierre</th>
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Periodo periodo : periodos) { %>
                        <tr>
                            <td><%= periodo.getNombreCiclo()%></td>
                            <td><%= periodo.getNombrePeriodo()%></td>
                            <td><%= periodo.getFechaInicio()%></td>
                            <td><%= periodo.getFechaFin()%></td>
                            <td>
                                <% if ("Activo".equals(periodo.getEstatus())) { %>
                                <span class="badge text-bg-success">Activo</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary">Cerrado</span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <a href="SPeriodos?editar=<%= periodo.getIdPeriodo()%>" class="btn btn-sm btn-outline-formal">Editar</a>
                                <form method="post" action="SPeriodos" class="d-inline">
                                    <input type="hidden" name="accion" value="Eliminar">
                                    <input type="hidden" name="idPeriodo" value="<%= periodo.getIdPeriodo()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal">Eliminar</button>
                                </form>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% } %>
        </div>

        <div class="modal fade modal-formal" id="modalPeriodo" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SPeriodos">
                        <div class="modal-header">
                            <h5 class="modal-title"><%= periodoEditar != null ? "Editar periodo" : "Nuevo periodo"%></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="<%= periodoEditar != null ? "Modificar" : "Agregar"%>">
                            <% if (periodoEditar != null) { %>
                            <input type="hidden" name="idPeriodo" value="<%= periodoEditar.getIdPeriodo()%>">
                            <% } %>
                            <div class="mb-3">
                                <label class="form-label">Ciclo escolar</label>
                                <select name="selCiclo" class="form-select" required>
                                    <% for (CicloEscolar ciclo : ciclos) { %>
                                    <option value="<%= ciclo.getIdCiclo()%>"
                                            <%= (periodoEditar != null && periodoEditar.getIdCiclo() == ciclo.getIdCiclo()) ? "selected" : ""%>>
                                        <%= ciclo.getNombreCiclo()%>
                                    </option>
                                    <% } %>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Nombre</label>
                                <input type="text" name="tfNombrePeriodo" class="form-control" placeholder="Mayo-Agosto 2026"
                                       value="<%= periodoEditar != null ? periodoEditar.getNombrePeriodo() : ""%>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Fecha de apertura</label>
                                <input type="date" name="tfFechaInicio" class="form-control"
                                       value="<%= periodoEditar != null ? periodoEditar.getFechaInicio() : ""%>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Fecha de cierre</label>
                                <input type="date" name="tfFechaFin" class="form-control"
                                       value="<%= periodoEditar != null ? periodoEditar.getFechaFin() : ""%>" required>
                            </div>
                            <% if (periodoEditar != null) { %>
                            <div class="mb-3">
                                <label class="form-label">Estatus</label>
                                <select name="selEstatus" class="form-select">
                                    <option value="Activo" <%= "Activo".equals(periodoEditar.getEstatus()) ? "selected" : ""%>>Activo</option>
                                    <option value="Cerrado" <%= "Cerrado".equals(periodoEditar.getEstatus()) ? "selected" : ""%>>Cerrado</option>
                                </select>
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

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
        <% if (periodoEditar != null) { %>
        <script>
            new bootstrap.Modal(document.getElementById("modalPeriodo")).show();
        </script>
        <% } %>
            </main>
    </div>
</div>
    </body>
</html>
