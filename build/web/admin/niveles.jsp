<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="modelo.NivelAcademico"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<NivelAcademico> niveles = (ArrayList<NivelAcademico>) request.getAttribute("niveles");
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

            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Nivel</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (NivelAcademico nivel : niveles) { %>
                        <tr>
                            <td><%= nivel.getNombreNivel()%></td>
                            <td class="text-end">
                                <form method="post" action="SNiveles" class="d-inline">
                                    <input type="hidden" name="accion" value="Eliminar">
                                    <input type="hidden" name="idNivel" value="<%= nivel.getIdNivel()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal">Eliminar</button>
                                </form>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="modal fade modal-formal" id="modalNivel" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SNiveles">
                        <div class="modal-header">
                            <h5 class="modal-title">Nuevo nivel académico</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="Agregar">
                            <div class="mb-3">
                                <label class="form-label">Nombre</label>
                                <input type="text" name="tfNombreNivel" class="form-control" placeholder="Licenciatura" required>
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
