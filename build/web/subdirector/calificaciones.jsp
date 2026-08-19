<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Calificacion"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Calificacion> calificaciones = (ArrayList<Calificacion>) request.getAttribute("calificaciones");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Calificaciones</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_subdirector.jsp" %>

        <div class="container">
            <div class="mt-4">
                <h2>Calificaciones</h2>
                <p class="texto-info mb-0">Consulta de solo lectura de las calificaciones de tu carrera. Para corregirlas, contacta a Control Escolar.</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error mt-3"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } else if (calificaciones.isEmpty()) { %>
            <div class="mensaje-exito mt-4">No hay calificaciones registradas todavía.</div>
            <% } else { %>
            <div class="tabla-formal-wrap mt-4">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Matrícula</th>
                            <th>Alumno</th>
                            <th>Materia</th>
                            <th>Grupo</th>
                            <th class="text-center">Cuat.</th>
                            <th class="text-center">P1</th>
                            <th class="text-center">P2</th>
                            <th class="text-center">P3</th>
                            <th class="text-center">Promedio</th>
                            <th class="text-center">Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Calificacion calificacion : calificaciones) { %>
                        <tr>
                            <td><%= calificacion.getMatricula()%></td>
                            <td><%= calificacion.getNombreAlumno()%></td>
                            <td><%= calificacion.getNombreMateria()%></td>
                            <td><%= calificacion.getNombreGrupo()%></td>
                            <td class="text-center"><%= calificacion.getNumeroCuatrimestre()%></td>
                            <td class="text-center"><%= calificacion.getParcial1() == null ? "—" : calificacion.getParcial1()%></td>
                            <td class="text-center"><%= calificacion.getParcial2() == null ? "—" : calificacion.getParcial2()%></td>
                            <td class="text-center"><%= calificacion.getParcial3() == null ? "—" : calificacion.getParcial3()%></td>
                            <td class="text-center fw-semibold"><%= calificacion.getPromedioFinal() == null ? "—" : calificacion.getPromedioFinal()%></td>
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
            <% } %>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
            </main>
    </div>
</div>
    </body>
</html>
