<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="modelo.Materia"%>
<%@page import="modelo.PlanCuatrimestre"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Materia> materias = (ArrayList<Materia>) request.getAttribute("materias");
    ArrayList<PlanCuatrimestre> cuatrimestres = (ArrayList<PlanCuatrimestre>) request.getAttribute("cuatrimestres");
    Materia materiaEditar = (Materia) request.getAttribute("materiaEditar");
    String idCuatrimestrePreseleccionado = (String) request.getAttribute("idCuatrimestrePreseleccionado");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Materias</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <div class="d-flex justify-content-between align-items-center mt-4">
                <div>
                    <h2>Materias</h2>
                    <p class="texto-info mb-0">Cada materia se crea ligada a un solo cuatrimestre de un solo plan/carrera; ya no es un
                        catálogo reutilizable. Si la misma materia se imparte en otro cuatrimestre o carrera, da de alta un registro nuevo.</p>
                </div>
                <button class="btn btn-primary-formal" data-bs-toggle="modal" data-bs-target="#modalMateria">
                    <i class="bi bi-plus-lg me-1"></i>Nueva materia
                </button>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>
            <% if (request.getAttribute("aviso") != null) { %>
            <div class="mensaje-exito"><i class="bi bi-info-circle me-1"></i><%= request.getAttribute("aviso")%></div>
            <% } %>

            <% if (cuatrimestres.isEmpty()) { %>
            <div class="mensaje-exito mt-3">Todavía no hay planes de estudio vigentes con cuatrimestres. <a href="SPlanes">Crea uno primero</a>.</div>
            <% } %>

            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Carrera / Plan / Cuatrimestre</th>
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Materia materia : materias) { %>
                        <tr>
                            <td><%= materia.getNombreMateria()%></td>
                            <td><%= materia.getNombreCarrera()%> — <%= materia.getNombrePlan()%> — Cuatrimestre <%= materia.getNumeroCuatrimestre()%></td>
                            <td>
                                <% if ("Activa".equals(materia.getEstatus())) { %>
                                <span class="badge text-bg-success">Activa</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary">Inactiva</span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <a href="SMaterias?editar=<%= materia.getIdMateria()%>" class="btn btn-sm btn-outline-formal">Editar</a>
                                <form method="post" action="SMaterias" class="d-inline"
                                      onsubmit="return confirm('¿Eliminar esta materia? Si ya está en un grupo, se desactivará en su lugar.');">
                                    <input type="hidden" name="accion" value="Eliminar">
                                    <input type="hidden" name="idMateria" value="<%= materia.getIdMateria()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal">Eliminar</button>
                                </form>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="modal fade modal-formal" id="modalMateria" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SMaterias">
                        <div class="modal-header">
                            <h5 class="modal-title"><%= materiaEditar != null ? "Editar materia" : "Nueva materia"%></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="<%= materiaEditar != null ? "Modificar" : "Agregar"%>">
                            <% if (materiaEditar != null) { %>
                            <input type="hidden" name="idMateria" value="<%= materiaEditar.getIdMateria()%>">
                            <% } %>
                            <div class="mb-3">
                                <label class="form-label">Nombre de la materia</label>
                                <input type="text" name="tfNombreMateria" class="form-control" placeholder="Programación Orientada a Objetos"
                                       value="<%= materiaEditar != null ? materiaEditar.getNombreMateria() : ""%>" required>
                            </div>
                            <% if (materiaEditar != null) { %>
                            <div class="mb-3">
                                <label class="form-label">Carrera / Plan / Cuatrimestre</label>
                                <input type="text" class="form-control" disabled
                                       value="<%= materiaEditar.getNombreCarrera()%> — <%= materiaEditar.getNombrePlan()%> — Cuatrimestre <%= materiaEditar.getNumeroCuatrimestre()%>">
                                <div class="form-text">No se puede cambiar después del alta. Si la materia debe ir en otro cuatrimestre o carrera, crea un registro nuevo.</div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Estatus</label>
                                <select name="selEstatus" class="form-select">
                                    <option value="Activa" <%= "Activa".equals(materiaEditar.getEstatus()) ? "selected" : ""%>>Activa</option>
                                    <option value="Inactiva" <%= "Inactiva".equals(materiaEditar.getEstatus()) ? "selected" : ""%>>Inactiva</option>
                                </select>
                            </div>
                            <% } else { %>
                            <div class="mb-3">
                                <label class="form-label">Carrera / Plan / Cuatrimestre</label>
                                <select name="selPlanCuatrimestre" class="form-select" required>
                                    <option value="">Selecciona...</option>
                                    <% for (PlanCuatrimestre cuatrimestre : cuatrimestres) { %>
                                    <option value="<%= cuatrimestre.getIdPlanCuatrimestre()%>"
                                            <%= (idCuatrimestrePreseleccionado != null && idCuatrimestrePreseleccionado.equals(String.valueOf(cuatrimestre.getIdPlanCuatrimestre()))) ? "selected" : ""%>>
                                        <%= cuatrimestre.getEtiqueta()%>
                                    </option>
                                    <% } %>
                                </select>
                                <div class="form-text">Queda ligada a este cuatrimestre desde el alta y ya no se puede reutilizar en otro.</div>
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
        <% if (materiaEditar != null || idCuatrimestrePreseleccionado != null) { %>
        <script>
            new bootstrap.Modal(document.getElementById("modalMateria")).show();
        </script>
        <% } %>
    </body>
</html>
