<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    int idUsuario = (Integer) request.getAttribute("idUsuario");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Verificar correo</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="estilo/styles.css">
    </head>
    <body>
        <div class="contenedor-centrado">
            <div class="card card-formal">
                <div class="card-header"><i class="bi bi-envelope-check me-2"></i>Verifica tu correo</div>
                <div class="card-body">
                    <p class="texto-info">Te enviamos un código de 6 dígitos. Vence a los 2 minutos de haberlo pedido.</p>

                    <% if (request.getAttribute("mensaje") != null) { %>
                    <div class="mensaje-exito"><i class="bi bi-check-circle me-1"></i><%= request.getAttribute("mensaje")%></div>
                    <% } %>

                    <% if (request.getAttribute("error") != null) { %>
                    <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
                    <% } %>

                    <form method="post" action="SVerificarCorreo" class="mt-3">
                        <input type="hidden" name="idUsuario" value="<%= idUsuario%>">
                        <div class="mb-3">
                            <label class="form-label">Código</label>
                            <input type="text" name="tfCodigo" class="form-control" maxlength="6" required autofocus>
                        </div>
                        <button type="submit" class="btn btn-primary-formal w-100">Verificar</button>
                    </form>

                    <form method="post" action="SVerificarCorreo" class="mt-2">
                        <input type="hidden" name="idUsuario" value="<%= idUsuario%>">
                        <input type="hidden" name="accion" value="reenviar">
                        <button type="submit" class="btn btn-outline-formal w-100">Reenviar código</button>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="estilo/app.js"></script>
    </body>
</html>
