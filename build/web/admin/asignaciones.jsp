<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="modelo.Grupo"%>
<%@page import="modelo.GrupoMateria"%>
<%@page import="modelo.Docente"%>
<%@page import="java.util.ArrayList"%>
<%
    Grupo grupo = (Grupo) request.getAttribute("grupo");
    ArrayList<GrupoMateria> materiasDelGrupo = (ArrayList<GrupoMateria>) request.getAttribute("materiasDelGrupo");
    ArrayList<Docente> docentes = (ArrayList<Docente>) request.getAttribute("docentes");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Asignación de docentes</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <h2 class="mt-4">Docentes del grupo <%= grupo.getNombreGrupo()%></h2>
            <p class="texto-info"><%= grupo.getNombreCarrera()%> — <%= grupo.getNombrePlan()%> — Cuatrimestre <%= grupo.getNumeroCuatrimestre()%> — <%= grupo.getNombrePeriodo()%></p>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (docentes.isEmpty()) { %>
            <div class="mensaje-exito"><a href="SDocentes">Da de alta al menos un docente</a> antes de asignar materias.</div>
            <% } %>

            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Materia</th>
                            <th>Docente asignado</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (GrupoMateria gm : materiasDelGrupo) { %>
                        <tr>
                            <td><%= gm.getNombreMateria()%></td>
                            <td>
                                <% if (gm.isTieneDocenteAsignado()) { %>
                                <%= gm.getNombreDocente()%>
                                <% } else { %>
                                <span class="text-muted small">Sin asignar</span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <% if (gm.isTieneDocenteAsignado()) { %>
                                <form method="post" action="SAsignaciones" class="d-inline">
                                    <input type="hidden" name="accion" value="Quitar">
                                    <input type="hidden" name="idGrupo" value="<%= grupo.getIdGrupo()%>">
                                    <input type="hidden" name="idGrupoMateria" value="<%= gm.getIdGrupoMateria()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal">Quitar</button>
                                </form>
                                <% } else if (!docentes.isEmpty()) { %>
                                <form method="post" action="SAsignaciones" class="d-flex gap-2 justify-content-end">
                                    <input type="hidden" name="accion" value="Asignar">
                                    <input type="hidden" name="idGrupo" value="<%= grupo.getIdGrupo()%>">
                                    <input type="hidden" name="idGrupoMateria" value="<%= gm.getIdGrupoMateria()%>">
                                    <select name="selDocente" class="form-select form-select-sm" style="max-width: 220px;" required>
                                        <% for (Docente docente : docentes) { %>
                                        <option value="<%= docente.getIdDocente()%>"><%= docente.getNombreCompleto()%></option>
                                        <% } %>
                                    </select>
                                    <button type="submit" class="btn btn-sm btn-primary-formal">Asignar</button>
                                </form>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
    </body>
</html>
