<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="modelo.PlanEstudio"%>
<%@page import="modelo.Carrera"%>
<%@page import="modelo.NivelAcademico"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<PlanEstudio> planes = (ArrayList<PlanEstudio>) request.getAttribute("planes");
    ArrayList<Carrera> carreras = (ArrayList<Carrera>) request.getAttribute("carreras");
    ArrayList<NivelAcademico> niveles = (ArrayList<NivelAcademico>) request.getAttribute("niveles");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Planes de estudio</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <div class="d-flex justify-content-between align-items-center mt-4">
                <div>
                    <h2>Planes de estudio</h2>
                    <p class="texto-info mb-0">Una carrera puede tener varios planes. Un plan viejo se cierra, nunca se edita, para no alterar el historial de quienes ya lo cursaron.</p>
                </div>
                <% if (carreras.isEmpty()) { %>
                <a href="SCarreras" class="btn btn-primary-formal"><i class="bi bi-plus-lg me-1"></i>Crea una carrera primero</a>
                <% } else { %>
                <button class="btn btn-primary-formal" data-bs-toggle="modal" data-bs-target="#modalPlan">
                    <i class="bi bi-plus-lg me-1"></i>Nuevo plan
                </button>
                <% } %>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (planes.isEmpty()) { %>
            <div class="mensaje-exito mt-4">No hay planes de estudio registrados todavía.</div>
            <% } else { %>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Carrera</th>
                            <th>Plan</th>
                            <th>Versión</th>
                            <th>Duración</th>
                            <th>Vigencia desde</th>
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (PlanEstudio plan : planes) { %>
                        <tr>
                            <td><%= plan.getNombreCarrera()%></td>
                            <td><%= plan.getNombrePlan()%></td>
                            <td><span class="badge text-bg-secondary"><%= plan.getVersion()%></span></td>
                            <td><%= plan.getDuracionCuatrimestres()%> cuatrimestres</td>
                            <td><%= plan.getFechaVigenciaInicio()%></td>
                            <td>
                                <% if ("Vigente".equals(plan.getEstatus())) { %>
                                <span class="badge text-bg-success">Vigente</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary"><%= plan.getEstatus()%></span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <a href="SCurriculo?idPlan=<%= plan.getIdPlan()%>" class="btn btn-sm btn-outline-formal">Currículo</a>
                                <% if ("Vigente".equals(plan.getEstatus())) { %>
                                <form method="post" action="SPlanes" class="d-inline"
                                      onsubmit="return confirm('¿Cerrar este plan? Ya no se podrán crear grupos nuevos con él, pero su historial se conserva.');">
                                    <input type="hidden" name="accion" value="Cerrar">
                                    <input type="hidden" name="idPlan" value="<%= plan.getIdPlan()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal">Cerrar</button>
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

        <div class="modal fade modal-formal" id="modalPlan" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <form method="post" action="SPlanes" id="formPlan">
                        <div class="modal-header">
                            <h5 class="modal-title">Nuevo plan de estudios</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="Agregar">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Carrera</label>
                                    <select name="selCarrera" class="form-select" required>
                                        <% for (Carrera carrera : carreras) { %>
                                        <option value="<%= carrera.getIdCarrera()%>"><%= carrera.getNombreCarrera()%></option>
                                        <% } %>
                                    </select>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Nombre del plan</label>
                                    <input type="text" name="tfNombrePlan" class="form-control" placeholder="Plan de estudios 2026" required>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label class="form-label">Versión</label>
                                    <input type="text" name="tfVersion" class="form-control" placeholder="2026-1" required>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label class="form-label">Duración (cuatrimestres)</label>
                                    <input type="number" min="1" max="20" name="tfDuracion" id="tfDuracion" class="form-control" placeholder="11" required>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label class="form-label">Vigente desde</label>
                                    <input type="date" name="tfFechaVigencia" class="form-control" required>
                                </div>
                            </div>

                            <hr>
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <label class="form-label mb-0">Tramos de nivel académico</label>
                                <button type="button" class="btn btn-sm btn-outline-formal" id="btnAgregarTramo">
                                    <i class="bi bi-plus-lg me-1"></i>Agregar tramo
                                </button>
                            </div>
                            <p class="texto-info">Ejemplo: TSU del cuatrimestre 1 al 6, Ingeniería del 7 al 11.</p>
                            <div id="contenedorTramos"></div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary-formal">Guardar y continuar con el currículo</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <template id="plantillaTramo">
            <div class="row tramo-nivel align-items-end mb-2">
                <div class="col-md-5">
                    <select name="selNivel[]" class="form-select" required>
                        <% for (NivelAcademico nivel : niveles) { %>
                        <option value="<%= nivel.getIdNivel()%>"><%= nivel.getNombreNivel()%></option>
                        <% } %>
                    </select>
                </div>
                <div class="col-md-3">
                    <input type="number" min="1" name="tfInicio[]" class="form-control" placeholder="Desde" required>
                </div>
                <div class="col-md-3">
                    <input type="number" min="1" name="tfFin[]" class="form-control" placeholder="Hasta" required>
                </div>
                <div class="col-md-1">
                    <button type="button" class="btn btn-sm btn-danger-formal btn-quitar-tramo"><i class="bi bi-x-lg"></i></button>
                </div>
            </div>
        </template>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
        <script>
            const contenedorTramos = document.getElementById("contenedorTramos");
            const plantillaTramo = document.getElementById("plantillaTramo");

            function agregarTramo()
            {
                contenedorTramos.appendChild(plantillaTramo.content.cloneNode(true));
            }

            document.getElementById("btnAgregarTramo").addEventListener("click", agregarTramo);
            contenedorTramos.addEventListener("click", function (evento)
            {
                const boton = evento.target.closest(".btn-quitar-tramo");
                if (boton)
                {
                    boton.closest(".tramo-nivel").remove();
                }
            });

            agregarTramo();
        </script>
    </body>
</html>
