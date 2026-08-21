<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Alumno"%>
<%@page import="modelo.TrayectoriaAcademica"%>
<%@page import="modelo.BoletaCuatrimestre"%>
<%@page import="modelo.Calificacion"%>
<%@page import="java.util.ArrayList"%>
<%
    Alumno alumno = (Alumno) request.getAttribute("alumno");
    ArrayList<TrayectoriaAcademica> trayectorias = (ArrayList<TrayectoriaAcademica>) request.getAttribute("trayectorias");
    ArrayList<BoletaCuatrimestre> boleta = (ArrayList<BoletaCuatrimestre>) request.getAttribute("boleta");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Trayectoria del alumno</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_subdirector.jsp" %>
        <div class="container trayectoria-page">
            <a href="SCalificaciones" class="trayectoria-back"><i class="bi bi-arrow-left"></i> Calificaciones</a>
            <header class="trayectoria-header">
                <div class="trayectoria-header-main">
                    <span class="trayectoria-eyebrow"><i class="bi bi-person-vcard"></i> Expediente académico</span>
                    <h1><%= alumno.getNombreCompleto()%></h1>
                    <div class="trayectoria-header-meta">
                        <span><i class="bi bi-envelope"></i> <%= alumno.getCorreo()%></span>
                        <span class="student-status-inline is-active"><i class="bi bi-person-check-fill"></i><%= alumno.getEstatus()%></span>
                    </div>
                </div>
            </header>
            <section class="trayectoria-section">
                <div class="section-heading-row">
                    <div><span class="trayectoria-eyebrow"><i class="bi bi-signpost-split"></i> Historial de programas</span><h2>Trayectoria académica</h2><p>Solo se muestra la trayectoria del alumno dentro de tu carrera.</p></div>
                </div>
                <div class="trayectoria-list">
                    <% for (TrayectoriaAcademica trayectoria : trayectorias) { %>
                    <article class="trayectoria-card">
                        <div class="trayectoria-card-content">
                            <div class="trayectoria-card-icon"><i class="bi bi-mortarboard-fill"></i></div>
                            <div class="trayectoria-card-copy"><span class="card-label">Carrera</span><h3><%= trayectoria.getNombreCarrera()%></h3><p class="trayectoria-specialty"><i class="bi bi-bookmark"></i> <%= trayectoria.getNombrePlan()%></p><div class="trayectoria-card-details"><span><i class="bi bi-upc-scan"></i> <%= trayectoria.getMatricula()%></span><span><i class="bi bi-calendar3"></i> Inicio <%= trayectoria.getFechaInicio()%></span></div></div>
                            <div class="trayectoria-card-actions"><span class="trajectory-status status-<%= trayectoria.getEstado().toLowerCase()%>"><%= trayectoria.getEstado()%></span></div>
                        </div>
                    </article>
                    <% } %>
                </div>
            </section>
            <section class="trayectoria-section historial-section" id="historialAcademico">
                <div class="section-heading-row">
                    <div><span class="trayectoria-eyebrow"><i class="bi bi-clock-history"></i> Rendimiento</span><h2>Historial académico por nivel</h2><p>Consulta las calificaciones registradas, incluso de trayectorias egresadas o dadas de baja.</p></div>
                </div>
                <% if (boleta == null || boleta.isEmpty()) { %>
                <div class="mensaje-exito">Todavía no hay calificaciones registradas para esta carrera.</div>
                <% } else { %>
                <div class="historial-grid">
                    <% for (BoletaCuatrimestre cuatrimestre : boleta) { %>
                    <article class="historial-card">
                        <div class="historial-card-top"><div><span class="card-label"><%= cuatrimestre.getNombreNivel()%> · Periodo académico</span><h3>Cuatrimestre <%= cuatrimestre.getNumeroCuatrimestre()%></h3></div><span class="historial-number"><%= cuatrimestre.getMaterias().size()%> materias</span></div>
                        <div class="historial-card-data"><span><i class="bi bi-calendar3"></i><%= cuatrimestre.getNombrePeriodo()%></span><span><i class="bi bi-people"></i>Grupo <%= cuatrimestre.getNombreGrupo()%></span></div>
                        <div class="historial-card-footer"><div><span class="card-label">Promedio</span><strong><%= cuatrimestre.getPromedioCuatrimestre() == null ? "—" : cuatrimestre.getPromedioCuatrimestre()%></strong><small><%= cuatrimestre.isCompleto() ? "Concluido" : "En curso"%></small></div><button type="button" class="btn btn-outline-formal" data-bs-toggle="collapse" data-bs-target="#materiasSubdirector<%= cuatrimestre.getIdTrayectoria()%>_<%= cuatrimestre.getNumeroCuatrimestre()%>" aria-expanded="false"><i class="bi bi-table me-1"></i>Ver materias</button></div>
                        <div class="collapse materias-panel" id="materiasSubdirector<%= cuatrimestre.getIdTrayectoria()%>_<%= cuatrimestre.getNumeroCuatrimestre()%>">
                            <div class="table-responsive"><table class="table table-formal align-middle mb-0"><thead><tr><th>Materia</th><th class="text-center">P1</th><th class="text-center">P2</th><th class="text-center">P3</th><th class="text-center">Final</th><th>Estado</th></tr></thead><tbody>
                                <% for (Calificacion materia : cuatrimestre.getMaterias()) { %>
                                <tr><td><strong><%= materia.getNombreMateria()%></strong></td><td class="text-center"><%= materia.getParcial1() == null ? "—" : materia.getParcial1()%></td><td class="text-center"><%= materia.getParcial2() == null ? "—" : materia.getParcial2()%></td><td class="text-center"><%= materia.getParcial3() == null ? "—" : materia.getParcial3()%></td><td class="text-center fw-semibold"><%= materia.getPromedioFinal() == null ? "—" : materia.getPromedioFinal()%></td><td><% if ("Aprobada".equals(materia.getEstadoMateria())) { %><span class="badge text-bg-success">Aprobada</span><% } else if ("Reprobada".equals(materia.getEstadoMateria())) { %><span class="badge text-bg-danger">Reprobada</span><% } else { %><span class="badge text-bg-secondary">En curso</span><% } %></td></tr>
                                <% } %>
                            </tbody></table></div>
                        </div>
                    </article>
                    <% } %>
                </div>
                <% } %>
            </section>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
    </body>
</html>
