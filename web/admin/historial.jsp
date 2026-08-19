<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Bitacora"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Bitacora> movimientos = (ArrayList<Bitacora>) request.getAttribute("movimientos");
    String entidadFiltro = (String) request.getAttribute("entidadFiltro");
    String correoFiltro = (String) request.getAttribute("correoFiltro");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Historial de auditoría</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <h2 class="mt-4">Historial de auditoría</h2>
            <p class="texto-info">Quién agregó o eliminó cada registro del sistema.</p>

            <div class="card card-formal">
                <div class="card-body">
                    <form method="get" action="SBitacora" class="row g-2">
                        <div class="col-md-5">
                            <input type="text" name="tfEntidad" class="form-control" placeholder="Módulo (ej. alumnos, grupos)"
                                   value="<%= entidadFiltro != null ? entidadFiltro : ""%>">
                        </div>
                        <div class="col-md-5">
                            <input type="email" name="tfCorreo" class="form-control" placeholder="Correo del responsable"
                                   value="<%= correoFiltro != null ? correoFiltro : ""%>">
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-outline-formal w-100">Filtrar</button>
                        </div>
                    </form>
                </div>
            </div>

            <div class="tabla-formal-wrap">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Fecha</th>
                            <th>Responsable</th>
                            <th>Acción</th>
                            <th>Módulo</th>
                            <th>Detalle</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Bitacora movimiento : movimientos) { %>
                        <tr>
                            <td><%= movimiento.getFechaAccion()%></td>
                            <td><%= movimiento.getCorreoUsuario()%></td>
                            <td>
                                <span class="badge-estatus <%= "ALTA".equals(movimiento.getTipoAccion()) ? "badge-aprobado" : "badge-rechazado"%>">
                                    <%= movimiento.getTipoAccion()%>
                                </span>
                            </td>
                            <td><%= movimiento.getEntidad()%></td>
                            <td><%= movimiento.getDescripcion()%></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
            </main>
    </div>
</div>
    </body>
</html>
