<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Subdirector"%>
<%@page import="modelo.Carrera"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Subdirector> subdirectores = (ArrayList<Subdirector>) request.getAttribute("subdirectores");
    ArrayList<Carrera> carreras = (ArrayList<Carrera>) request.getAttribute("carreras");
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
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Correo</th>
                            <th>Carrera</th>
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Subdirector subdirector : subdirectores) { %>
                        <tr>
                            <td><%= subdirector.getNombreCompleto()%></td>
                            <td><%= subdirector.getCorreo()%></td>
                            <td><%= subdirector.getNombreCarrera()%></td>
                            <td>
                                <% if ("Activo".equals(subdirector.getEstatus())) { %>
                                <span class="badge text-bg-success">Activo</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary">Inactivo</span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <% if ("Activo".equals(subdirector.getEstatus())) { %>
                                <form method="post" action="SSubdirectores" class="d-inline"
                                      onsubmit="return confirm('¿Desactivar a este subdirector?');">
                                    <input type="hidden" name="accion" value="Desactivar">
                                    <input type="hidden" name="idSubdirector" value="<%= subdirector.getIdSubdirector()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal">Desactivar</button>
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

        <div class="modal fade modal-formal" id="modalSubdirector" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SSubdirectores">
                        <div class="modal-header">
                            <h5 class="modal-title">Nuevo subdirector</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="Agregar">
                            <div class="mb-3">
                                <label class="form-label">Nombres</label>
                                <input type="text" name="tfNombres" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Apellido paterno</label>
                                <input type="text" name="tfApellidoPaterno" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Apellido materno</label>
                                <input type="text" name="tfApellidoMaterno" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Correo institucional</label>
                                <input type="email" name="tfCorreo" class="form-control" required>
                                <div class="form-text">Con este correo el subdirector podrá crear su cuenta después.</div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Carrera</label>
                                <select name="selCarrera" class="form-select" required>
                                    <option value="">-- Carrera --</option>
                                    <% for (Carrera c : carreras) { %>
                                    <option value="<%= c.getIdCarrera()%>"><%= c.getNombreCarrera()%></option>
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
            </main>
    </div>
</div>
    </body>
</html>
