<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
                <a href="SMaterias?nueva=1" class="btn btn-primary-formal">
                    <i class="bi bi-plus-lg me-1"></i>Nueva materia
                </a>
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

            <div class="barra-filtros" data-filtros-tabla="#tbodyMaterias">
                <div class="campo-filtro">
                    <label for="filtroEstatusMaterias">Estatus</label>
                    <select id="filtroEstatusMaterias" class="form-select form-select-sm" data-filtro-campo="estatus">
                        <option value="Activa" selected>Activas (actuales)</option>
                        <option value="">Todas</option>
                        <option value="Inactiva">Inactivas</option>
                    </select>
                </div>
                <div class="campo-filtro">
                    <label for="filtroCarreraMaterias">Carrera</label>
                    <select id="filtroCarreraMaterias" class="form-select form-select-sm" data-filtro-campo="carrera">
                        <option value="" selected>Todas</option>
                        <% java.util.LinkedHashSet<String> carrerasMaterias = new java.util.LinkedHashSet<String>();
                           for (Materia m : materias) { carrerasMaterias.add(m.getNombreCarrera()); }
                           for (String nombreCarrera : carrerasMaterias) { %>
                        <option value="<%= nombreCarrera%>"><%= nombreCarrera%></option>
                        <% } %>
                    </select>
                </div>
                <div class="campo-filtro campo-filtro-texto">
                    <label for="filtroTextoMaterias">Buscar</label>
                    <input type="text" id="filtroTextoMaterias" class="form-control form-control-sm" data-filtro-texto placeholder="Nombre de la materia...">
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Carrera / Plan / Cuatrimestre</th>
                            <th>Estatus</th>
                            <th>Temario (PDF)</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="tbodyMaterias">
                        <% for (Materia materia : materias) { %>
                        <tr data-fila-filtrable data-estatus="<%= materia.getEstatus()%>" data-carrera="<%= materia.getNombreCarrera()%>">
                            <td><%= materia.getNombreMateria()%></td>
                            <td><%= materia.getNombreCarrera()%> — <%= materia.getNombrePlan()%> — Cuatrimestre <%= materia.getNumeroCuatrimestre()%></td>
                            <td>
                                <% if ("Activa".equals(materia.getEstatus())) { %>
                                <span class="badge text-bg-success">Activa</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary">Inactiva</span>
                                <% } %>
                            </td>
                            <td>
                                <% if (materia.isTieneTemario()) { %>
                                <span class="badge text-bg-success"><i class="bi bi-file-earmark-pdf me-1"></i>Disponible</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary">Sin PDF</span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <a href="SMaterias?editar=<%= materia.getIdMateria()%>" class="btn btn-sm btn-icon-formal" title="Editar materia" aria-label="Editar materia"><i class="bi bi-pencil-square"></i></a>
                                <form method="post" action="SMaterias" class="d-inline"
                                      onsubmit="return confirm('¿Eliminar esta materia? Si ya está en un grupo, se desactivará en su lugar.');">
                                    <input type="hidden" name="accion" value="Eliminar">
                                    <input type="hidden" name="idMateria" value="<%= materia.getIdMateria()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal btn-icon-formal" title="Eliminar materia" aria-label="Eliminar materia"><i class="bi bi-trash3"></i></button>
                                </form>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <div class="mensaje-exito mt-3" data-filtro-vacio style="display:none;">Ningún registro coincide con los filtros seleccionados.</div>
            </div>
        </div>

        <div class="modal fade modal-formal" id="modalMateria" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form id="formMateria" method="post" action="SMaterias" enctype="multipart/form-data">
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
                            </form>
                            <hr>
                            <div class="mb-3">
                                <label class="form-label">Temario en PDF</label>
                                <div class="temario-upload-box">
                                <% if (materiaEditar.isTieneTemario()) { %>
                                <div class="temario-current">
                                    <div class="temario-file-icon"><i class="bi bi-file-earmark-pdf-fill"></i></div>
                                    <div class="temario-current-info">
                                        <strong>Temario actual</strong>
                                        <span><%= materiaEditar.getPdfTemario()%></span>
                                    </div>
                                    <a href="../recursos/temarios/<%= materiaEditar.getPdfTemario()%>" target="_blank" rel="noopener" class="btn btn-sm btn-outline-formal" title="Abrir PDF actual">
                                        <i class="bi bi-box-arrow-up-right"></i><span>Ver PDF</span>
                                    </a>
                                </div>
                                <p class="temario-help">Puedes reemplazarlo con una nueva versión o quitarlo.</p>
                                <div class="temario-form">
                                    <label class="temario-file-input"><i class="bi bi-cloud-arrow-up"></i><span>Seleccionar nuevo PDF</span><input form="formMateria" type="file" name="archivoPdf" accept="application/pdf"></label>
                                </div>
                                <form method="post" action="SMaterias" class="temario-remove-form" onsubmit="return confirm('¿Quitar el temario en PDF de esta materia?');">
                                    <input type="hidden" name="accion" value="QuitarPdf">
                                    <input type="hidden" name="idMateria" value="<%= materiaEditar.getIdMateria()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal"><i class="bi bi-trash3"></i>Quitar PDF</button>
                                </form>
                                <% } else { %>
                                <p class="temario-help">Adjunta el documento oficial de la materia en formato PDF.</p>
                                <div class="temario-form">
                                    <label class="temario-file-input"><i class="bi bi-cloud-arrow-up"></i><span>Seleccionar PDF</span><input form="formMateria" type="file" name="archivoPdf" accept="application/pdf"></label>
                                </div>
                                <% } %>
                                </div>
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
                            <button type="submit" form="formMateria" class="btn btn-primary-formal"><i class="bi bi-check2"></i><%= materiaEditar != null ? "Guardar cambios" : "Guardar materia"%></button>
                        </div>
                        <% if (materiaEditar == null) { %>
                        </form>
                        <% } %>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
        <script>
            document.querySelectorAll('.temario-file-input input').forEach(function (entrada) {
                entrada.addEventListener('change', function () {
                    var nombre = entrada.files.length ? entrada.files[0].name : 'Seleccionar PDF';
                    entrada.closest('.temario-file-input').querySelector('span').textContent = nombre;
                });
            });
        </script>
        <% if (materiaEditar != null || idCuatrimestrePreseleccionado != null || request.getAttribute("nuevaMateria") != null) { %>
        <script>
            new bootstrap.Modal(document.getElementById("modalMateria")).show();
        </script>
        <% } %>
            </main>
    </div>
</div>
    </body>
</html>
