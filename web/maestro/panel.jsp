<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="modelo.Usuario"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Panel del Maestro</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_maestro.jsp" %>

        <div class="container">
            <div class="contenedor-centrado text-center" style="max-width: 600px;">
                <i class="bi bi-person-badge" style="font-size: 2.4rem; color: var(--color-primary);"></i>
                <h2 class="mt-3">Hola, <%= usuarioSesion.getNombres()%></h2>
                <p class="texto-info">Desde aquí puedes ver los grupos y materias que tienes a tu cargo y capturar las
                    calificaciones del parcial activo.</p>
                <a href="SCalificaciones" class="btn btn-primary-formal mt-2">
                    <i class="bi bi-journal-check me-1"></i>Ir a mis grupos y calificaciones
                </a>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
    </body>
</html>
