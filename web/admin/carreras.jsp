<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Carrera"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Carrera> carreras = (ArrayList<Carrera>) request.getAttribute("carreras");
    Carrera carreraEditar = (Carrera) request.getAttribute("carreraEditar");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Carreras</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <div class="d-flex justify-content-between align-items-center mt-4">
                <div>
                    <h2>Carreras</h2>
                    <p class="texto-info mb-0">Da de alta cada carrera que ofrece la institución. Es el primer paso antes de crear un plan de estudios.</p>
                </div>
                <a href="SCarreras?nuevo=1" class="btn btn-primary-formal">
                    <i class="bi bi-plus-lg me-1"></i>Nueva carrera
                </a>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>
            <% if (request.getAttribute("aviso") != null) { %>
            <div class="mensaje-exito"><i class="bi bi-info-circle me-1"></i><%= request.getAttribute("aviso")%></div>
            <% } %>

            <% if (carreras.isEmpty()) { %>
            <div class="mensaje-exito mt-4">
                <i class="bi bi-signpost-split me-1"></i>
                Todavía no hay carreras registradas. Da de alta la primera con el botón "Nueva carrera" para poder
                crear después sus planes de estudio.
            </div>
            <% } else { %>
            <div class="barra-filtros" data-filtros-tabla="#tbodyCarreras">
                <div class="campo-filtro">
                    <label for="filtroEstatusCarreras">Estatus</label>
                    <select id="filtroEstatusCarreras" class="form-select form-select-sm" data-filtro-campo="estatus">
                        <option value="Activa" selected>Activas (actuales)</option>
                        <option value="">Todas</option>
                        <option value="Inactiva">Inactivas</option>
                    </select>
                </div>
                <div class="campo-filtro campo-filtro-texto">
                    <label for="filtroTextoCarreras">Buscar</label>
                    <input type="text" id="filtroTextoCarreras" class="form-control form-control-sm" data-filtro-texto placeholder="Nombre o clave...">
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Clave</th>
                            <th>Nombre</th>
                            <th>Nivel(es)</th>
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="tbodyCarreras">
                        <% for (Carrera carrera : carreras) { %>
                        <tr data-fila-filtrable data-estatus="<%= carrera.getEstatus()%>">
                            <td><span class="badge text-bg-secondary"><%= carrera.getClaveCarrera()%></span></td>
                            <td><%= carrera.getNombreCarrera()%></td>
                            <td>
                                <% if (carrera.getNivelesTexto() != null && !carrera.getNivelesTexto().isBlank()) { %>
                                <%= carrera.getNivelesTexto()%>
                                <% } else { %>
                                <span class="texto-info">Sin plan vigente</span>
                                <% } %>
                            </td>
                            <td>
                                <% if ("Activa".equals(carrera.getEstatus())) { %>
                                <span class="badge text-bg-success">Activa</span>
                                <% } else { %>
                                <span class="badge text-bg-secondary">Inactiva</span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <a href="SCarreras?editar=<%= carrera.getIdCarrera()%>" class="btn btn-sm btn-icon-formal" title="Editar carrera" aria-label="Editar carrera"><i class="bi bi-pencil-square"></i></a>
                                <form method="post" action="SCarreras" class="d-inline"
                                      onsubmit="return confirm('¿Eliminar esta carrera? Si ya tiene planes de estudio registrados, en su lugar se desactivará.');">
                                    <input type="hidden" name="accion" value="Eliminar">
                                    <input type="hidden" name="idCarrera" value="<%= carrera.getIdCarrera()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal btn-icon-formal" title="Eliminar carrera" aria-label="Eliminar carrera"><i class="bi bi-trash3"></i></button>
                                </form>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <div class="mensaje-exito mt-3" data-filtro-vacio style="display:none;">Ningún registro coincide con los filtros seleccionados.</div>
            </div>
            <% } %>
        </div>

        <div class="modal fade modal-formal" id="modalCarrera" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SCarreras">
                        <div class="modal-header">
                            <h5 class="modal-title"><%= carreraEditar != null ? "Editar carrera" : "Nueva carrera"%></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="<%= carreraEditar != null ? "Modificar" : "Agregar"%>">
                            <% if (carreraEditar != null) { %>
                            <input type="hidden" name="idCarrera" value="<%= carreraEditar.getIdCarrera()%>">
                            <% } %>
                            <div class="mb-3">
                                <label class="form-label">Nombre de la carrera</label>
                                <input type="text" name="tfNombreCarrera" class="form-control" placeholder="Ingeniería en Desarrollo de Software"
                                       value="<%= carreraEditar != null ? carreraEditar.getNombreCarrera() : ""%>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Clave</label>
                                <input type="text" name="tfClaveCarrera" class="form-control text-uppercase" placeholder="IDS" maxlength="20"
                                       value="<%= carreraEditar != null ? carreraEditar.getClaveCarrera() : ""%>" required>
                                <div class="form-text">Se usa como identificador corto, por ejemplo en matrículas y reportes.</div>
                            </div>
                            <% if (carreraEditar != null) { %>
                            <div class="mb-3">
                                <label class="form-label">Estatus</label>
                                <select name="selEstatus" class="form-select">
                                    <option value="Activa" <%= "Activa".equals(carreraEditar.getEstatus()) ? "selected" : ""%>>Activa</option>
                                    <option value="Inactiva" <%= "Inactiva".equals(carreraEditar.getEstatus()) ? "selected" : ""%>>Inactiva</option>
                                </select>
                                <div class="form-text">Una carrera inactiva deja de estar disponible para crear planes, grupos o alumnos nuevos, pero conserva su historial.</div>
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
        <% if (carreraEditar != null || "1".equals(request.getParameter("nuevo"))) { %>
        <script>
            new bootstrap.Modal(document.getElementById("modalCarrera")).show();
        </script>
        <% } %>
            </main>
    </div>
</div>
    </body>
</html>
