<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Bienvenido | Control Escolar</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="estilo/styles.css">
    </head>
    <body>
        <script>(function(){try{var t=localStorage.getItem("ce-tema");if(!t){t=(window.matchMedia&&window.matchMedia("(prefers-color-scheme: dark)").matches)?"dark":"light";}document.documentElement.setAttribute("data-theme",t);}catch(e){}})();</script>
        <button type="button" class="theme-toggle-flotante theme-toggle" data-theme-toggle data-tooltip="Cambiar tema" aria-pressed="false">
            <i class="bi bi-moon-stars-fill"></i><i class="bi bi-sun-fill"></i>
        </button>
        <main class="bienvenida-pagina">
            <section class="bienvenida-hero">
                <div class="bienvenida-marca">
                    <span class="bienvenida-icono"><i class="bi bi-mortarboard"></i></span>
                    <span>CONTROL ESCOLAR</span>
                </div>
                <p class="bienvenida-etiqueta">Plataforma académica institucional</p>
                <h1>Bienvenido a Control Escolar</h1>
                <p class="bienvenida-descripcion">Organiza la información académica de tu institución y consulta todo lo necesario desde un solo lugar.</p>
                <div class="d-flex flex-wrap gap-2 mt-4">
                    <a href="SLogin" class="btn btn-primary-formal btn-lg"><i class="bi bi-box-arrow-in-right me-1"></i>Iniciar sesión</a>
                    <a href="SRegistro" class="btn btn-outline-formal btn-lg"><i class="bi bi-person-plus me-1"></i>Crear una cuenta</a>
                </div>
            </section>
            <section class="bienvenida-funciones" aria-label="Funciones principales">
                <article><i class="bi bi-diagram-3"></i><h2>Organización académica</h2><p>Gestiona carreras, planes, grupos y materias.</p></article>
                <article><i class="bi bi-people"></i><h2>Comunidad escolar</h2><p>Administra alumnos, docentes y responsables.</p></article>
                <article><i class="bi bi-clipboard-data"></i><h2>Seguimiento</h2><p>Consulta inscripciones y calificaciones.</p></article>
            </section>
        </main>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="estilo/app.js"></script>
    </body>
</html>
