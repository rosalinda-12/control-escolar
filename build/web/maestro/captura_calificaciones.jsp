<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="modelo.DocenteAsignacion"%>
<%@page import="modelo.Calificacion"%>
<%@page import="java.util.ArrayList"%>
<%
    DocenteAsignacion contexto = (DocenteAsignacion) request.getAttribute("contexto");
    ArrayList<Calificacion> alumnos = (ArrayList<Calificacion>) request.getAttribute("alumnos");
    Integer parcialActivo = contexto == null ? null : contexto.getParcialActivo();
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Captura de calificaciones</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_maestro.jsp" %>

        <div class="container">
            <p class="mt-4 mb-1"><a href="SCalificaciones"><i class="bi bi-arrow-left me-1"></i>Volver a mis grupos</a></p>

            <% if (contexto != null) { %>
            <div class="d-flex justify-content-between align-items-center flex-wrap">
                <div>
                    <h2 class="mb-0"><%= contexto.getNombreMateria()%></h2>
                    <p class="texto-info mb-0">Grupo <%= contexto.getNombreGrupo()%> · <%= contexto.getNombreCarrera()%> · <%= contexto.getNombrePeriodo()%></p>
                </div>
                <% if (parcialActivo != null) { %>
                <span class="badge text-bg-primary fs-6">Parcial activo: <%= parcialActivo%></span>
                <% } %>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error mt-3"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (request.getAttribute("exito") != null) { %>
            <div class="mensaje-exito mt-3"><i class="bi bi-check-circle me-1"></i><%= request.getAttribute("exito")%></div>
            <% } %>

            <% if (parcialActivo == null) { %>
            <div class="mensaje-error mt-3">
                Todavía no hay un parcial activo configurado para este periodo, así que no puedes capturar calificaciones.
            </div>
            <% } else if (alumnos.isEmpty()) { %>
            <div class="mensaje-exito mt-4">No hay alumnos inscritos en esta materia de grupo.</div>
            <% } else { %>

            <form method="post" action="SCalificaciones" class="mt-4">
                <input type="hidden" name="idGrupoMateria" value="<%= contexto.getIdGrupoMateria()%>">
                <input type="hidden" name="numeroParcial" value="<%= parcialActivo%>">

                <div class="tabla-formal-wrap">
                    <table class="table table-formal align-middle">
                        <thead>
                            <tr>
                                <th>Matrícula</th>
                                <th>Alumno</th>
                                <th class="text-center">Parcial 1</th>
                                <th class="text-center">Parcial 2</th>
                                <th class="text-center">Parcial 3</th>
                                <th class="text-center">Promedio</th>
                                <th class="text-center">Estado</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Calificacion calificacion : alumnos) { %>
                            <tr>
                                <td><%= calificacion.getMatricula()%></td>
                                <td><%= calificacion.getNombreAlumno()%></td>
                                <% for (int numeroColumna = 1; numeroColumna <= 3; numeroColumna++) { %>
                                <td class="text-center" style="max-width: 100px;">
                                    <% if (numeroColumna == parcialActivo) { %>
                                    <input type="number" step="0.1" min="0" max="10" class="form-control form-control-sm text-center"
                                           name="nota_<%= calificacion.getIdInscripcionMateria()%>"
                                           value="<%= calificacion.getParcial(numeroColumna) == null ? "" : calificacion.getParcial(numeroColumna)%>">
                                    <% } else { %>
                                    <%= calificacion.getParcial(numeroColumna) == null ? "—" : calificacion.getParcial(numeroColumna)%>
                                    <% } %>
                                </td>
                                <% } %>
                                <td class="text-center fw-semibold">
                                    <%= calificacion.getPromedioFinal() == null ? "—" : calificacion.getPromedioFinal()%>
                                </td>
                                <td class="text-center">
                                    <% if ("Aprobada".equals(calificacion.getEstadoMateria())) { %>
                                    <span class="badge text-bg-success">Aprobada</span>
                                    <% } else if ("Reprobada".equals(calificacion.getEstadoMateria())) { %>
                                    <span class="badge text-bg-danger">Reprobada</span>
                                    <% } else { %>
                                    <span class="badge text-bg-secondary">Cursando</span>
                                    <% } %>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

                <p class="texto-info">Deja el campo vacío para borrar una calificación capturada por error. El promedio y el
                    estado se calculan solos en cuanto los tres parciales tienen valor.</p>

                <button type="submit" class="btn btn-primary-formal">
                    <i class="bi bi-save me-1"></i>Guardar calificaciones del parcial <%= parcialActivo%>
                </button>
            </form>
            <% } %>

            <% } else { %>
            <div class="mensaje-error mt-4">Esa materia de grupo no está a tu cargo.</div>
            <% } %>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
    </body>
</html>
