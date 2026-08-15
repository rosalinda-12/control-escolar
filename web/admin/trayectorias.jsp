<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="modelo.Alumno"%>
<%@page import="modelo.TrayectoriaAcademica"%>
<%@page import="modelo.PlanEstudio"%>
<%@page import="java.util.ArrayList"%>
<%
    Alumno alumno = (Alumno) request.getAttribute("alumno");
    ArrayList<TrayectoriaAcademica> trayectorias = (ArrayList<TrayectoriaAcademica>) request.getAttribute("trayectorias");
    ArrayList<PlanEstudio> planes = (ArrayList<PlanEstudio>) request.getAttribute("planes");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Trayectorias</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <div class="d-flex justify-content-between align-items-center mt-4">
                <div>
                    <h2>Trayectoria de <%= alumno.getNombreCompleto()%></h2>
                    <p class="texto-info mb-0">Orden cronológico. Una trayectoria nueva no borra ni modifica las anteriores.</p>
                </div>
                <% if (!planes.isEmpty()) { %>
                <button class="btn btn-primary-formal" data-bs-toggle="modal" data-bs-target="#modalTrayectoria">
                    <i class="bi bi-plus-lg me-1"></i>Registrar cambio de carrera/plan
                </button>
                <% } %>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (trayectorias.isEmpty()) { %>
            <div class="mensaje-exito mt-4">Este alumno todavía no tiene ninguna trayectoria registrada.</div>
            <% } else { %>
            <div class="mt-4">
                <% for (TrayectoriaAcademica trayectoria : trayectorias) { %>
                <div class="card-formal mb-3">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <h5 class="mb-1"><%= trayectoria.getNombreCarrera()%> — <%= trayectoria.getNombrePlan()%></h5>
                            <p class="mb-1">Matrícula: <span class="badge text-bg-secondary"><%= trayectoria.getMatricula()%></span></p>
                            <p class="texto-info mb-0">
                                Desde <%= trayectoria.getFechaInicio()%>
                                <% if (trayectoria.getFechaFin() != null) { %>
                                hasta <%= trayectoria.getFechaFin()%>
                                <% } %>
                            </p>
                        </div>
                        <div class="text-end">
                            <% if ("ACTIVA".equals(trayectoria.getEstado())) { %>
                            <span class="badge text-bg-success mb-2">Activa</span><br>
                            <form method="post" action="STrayectorias" class="d-inline">
                                <input type="hidden" name="accion" value="Pausar">
                                <input type="hidden" name="idAlumno" value="<%= alumno.getIdAlumno()%>">
                                <input type="hidden" name="idTrayectoria" value="<%= trayectoria.getIdTrayectoria()%>">
                                <button type="submit" class="btn btn-sm btn-outline-formal">Pausar</button>
                            </form>
                            <form method="post" action="STrayectorias" class="d-inline">
                                <input type="hidden" name="accion" value="Cerrar">
                                <input type="hidden" name="idAlumno" value="<%= alumno.getIdAlumno()%>">
                                <input type="hidden" name="idTrayectoria" value="<%= trayectoria.getIdTrayectoria()%>">
                                <button type="submit" class="btn btn-sm btn-danger-formal">Cerrar</button>
                            </form>
                            <% } else if ("PAUSADA".equals(trayectoria.getEstado())) { %>
                            <span class="badge text-bg-warning mb-2">Pausada</span><br>
                            <form method="post" action="STrayectorias" class="d-inline">
                                <input type="hidden" name="accion" value="Reanudar">
                                <input type="hidden" name="idAlumno" value="<%= alumno.getIdAlumno()%>">
                                <input type="hidden" name="idTrayectoria" value="<%= trayectoria.getIdTrayectoria()%>">
                                <button type="submit" class="btn btn-sm btn-primary-formal">Reanudar</button>
                            </form>
                            <% } else if ("CAMBIO".equals(trayectoria.getEstado())) { %>
                            <span class="badge text-bg-info">Continuó en otra trayectoria</span>
                            <% } else { %>
                            <span class="badge text-bg-secondary">Cerrada</span>
                            <% } %>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>
            <% } %>
        </div>

        <div class="modal fade modal-formal" id="modalTrayectoria" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="STrayectorias">
                        <div class="modal-header">
                            <h5 class="modal-title">Registrar cambio de carrera/plan</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="Agregar">
                            <input type="hidden" name="idAlumno" value="<%= alumno.getIdAlumno()%>">
                            <div class="mb-3">
                                <label class="form-label">Carrera / Plan</label>
                                <select name="selPlan" class="form-select" required>
                                    <% for (PlanEstudio plan : planes) { %>
                                    <option value="<%= plan.getIdPlan()%>"><%= plan.getNombreCarrera()%> — <%= plan.getNombrePlan()%> (v<%= plan.getVersion()%>)</option>
                                    <% } %>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Matrícula</label>
                                <input type="text" name="tfMatricula" class="form-control" placeholder="123456" required>
                                <div class="form-text">Convención sugerida: TSU sin sufijo, Ingeniería "_i", Licenciatura "_l" (ej. 123456_i).</div>
                            </div>
                            <% if (!trayectorias.isEmpty()) { %>
                            <div class="mb-3">
                                <label class="form-label">¿Continúa una trayectoria anterior?</label>
                                <select name="selTrayectoriaAnterior" class="form-select">
                                    <option value="">No, es independiente</option>
                                    <% for (TrayectoriaAcademica trayectoria : trayectorias) { %>
                                    <% if ("ACTIVA".equals(trayectoria.getEstado()) || "PAUSADA".equals(trayectoria.getEstado())) { %>
                                    <option value="<%= trayectoria.getIdTrayectoria()%>"><%= trayectoria.getMatricula()%> — <%= trayectoria.getNombreCarrera()%></option>
                                    <% } %>
                                    <% } %>
                                </select>
                                <div class="form-text">Si la eliges, esa trayectoria se cerrará automáticamente (ej. pasó de TSU a Ingeniería).</div>
                            </div>
                            <% } %>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary-formal">Guardar</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
    </body>
</html>
