<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Grupo"%>
<%@page import="modelo.TrayectoriaAcademica"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%
    ArrayList<Grupo> grupos = (ArrayList<Grupo>) request.getAttribute("grupos");
    ArrayList<TrayectoriaAcademica> trayectorias = (ArrayList<TrayectoriaAcademica>) request.getAttribute("trayectorias");
    String error = (String) session.getAttribute("errorMasivo");
    String aviso = (String) session.getAttribute("avisoMasivo");
    session.removeAttribute("errorMasivo");
    session.removeAttribute("avisoMasivo");
    Map<Integer, String> gruposAnteriores = new LinkedHashMap<>();
    for (TrayectoriaAcademica trayectoria : trayectorias) {
        if (trayectoria.getIdGrupoAnterior() != null && trayectoria.getGrupoAnterior() != null) {
            gruposAnteriores.put(trayectoria.getIdGrupoAnterior(), trayectoria.getGrupoAnterior());
        }
    }
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Inscripción masiva</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>
        <main class="container automatizaciones-page">
            <header class="automatizaciones-hero">
                <div>
                    <span class="trayectoria-eyebrow"><i class="bi bi-people"></i> Operación por lote</span>
                    <h1>Inscripción masiva por grupo</h1>
                    <p>Selecciona un grupo y varios alumnos activos para procesar sus inscripciones en una sola operación.</p>
                </div>
                <div class="automatizaciones-hero-icon"><i class="bi bi-person-plus"></i></div>
            </header>
            <% if (error != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-2"></i><%= error %></div>
            <% } %>
            <% if (aviso != null) { %>
            <div class="mensaje-exito"><i class="bi bi-check-circle me-2"></i><%= aviso %></div>
            <% } %>
            <section class="automation-panel">
                <form method="post" action="SInscripcionMasiva">
                    <div class="automation-selector">
                        <label for="idGrupoMasivo">Grupo y periodo destino</label>
                        <select id="idGrupoMasivo" name="idGrupo" class="form-select" required>
                            <option value="">Selecciona un grupo</option>
                            <% for (Grupo grupo : grupos) { %>
                            <option value="<%= grupo.getIdGrupo() %>" data-carrera="<%= grupo.getIdCarrera() %>" data-periodo="<%= grupo.getIdPeriodo() %>">
                                <%= grupo.getNombreGrupo() %> · Cuatrimestre <%= grupo.getNumeroCuatrimestre() %> · <%= grupo.getNombrePeriodo() %> · <%= grupo.getNombreCarrera() %>
                            </option>
                            <% } %>
                        </select>
                        <input type="hidden" name="idPeriodo" id="idPeriodoMasivo">
                    </div>
                    <div class="automation-selector">
                        <label for="idGrupoAnteriorMasivo">Grupo anterior de los alumnos</label>
                        <select id="idGrupoAnteriorMasivo" name="idGrupoAnterior" class="form-select">
                            <option value="">Todos los grupos anteriores</option>
                            <% for (Map.Entry<Integer, String> grupoAnterior : gruposAnteriores.entrySet()) { %>
                            <option value="<%= grupoAnterior.getKey() %>"><%= grupoAnterior.getValue() %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="massive-students-heading">
                        <div>
                            <span class="trayectoria-eyebrow">Alumnos disponibles</span>
                            <h2>Seleccionar alumnos</h2>
                            <p id="filtroCarreraMasivo" class="texto-info mb-0">Selecciona un grupo para mostrar alumnos de su carrera.</p>
                        </div>
                        <button type="button" class="btn btn-outline-formal" data-seleccionar-todos>
                            <i class="bi bi-check2-square me-1"></i>Seleccionar todos
                        </button>
                    </div>
                    <div class="massive-students-grid">
                        <% for (TrayectoriaAcademica trayectoria : trayectorias) { %>
                            <% if ("ACTIVA".equals(trayectoria.getEstado())) { %>
                            <label class="massive-student" data-carrera="<%= trayectoria.getIdCarrera() %>" data-grupo-anterior="<%= trayectoria.getIdGrupoAnterior() == null ? "" : trayectoria.getIdGrupoAnterior() %>">
                                <input type="checkbox" name="idsTrayectoria" value="<%= trayectoria.getIdTrayectoria() %>">
                                <span>
                                    <strong><%= trayectoria.getNombreAlumno() %></strong>
                                    <small><%= trayectoria.getMatricula() %> · <%= trayectoria.getNombreCarrera() %>
                                        · Anterior: <%= trayectoria.getGrupoAnterior() == null ? "Sin inscripción" : trayectoria.getGrupoAnterior() %></small>
                                </span>
                            </label>
                            <% } %>
                        <% } %>
                    </div>
                    <button type="submit" class="btn btn-primary-formal automation-submit">
                        <i class="bi bi-lightning-charge me-2"></i>Procesar inscripción masiva
                    </button>
                </form>
            </section>
        </main>
        <script>
            const grupoMasivo = document.getElementById('idGrupoMasivo');
            const grupoAnteriorMasivo = document.getElementById('idGrupoAnteriorMasivo');
            const alumnosMasivos = Array.from(document.querySelectorAll('.massive-student'));
            const avisoCarrera = document.getElementById('filtroCarreraMasivo');
            function actualizarFiltroCarrera() {
                const opcion = grupoMasivo.options[grupoMasivo.selectedIndex];
                const idCarrera = opcion ? opcion.dataset.carrera : '';
                const idGrupoAnterior = grupoAnteriorMasivo.value;
                let visibles = 0;
                alumnosMasivos.forEach((alumno) => {
                    const compatible = idCarrera !== '' && alumno.dataset.carrera === idCarrera
                            && (idGrupoAnterior === '' || alumno.dataset.grupoAnterior === idGrupoAnterior);
                    alumno.hidden = !compatible;
                    if (!compatible) alumno.querySelector('input').checked = false;
                    if (compatible) visibles++;
                });
                document.getElementById('idPeriodoMasivo').value = opcion ? opcion.dataset.periodo || '' : '';
                avisoCarrera.textContent = idCarrera === ''
                        ? 'Selecciona un grupo para mostrar alumnos de su carrera.'
                        : visibles + ' alumno(s) disponible(s) con los filtros seleccionados.';
            }
            grupoMasivo.addEventListener('change', actualizarFiltroCarrera);
            grupoAnteriorMasivo.addEventListener('change', actualizarFiltroCarrera);
            document.querySelector('[data-seleccionar-todos]').addEventListener('click', () => {
                alumnosMasivos.filter((alumno) => !alumno.hidden).forEach((alumno) => {
                    alumno.querySelector('input').checked = true;
                });
            });
            actualizarFiltroCarrera();
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
    </body>
</html>
