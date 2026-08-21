<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.ConfiguracionParcial"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<ConfiguracionParcial> configuraciones = (ArrayList<ConfiguracionParcial>) request.getAttribute("configuraciones");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Parcial activo</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <h2 class="mt-4">Parcial activo por periodo</h2>
            <p class="texto-info">Cada periodo tiene su propio parcial activo. Solo ese parcial se muestra y se puede capturar en los grupos de ese periodo.</p>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>

            <div class="barra-filtros" data-filtros-tabla="#tbodyConfigParcial">
                <div class="campo-filtro campo-filtro-texto">
                    <label for="filtroTextoConfigParcial">Buscar</label>
                    <input type="text" id="filtroTextoConfigParcial" class="form-control form-control-sm" data-filtro-texto placeholder="Periodo...">
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>
            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Periodo</th>
                            <th>Parcial activo</th>
                            <th>Última actualización</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="tbodyConfigParcial">
                        <% for (ConfiguracionParcial configuracion : configuraciones) { %>
                        <tr data-fila-filtrable>
                            <td><%= configuracion.getNombrePeriodo()%></td>
                            <td><span class="badge-estatus badge-aprobado">Parcial <%= configuracion.getParcialActivo()%></span></td>
                            <td><%= configuracion.getFechaActualizacion()%></td>
                            <td class="text-end">
                                <form method="post" action="SConfiguracionParcial" class="d-inline-flex gap-2">
                                    <input type="hidden" name="tfIdPeriodo" value="<%= configuracion.getIdPeriodo()%>">
                                    <select name="tfParcialActivo" class="form-select form-select-sm">
                                        <option value="1" <%= configuracion.getParcialActivo() == 1 ? "selected" : ""%>>Parcial 1</option>
                                        <option value="2" <%= configuracion.getParcialActivo() == 2 ? "selected" : ""%>>Parcial 2</option>
                                        <option value="3" <%= configuracion.getParcialActivo() == 3 ? "selected" : ""%>>Parcial 3</option>
                                    </select>
                                    <button type="submit" class="btn btn-sm btn-primary-formal">Aplicar</button>
                                </form>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <div class="mensaje-exito mt-3" data-filtro-vacio style="display:none;">Ningún registro coincide con la búsqueda.</div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
            </main>
    </div>
</div>
    </body>
</html>
