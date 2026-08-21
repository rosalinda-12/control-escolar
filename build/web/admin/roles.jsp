<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Rol"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Rol> roles = (ArrayList<Rol>) request.getAttribute("roles");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Roles</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <div class="d-flex justify-content-between align-items-center mt-4">
                <div>
                    <h2>Roles</h2>
                    <p class="texto-info mb-0">Un rol nuevo empieza sin ningún permiso activo; actívalos desde "Permisos".</p>
                </div>
                <button class="btn btn-primary-formal" data-bs-toggle="modal" data-bs-target="#modalRol">
                    <i class="bi bi-plus-lg me-1"></i>Nuevo rol
                </button>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <div class="barra-filtros" data-filtros-tabla="#tbodyRoles">
                <div class="campo-filtro campo-filtro-texto">
                    <label for="filtroTextoRoles">Buscar</label>
                    <input type="text" id="filtroTextoRoles" class="form-control form-control-sm" data-filtro-texto placeholder="Nombre o descripción...">
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>
            <div class="tabla-formal-wrap mt-3">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Descripción</th>
                            <th>Acceso</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="tbodyRoles">
                        <% for (Rol r : roles) { %>
                        <tr data-fila-filtrable>
                            <td><%= r.getNombreRol()%></td>
                            <td><%= r.getDescripcion() != null ? r.getDescripcion() : ""%></td>
                            <td>
                                <% if (r.isEsAdministradorPrincipal()) { %>
                                <span class="badge text-bg-dark">Total (fijo)</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary">Según permisos</span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <% if (!r.isEsAdministradorPrincipal()) { %>
                                <a href="SPermisos?idRol=<%= r.getIdRol()%>" class="btn btn-sm btn-outline-formal">Editar permisos</a>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <div class="mensaje-exito mt-3" data-filtro-vacio style="display:none;">Ningún registro coincide con la búsqueda.</div>
            </div>
        </div>

        <div class="modal fade modal-formal" id="modalRol" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SRoles">
                        <div class="modal-header">
                            <h5 class="modal-title">Nuevo rol</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="mb-3">
                                <label class="form-label">Nombre del rol</label>
                                <input type="text" name="tfNombreRol" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Descripción</label>
                                <input type="text" name="tfDescripcion" class="form-control">
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary-formal">Crear rol</button>
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
