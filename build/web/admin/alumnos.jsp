<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="modelo.Alumno"%>
<%@page import="modelo.PlanEstudio"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Alumno> alumnos = (ArrayList<Alumno>) request.getAttribute("alumnos");
    ArrayList<PlanEstudio> planes = (ArrayList<PlanEstudio>) request.getAttribute("planes");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Alumnos</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <div class="d-flex justify-content-between align-items-center mt-4">
                <div>
                    <h2>Alumnos</h2>
                    <p class="texto-info mb-0">El alta del alumno y su primera trayectoria (carrera, plan y matrícula) se hacen juntas en un solo formulario: la matrícula es obligatoria desde el inicio.</p>
                </div>
                <% if (!planes.isEmpty()) { %>
                <button class="btn btn-primary-formal" data-bs-toggle="modal" data-bs-target="#modalAlumno">
                    <i class="bi bi-plus-lg me-1"></i>Nuevo alumno
                </button>
                <% } %>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>
            <% if (planes.isEmpty()) { %>
            <div class="mensaje-exito mt-3">Todavía no hay planes de estudio vigentes. <a href="SPlanes">Crea uno primero</a> para poder dar de alta alumnos.</div>
            <% } %>

            <% if (alumnos.isEmpty()) { %>
            <div class="mensaje-exito mt-4">No hay alumnos registrados todavía.</div>
            <% } else { %>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Correo</th>
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Alumno alumno : alumnos) { %>
                        <tr>
                            <td><%= alumno.getNombreCompleto()%></td>
                            <td><%= alumno.getCorreo()%></td>
                            <td>
                                <% if ("Activo".equals(alumno.getEstatus())) { %>
                                <span class="badge text-bg-success">Activo</span>
                                <% } else if ("Pausado".equals(alumno.getEstatus())) { %>
                                <span class="badge text-bg-warning">Pausado</span>
                                <% } else if ("Egresado".equals(alumno.getEstatus())) { %>
                                <span class="badge text-bg-primary">Egresado</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary">Baja</span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <a href="STrayectorias?idAlumno=<%= alumno.getIdAlumno()%>" class="btn btn-sm btn-outline-formal">Trayectorias</a>
                                <% if (!"Baja".equals(alumno.getEstatus())) { %>
                                <form method="post" action="SAlumnos" class="d-inline"
                                      onsubmit="return confirm('¿Dar de baja a este alumno? Su historial académico se conserva.');">
                                    <input type="hidden" name="accion" value="Baja">
                                    <input type="hidden" name="idAlumno" value="<%= alumno.getIdAlumno()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal">Baja</button>
                                </form>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% } %>
        </div>

        <div class="modal fade modal-formal" id="modalAlumno" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SAlumnos">
                        <div class="modal-header">
                            <h5 class="modal-title">Nuevo alumno</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="Agregar">
                            <div class="mb-3">
                                <label class="form-label">Nombres</label>
                                <input type="text" name="tfNombres" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Apellido paterno</label>
                                <input type="text" name="tfApellidoPaterno" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Apellido materno</label>
                                <input type="text" name="tfApellidoMaterno" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Correo</label>
                                <input type="email" name="tfCorreo" class="form-control" required>
                            </div>
                            <hr>
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
