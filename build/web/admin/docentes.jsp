<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Docente"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Docente> docentes = (ArrayList<Docente>) request.getAttribute("docentes");
    Docente docenteEditar = (Docente) request.getAttribute("docenteEditar");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Docentes</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <div class="d-flex justify-content-between align-items-center mt-4">
                <h2>Docentes</h2>
                <button class="btn btn-primary-formal" data-bs-toggle="modal" data-bs-target="#modalDocente">
                    <i class="bi bi-plus-lg me-1"></i>Nuevo docente
                </button>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (docentes.isEmpty()) { %>
            <div class="mensaje-exito mt-4">No hay docentes registrados todavía.</div>
            <% } else { %>
            <div class="barra-filtros" data-filtros-tabla="#tbodyDocentes">
                <div class="campo-filtro">
                    <label for="filtroEstatusDocentes">Estatus</label>
                    <select id="filtroEstatusDocentes" class="form-select form-select-sm" data-filtro-campo="estatus">
                        <option value="Activo" selected>Activos (actuales)</option>
                        <option value="">Todos</option>
                        <option value="Inactivo">Inactivos</option>
                    </select>
                </div>
                <div class="campo-filtro campo-filtro-texto">
                    <label for="filtroTextoDocentes">Buscar</label>
                    <input type="text" id="filtroTextoDocentes" class="form-control form-control-sm" data-filtro-texto placeholder="Nombre o correo...">
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Correo</th>
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="tbodyDocentes">
                        <% for (Docente docente : docentes) { %>
                        <tr data-fila-filtrable data-estatus="<%= "Activo".equals(docente.getEstatus()) ? "Activo" : "Inactivo"%>">
                            <td><%= docente.getNombreCompleto()%></td>
                            <td><%= docente.getCorreo()%></td>
                            <td>
                                <% if ("Activo".equals(docente.getEstatus())) { %>
                                <span class="badge text-bg-success">Activo</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary">Inactivo</span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <a href="SDocentes?editar=<%= docente.getIdDocente()%>" class="btn btn-sm btn-icon-formal" title="Editar docente" aria-label="Editar docente"><i class="bi bi-pencil-square"></i></a>
                                <% if ("Activo".equals(docente.getEstatus())) { %>
                                <form method="post" action="SDocentes" class="d-inline"
                                      onsubmit="return confirm('¿Desactivar a este docente? Ya no podrá recibir nuevas asignaciones.');">
                                    <input type="hidden" name="accion" value="Desactivar">
                                    <input type="hidden" name="idDocente" value="<%= docente.getIdDocente()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal btn-icon-formal" title="Desactivar docente" aria-label="Desactivar docente"><i class="bi bi-person-dash"></i></button>
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

        <div class="modal fade modal-formal" id="modalDocente" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SDocentes">
                        <div class="modal-header">
                            <h5 class="modal-title"><%= docenteEditar == null ? "Nuevo docente" : "Editar docente"%></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="<%= docenteEditar == null ? "Agregar" : "Modificar"%>">
                            <% if (docenteEditar != null) { %>
                            <input type="hidden" name="idDocente" value="<%= docenteEditar.getIdDocente()%>">
                            <input type="hidden" name="idPersona" value="<%= docenteEditar.getIdPersona()%>">
                            <% } %>
                            <div class="mb-3">
                                <label class="form-label">Nombres</label>
                                <input type="text" name="tfNombres" class="form-control" value="<%= docenteEditar == null ? "" : docenteEditar.getNombres()%>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Apellido paterno</label>
                                <input type="text" name="tfApellidoPaterno" class="form-control" value="<%= docenteEditar == null ? "" : docenteEditar.getApellidoPaterno()%>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Apellido materno</label>
                                <input type="text" name="tfApellidoMaterno" class="form-control" value="<%= docenteEditar == null ? "" : docenteEditar.getApellidoMaterno()%>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Correo institucional</label>
                                <input type="email" name="tfCorreo" class="form-control" value="<%= docenteEditar == null ? "" : docenteEditar.getCorreo()%>" required>
                                <div class="form-text">Con este correo el docente podrá crear su cuenta después.</div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary-formal"><%= docenteEditar == null ? "Guardar" : "Actualizar"%></button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
        <% if (docenteEditar != null) { %>
        <script>new bootstrap.Modal(document.getElementById("modalDocente")).show();</script>
        <% } %>
            </main>
    </div>
</div>
    </body>
</html>
