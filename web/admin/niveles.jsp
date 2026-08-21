<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.NivelAcademico"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<NivelAcademico> niveles = (ArrayList<NivelAcademico>) request.getAttribute("niveles");
    NivelAcademico nivelEditar = (NivelAcademico) request.getAttribute("nivelEditar");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Niveles académicos</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <div class="d-flex justify-content-between align-items-center mt-4">
                <div>
                    <h2>Niveles académicos</h2>
                    <p class="texto-info mb-0">Catálogo general: TSU, Ingeniería, Licenciatura. Se usan al definir los planes de estudio.</p>
                </div>
                <button class="btn btn-primary-formal" data-bs-toggle="modal" data-bs-target="#modalNivel">
                    <i class="bi bi-plus-lg me-1"></i>Nuevo nivel
                </button>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <div class="barra-filtros" data-filtros-tabla="#tbodyNiveles">
                <div class="campo-filtro campo-filtro-texto">
                    <label for="filtroTextoNiveles">Buscar</label>
                    <input type="text" id="filtroTextoNiveles" class="form-control form-control-sm" data-filtro-texto placeholder="Nombre del nivel...">
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Nivel</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="tbodyNiveles">
                        <% for (NivelAcademico nivel : niveles) { %>
                        <tr data-fila-filtrable>
                            <td><%= nivel.getNombreNivel()%></td>
                            <td class="text-end">
                                <a href="SNiveles?editar=<%= nivel.getIdNivel()%>" class="btn btn-sm btn-icon-formal" title="Editar nivel" aria-label="Editar nivel"><i class="bi bi-pencil-square"></i></a>
                                <form method="post" action="SNiveles" class="d-inline">
                                    <input type="hidden" name="accion" value="Eliminar">
                                    <input type="hidden" name="idNivel" value="<%= nivel.getIdNivel()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal btn-icon-formal" title="Eliminar nivel" aria-label="Eliminar nivel"><i class="bi bi-trash3"></i></button>
                                </form>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <div class="mensaje-exito mt-3" data-filtro-vacio style="display:none;">Ningún registro coincide con la búsqueda.</div>
            </div>
        </div>

        <div class="modal fade modal-formal" id="modalNivel" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SNiveles">
                        <div class="modal-header">
                            <h5 class="modal-title"><%= nivelEditar == null ? "Nuevo nivel académico" : "Editar nivel académico"%></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="<%= nivelEditar == null ? "Agregar" : "Modificar"%>">
                            <% if (nivelEditar != null) { %><input type="hidden" name="idNivel" value="<%= nivelEditar.getIdNivel()%>"><% } %>
                            <div class="mb-3">
                                <label class="form-label">Nombre</label>
                                <input type="text" name="tfNombreNivel" class="form-control" placeholder="Licenciatura" value="<%= nivelEditar == null ? "" : nivelEditar.getNombreNivel()%>" required>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary-formal"><%= nivelEditar == null ? "Guardar" : "Actualizar"%></button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
        <% if (nivelEditar != null) { %>
        <script>new bootstrap.Modal(document.getElementById("modalNivel")).show();</script>
        <% } %>
            </main>
    </div>
</div>
    </body>
</html>
