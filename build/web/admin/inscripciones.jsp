<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="modelo.Inscripcion"%>
<%@page import="modelo.Grupo"%>
<%@page import="modelo.TrayectoriaAcademica"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Inscripcion> inscripciones = (ArrayList<Inscripcion>) request.getAttribute("inscripciones");
    ArrayList<Grupo> grupos = (ArrayList<Grupo>) request.getAttribute("grupos");
    TrayectoriaAcademica trayectoriaEncontrada = (TrayectoriaAcademica) request.getAttribute("trayectoriaEncontrada");
    String matriculaBuscada = (String) request.getAttribute("matriculaBuscada");
    boolean verTodas = request.getAttribute("verTodas") != null;
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Inscripciones</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <h2 class="mt-4">Inscripciones</h2>
            <p class="texto-info">Busca al alumno por matrícula y elige el grupo. Las materias y calificaciones vacías se generan solas a partir de lo que ya tiene el grupo. Este mismo formulario sirve para reinscribir: solo busca la matrícula y elige el nuevo grupo/periodo.</p>

            <form method="get" action="SInscripciones" class="d-flex gap-2 mb-4" style="max-width: 420px;">
                <input type="text" name="matricula" class="form-control" placeholder="Buscar por matrícula" value="<%= matriculaBuscada != null ? matriculaBuscada : ""%>" required>
                <button type="submit" class="btn btn-primary-formal"><i class="bi bi-search"></i></button>
                <% if (matriculaBuscada != null) { %>
                <a href="SInscripciones" class="btn btn-outline-secondary text-nowrap">Limpiar</a>
                <% } %>
            </form>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (trayectoriaEncontrada != null) { %>
            <div class="card-formal mb-4">
                <h5><%= trayectoriaEncontrada.getNombreAlumno()%></h5>
                <p class="mb-2"><%= trayectoriaEncontrada.getNombreCarrera()%> — <%= trayectoriaEncontrada.getNombrePlan()%> — Matrícula <%= trayectoriaEncontrada.getMatricula()%></p>

                <% if (!"ACTIVA".equals(trayectoriaEncontrada.getEstado())) { %>
                <div class="mensaje-error">Esta trayectoria está <%= trayectoriaEncontrada.getEstado().toLowerCase()%>, no se puede inscribir hasta reanudarla.</div>
                <% } else { %>
                <form method="post" action="SInscripciones" class="d-flex gap-2">
                    <input type="hidden" name="accion" value="Agregar">
                    <input type="hidden" name="idTrayectoria" value="<%= trayectoriaEncontrada.getIdTrayectoria()%>">
                    <select name="idGrupo" class="form-select" required>
                        <% for (Grupo grupo : grupos) { %>
                        <% if ("Activo".equals(grupo.getEstatus())) { %>
                        <option value="<%= grupo.getIdGrupo()%>"><%= grupo.getNombreGrupo()%> — <%= grupo.getNombreCarrera()%> — Cuatrimestre <%= grupo.getNumeroCuatrimestre()%> — <%= grupo.getNombrePeriodo()%></option>
                        <% } %>
                        <% } %>
                    </select>
                    <button type="submit" class="btn btn-primary-formal text-nowrap">Inscribir</button>
                </form>
                <% } %>
            </div>
            <% } %>

            <% if (inscripciones.isEmpty()) { %>
            <div class="mensaje-exito">
                <% if (matriculaBuscada != null) { %>
                Esa matrícula todavía no tiene inscripciones registradas.
                <% } else { %>
                No hay inscripciones activas en este momento.
                <% } %>
            </div>
            <% } else { %>
            <div class="d-flex justify-content-between align-items-center flex-wrap mb-2">
                <h6 class="texto-info mb-0">
                    <% if (matriculaBuscada != null) { %>
                    Historial de la matrícula <%= matriculaBuscada%>
                    <% } else if (verTodas) { %>
                    Historial completo (incluye bajas)
                    <% } else { %>
                    Inscripciones activas
                    <% } %>
                </h6>
                <% if (matriculaBuscada == null) { %>
                <a href="SInscripciones<%= verTodas ? "" : "?todas=1"%>" class="texto-info">
                    <% if (verTodas) { %>Ver solo activas<% } else { %>Ver historial completo<% } %>
                </a>
                <% } %>
            </div>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Matrícula</th>
                            <th>Alumno</th>
                            <th>Grupo</th>
                            <th>Periodo</th>
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Inscripcion inscripcion : inscripciones) { %>
                        <tr>
                            <td><%= inscripcion.getMatricula()%></td>
                            <td><%= inscripcion.getNombreAlumno()%></td>
                            <td><%= inscripcion.getNombreGrupo()%></td>
                            <td><%= inscripcion.getNombrePeriodo()%></td>
                            <td>
                                <% if ("Activa".equals(inscripcion.getEstado())) { %>
                                <span class="badge text-bg-success">Activa</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary">Baja</span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <% if ("Activa".equals(inscripcion.getEstado())) { %>
                                <form method="post" action="SInscripciones" class="d-inline">
                                    <input type="hidden" name="accion" value="Baja">
                                    <input type="hidden" name="idInscripcion" value="<%= inscripcion.getIdInscripcion()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal">Baja</button>
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

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
    </body>
</html>
