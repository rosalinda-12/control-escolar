<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Subdirector"%>
<%@page import="modelo.Carrera"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Subdirector> subdirectores = (ArrayList<Subdirector>) request.getAttribute("subdirectores");
    ArrayList<Carrera> carreras = (ArrayList<Carrera>) request.getAttribute("carreras");
    Subdirector subdirectorEditar = (Subdirector) request.getAttribute("subdirectorEditar");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Subdirectores</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <div class="d-flex justify-content-between align-items-center mt-4">
                <div>
                    <h2>Subdirectores</h2>
                    <p class="texto-info mb-0">
                        Aquí solo se registra a la persona y su correo. Ella deberá crear su propia cuenta en
                        la pantalla de registro con ese mismo correo, verificarlo y esperar tu aprobación para
                        poder iniciar sesión.
                    </p>
                </div>
                <button class="btn btn-primary-formal" data-bs-toggle="modal" data-bs-target="#modalSubdirector">
                    <i class="bi bi-plus-lg me-1"></i>Nuevo subdirector
                </button>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (subdirectores.isEmpty()) { %>
            <div class="mensaje-exito mt-4">No hay subdirectores registrados todavía.</div>
            <% } else { %>
            <div class="barra-filtros" data-filtros-tabla="#tbodySubdirectores">
                <div class="campo-filtro">
                    <label for="filtroEstatusSubdirectores">Estatus</label>
                    <select id="filtroEstatusSubdirectores" class="form-select form-select-sm" data-filtro-campo="estatus">
                        <option value="Activo" selected>Activos (actuales)</option>
                        <option value="">Todos</option>
                        <option value="Inactivo">Inactivos</option>
                    </select>
                </div>
                <div class="campo-filtro campo-filtro-texto">
                    <label for="filtroTextoSubdirectores">Buscar</label>
                    <input type="text" id="filtroTextoSubdirectores" class="form-control form-control-sm" data-filtro-texto placeholder="Nombre, correo o carrera...">
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Correo</th>
                            <th>Carreras</th>
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="tbodySubdirectores">
                        <% for (Subdirector subdirector : subdirectores) { %>
                        <tr data-fila-filtrable data-estatus="<%= "Activo".equals(subdirector.getEstatus()) ? "Activo" : "Inactivo"%>">
                            <td><%= subdirector.getNombreCompleto()%></td>
                            <td><%= subdirector.getCorreo()%></td>
                            <td><%= subdirector.getNombresCarrera().isEmpty() ? subdirector.getNombreCarrera() : String.join(", ", subdirector.getNombresCarrera())%></td>
                            <td>
                                <% if ("Activo".equals(subdirector.getEstatus())) { %>
                                <span class="badge text-bg-success">Activo</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary">Inactivo</span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <a href="SSubdirectores?editar=<%= subdirector.getIdSubdirector()%>" class="btn btn-sm btn-icon-formal" title="Editar subdirector" aria-label="Editar subdirector"><i class="bi bi-pencil-square"></i></a>
                                <% if ("Activo".equals(subdirector.getEstatus())) { %>
                                <form method="post" action="SSubdirectores" class="d-inline"
                                      onsubmit="return confirm('¿Desactivar a este subdirector?');">
                                    <input type="hidden" name="accion" value="Desactivar">
                                    <input type="hidden" name="idSubdirector" value="<%= subdirector.getIdSubdirector()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal btn-icon-formal" title="Desactivar subdirector" aria-label="Desactivar subdirector"><i class="bi bi-person-dash"></i></button>
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

        <div class="modal fade modal-formal" id="modalSubdirector" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SSubdirectores">
                        <div class="modal-header">
                            <h5 class="modal-title"><%= subdirectorEditar == null ? "Nuevo subdirector" : "Editar subdirector"%></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="<%= subdirectorEditar == null ? "Agregar" : "Modificar"%>">
                            <% if (subdirectorEditar != null) { %>
                            <input type="hidden" name="idSubdirector" value="<%= subdirectorEditar.getIdSubdirector()%>">
                            <input type="hidden" name="idPersona" value="<%= subdirectorEditar.getIdPersona()%>">
                            <% } %>
                            <div class="mb-3">
                                <label class="form-label">Nombres</label>
                                <input type="text" name="tfNombres" class="form-control" value="<%= subdirectorEditar == null ? "" : subdirectorEditar.getNombres()%>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Apellido paterno</label>
                                <input type="text" name="tfApellidoPaterno" class="form-control" value="<%= subdirectorEditar == null ? "" : subdirectorEditar.getApellidoPaterno()%>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Apellido materno</label>
                                <input type="text" name="tfApellidoMaterno" class="form-control" value="<%= subdirectorEditar == null ? "" : subdirectorEditar.getApellidoMaterno()%>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Correo institucional</label>
                                <input type="email" name="tfCorreo" class="form-control" value="<%= subdirectorEditar == null ? "" : subdirectorEditar.getCorreo()%>" required>
                                <div class="form-text">Con este correo el subdirector podrá crear su cuenta después.</div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Carreras a cargo</label>
                                <select name="selCarrera" class="form-select" multiple size="5" required>
                                    <% for (Carrera c : carreras) { %>
                                    <option value="<%= c.getIdCarrera()%>" <%= subdirectorEditar != null && subdirectorEditar.getIdsCarrera().contains(c.getIdCarrera()) ? "selected" : ""%>><%= c.getNombreCarrera()%></option>
                                    <% } %>
                                </select>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary-formal"><%= subdirectorEditar == null ? "Guardar" : "Actualizar"%></button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
        <% if (subdirectorEditar != null) { %>
        <script>new bootstrap.Modal(document.getElementById("modalSubdirector")).show();</script>
        <% } %>
            </main>
    </div>
</div>
    </body>
</html>
