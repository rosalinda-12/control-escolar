<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Grupo"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Grupo> grupos = (ArrayList<Grupo>) request.getAttribute("grupos");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Grupos de mi carrera</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_subdirector.jsp" %>

        <div class="container">
            <div class="mt-4">
                <h2>Grupos de mi carrera</h2>
                <p class="texto-info mb-0">Elige un grupo para asignarle materias a tus docentes.</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error mt-3"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } else if (grupos.isEmpty()) { %>
            <div class="mensaje-exito mt-3">
                <i class="bi bi-info-circle me-1"></i>Todavía no hay grupos registrados para tu carrera.
            </div>
            <% } else { %>
            <div class="tabla-formal-wrap mt-3">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Grupo</th>
                            <th>Plan</th>
                            <th>Cuatrimestre</th>
                            <th>Periodo</th>
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Grupo g : grupos) { %>
                        <tr>
                            <td><%= g.getNombreGrupo()%></td>
                            <td><%= g.getNombrePlan()%></td>
                            <td><%= g.getNumeroCuatrimestre()%>°</td>
                            <td><%= g.getNombrePeriodo()%></td>
                            <td>
                                <% if ("Activo".equals(g.getEstatus())) { %>
                                <span class="badge text-bg-success">Activo</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary"><%= g.getEstatus()%></span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <a href="SAsignaciones?idGrupo=<%= g.getIdGrupo()%>" class="btn btn-sm btn-outline-formal">
                                    Asignar docentes
                                </a>
                            </td>
                        </tr>
                        <% } %>
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
