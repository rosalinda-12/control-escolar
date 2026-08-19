<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Usuario"%>
<%@page import="modelo.Carrera"%>
<%
    // usuarioSesion la declara el include estatico de menu_subdirector.jsp
    // mas abajo (mismo metodo Java generado).
    Carrera carrera = (Carrera) request.getAttribute("carrera");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Panel del Subdirector</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_subdirector.jsp" %>

        <div class="container">
            <div class="contenedor-centrado text-center" style="max-width: 600px;">
                <i class="bi bi-person-badge" style="font-size: 2.4rem; color: var(--color-primary);"></i>
                <h2 class="mt-3">Hola, <%= usuarioSesion.getNombres()%></h2>
                <% if (carrera != null) { %>
                <p class="texto-info">Estás a cargo de <strong><%= carrera.getNombreCarrera()%></strong>. Desde aquí
                    puedes consultar los grupos de esa carrera y asignarles docentes.</p>
                <a href="SGrupos" class="btn btn-primary-formal mt-2">
                    <i class="bi bi-diagram-3 me-1"></i>Ver grupos de mi carrera
                </a>
                <% } else { %>
                <div class="mensaje-error mt-3">
                    <i class="bi bi-exclamation-triangle me-1"></i>
                    Tu cuenta de Subdirector todavía no tiene una carrera asignada. Pide al Administrador que la
                    configure desde Usuarios.
                </div>
                <% } %>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
            </main>
    </div>
</div>
    </body>
</html>
