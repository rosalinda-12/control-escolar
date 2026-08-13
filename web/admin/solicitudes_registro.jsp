<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="modelo.Usuario"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Usuario> solicitudes = (ArrayList<Usuario>) request.getAttribute("solicitudes");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Solicitudes de registro</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <h2 class="mt-4">Solicitudes de registro pendientes</h2>
            <p class="texto-info">Estas cuentas ya verificaron su correo, pero necesitan tu aprobación antes de poder iniciar sesión.</p>

            <div class="tabla-formal-wrap">
                <% if (solicitudes.isEmpty()) { %>
                <p class="texto-info mb-0"><i class="bi bi-check2-circle me-1"></i>No hay solicitudes pendientes.</p>
                <% } else { %>
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Correo</th>
                            <th>Rol</th>
                            <th>Correo verificado</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Usuario solicitante : solicitudes) { %>
                        <tr>
                            <td><%= solicitante.getNombres()%> <%= solicitante.getApellidoPaterno()%></td>
                            <td><%= solicitante.getCorreo()%></td>
                            <td><%= solicitante.getNombreRol()%></td>
                            <td>
                                <span class="badge-estatus <%= solicitante.isCorreoVerificado() ? "badge-aprobado" : "badge-pendiente"%>">
                                    <%= solicitante.isCorreoVerificado() ? "Sí" : "Todavía no"%>
                                </span>
                            </td>
                            <td class="text-end">
                                <form method="post" action="SSolicitudesRegistro" class="d-inline">
                                    <input type="hidden" name="idUsuario" value="<%= solicitante.getIdUsuario()%>">
                                    <input type="hidden" name="accion" value="Aprobar">
                                    <button type="submit" class="btn btn-sm btn-primary-formal">Aprobar</button>
                                </form>
                                <form method="post" action="SSolicitudesRegistro" class="d-inline">
                                    <input type="hidden" name="idUsuario" value="<%= solicitante.getIdUsuario()%>">
                                    <input type="hidden" name="accion" value="Rechazar">
                                    <button type="submit" class="btn btn-sm btn-danger-formal">Rechazar</button>
                                </form>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <% } %>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
    </body>
</html>
