<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Panel del Administrador</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
            <h2 class="mt-4">Panel del Administrador</h2>
            <p class="texto-info">Desde aquí gestionas todo el sistema: catálogos, inscripciones, el parcial activo por periodo y el historial de auditoría.</p>

            <div class="row g-3 mt-2">
                <div class="col-md-4">
                    <a href="SSolicitudesRegistro" class="text-decoration-none">
                        <div class="card card-formal h-100">
                            <div class="card-body">
                                <i class="bi bi-person-check" style="font-size: 1.6rem; color: var(--color-primary);"></i>
                                <h5 class="mt-2">Solicitudes de registro</h5>
                                <p class="texto-info mb-0">Aprueba o rechaza a los alumnos y maestros que ya se registraron.</p>
                            </div>
                        </div>
                    </a>
                </div>
                <div class="col-md-4">
                    <a href="SConfiguracionParcial" class="text-decoration-none">
                        <div class="card card-formal h-100">
                            <div class="card-body">
                                <i class="bi bi-toggle2-on" style="font-size: 1.6rem; color: var(--color-primary);"></i>
                                <h5 class="mt-2">Parcial activo</h5>
                                <p class="texto-info mb-0">Habilita el parcial que se puede capturar en cada periodo.</p>
                            </div>
                        </div>
                    </a>
                </div>
                <div class="col-md-4">
                    <a href="SReinscripciones" class="text-decoration-none">
                        <div class="card card-formal h-100">
                            <div class="card-body">
                                <i class="bi bi-arrow-repeat" style="font-size: 1.6rem; color: var(--color-primary);"></i>
                                <h5 class="mt-2">Reinscripciones</h5>
                                <p class="texto-info mb-0">Mueve alumnos a su grupo del siguiente cuatrimestre.</p>
                            </div>
                        </div>
                    </a>
                </div>
                <div class="col-md-4">
                    <a href="SBitacora" class="text-decoration-none">
                        <div class="card card-formal h-100">
                            <div class="card-body">
                                <i class="bi bi-clock-history" style="font-size: 1.6rem; color: var(--color-primary);"></i>
                                <h5 class="mt-2">Historial de auditoría</h5>
                                <p class="texto-info mb-0">Quién agregó o eliminó cada registro del sistema.</p>
                            </div>
                        </div>
                    </a>
                </div>
                <div class="col-md-4">
                    <a href="SGrupos" class="text-decoration-none">
                        <div class="card card-formal h-100">
                            <div class="card-body">
                                <i class="bi bi-people" style="font-size: 1.6rem; color: var(--color-primary);"></i>
                                <h5 class="mt-2">Grupos y alumnos</h5>
                                <p class="texto-info mb-0">Catálogos base: grupos, alumnos, materias y profesores.</p>
                            </div>
                        </div>
                    </a>
                </div>
                <div class="col-md-4">
                    <a href="SAsignaciones" class="text-decoration-none">
                        <div class="card card-formal h-100">
                            <div class="card-body">
                                <i class="bi bi-diagram-3" style="font-size: 1.6rem; color: var(--color-primary);"></i>
                                <h5 class="mt-2">Asignaciones e inscripciones</h5>
                                <p class="texto-info mb-0">Qué profesor imparte qué, y qué alumno cursa qué.</p>
                            </div>
                        </div>
                    </a>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
    </body>
</html>
