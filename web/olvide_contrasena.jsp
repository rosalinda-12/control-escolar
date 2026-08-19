<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Olvidé mi contraseña</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="estilo/styles.css">
    </head>
    <body>
        <script>(function(){try{var t=localStorage.getItem("ce-tema");if(!t){t=(window.matchMedia&&window.matchMedia("(prefers-color-scheme: dark)").matches)?"dark":"light";}document.documentElement.setAttribute("data-theme",t);}catch(e){}})();</script>
        <button type="button" class="theme-toggle-flotante theme-toggle" data-theme-toggle data-tooltip="Cambiar tema" aria-pressed="false">
            <i class="bi bi-moon-stars-fill"></i><i class="bi bi-sun-fill"></i>
        </button>
        <div class="contenedor-centrado">
            <div class="card card-formal">
                <div class="card-header"><i class="bi bi-key me-2"></i>Recuperar contraseña</div>
                <div class="card-body">
                    <p class="texto-info">Escribe el correo con el que te registraste. Te enviaremos un código de verificación para que puedas definir una contraseña nueva.</p>

                    <% if (request.getAttribute("error") != null) { %>
                    <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
                    <% } %>

                    <form method="post" action="SOlvideContrasena" class="mt-3">
                        <div class="mb-3">
                            <label class="form-label">Correo</label>
                            <input type="email" name="tfCorreo" class="form-control" required autofocus>
                        </div>
                        <button type="submit" class="btn btn-primary-formal w-100">Enviar código</button>
                    </form>

                    <p class="texto-info text-center mt-3">
                        <a href="login.jsp"><i class="bi bi-arrow-left me-1"></i>Volver a iniciar sesión</a>
                    </p>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="estilo/app.js"></script>
    </body>
</html>
