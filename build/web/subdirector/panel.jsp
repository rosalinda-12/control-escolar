<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Usuario"%>
<%@page import="modelo.Carrera"%>
<%@page import="modelo.Grupo"%>
<%@page import="java.util.ArrayList"%>
<%

    ArrayList<Carrera> carreras = (ArrayList<Carrera>) request.getAttribute("carreras");
    ArrayList<Grupo> grupos = (ArrayList<Grupo>) request.getAttribute("grupos");
    int gruposActivos = 0;
    int gruposInactivos = 0;
    for (Grupo grupo : grupos) {
        if ("Activo".equals(grupo.getEstatus())) gruposActivos++;
        else gruposInactivos++;
    }
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Panel del Subdirector</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_subdirector.jsp" %>

        <div class="container">
            <div class="contenedor-centrado text-center" style="max-width: 600px;">
                <i class="bi bi-person-badge" style="font-size: 2.4rem; color: var(--color-primary);"></i>
                <h2 class="mt-3">Hola, <%= usuarioSesion.getNombres()%></h2>
                <% if (!carreras.isEmpty()) { %>
                <p class="texto-info">Est&aacute;s a cargo de <strong><%= carreras.size() == 1 ? carreras.get(0).getNombreCarrera() : carreras.size() + " carreras"%></strong>. Desde aqu&iacute; puedes consultar sus grupos y docentes asignados.</p>
                <a href="SGrupos" class="btn btn-primary-formal mt-2">
                    <i class="bi bi-diagram-3 me-1"></i>Ver grupos de mis carreras
                </a>
                <% } else { %>
                <div class="mensaje-error mt-3">
                    <i class="bi bi-exclamation-triangle me-1"></i>
                    Tu cuenta de Subdirector todav&iacute;a no tiene una carrera asignada. Pide al Administrador que la configure desde Usuarios.
                </div>
                <% } %>
            </div>

            <% if (!carreras.isEmpty()) { %>
            <section class="dashboard-graficas mt-4">
                <div class="dashboard-grafica">
                    <div class="dashboard-grafica-heading"><h3>Grupos por carrera</h3><span><%= grupos.size()%> total</span></div>
                    <div class="dashboard-chart-wrap"><canvas id="graficaGruposCarrera" aria-label="Grupos por carrera"></canvas></div>
                </div>
                <div class="dashboard-grafica">
                    <div class="dashboard-grafica-heading"><h3>Estado de los grupos</h3><span><%= gruposActivos %> activos</span></div>
                    <div class="dashboard-chart-wrap dashboard-chart-doughnut"><canvas id="graficaEstadoGrupos" aria-label="Estado de los grupos"></canvas></div>
                </div>
            </section>
            <% } %>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.4/dist/chart.umd.min.js"></script>
        <% if (!carreras.isEmpty()) { %>
        <script>
            new Chart(document.getElementById('graficaGruposCarrera'), {
                type: 'bar',
                data: {
                    labels: [<% for (Carrera carreraGrafica : carreras) { %>'<%= carreraGrafica.getNombreCarrera().replace("'", "\\'") %>',<% } %>],
                    datasets: [{ label: 'Grupos', data: [<% for (Carrera carreraGrafica : carreras) { int total = 0; for (Grupo grupo : grupos) if (grupo.getIdCarrera() == carreraGrafica.getIdCarrera()) total++; %><%= total %>,<% } %>], backgroundColor: '#5b84ed', hoverBackgroundColor: '#86a8ff', borderRadius: 7, maxBarThickness: 58 }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { display: false }, tooltip: { callbacks: { title: function (items) { return items[0].label; } } } },
                    scales: {
                        x: { ticks: { color: '#9aa6bd', maxRotation: 0, minRotation: 0, autoSkip: false, callback: function (value) { var texto = this.getLabelForValue(value); return texto.match(/.{1,22}(?:\s|$)/g) || texto; } }, grid: { display: false } },
                        y: { beginAtZero: true, ticks: { precision: 0, color: '#9aa6bd' }, grid: { color: 'rgba(154, 166, 189, .16)' } }
                    }
                }
            });
            new Chart(document.getElementById('graficaEstadoGrupos'), {
                type: 'doughnut',
                data: { labels: ['Activos', 'Inactivos'], datasets: [{ data: [<%= gruposActivos %>, <%= gruposInactivos %>], backgroundColor: ['#35b779', '#e07a5f'], borderWidth: 0 }] },
                options: { responsive: true, maintainAspectRatio: false, cutout: '68%', plugins: { legend: { position: 'bottom', labels: { color: '#9aa6bd', padding: 18, usePointStyle: true } } } }
            });
        </script>
        <% } %>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
            </main>
    </div>
</div>
    </body>
</html>
