<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Iniciar sesión</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="estilo/styles.css">
    </head>
    <body>
        <div class="contenedor-centrado">
            <div class="card card-formal">
                <div class="card-header"><i class="bi bi-box-arrow-in-right me-2"></i>Iniciar sesión</div>
                <div class="card-body">

                    <% if (request.getParameter("verificado") != null) { %>
                    <div class="mensaje-exito"><i class="bi bi-check-circle me-1"></i>Correo verificado. Ya puedes iniciar sesión.</div>
                    <% } %>

                    <% if (request.getParameter("recuperada") != null) { %>
                    <div class="mensaje-exito"><i class="bi bi-check-circle me-1"></i>Tu contraseña se actualizó. Ya puedes iniciar sesión con la nueva.</div>
                    <% } %>

                    <% if (request.getAttribute("error") != null) { %>
                    <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
                    <% } %>

                    <form method="post" action="SLogin" class="mt-3">
                        <div class="mb-3">
                            <label class="form-label">Correo</label>
                            <input type="email" name="tfCorreo" class="form-control" required autofocus>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Contraseña</label>
                            <input type="password" name="tfContrasena" class="form-control" required>
                        </div>
                        <div class="mb-3 text-end">
                            <a href="SOlvideContrasena" class="texto-info">¿Olvidaste tu contraseña?</a>
                        </div>
                        <button type="submit" class="btn btn-primary-formal w-100">Entrar</button>
                    </form>

                    <p class="texto-info text-center mt-3">
                        ¿No tienes cuenta? <a href="SRegistro">Regístrate</a>
                    </p>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="estilo/app.js"></script>
    </body>
</html>
