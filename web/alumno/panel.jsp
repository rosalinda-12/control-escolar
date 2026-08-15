<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Panel del Alumno</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_alumno.jsp" %>

        <div class="container">
            <div class="contenedor-centrado text-center" style="max-width: 600px;">
                <i class="bi bi-mortarboard" style="font-size: 2.4rem; color: var(--color-primary);"></i>
                <h2 class="mt-3">Hola, <%= usuarioSesion.getNombres()%></h2>
                <p class="texto-info">Consulta tus calificaciones por cuatrimestre en la sección "Mis calificaciones".</p>
                <a href="SCalificaciones" class="btn btn-primary-formal mt-2">
                    <i class="bi bi-journal-check me-1"></i>Ver mis calificaciones
                </a>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
    </body>
</html>
