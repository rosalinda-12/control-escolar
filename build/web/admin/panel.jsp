<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Usuario"%>
<%@page import="servicio.ServicioUsuario"%>
<%@page import="servicio.ServicioAlumno"%>
<%@page import="servicio.ServicioAprobacionRegistro"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.YearMonth"%>
<%@page import="java.time.format.TextStyle"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%
    Usuario usuarioSesionPanel = (Usuario) session.getAttribute("usuario");
    int totalUsuarios = new ServicioUsuario().listarTodos().size();
    int totalAlumnos = new ServicioAlumno().listar().size();
    ArrayList<Usuario> solicitudesPendientes = new ServicioAprobacionRegistro().listarPendientes();
    int totalSolicitudes = solicitudesPendientes.size();

    // ---- Calendario del mes actual (widget) ----
    Locale es = new Locale("es", "MX");
    LocalDate hoy = LocalDate.now();
    YearMonth mesActual = YearMonth.from(hoy);
    LocalDate primerDiaMes = mesActual.atDay(1);
    // Domingo = 1 ... Sábado = 7 (para que la semana empiece en domingo, como en la imagen)
    int desplazamientoInicial = primerDiaMes.getDayOfWeek().getValue() % 7;
    int diasEnMes = mesActual.lengthOfMonth();
    String nombreMes = mesActual.getMonth().getDisplayName(TextStyle.FULL, es);
    nombreMes = nombreMes.substring(0, 1).toUpperCase(es) + nombreMes.substring(1);
