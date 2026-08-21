<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Preparar carrera</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet"><link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet"><link rel="stylesheet" href="../estilo/styles.css">
</head>
<body>
<%@ include file="menu_admin.jspf" %>
<main class="container automatizaciones-page">
<header class="automatizaciones-hero"><div><span class="trayectoria-eyebrow"><i class="bi bi-mortarboard"></i> Alta guiada</span><h1>Preparar una carrera</h1><p>Crea la carrera, su plan, los cuatrimestres y los tramos académicos en un solo proceso.</p></div><div class="automatizaciones-hero-icon"><i class="bi bi-magic"></i></div></header>
<% if (request.getAttribute("error") != null) { %><div class="mensaje-error"><i class="bi bi-exclamation-triangle me-2"></i><%= request.getAttribute("error") %></div><% } %>
<section class="automation-panel automation-career-page-panel"><div class="automation-panel-heading"><div><span class="trayectoria-eyebrow">Datos de la estructura</span><h2>Información base</h2><p>Para planes de más de seis cuatrimestres se crean automáticamente los tramos TSU e Ingeniería.</p></div><span class="automation-badge"><i class="bi bi-check2-circle"></i> Estructura base</span></div>
<form method="post" action="SPrepararCarrera" class="career-automation-form"><div><label for="tfNombreCarreraAuto">Nombre de la carrera</label><input id="tfNombreCarreraAuto" name="tfNombreCarrera" class="form-control" placeholder="Ingeniería en Desarrollo de Software" required></div><div><label for="tfClaveCarreraAuto">Clave</label><input id="tfClaveCarreraAuto" name="tfClaveCarrera" class="form-control text-uppercase" placeholder="IDS" maxlength="20" required></div><div><label for="tfNombrePlanAuto">Nombre del plan</label><input id="tfNombrePlanAuto" name="tfNombrePlan" class="form-control" placeholder="Plan 2026" required></div><div><label for="tfVersionAuto">Versión</label><input id="tfVersionAuto" name="tfVersion" class="form-control" placeholder="2026-1" required></div><div><label for="tfDuracionAuto">Duración</label><input id="tfDuracionAuto" name="tfDuracion" type="number" min="1" max="20" value="11" class="form-control" required></div><button type="submit" class="btn btn-primary-formal career-automation-submit"><i class="bi bi-magic me-2"></i>Crear estructura académica</button></form><p class="texto-info mb-0 mt-3"><i class="bi bi-info-circle me-1"></i>Al terminar irás al Currículo para capturar las materias oficiales.</p></section>
</main><script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script><script src="../estilo/app.js"></script>
</body></html>
