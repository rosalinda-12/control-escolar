<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Inscripcion"%>
<%@page import="modelo.Grupo"%>
<%@page import="modelo.TrayectoriaAcademica"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Inscripcion> inscripciones = (ArrayList<Inscripcion>) request.getAttribute("inscripciones");
    ArrayList<Grupo> grupos = (ArrayList<Grupo>) request.getAttribute("grupos");
    TrayectoriaAcademica trayectoriaEncontrada = (TrayectoriaAcademica) request.getAttribute("trayectoriaEncontrada");
    String matriculaBuscada = (String) request.getAttribute("matriculaBuscada");
    int gruposActivos = 0;
    if (grupos != null) {
        for (Grupo grupo : grupos) {
            if ("Activo".equals(grupo.getEstatus())) gruposActivos++;
        }
    }
    java.util.LinkedHashSet<String> carrerasInscripciones = new java.util.LinkedHashSet<String>();
    java.util.LinkedHashSet<String> periodosInscripciones = new java.util.LinkedHashSet<String>();
    for (Inscripcion inscripcion : inscripciones) {
        carrerasInscripciones.add(inscripcion.getNombreCarrera());
        periodosInscripciones.add(inscripcion.getNombrePeriodo());
    }
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Inscripciones</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <main class="container inscripciones-page">
            <header class="inscripciones-heading">
                <div>
                    <span class="inscripciones-eyebrow"><i class="bi bi-mortarboard-fill"></i> Gesti&oacute;n acad&eacute;mica</span>
                    <h1>Inscripciones</h1>
                    <p>Busca un alumno, elige su grupo y confirma el movimiento acad&eacute;mico.</p>
                </div>
                <div class="inscripciones-heading-icon" aria-hidden="true"><i class="bi bi-journal-check"></i></div>
            </header>

            <form method="get" action="SInscripciones" class="inscripciones-search" data-busqueda-tiempo-real>
                <div class="inscripciones-search-field">
                    <i class="bi bi-search" aria-hidden="true"></i>
                    <input type="search" name="matricula" placeholder="Matr&iacute;cula, nombre o carrera" value="<%= matriculaBuscada != null ? matriculaBuscada : ""%>" autocomplete="off" required aria-label="Buscar alumno">
                </div>
                <button type="submit" class="btn btn-primary-formal"><i class="bi bi-search me-1"></i> Buscar</button>
                <button type="button" class="btn btn-outline-formal" data-busqueda-limpiar>Limpiar</button>
            </form>

            <div class="barra-filtros inscripciones-filter" data-filtros-tabla="#tbodyInscripciones">
                <div class="campo-filtro">
                    <label for="filtroEstatusInscripciones">Estatus</label>
                    <select id="filtroEstatusInscripciones" class="form-select form-select-sm" data-filtro-campo="estatus">
                        <option value="Activo" selected>Activos actuales</option>
                        <option value="">Todos</option>
                        <option value="Egresado">Egresados</option>
                        <option value="Baja">Bajas</option>
                    </select>
                </div>
                <div class="campo-filtro">
                    <label for="filtroCarreraInscripciones">Carrera</label>
                    <select id="filtroCarreraInscripciones" class="form-select form-select-sm" data-filtro-campo="carrera">
                        <option value="" selected>Todas</option>
                        <% for (String carrera : carrerasInscripciones) { %>
                        <option value="<%= carrera%>"><%= carrera%></option>
                        <% } %>
                    </select>
                </div>
                <div class="campo-filtro">
                    <label for="filtroPeriodoInscripciones">Periodo</label>
                    <select id="filtroPeriodoInscripciones" class="form-select form-select-sm" data-filtro-campo="periodo">
                        <option value="" selected>Todos</option>
                        <% for (String periodo : periodosInscripciones) { %>
                        <option value="<%= periodo%>"><%= periodo%></option>
                        <% } %>
                    </select>
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>

            <div id="resultadoInscripciones">
            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (trayectoriaEncontrada != null) { %>
            <div class="inscripciones-workspace">
                <section class="inscripciones-student-panel">
                    <div class="panel-kicker"><i class="bi bi-person-vcard"></i> Alumno encontrado</div>
                    <div class="student-avatar"><i class="bi bi-person-fill"></i></div>
                    <h2><%= trayectoriaEncontrada.getNombreAlumno()%></h2>
                    <div class="student-id"><i class="bi bi-hash"></i> <%= trayectoriaEncontrada.getMatricula()%></div>
                    <div class="student-details">
                        <div><span><i class="bi bi-mortarboard"></i> Carrera</span><strong><%= trayectoriaEncontrada.getNombreCarrera()%></strong></div>
                        <div><span><i class="bi bi-bookmark"></i> Plan de estudios</span><strong><%= trayectoriaEncontrada.getNombrePlan()%></strong></div>
                    </div>
                    <div class="student-status <%= "ACTIVA".equals(trayectoriaEncontrada.getEstado()) ? "is-active" : "is-inactive" %>">
                        <i class="bi <%= "ACTIVA".equals(trayectoriaEncontrada.getEstado()) ? "bi-check-circle-fill" : "bi-exclamation-circle-fill" %>"></i>
                        <span><%= trayectoriaEncontrada.getEstado()%></span>
                    </div>
                </section>

                <section class="inscripciones-groups-panel">
                    <div class="panel-title-row">
                        <div>
                            <div class="panel-kicker"><i class="bi bi-grid-3x3-gap"></i> Paso 2 de 3</div>
                            <h2>Selecciona un grupo</h2>
                        </div>
                        <span class="groups-count"><%= gruposActivos %> disponibles</span>
                    </div>
                    <% if (request.getAttribute("avanceNivel") != null) { %>
                    <div class="mensaje-exito inscripciones-avance"><i class="bi bi-arrow-up-right-circle me-2"></i><%= request.getAttribute("avanceNivel")%> Selecciona el grupo del siguiente nivel para continuar.</div>
                    <% } %>

                <% if (!"ACTIVA".equals(trayectoriaEncontrada.getEstado())) { %>
                <div class="mensaje-error">Esta trayectoria est&aacute; <%= trayectoriaEncontrada.getEstado().toLowerCase()%>, no se puede inscribir hasta reanudarla.</div>
                <% } else if (gruposActivos == 0) { %>
                <div class="mensaje-error"><i class="bi bi-info-circle me-2"></i>No hay grupos disponibles para inscribir a este alumno en este momento.</div>
                <% } else { %>
                <form method="post" action="SInscripciones" class="inscripciones-form">
                    <input type="hidden" name="accion" value="Agregar">
                    <input type="hidden" name="idTrayectoria" value="<%= trayectoriaEncontrada.getIdTrayectoria()%>">
                    <div class="group-options" role="radiogroup" aria-label="Grupos disponibles">
                        <% for (Grupo grupo : grupos) { %>
                        <% if ("Activo".equals(grupo.getEstatus())) { %>
                        <label class="group-option">
                            <input type="radio" name="idGrupo" value="<%= grupo.getIdGrupo()%>" required>
                            <span class="group-option-card">
                                <span class="group-option-top"><strong><%= grupo.getNombreGrupo()%></strong><span class="badge text-bg-success">Disponible</span></span>
                                <span class="group-option-meta"><i class="bi bi-layers"></i> Cuatrimestre <%= grupo.getNumeroCuatrimestre()%></span>
                                <span class="group-option-period"><i class="bi bi-calendar3"></i> <%= grupo.getNombrePeriodo()%></span>
                                <i class="bi bi-check-circle-fill group-option-check" aria-hidden="true"></i>
                            </span>
                        </label>
                        <% } %>
                        <% } %>
                    </div>
                    <div class="inscripciones-confirmation">
                        <div class="confirmation-copy"><span class="panel-kicker"><i class="bi bi-clipboard-check"></i> Paso 3 de 3</span><strong>Revisa y confirma</strong><span>Grupo seleccionado: <b data-grupo-seleccionado>Sin seleccionar</b></span></div>
                        <button type="submit" class="btn btn-primary-formal inscripciones-submit"><i class="bi bi-check2-circle me-2"></i>Confirmar inscripci&oacute;n</button>
                    </div>
                </form>
                <% } %>
                </section>
            </div>
            <% } %>

            <% if (inscripciones.isEmpty()) { %>
            <div class="mensaje-exito">No hay inscripciones registradas todav&iacute;a.</div>
            <% } else { %>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Matr&iacute;cula</th>
                            <th>Alumno</th>
                            <th>Grupo</th>
                            <th>Periodo</th>
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="tbodyInscripciones">
                        <% for (Inscripcion inscripcion : inscripciones) { %>
                        <tr data-fila-filtrable
                            data-estatus="<%= "Egresado".equals(inscripcion.getEstatusAlumno()) ? "Egresado" : "Activa".equals(inscripcion.getEstado()) ? "Activo" : "Baja"%>"
                            data-carrera="<%= inscripcion.getNombreCarrera()%>"
                            data-periodo="<%= inscripcion.getNombrePeriodo()%>">
                            <td><%= inscripcion.getMatricula()%></td>
                            <td><%= inscripcion.getNombreAlumno()%></td>
                            <td><%= inscripcion.getNombreGrupo()%></td>
                            <td><%= inscripcion.getNombrePeriodo()%></td>
                            <td>
                                <% if ("Egresado".equals(inscripcion.getEstatusAlumno())) { %>
                                <span class="badge text-bg-success">Egresado</span>
                                <% } else if ("Activa".equals(inscripcion.getEstado())) { %>
                                <span class="badge text-bg-success">Activa</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary">Baja</span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <a href="STrayectorias?idAlumno=<%= inscripcion.getIdAlumno()%>" class="btn btn-sm btn-outline-formal">
                                    <i class="bi bi-signpost-split me-1"></i>Trayectoria
                                </a>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <div class="mensaje-exito mt-3" data-filtro-vacio style="display:none;">Ningún registro coincide con los filtros seleccionados.</div>
            </div>
            <% } %>
            </div>
        </main>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
    </body>
</html>
