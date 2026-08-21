<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Periodo"%>
<%@page import="modelo.ResumenCierrePeriodo"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Periodo> periodos = (ArrayList<Periodo>) request.getAttribute("periodos");
    ResumenCierrePeriodo resumen = (ResumenCierrePeriodo) request.getAttribute("resumen");
    String error = (String) session.getAttribute("errorAutomatizacion");
    if (error != null) session.removeAttribute("errorAutomatizacion");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Automatizaciones</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>
        <main class="container automatizaciones-page">
            <header class="automatizaciones-hero">
                <div>
                    <span class="trayectoria-eyebrow"><i class="bi bi-magic"></i> Operación asistida</span>
                    <h1>Automatizaciones</h1>
                    <p>Procesos guiados para cerrar periodos sin perder historial ni revisar cada módulo por separado.</p>
                </div>
                <div class="automatizaciones-hero-icon"><i class="bi bi-arrow-repeat"></i></div>
            </header>

            <% if (error != null) { %><div class="mensaje-error"><i class="bi bi-exclamation-triangle me-2"></i><%= error %></div><% } %>

            <section class="automation-workspace">
                <aside class="automation-sidebar">
                    <div class="automation-sidebar-title"><span class="trayectoria-eyebrow"><i class="bi bi-lightning-charge"></i> Procesos disponibles</span></div>
                    <a class="automation-nav-item active" href="#cierrePeriodo"><i class="bi bi-calendar2-check"></i><span><strong>Cierre de periodo</strong><small>Cierra grupos y periodo en una operación</small></span></a>
                    <a class="automation-nav-item" href="#validacionAcademica"><i class="bi bi-clipboard2-pulse"></i><span><strong>Validación académica</strong><small>Revisa calificaciones pendientes</small></span></a>
                </aside>

                <section class="automation-panel" id="cierrePeriodo">
                    <div class="automation-panel-heading"><div><span class="trayectoria-eyebrow">Paso 1 de 2</span><h2>Cierre de periodo</h2><p>Selecciona un periodo activo para revisar su estado antes de cerrarlo.</p></div><span class="automation-badge"><i class="bi bi-shield-check"></i> Con historial</span></div>
                    <form method="get" action="SAutomatizaciones" class="automation-selector">
                        <label for="idPeriodo">Periodo a procesar</label>
                        <div class="automation-selector-row"><select id="idPeriodo" name="idPeriodo" class="form-select" required><option value="">Selecciona un periodo</option><% for (Periodo periodo : periodos) { %><option value="<%= periodo.getIdPeriodo()%>" <%= resumen != null && resumen.getIdPeriodo() == periodo.getIdPeriodo() ? "selected" : ""%>><%= periodo.getNombrePeriodo()%> · <%= periodo.getNombreCiclo()%> · <%= periodo.getEstatus()%></option><% } %></select><button type="submit" class="btn btn-primary-formal"><i class="bi bi-search me-1"></i>Revisar</button></div>
                    </form>

                    <% if (resumen != null) { %>
                    <div class="automation-review"><div class="automation-review-heading"><div><span class="trayectoria-eyebrow">Paso 2 de 2</span><h3><%= resumen.getNombrePeriodo()%></h3></div><span class="badge <%= "Activo".equals(resumen.getEstatusPeriodo()) ? "text-bg-success" : "text-bg-secondary"%>"><%= resumen.getEstatusPeriodo()%></span></div><div class="automation-metrics"><div><strong><%= resumen.getGruposActivos()%></strong><span>Grupos activos</span></div><div><strong><%= resumen.getInscripcionesActivas()%></strong><span>Inscripciones activas</span></div><div class="<%= resumen.getMateriasPendientes() > 0 ? "has-warning" : "is-ready"%>"><strong><%= resumen.getMateriasPendientes()%></strong><span>Materias sin final</span></div></div><div class="automation-checklist"><div class="<%= resumen.getMateriasPendientes() == 0 ? "check-ok" : "check-warning"%>"><i class="bi <%= resumen.getMateriasPendientes() == 0 ? "bi-check-circle-fill" : "bi-exclamation-circle-fill"%>"></i><span><strong><%= resumen.getMateriasPendientes() == 0 ? "Listo para cerrar" : "Faltan calificaciones"%></strong><small><%= resumen.getMateriasPendientes() == 0 ? "Se cerrarán grupos y periodo, conservando el historial." : "Completa las calificaciones finales antes de ejecutar el cierre."%></small></span></div></div><% if ("Activo".equals(resumen.getEstatusPeriodo())) { %><form method="post" action="SAutomatizaciones" onsubmit="return confirm('¿Cerrar este periodo y todos sus grupos activos? Esta acción conservará el historial, pero bloqueará la operación normal del periodo.');"><input type="hidden" name="idPeriodo" value="<%= resumen.getIdPeriodo()%>"><button type="submit" class="btn btn-primary-formal automation-submit" <%= resumen.isListoParaCerrar() ? "" : "disabled"%>><i class="bi bi-lock-fill me-2"></i>Cerrar periodo automáticamente</button></form><% } %></div>
                    <% } else { %><div class="automation-empty"><i class="bi bi-calendar2-week"></i><strong>Revisa un periodo para comenzar</strong><span>El sistema validará calificaciones, grupos e inscripciones antes de ejecutar cambios.</span></div><% } %>
                </section>
            </section>

        </main>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
    </body>
</html>
