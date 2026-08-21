<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Alumno"%>
<%@page import="modelo.PlanEstudio"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.LinkedHashSet"%>
<%@page import="java.util.Set"%>
<%
    ArrayList<Alumno> alumnos = (ArrayList<Alumno>) request.getAttribute("alumnos");
    ArrayList<PlanEstudio> planes = (ArrayList<PlanEstudio>) request.getAttribute("planes");
    Alumno alumnoEditar = (Alumno) request.getAttribute("alumnoEditar");
    Set<String> carrerasAlumnos = new LinkedHashSet<>();
    Set<String> gruposAlumnos = new LinkedHashSet<>();
    for (Alumno alumno : alumnos) {
        carrerasAlumnos.add(alumno.getNombreCarrera());
        gruposAlumnos.add(alumno.getNombreGrupo());
    }
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
            <div class="barra-filtros" data-filtros-tabla="#tbodyAlumnos">
                <div class="campo-filtro">
                    <label for="filtroEstatusAlumnos">Estatus</label>
                    <select id="filtroEstatusAlumnos" class="form-select form-select-sm" data-filtro-campo="estatus">
                        <option value="Activo" selected>Activos (actuales)</option>
                        <option value="">Todos</option>
                        <option value="BajaTemporal">Baja temporal</option>
                        <option value="BajaDefinitiva">Baja definitiva</option>
                        <option value="Egresado">Egresados</option>
                    </select>
                </div>
                <div class="campo-filtro">
                    <label for="filtroCarreraAlumnos">Carrera</label>
                    <select id="filtroCarreraAlumnos" class="form-select form-select-sm" data-filtro-campo="carrera">
                        <option value="" selected>Todas</option>
                        <% for (String carrera : carrerasAlumnos) { %>
                        <option value="<%= carrera%>"><%= carrera%></option>
                        <% } %>
                    </select>
                </div>
                <div class="campo-filtro">
                    <label for="filtroGrupoAlumnos">Grupo</label>
                    <select id="filtroGrupoAlumnos" class="form-select form-select-sm" data-filtro-campo="grupo">
                        <option value="" selected>Todos</option>
                        <% for (String grupo : gruposAlumnos) { %>
                        <option value="<%= grupo%>"><%= grupo%></option>
                        <% } %>
                    </select>
                </div>
                <div class="campo-filtro campo-filtro-texto">
                    <label for="filtroTextoAlumnos">Buscar</label>
                    <input type="text" id="filtroTextoAlumnos" class="form-control form-control-sm" data-filtro-texto placeholder="Nombre o correo...">
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Correo</th>
                            <th>Carrera</th>
                            <th>Grupo</th>
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="tbodyAlumnos">
                        <% for (Alumno alumno : alumnos) { %>
                        <tr data-fila-filtrable data-estatus="<%= alumno.getEstatus()%>" data-carrera="<%= alumno.getNombreCarrera()%>" data-grupo="<%= alumno.getNombreGrupo()%>">
                            <td><%= alumno.getNombreCompleto()%></td>
                            <td><%= alumno.getCorreo()%></td>
                            <td><%= alumno.getNombreCarrera()%></td>
                            <td><%= alumno.getNombreGrupo()%></td>
                            <td>
                                <% if ("Activo".equals(alumno.getEstatus())) { %>
                                <span class="badge text-bg-success">Activo</span>
                                <% } else if ("BajaTemporal".equals(alumno.getEstatus())) { %>
                                <span class="badge text-bg-warning">Baja temporal</span>
                                <% } else if ("BajaDefinitiva".equals(alumno.getEstatus())) { %>
                                <span class="badge text-bg-secondary">Baja definitiva</span>
                                <% } else { %>
                                <span class="badge text-bg-success">Egresado</span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <a href="SAlumnos?editar=<%= alumno.getIdAlumno()%>" class="btn btn-sm btn-icon-formal" title="Editar alumno" aria-label="Editar alumno"><i class="bi bi-pencil-square"></i></a>
                                <a href="STrayectorias?idAlumno=<%= alumno.getIdAlumno()%>" class="btn btn-sm btn-icon-formal" title="Ver trayectorias" aria-label="Ver trayectorias"><i class="bi bi-signpost-split"></i></a>
                                <% if ("Activo".equals(alumno.getEstatus())) { %>
                                <form method="post" action="SAlumnos" class="d-inline">
                                    <input type="hidden" name="accion" value="BajaTemporal">
                                    <input type="hidden" name="idAlumno" value="<%= alumno.getIdAlumno()%>">
                                    <button type="submit" class="btn btn-sm btn-icon-formal" title="Dar de baja temporal" aria-label="Dar de baja temporal"><i class="bi bi-pause-circle"></i></button>
                                </form>
                                <form method="post" action="SAlumnos" class="d-inline"
                                      onsubmit="return confirm('¿Dar de baja definitiva a este alumno? Ya no podrá inscribirse nunca más, salvo que se revierta por error. Su historial académico se conserva.');">
                                    <input type="hidden" name="accion" value="BajaDefinitiva">
                                    <input type="hidden" name="idAlumno" value="<%= alumno.getIdAlumno()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal btn-icon-formal" title="Dar de baja definitiva" aria-label="Dar de baja definitiva"><i class="bi bi-person-x"></i></button>
                                </form>
                                <% } else if ("BajaTemporal".equals(alumno.getEstatus())) { %>
                                <form method="post" action="SAlumnos" class="d-inline">
                                    <input type="hidden" name="accion" value="Reactivar">
                                    <input type="hidden" name="idAlumno" value="<%= alumno.getIdAlumno()%>">
                                    <button type="submit" class="btn btn-sm btn-primary-formal btn-icon-formal" title="Reactivar alumno" aria-label="Reactivar alumno"><i class="bi bi-person-check"></i></button>
                                </form>
                                <form method="post" action="SAlumnos" class="d-inline"
                                      onsubmit="return confirm('¿Dar de baja definitiva a este alumno? Ya no podrá inscribirse nunca más, salvo que se revierta por error.');">
                                    <input type="hidden" name="accion" value="BajaDefinitiva">
                                    <input type="hidden" name="idAlumno" value="<%= alumno.getIdAlumno()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal btn-icon-formal" title="Dar de baja definitiva" aria-label="Dar de baja definitiva"><i class="bi bi-person-x"></i></button>
                                </form>
                                <% } else if ("BajaDefinitiva".equals(alumno.getEstatus())) { %>
                                <form method="post" action="SAlumnos" class="d-inline"
                                      onsubmit="return confirm('¿Revertir la baja definitiva? Úsalo solo para corregir un error de captura.');">
                                    <input type="hidden" name="accion" value="RevertirBaja">
                                    <input type="hidden" name="idAlumno" value="<%= alumno.getIdAlumno()%>">
                                    <button type="submit" class="btn btn-sm btn-icon-formal" title="Revertir baja definitiva" aria-label="Revertir baja definitiva"><i class="bi bi-arrow-counterclockwise"></i></button>
                                </form>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <div class="mensaje-exito mt-3" data-filtro-vacio style="display:none;">Ningún registro coincide con los filtros seleccionados.</div>
            </div>
            <% } %>
        </div>

        <div class="modal fade modal-formal" id="modalAlumno" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SAlumnos">
                        <div class="modal-header">
                            <h5 class="modal-title"><%= alumnoEditar == null ? "Nuevo alumno" : "Editar alumno"%></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="<%= alumnoEditar == null ? "Agregar" : "Modificar"%>">
                            <% if (alumnoEditar != null) { %><input type="hidden" name="idAlumno" value="<%= alumnoEditar.getIdAlumno()%>"><input type="hidden" name="idPersona" value="<%= alumnoEditar.getIdPersona()%>"><% } %>
                            <div class="mb-3">
                                <label class="form-label">Nombres</label>
                                <input type="text" name="tfNombres" class="form-control" value="<%= alumnoEditar == null ? "" : alumnoEditar.getNombres()%>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Apellido paterno</label>
                                <input type="text" name="tfApellidoPaterno" class="form-control" value="<%= alumnoEditar == null ? "" : alumnoEditar.getApellidoPaterno()%>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Apellido materno</label>
                                <input type="text" name="tfApellidoMaterno" class="form-control" value="<%= alumnoEditar == null ? "" : alumnoEditar.getApellidoMaterno()%>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Correo</label>
                                <input type="email" name="tfCorreo" class="form-control" value="<%= alumnoEditar == null ? "" : alumnoEditar.getCorreo()%>" required>
                            </div>
                            <% if (alumnoEditar == null) { %><hr>
                            <div class="mb-3">
                                <label class="form-label">Carrera / Plan</label>
                                <select name="selPlan" class="form-select" required>
                                    <% for (PlanEstudio plan : planes) { %>
                                    <option value="<%= plan.getIdPlan()%>"><%= plan.getNombreCarrera()%> — <%= plan.getNombrePlan()%> (v<%= plan.getVersion()%>)</option>
                                    <% } %>
                                </select>
                            </div><% } %>
                            <% if (alumnoEditar == null) { %>
                            <div class="mb-3">
                                <label class="form-label">Matrícula</label>
                                <input type="text" name="tfMatricula" class="form-control" placeholder="123456" required>
                                <div class="form-text">Convención sugerida: TSU sin sufijo, Ingeniería "_i", Licenciatura "_l" (ej. 123456_i).</div>
                            </div>
                            <% } %>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary-formal"><%= alumnoEditar == null ? "Guardar" : "Actualizar"%></button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
        <% if (alumnoEditar != null) { %><script>new bootstrap.Modal(document.getElementById("modalAlumno")).show();</script><% } %>
            </main>
    </div>
</div>
    </body>
</html>
