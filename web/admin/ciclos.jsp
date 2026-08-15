<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="modelo.CicloEscolar"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<CicloEscolar> ciclos = (ArrayList<CicloEscolar>) request.getAttribute("ciclos");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Ciclos escolares</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <div class="d-flex justify-content-between align-items-center mt-4">
                <div>
                    <h2>Ciclos escolares</h2>
                    <p class="texto-info mb-0">Agrupan los periodos de un mismo año escolar, por ejemplo "2026".</p>
                </div>
                <button class="btn btn-primary-formal" data-bs-toggle="modal" data-bs-target="#modalCiclo">
                    <i class="bi bi-plus-lg me-1"></i>Nuevo ciclo
                </button>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <% if (ciclos.isEmpty()) { %>
            <div class="mensaje-exito mt-4">
                <i class="bi bi-calendar3 me-1"></i>
                Todavía no hay ciclos escolares. Crea el primero para poder registrar periodos.
            </div>
            <% } else { %>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Ciclo</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (CicloEscolar ciclo : ciclos) { %>
                        <tr>
                            <td><%= ciclo.getNombreCiclo()%></td>
                            <td class="text-end">
                                <form method="post" action="SCiclos" class="d-inline">
                                    <input type="hidden" name="accion" value="Eliminar">
                                    <input type="hidden" name="idCiclo" value="<%= ciclo.getIdCiclo()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal">Eliminar</button>
                                </form>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% } %>
        </div>

        <div class="modal fade modal-formal" id="modalCiclo" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form method="post" action="SCiclos">
                        <div class="modal-header">
                            <h5 class="modal-title">Nuevo ciclo escolar</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="Agregar">
                            <div class="mb-3">
                                <label class="form-label">Nombre del ciclo</label>
                                <input type="text" name="tfNombreCiclo" class="form-control" placeholder="2026" required>
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