%>
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
            <div class="mt-1 mb-4">
                <h2 class="mb-1">¡Bienvenido/a, <%= usuarioSesionPanel.getNombres()%>! </h2>
                <p class="texto-info mb-0">Aquí tienes un resumen de la actividad del sistema.</p>
            </div>

            <div class="row g-3">
                <div class="col-md-3 col-sm-6">
                    <a href="SSolicitudesRegistro" class="text-decoration-none">
                        <div class="stat-card">
                            <div class="stat-card-icon tono-azul"><i class="bi bi-file-earmark-check"></i></div>
                            <div class="stat-card-label">Solicitudes pendientes</div>
                            <div class="stat-card-value"><%= totalSolicitudes%></div>
                            <div class="stat-card-hint">Requieren tu atención <i class="bi bi-arrow-right"></i></div>
                        </div>
                    </a>
                </div>
                <div class="col-md-3 col-sm-6">
                    <a href="SUsuarios" class="text-decoration-none">
                        <div class="stat-card">
                            <div class="stat-card-icon tono-verde"><i class="bi bi-people"></i></div>
                            <div class="stat-card-label">Usuarios registrados</div>
                            <div class="stat-card-value"><%= totalUsuarios%></div>
                            <div class="stat-card-hint">Total de cuentas <i class="bi bi-arrow-right"></i></div>
                        </div>
                    </a>
                </div>
                <div class="col-md-3 col-sm-6">
                    <a href="SAlumnos" class="text-decoration-none">
                        <div class="stat-card">
                            <div class="stat-card-icon tono-morado"><i class="bi bi-mortarboard"></i></div>
                            <div class="stat-card-label">Alumnos activos</div>
                            <div class="stat-card-value"><%= totalAlumnos%></div>
                            <div class="stat-card-hint">Ciclo actual <i class="bi bi-arrow-right"></i></div>
                        </div>
                    </a>
                </div>
                <div class="col-md-3 col-sm-6">
                    <a href="SBitacora" class="text-decoration-none">
                        <div class="stat-card">
                            <div class="stat-card-icon tono-naranja"><i class="bi bi-clock-history"></i></div>
                            <div class="stat-card-label">Historial de auditoría</div>
                            <div class="stat-card-value" style="font-size:1.15rem;"><i class="bi bi-journal-text"></i></div>
                            <div class="stat-card-hint">Ver bitácora <i class="bi bi-arrow-right"></i></div>
                        </div>
                    </a>
                </div>
            </div>

            <div class="row g-3 mt-1">
                <div class="col-lg-8">
                    <div class="panel-seccion h-100">
                        <div class="panel-seccion-titulo">
                            <h5><i class="bi bi-file-earmark-check"></i>Solicitudes pendientes</h5>
                            <a href="SSolicitudesRegistro" class="texto-info fw-semibold text-decoration-none">Ver todas <i class="bi bi-arrow-right"></i></a>
                        </div>

                        <% if (solicitudesPendientes.isEmpty()) { %>
                        <div class="text-center py-4">
                            <i class="bi bi-emoji-smile" style="font-size: 1.8rem; color: var(--color-text-faint);"></i>
                            <p class="texto-info mt-2 mb-0">No hay solicitudes pendientes por ahora.</p>
                        </div>
                        <% } else { %>
                        <div class="table-responsive">
                            <table class="table table-formal align-middle mb-0">
                                <thead>
                                    <tr>
                                        <th>Solicitante</th>
                                        <th>Rol</th>
                                        <th>Correo verificado</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        String[] tonos = {"tono-1", "tono-2", "tono-3", "tono-4", "tono-5"};
                                        int indiceTono = 0;
                                        int mostrados = 0;
                                        for (Usuario solicitante : solicitudesPendientes) {
                                            if (mostrados >= 5) break;
                                            mostrados++;
                                            String inicialesSolicitante = "" + (solicitante.getNombres() != null && !solicitante.getNombres().isEmpty() ? solicitante.getNombres().charAt(0) : '?')
                                                    + (solicitante.getApellidoPaterno() != null && !solicitante.getApellidoPaterno().isEmpty() ? solicitante.getApellidoPaterno().charAt(0) : "");
                                    %>
                                    <tr>
                                        <td>
                                            <div class="persona-celda">
                                                <div class="avatar-circle <%= tonos[indiceTono++ % tonos.length]%>"><%= inicialesSolicitante.toUpperCase()%></div>
                                                <div>
                                                    <div class="persona-nombre"><%= solicitante.getNombres()%> <%= solicitante.getApellidoPaterno()%></div>
                                                    <div class="persona-correo"><%= solicitante.getCorreo()%></div>
                                                </div>
                                            </div>
                                        </td>
                                        <td class="texto-info"><%= solicitante.getNombreRol()%></td>
                                        <td>
                                            <% if (solicitante.isCorreoVerificado()) { %>
                                            <span class="badge-estatus badge-aprobado">Verificado</span>
                                            <% } else { %>
                                            <span class="badge-estatus badge-pendiente">Pendiente</span>
                                            <% } %>
                                        </td>
                                        <td class="text-end">
                                            <a class="accion-icono" href="SSolicitudesRegistro" data-tooltip="Ver solicitud"><i class="bi bi-eye"></i></a>
                                        </td>
                                    </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                        <% } %>
                    </div>

                    <h5 class="mt-4 mb-2"><i class="bi bi-lightning-charge me-2" style="color: var(--accent);"></i>Accesos rápidos</h5>
                    <div class="accesos-grid">
                        <a href="SSolicitudesRegistro" class="acceso-tile">
                            <span class="acceso-tile-icon"><i class="bi bi-person-check"></i></span>
                            <span class="acceso-tile-texto">Solicitudes</span>
                            <i class="bi bi-chevron-right acceso-tile-flecha"></i>
                        </a>
                        <a href="SAlumnos" class="acceso-tile">
                            <span class="acceso-tile-icon"><i class="bi bi-mortarboard"></i></span>
                            <span class="acceso-tile-texto">Lista de alumnos</span>
                            <i class="bi bi-chevron-right acceso-tile-flecha"></i>
                        </a>
                        <a href="SGrupos" class="acceso-tile">
                            <span class="acceso-tile-icon"><i class="bi bi-people"></i></span>
                            <span class="acceso-tile-texto">Control de grupos</span>
                            <i class="bi bi-chevron-right acceso-tile-flecha"></i>
                        </a>
                        <a href="SMaterias" class="acceso-tile">
                            <span class="acceso-tile-icon"><i class="bi bi-journal-bookmark"></i></span>
                            <span class="acceso-tile-texto">Asignar materias</span>
                            <i class="bi bi-chevron-right acceso-tile-flecha"></i>
                        </a>
                        <a href="SUsuarios" class="acceso-tile">
                            <span class="acceso-tile-icon"><i class="bi bi-search"></i></span>
                            <span class="acceso-tile-texto">Buscar usuario</span>
                            <i class="bi bi-chevron-right acceso-tile-flecha"></i>
                        </a>
                        <a href="SConfiguracionParcial" class="acceso-tile">
                            <span class="acceso-tile-icon"><i class="bi bi-toggles"></i></span>
                            <span class="acceso-tile-texto">Configuración</span>
                            <i class="bi bi-chevron-right acceso-tile-flecha"></i>
                        </a>
                        <a href="SReinscripciones" class="acceso-tile">
                            <span class="acceso-tile-icon"><i class="bi bi-arrow-repeat"></i></span>
                            <span class="acceso-tile-texto">Reinscripciones</span>
                            <i class="bi bi-chevron-right acceso-tile-flecha"></i>
                        </a>
                        <a href="SBitacora" class="acceso-tile">
                            <span class="acceso-tile-icon"><i class="bi bi-clock-history"></i></span>
                            <span class="acceso-tile-texto">Historial</span>
                            <i class="bi bi-chevron-right acceso-tile-flecha"></i>
                        </a>
                    </div>
                </div>

                <div class="col-lg-4">
                    <div class="panel-seccion mb-3">
                        <div class="mini-calendario-cabecera">
                            <h5 class="mb-0 d-flex align-items-center gap-2"><i class="bi bi-calendar3" style="color: var(--accent);"></i>Calendario escolar</h5>
                        </div>
                        <div class="mini-calendario-mes text-center mb-2"><%= nombreMes%> <%= mesActual.getYear()%></div>
                        <div class="mini-calendario-grid">
                            <div class="dia-nombre">D</div>
                            <div class="dia-nombre">L</div>
                            <div class="dia-nombre">M</div>
                            <div class="dia-nombre">M</div>
                            <div class="dia-nombre">J</div>
                            <div class="dia-nombre">V</div>
                            <div class="dia-nombre">S</div>

                            <% for (int i = 0; i < desplazamientoInicial; i++) { %>
                            <div class="dia-celda otro-mes"><%= mesActual.minusMonths(1).lengthOfMonth() - desplazamientoInicial + i + 1%></div>
                            <% } %>

                            <% for (int dia = 1; dia <= diasEnMes; dia++) { %>
                            <div class="dia-celda <%= dia == hoy.getDayOfMonth() ? "hoy" : ""%>"><%= dia%></div>
                            <% } %>
                        </div>
                    </div>

                    <div class="panel-seccion">
                        <h5 class="mb-3 d-flex align-items-center gap-2"><i class="bi bi-clock" style="color: var(--accent);"></i>Próximos eventos</h5>
                        <div class="evento-item">
                            <span class="evento-punto tono-verde"></span>
                            <div>
                                <div class="evento-titulo">Entrega de calificaciones</div>
                                <div class="evento-fecha">Consulta el periodo activo en "Parcial activo"</div>
                            </div>
                        </div>
                        <div class="evento-item">
                            <span class="evento-punto tono-naranja"></span>
                            <div>
                                <div class="evento-titulo">Aprobación de registros</div>
                                <div class="evento-fecha"><%= totalSolicitudes%> solicitud(es) esperando revisión</div>
                            </div>
                        </div>
                        <div class="evento-item">
                            <span class="evento-punto tono-azul"></span>
                            <div>
                                <div class="evento-titulo">Reinscripciones</div>
                                <div class="evento-fecha">Revisa el catálogo de grupos del próximo ciclo</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
            </main>
    </div>
</div>
    </body>
</html>
