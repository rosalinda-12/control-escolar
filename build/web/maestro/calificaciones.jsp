<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.DocenteAsignacion"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<DocenteAsignacion> asignaciones = (ArrayList<DocenteAsignacion>) request.getAttribute("asignaciones");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Mis grupos y calificaciones</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_maestro.jsp" %>

        <div class="container">
            <h2 class="mt-4">Mis grupos y calificaciones</h2>
            <p class="texto-info">Estas son las materias de grupo que tienes asignadas. Entra a una para capturar las
                calificaciones del parcial activo.</p>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (asignaciones.isEmpty()) { %>
            <div class="mensaje-exito mt-4">Todavía no tienes materias de grupo asignadas. Pide al Administrador que te asigne alguna.</div>
            <% } else { %>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Materia</th>
                            <th>Grupo</th>
                            <th>Carrera</th>
                            <th>Periodo</th>
                            <th>Parcial activo</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (DocenteAsignacion asignacion : asignaciones) { %>
                        <tr>
                            <td><%= asignacion.getNombreMateria()%></td>
                            <td><%= asignacion.getNombreGrupo()%>
                                <% if ("Cerrado".equals(asignacion.getEstatusGrupo())) { %>
                                <span class="badge text-bg-secondary ms-1">Cerrado</span>
                                <% } %>
                            </td>
                            <td><%= asignacion.getNombreCarrera()%></td>
                            <td><%= asignacion.getNombrePeriodo()%></td>
                            <td>
                                <% if (asignacion.getParcialActivo() == null) { %>
                                <span class="badge text-bg-secondary">Sin configurar</span>
                                <% } else { %>
                                <span class="badge text-bg-primary">Parcial <%= asignacion.getParcialActivo()%></span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <a href="SCalificaciones?idGrupoMateria=<%= asignacion.getIdGrupoMateria()%>" class="btn btn-sm btn-primary-formal">
                                    <i class="bi bi-journal-check me-1"></i>Capturar
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
