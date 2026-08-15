<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Control Escolar</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="estilo/styles.css">
    </head>
    <body>
        <div class="contenedor-centrado text-center">
            <i class="bi bi-mortarboard" style="font-size: 3rem; color: var(--color-primary);"></i>
            <h1 class="mt-3">Control Escolar</h1>
            <p class="texto-info">Administradores, maestros y alumnos inician sesión desde un mismo lugar.</p>

            <div class="d-grid gap-2 mt-4">
                <a href="SLogin" class="btn btn-primary-formal btn-lg">
                    <i class="bi bi-box-arrow-in-right me-1"></i>Iniciar sesión
                </a>
                <a href="SRegistro" class="btn btn-outline-formal btn-lg">
                    <i class="bi bi-person-plus me-1"></i>Crear una cuenta
                </a>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="estilo/app.js"></script>
    </body>
</html>
