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

        <div class="container curriculo-page">
            <header class="curriculo-hero">
                <div>
                    <span class="curriculo-eyebrow"><i class="bi bi-journal-bookmark-fill"></i> Estructura académica</span>
                    <h1>Currículo</h1>
                    <p class="texto-info">Consulta las materias organizadas por cuatrimestre y administra el contenido de cada plan de estudios.</p>
                </div>
                <div class="curriculo-hero-icon" aria-hidden="true"><i class="bi bi-diagram-3"></i></div>
            </header>

            <form method="get" action="SCurriculo" class="curriculo-plan-selector">
                <label class="form-label" for="idPlan">Plan de estudios</label>
                <select id="idPlan" name="idPlan" class="form-select" onchange="this.form.submit()">
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

            <div class="curriculo-toolbar" data-filtros-tabla="#filaCuatrimestres">
                <div class="campo-filtro campo-filtro-texto">
                    <label for="filtroTextoCurriculo"><i class="bi bi-search"></i> Buscar materia</label>
                    <input type="text" id="filtroTextoCurriculo" class="form-control form-control-sm" data-filtro-texto placeholder="Nombre de la materia...">
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>
            <div class="row g-4 curriculo-grid" id="filaCuatrimestres">
                <% for (Map.Entry<PlanCuatrimestre, ArrayList<Materia>> entrada : mapaCurricular.entrySet()) {
                    PlanCuatrimestre cuatrimestre = entrada.getKey();
                    ArrayList<Materia> materiasDelCuatrimestre = entrada.getValue();
                %>
                <div class="col-md-6 col-lg-4" data-fila-filtrable>
                    <div class="curriculo-card h-100">
                        <div class="curriculo-card-header">
                            <div class="curriculo-card-title">
                                <span class="curriculo-card-number"><%= cuatrimestre.getNumeroCuatrimestre()%></span>
                                <h2>Cuatrimestre <%= cuatrimestre.getNumeroCuatrimestre()%></h2>
                            </div>
                            <span class="curriculo-card-count"><%= materiasDelCuatrimestre.size()%> materias</span>
                        </div>

                        <ul class="curriculo-materias">
                            <% if (materiasDelCuatrimestre.isEmpty()) { %>
                            <li class="curriculo-materia curriculo-vacio"><i class="bi bi-inbox"></i> Sin materias todavía.</li>
                            <% } %>
                            <% for (Materia materia : materiasDelCuatrimestre) { %>
                            <li class="curriculo-materia">
                                <% if (materia.isTieneTemario()) { %>
                                <a href="../recursos/temarios/<%= materia.getPdfTemario()%>" target="_blank" rel="noopener" title="Ver temario en PDF">
                                    <i class="bi bi-file-earmark-pdf"></i><span><%= materia.getNombreMateria()%></span>
                                </a>
                                <% } else { %>
                                <span class="curriculo-materia-nombre"><i class="bi bi-book"></i><span><%= materia.getNombreMateria()%></span></span>
                                <% } %>
                                <% if ("Inactiva".equals(materia.getEstatus())) { %>
                                <span class="curriculo-inactiva">Inactiva</span>
                                <% } %>
                            </li>
                            <% } %>
                        </ul>

                        <a href="SMaterias?cuatrimestre=<%= cuatrimestre.getIdPlanCuatrimestre()%>" class="curriculo-add-btn">
                            <i class="bi bi-plus-circle"></i>Nueva materia aquí
                        </a>
                    </div>
                </div>
                <% } %>
            </div>
            <div class="mensaje-exito mt-3" data-filtro-vacio style="display:none;">Ninguna materia coincide con la búsqueda.</div>
            <% } %>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
            </main>
    </div>
</div>
    </body>
</html>
