<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.PlanEstudio"%>
<%@page import="modelo.Carrera"%>
<%@page import="modelo.NivelAcademico"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<PlanEstudio> planes = (ArrayList<PlanEstudio>) request.getAttribute("planes");
    ArrayList<Carrera> carreras = (ArrayList<Carrera>) request.getAttribute("carreras");
    ArrayList<NivelAcademico> niveles = (ArrayList<NivelAcademico>) request.getAttribute("niveles");
    PlanEstudio planEditar = (PlanEstudio) request.getAttribute("planEditar");
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
            <div class="barra-filtros" data-filtros-tabla="#tbodyPlanes">
                <div class="campo-filtro">
                    <label for="filtroEstatusPlanes">Estatus</label>
                    <select id="filtroEstatusPlanes" class="form-select form-select-sm" data-filtro-campo="estatus">
                        <option value="Vigente" selected>Vigentes (actuales)</option>
                        <option value="">Todos</option>
                        <option value="Cerrado">Cerrados</option>
                    </select>
                </div>
                <div class="campo-filtro">
                    <label for="filtroCarreraPlanes">Carrera</label>
                    <select id="filtroCarreraPlanes" class="form-select form-select-sm" data-filtro-campo="carrera">
                        <option value="" selected>Todas</option>
                        <% java.util.LinkedHashSet<String> carrerasPlanes = new java.util.LinkedHashSet<String>();
                           for (PlanEstudio pl : planes) { carrerasPlanes.add(pl.getNombreCarrera()); }
                           for (String nombreCarrera : carrerasPlanes) { %>
                        <option value="<%= nombreCarrera%>"><%= nombreCarrera%></option>
                        <% } %>
                    </select>
                </div>
                <div class="campo-filtro campo-filtro-texto">
                    <label for="filtroTextoPlanes">Buscar</label>
                    <input type="text" id="filtroTextoPlanes" class="form-control form-control-sm" data-filtro-texto placeholder="Plan, versión...">
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>
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
                    <tbody id="tbodyPlanes">
                        <% for (PlanEstudio plan : planes) { %>
                        <tr data-fila-filtrable data-estatus="<%= "Vigente".equals(plan.getEstatus()) ? "Vigente" : "Cerrado"%>" data-carrera="<%= plan.getNombreCarrera()%>">
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
                                <% if (!"Cerrado".equals(plan.getEstatus())) { %><a href="SPlanes?editar=<%= plan.getIdPlan()%>" class="btn btn-sm btn-icon-formal" title="Editar plan" aria-label="Editar plan"><i class="bi bi-pencil-square"></i></a><% } %>
                                <a href="SCurriculo?idPlan=<%= plan.getIdPlan()%>" class="btn btn-sm btn-icon-formal" title="Ver currículo" aria-label="Ver currículo"><i class="bi bi-journal-text"></i></a>
                                <% if ("Vigente".equals(plan.getEstatus())) { %>
                                <form method="post" action="SPlanes" class="d-inline"
                                      onsubmit="return confirm('¿Cerrar este plan? Ya no se podrán crear grupos nuevos con él, pero su historial se conserva.');">
                                    <input type="hidden" name="accion" value="Cerrar">
                                    <input type="hidden" name="idPlan" value="<%= plan.getIdPlan()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal btn-icon-formal" title="Cerrar plan" aria-label="Cerrar plan"><i class="bi bi-lock"></i></button>
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

        <div class="modal fade modal-formal" id="modalPlan" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <form method="post" action="SPlanes" id="formPlan">
                        <div class="modal-header">
                            <h5 class="modal-title"><%= planEditar == null ? "Nuevo plan de estudios" : "Editar plan de estudios"%></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="<%= planEditar == null ? "Agregar" : "Modificar"%>">
                            <% if (planEditar != null) { %><input type="hidden" name="idPlan" value="<%= planEditar.getIdPlan()%>"><% } %>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Carrera</label>
                                    <select name="selCarrera" class="form-select" required>
                                        <% for (Carrera carrera : carreras) { %>
                                        <option value="<%= carrera.getIdCarrera()%>" <%= planEditar != null && planEditar.getIdCarrera() == carrera.getIdCarrera() ? "selected" : ""%>><%= carrera.getNombreCarrera()%></option>
                                        <% } %>
                                    </select>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Nombre del plan</label>
                                    <input type="text" name="tfNombrePlan" class="form-control" placeholder="Plan de estudios 2026" value="<%= planEditar == null ? "" : planEditar.getNombrePlan()%>" required>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label class="form-label">Versión</label>
                                    <input type="text" name="tfVersion" class="form-control" placeholder="2026-1" value="<%= planEditar == null ? "" : planEditar.getVersion()%>" required>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label class="form-label">Duración (cuatrimestres)</label>
                                    <input type="number" min="1" max="20" name="tfDuracion" id="tfDuracion" class="form-control" placeholder="11" value="<%= planEditar == null ? "" : planEditar.getDuracionCuatrimestres()%>" required>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label class="form-label">Vigente desde</label>
                                    <input type="date" name="tfFechaVigencia" class="form-control" value="<%= planEditar == null ? "" : planEditar.getFechaVigenciaInicio()%>" required>
                                </div>
                            </div>

                            <% if (planEditar == null) { %><hr>
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <label class="form-label mb-0">Niveles dentro del plan <span class="text-danger">*</span></label>
                                <button type="button" class="btn btn-sm btn-outline-formal" id="btnAgregarTramo">
                                    <i class="bi bi-plus-lg me-1"></i>Agregar tramo
                                </button>
                            </div>
                            <p class="texto-info">Ejemplo: TSU del cuatrimestre 1 al 6, Ingeniería del 7 al 11. Indica también el título que se otorga en cada tramo
                                (si en tu plan el TSU y la Ingeniería llevan nombres distintos, aquí es donde se captura cada uno; si es el mismo nombre en ambos, repítelo). <strong>Obligatorio:</strong> un plan no puede quedar Vigente sin al menos un nivel académico definido.</p>
                            <div id="contenedorTramos"></div><% } %>
                            <div id="avisoSinTramos" class="mensaje-error d-none">
                                <i class="bi bi-exclamation-triangle me-1"></i>Agrega al menos un nivel académico antes de guardar el plan; sin esto el plan no puede quedar Vigente.
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary-formal" id="btnGuardarPlan"><%= planEditar == null ? "Guardar y continuar con el currículo" : "Actualizar"%></button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <template id="plantillaTramo">
            <div class="row tramo-nivel align-items-end mb-3 pb-2 border-bottom">
                <div class="col-md-4">
                    <label class="form-label small">Nivel académico</label>
                    <select name="selNivel[]" class="form-select" required>
                        <% for (NivelAcademico nivel : niveles) { %>
                        <option value="<%= nivel.getIdNivel()%>"><%= nivel.getNombreNivel()%></option>
                        <% } %>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label small">Desde</label>
                    <input type="number" min="1" name="tfInicio[]" class="form-control" placeholder="Desde" required>
                </div>
                <div class="col-md-2">
                    <label class="form-label small">Hasta</label>
                    <input type="number" min="1" name="tfFin[]" class="form-control" placeholder="Hasta" required>
                </div>
                <div class="col-md-3">
                    <label class="form-label small">Título que se otorga</label>
                    <input type="text" name="tfTitulo[]" class="form-control" placeholder="Ej. Ingeniería en Desarrollo de Software Multiplataforma" required>
                </div>
                <div class="col-md-1">
                    <button type="button" class="btn btn-sm btn-danger-formal btn-quitar-tramo"><i class="bi bi-x-lg"></i></button>
                </div>
            </div>
        </template>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
        <% if (planEditar != null) { %><script>new bootstrap.Modal(document.getElementById("modalPlan")).show();</script><% } %>
        <% if (planEditar == null) { %>
        <script>
            const contenedorTramos = document.getElementById("contenedorTramos");
            const plantillaTramo = document.getElementById("plantillaTramo");
            const avisoSinTramos = document.getElementById("avisoSinTramos");
            const formPlan = document.getElementById("formPlan");
            const btnGuardarPlan = document.getElementById("btnGuardarPlan");

            function agregarTramo()
            {
                contenedorTramos.appendChild(plantillaTramo.content.cloneNode(true));
                actualizarEstadoTramos();
            }


            function actualizarEstadoTramos()
            {
                const filas = contenedorTramos.querySelectorAll(".tramo-nivel");
                const hayTramos = filas.length > 0;

                filas.forEach(function (fila)
                {
                    const botonQuitar = fila.querySelector(".btn-quitar-tramo");
                    botonQuitar.disabled = filas.length === 1;
                });

                avisoSinTramos.classList.toggle("d-none", hayTramos);
                btnGuardarPlan.disabled = !hayTramos;

                return hayTramos;
            }

            document.getElementById("btnAgregarTramo").addEventListener("click", agregarTramo);
            contenedorTramos.addEventListener("click", function (evento)
            {
                const boton = evento.target.closest(".btn-quitar-tramo");
                if (boton && !boton.disabled)
                {
                    boton.closest(".tramo-nivel").remove();
                    actualizarEstadoTramos();
                }
            });

            formPlan.addEventListener("submit", function (evento)
            {
                if (!actualizarEstadoTramos())
                {
                    evento.preventDefault();
                    avisoSinTramos.scrollIntoView({behavior: "smooth", block: "center"});
                }
            });

            agregarTramo();
        </script>
        <% } %>
            </main>
    </div>
</div>
    </body>
</html>
