<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Rol"%>
<%@page import="modelo.Permiso"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.LinkedHashMap"%>
<%
    ArrayList<Rol> roles = (ArrayList<Rol>) request.getAttribute("roles");
    Rol rolSeleccionado = (Rol) request.getAttribute("rolSeleccionado");
    ArrayList<Permiso> permisos = (ArrayList<Permiso>) request.getAttribute("permisos");

    LinkedHashMap<String, ArrayList<Permiso>> porModulo = new LinkedHashMap<>();
    for (Permiso p : permisos)
    {
        porModulo.computeIfAbsent(p.getModulo(), k -> new ArrayList<>()).add(p);
    }
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Permisos</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <div class="mt-4">
                <h2>Permisos por rol</h2>
                <p class="texto-info mb-0">Activa o desactiva cada permiso para el rol elegido. El Administrador siempre tiene acceso total y no aparece aquí.</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <ul class="nav nav-tabs mt-3">
                <% for (Rol r : roles) { if (r.isEsAdministradorPrincipal()) continue; %>
                <li class="nav-item">
                    <a class="nav-link <%= rolSeleccionado != null && rolSeleccionado.getIdRol() == r.getIdRol() ? "active" : ""%>"
                       href="SPermisos?idRol=<%= r.getIdRol()%>"><%= r.getNombreRol()%></a>
                </li>
                <% } %>
            </ul>

            <% if (rolSeleccionado != null) { %>
            <div class="tabla-formal-wrap mt-3">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Módulo</th>
                            <th>Permiso</th>
                            <th>Descripción</th>
                            <th class="text-center">Activo</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (String modulo : porModulo.keySet()) {
                            ArrayList<Permiso> delModulo = porModulo.get(modulo);
                            for (int i = 0; i < delModulo.size(); i++) {
                                Permiso p = delModulo.get(i); %>
                        <tr>
                            <% if (i == 0) { %>
                            <td rowspan="<%= delModulo.size()%>" class="text-capitalize fw-semibold"><%= modulo%></td>
                            <% } %>
                            <td><code><%= p.getClave()%></code></td>
                            <td><%= p.getDescripcion() != null ? p.getDescripcion() : ""%></td>
                            <td class="text-center">
                                <form method="post" action="SPermisos">
                                    <input type="hidden" name="idRol" value="<%= rolSeleccionado.getIdRol()%>">
                                    <input type="hidden" name="idPermiso" value="<%= p.getIdPermiso()%>">
                                    <input type="hidden" name="activo" value="<%= p.isActivo() ? "0" : "1"%>">
                                    <button type="submit" class="btn btn-sm <%= p.isActivo() ? "btn-primary-formal" : "btn-outline-formal"%>">
                                        <%= p.isActivo() ? "Sí" : "No"%>
                                    </button>
                                </form>
                            </td>
                        </tr>
                        <% } } %>
                    </tbody>
                </table>
            </div>
            <% } %>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
            </main>
    </div>
</div>
    </body>
</html>
