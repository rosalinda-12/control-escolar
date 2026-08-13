<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    boolean existeAdministrador = Boolean.TRUE.equals(request.getAttribute("existeAdministrador"));
    String tipoSeleccionado = (String) request.getAttribute("tipoSeleccionado");
    if (tipoSeleccionado == null)
    {
        tipoSeleccionado = existeAdministrador ? "Alumno" : "Administrador";
    }
    boolean esAdministrador = "Administrador".equals(tipoSeleccionado);
    boolean esAlumno = "Alumno".equals(tipoSeleccionado);
    boolean esMaestro = "Maestro".equals(tipoSeleccionado);
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Crear cuenta</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="estilo/styles.css">
    </head>
    <body>
        <div class="contenedor-centrado" style="max-width: 480px;">
            <div class="card card-formal">
                <div class="card-header"><i class="bi bi-person-plus me-2"></i>Crear cuenta</div>
                <div class="card-body">

                    <% if (!existeAdministrador) { %>
                    <p class="texto-info">
                        <i class="bi bi-info-circle me-1"></i>Todavía no hay un Administrador registrado. Esta primera cuenta será la del Administrador del sistema.
                    </p>
                    <% } else { %>
                    <div class="btn-group w-100 mb-3" role="group">
                        <a href="SRegistro?tipo=Alumno" class="btn btn-outline-formal <%= esAlumno ? "active" : ""%>">
                            <i class="bi bi-mortarboard me-1"></i>Soy alumno
                        </a>
                        <a href="SRegistro?tipo=Maestro" class="btn btn-outline-formal <%= esMaestro ? "active" : ""%>">
                            <i class="bi bi-person-badge me-1"></i>Soy maestro
                        </a>
                    </div>
                    <% } %>

                    <% if (request.getAttribute("error") != null) { %>
                    <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
                    <% } %>

                    <form method="post" action="SRegistro" class="mt-3">
                        <input type="hidden" name="tipo" value="<%= tipoSeleccionado%>">

                        <% if (esAdministrador) { %>
                        <div class="mb-3">
                            <label class="form-label">Nombres</label>
                            <input type="text" name="tfNombres" class="form-control" required autofocus>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Apellido paterno</label>
                            <input type="text" name="tfApellidoPaterno" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Apellido materno</label>
                            <input type="text" name="tfApellidoMaterno" class="form-control" required>
                        </div>
                        <% } %>

                        <% if (esAlumno) { %>
                        <div class="mb-3">
                            <label class="form-label">Matrícula</label>
                            <input type="text" name="tfMatricula" class="form-control" placeholder="123456 o 123456_i" required autofocus>
                        </div>
                        <% } %>

                        <div class="mb-3">
                            <label class="form-label">Correo</label>
                            <input type="email" name="tfCorreo" class="form-control" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Contraseña</label>
                            <input type="password" name="tfContrasena" class="form-control" minlength="8" required>
                        </div>

                        <button type="submit" class="btn btn-primary-formal w-100">Registrarme</button>
                    </form>

                    <p class="texto-info text-center mt-3">
                        ¿Ya tienes cuenta? <a href="SLogin">Inicia sesión</a>
                    </p>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="estilo/app.js"></script>
    </body>
</html>
