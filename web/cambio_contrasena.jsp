<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Cambiar contraseña</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="estilo/styles.css">
    </head>
    <body>
        <div class="contenedor-centrado">
            <div class="card card-formal">
                <div class="card-header"><i class="bi bi-key me-2"></i>Es tu primer inicio de sesión</div>
                <div class="card-body">
                    <p class="texto-info">Puedes cambiar tu contraseña ahora, o dejarla igual y hacerlo después.</p>

                    <% if (request.getAttribute("error") != null) { %>
                    <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
                    <% } %>

                    <form method="post" action="SCambioContrasena" class="mt-3">
                        <div class="mb-3">
                            <label class="form-label">Contraseña actual</label>
                            <input type="password" name="tfContrasenaActual" class="form-control" required autofocus>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Contraseña nueva</label>
                            <input type="password" name="tfContrasenaNueva" class="form-control" minlength="8" required>
                        </div>
                        <button type="submit" class="btn btn-primary-formal w-100">Cambiar contraseña</button>
                    </form>

                    <form method="post" action="SCambioContrasena" class="mt-2">
                        <input type="hidden" name="accion" value="omitir">
                        <button type="submit" class="btn btn-outline-formal w-100">Continuar sin cambiarla</button>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="estilo/app.js"></script>
    </body>
</html>
