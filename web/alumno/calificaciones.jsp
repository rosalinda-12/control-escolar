<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.BoletaCuatrimestre"%>
<%@page import="modelo.Calificacion"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<BoletaCuatrimestre> boleta = (ArrayList<BoletaCuatrimestre>) request.getAttribute("boleta");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Mis calificaciones</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_alumno.jsp" %>

        <div class="container">
            <h2 class="mt-4">Mis calificaciones</h2>
            <p class="texto-info">Tus materias agrupadas por cuatrimestre, con el promedio final de cada una y el
                promedio general del cuatrimestre.</p>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (boleta.isEmpty()) { %>
            <div class="mensaje-exito mt-4">Todavía no tienes materias inscritas.</div>
            <% } %>

            <% for (BoletaCuatrimestre cuatrimestre : boleta) { %>
            <div class="card card-formal">
                <div class="card-header d-flex justify-content-between align-items-center flex-wrap">
                    <span><i class="bi bi-calendar3 me-2"></i>Cuatrimestre <%= cuatrimestre.getNumeroCuatrimestre()%></span>
                    <span class="texto-info" style="color: #FFFFFF; opacity: 0.85;">
                        <%= cuatrimestre.getNombrePeriodo()%> · Grupo <%= cuatrimestre.getNombreGrupo()%>
                    </span>
                </div>
                <div class="card-body p-0">
                    <div class="tabla-formal-wrap mt-0" style="border: none; border-radius: 0; box-shadow: none;">
                        <table class="table table-formal align-middle mb-0">
                            <thead>
                                <tr>
                                    <th>Materia</th>
                                    <th class="text-center">Parcial 1</th>
                                    <th class="text-center">Parcial 2</th>
                                    <th class="text-center">Parcial 3</th>
                                    <th class="text-center">Promedio</th>
                                    <th class="text-center">Estado</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Calificacion materia : cuatrimestre.getMaterias()) { %>
                                <tr>
                                    <td><%= materia.getNombreMateria()%></td>
                                    <td class="text-center"><%= materia.getParcial1() == null ? "—" : materia.getParcial1()%></td>
                                    <td class="text-center"><%= materia.getParcial2() == null ? "—" : materia.getParcial2()%></td>
                                    <td class="text-center"><%= materia.getParcial3() == null ? "—" : materia.getParcial3()%></td>
                                    <td class="text-center fw-semibold"><%= materia.getPromedioFinal() == null ? "—" : materia.getPromedioFinal()%></td>
                                    <td class="text-center">
                                        <% if ("Aprobada".equals(materia.getEstadoMateria())) { %>
                                        <span class="badge text-bg-success">Aprobada</span>
                                        <% } else if ("Reprobada".equals(materia.getEstadoMateria())) { %>
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
                    <div class="d-flex justify-content-between align-items-center flex-wrap px-3 py-3">
                        <span class="fw-semibold">
                            Promedio del cuatrimestre:
                            <%= cuatrimestre.getPromedioCuatrimestre() == null ? "—" : cuatrimestre.getPromedioCuatrimestre()%>
                        </span>
                        <% if (cuatrimestre.getPromedioCuatrimestre() != null && !cuatrimestre.isCompleto()) { %>
                        <span class="texto-info">
                            <i class="bi bi-info-circle me-1"></i>Preliminar: calculado con
                            <%= cuatrimestre.getMateriasConPromedio()%> de <%= cuatrimestre.getMaterias().size()%> materias.
                        </span>
                        <% } %>
                    </div>
                </div>
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
