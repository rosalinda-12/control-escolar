<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.PlanEstudio"%>
<%@page import="modelo.PlanCuatrimestre"%>
<%@page import="modelo.Materia"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%
    ArrayList<PlanEstudio> planes = (ArrayList<PlanEstudio>) request.getAttribute("planes");
    PlanEstudio planSeleccionado = (PlanEstudio) request.getAttribute("planSeleccionado");
    LinkedHashMap<PlanCuatrimestre, ArrayList<Materia>> mapaCurricular =
            (LinkedHashMap<PlanCuatrimestre, ArrayList<Materia>>) request.getAttribute("mapaCurricular");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Currículo</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <h2 class="mt-4">Currículo</h2>
            <p class="texto-info">Elige un plan para ver sus materias por cuatrimestre. Cada materia queda ligada a su cuatrimestre desde
                que se da de alta en <a href="SMaterias">Materias</a>; si necesitas otra ahí, créala directamente en ese cuatrimestre.</p>

            <form method="get" action="SCurriculo" class="mb-4" style="max-width: 480px;">
                <label class="form-label">Plan de estudios</label>
                <select name="idPlan" class="form-select" onchange="this.form.submit()">
                    <option value="">Selecciona un plan...</option>
                    <% for (PlanEstudio plan : planes) { %>
                    <option value="<%= plan.getIdPlan()%>" <%= (planSeleccionado != null && planSeleccionado.getIdPlan() == plan.getIdPlan()) ? "selected" : ""%>>
                        <%= plan.getNombreCarrera()%> — <%= plan.getNombrePlan()%> (v<%= plan.getVersion()%>)
                    </option>
                    <% } %>
                </select>
            </form>

            <% if (planSeleccionado == null) { %>
            <% if (planes.isEmpty()) { %>
            <div class="mensaje-exito">Todavía no hay planes de estudio. <a href="SPlanes">Crea uno primero</a>.</div>
            <% } %>
            <% } else { %>

            <div class="row g-3">
                <% for (Map.Entry<PlanCuatrimestre, ArrayList<Materia>> entrada : mapaCurricular.entrySet()) {
                    PlanCuatrimestre cuatrimestre = entrada.getKey();
                    ArrayList<Materia> materiasDelCuatrimestre = entrada.getValue();
                %>
                <div class="col-md-6 col-lg-4">
                    <div class="card-formal h-100">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <h5 class="mb-0">Cuatrimestre <%= cuatrimestre.getNumeroCuatrimestre()%></h5>
                            <span class="badge text-bg-secondary"><%= materiasDelCuatrimestre.size()%> materias</span>
                        </div>

                        <ul class="list-group list-group-flush mb-3">
                            <% if (materiasDelCuatrimestre.isEmpty()) { %>
                            <li class="list-group-item text-muted small">Sin materias todavía.</li>
                            <% } %>
                            <% for (Materia materia : materiasDelCuatrimestre) { %>
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                <%= materia.getNombreMateria()%>
                                <% if ("Inactiva".equals(materia.getEstatus())) { %>
                                <span class="badge text-bg-secondary">Inactiva</span>
                                <% } %>
                            </li>
                            <% } %>
                        </ul>

                        <a href="SMaterias?cuatrimestre=<%= cuatrimestre.getIdPlanCuatrimestre()%>" class="btn btn-sm btn-outline-formal">
                            <i class="bi bi-plus-lg me-1"></i>Nueva materia aquí
                        </a>
                    </div>
                </div>
                <% } %>
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
