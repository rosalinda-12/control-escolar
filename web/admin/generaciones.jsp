<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="modelo.Generacion"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Generacion> generaciones = (ArrayList<Generacion>) request.getAttribute("generaciones");
    Generacion generacionEditar = (Generacion) request.getAttribute("generacionEditar");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Generaciones</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <div class="d-flex justify-content-between align-items-center mt-4">
                <h2>Generaciones</h2>
                <button class="btn btn-primary-formal" data-bs-toggle="modal" data-bs-target="#modalGeneracion">
                    <i class="bi bi-plus-lg me-1"></i>Nueva generación
                </button>
            </div>

            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Generacion generacion : generaciones) { %>
                        <tr>
                            <td><%= generacion.getNombreGeneracion()%></td>
                            <td>
                                <span class="badge-estatus <%= "Activa".equals(generacion.getEstatus()) ? "badge-aprobado" : "badge-pendiente"%>">
                                    <%= generacion.getEstatus()%>
                                </span>
                            </td>
                            <td class="text-end">
                                <a href="SGeneraciones?editar=<%= generacion.getIdGeneracion()%>" class="btn btn-sm btn-outline-formal">Editar</a>
                                <form method="post" action="SGeneraciones" class="d-inline">
                                    <input type="hidden" name="accion" value="Eliminar">
                                    <input type="hidden" name="idGeneracion" value="<%= generacion.getIdGeneracion()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal">Eliminar</button>
                                </form>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="modal fade modal-formal" id="modalGeneracion" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SGeneraciones">
                        <div class="modal-header">
                            <h5 class="modal-title"><%= generacionEditar != null ? "Editar generación" : "Nueva generación"%></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="<%= generacionEditar != null ? "Modificar" : "Agregar"%>">
                            <% if (generacionEditar != null) { %>
                            <input type="hidden" name="idGeneracion" value="<%= generacionEditar.getIdGeneracion()%>">
                            <% } %>
                            <div class="mb-3">
                                <label class="form-label">Nombre</label>
                                <input type="text" name="tfNombreGeneracion" class="form-control" placeholder="Generación 2024"
                                       value="<%= generacionEditar != null ? generacionEditar.getNombreGeneracion() : ""%>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Estatus</label>
                                <select name="tfEstatus" class="form-select">
                                    <option value="Activa" <%= (generacionEditar == null || "Activa".equals(generacionEditar.getEstatus())) ? "selected" : ""%>>Activa</option>
                                    <option value="Egresada" <%= (generacionEditar != null && "Egresada".equals(generacionEditar.getEstatus())) ? "selected" : ""%>>Egresada</option>
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
        <% if (generacionEditar != null) { %>
        <script>
            new bootstrap.Modal(document.getElementById("modalGeneracion")).show();
        </script>
        <% } %>
    </body>
</html>
