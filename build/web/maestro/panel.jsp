<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="modelo.Usuario"%>
<%
    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
%>
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
        <nav class="navbar navbar-expand-lg navbar-formal">
            <div class="container-fluid">
                <a class="navbar-brand" href="SPanel"><i class="bi bi-mortarboard me-2"></i>Control Escolar</a>
                <a class="nav-link" href="../SLogout"><i class="bi bi-box-arrow-right me-1"></i>Salir</a>
            </div>
        </nav>

        <div class="container">
            <div class="contenedor-centrado text-center" style="max-width: 600px;">
                <i class="bi bi-person-badge" style="font-size: 2.4rem; color: var(--color-primary);"></i>
                <h2 class="mt-3">Hola, <%= usuarioSesion.getNombres()%></h2>
                <p class="texto-info">Tu módulo de grupos, materias y captura de calificaciones se construye en la siguiente fase del proyecto.</p>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
    </body>
</html>
