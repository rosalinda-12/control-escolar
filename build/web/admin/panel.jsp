<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Usuario"%>
<%@page import="servicio.ServicioUsuario"%>
<%@page import="servicio.ServicioAlumno"%>
<%@page import="servicio.ServicioAprobacionRegistro"%>
<%@page import="servicio.ServicioCarrera"%>
<%@page import="servicio.ServicioDocente"%>
<%@page import="servicio.ServicioGrupo"%>
<%@page import="servicio.ServicioMateria"%>
<%@page import="servicio.ServicioPlanEstudio"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.YearMonth"%>
<%@page import="java.time.format.TextStyle"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%
    Usuario usuarioSesionPanel = (Usuario) session.getAttribute("usuario");
    int totalUsuarios = new ServicioUsuario().listarTodos().size();
    int totalAlumnos = new ServicioAlumno().listar().size();
    int totalDocentes = new ServicioDocente().listar().size();
    ArrayList<Usuario> solicitudesPendientes = new ServicioAprobacionRegistro().listarPendientes();
    int totalSolicitudes = solicitudesPendientes.size();
    int totalCarreras = new ServicioCarrera().listar().size();
    int totalMaterias = new ServicioMateria().listar().size();
    int totalGrupos = new ServicioGrupo().listar().size();
    int totalPlanes = new ServicioPlanEstudio().listar().size();
    int maxCatalogo = Math.max(1, Math.max(Math.max(totalCarreras, totalMaterias), Math.max(totalGrupos, totalPlanes)));

    Locale es = new Locale("es", "MX");
    LocalDate hoy = LocalDate.now();
    YearMonth mesActual = YearMonth.from(hoy);
    LocalDate primerDiaMes = mesActual.atDay(1);

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
                <div class="col-lg-8 d-flex">
                    <div class="row g-3 flex-grow-1">
                        <div class="col-md-6">
                            <div class="panel-seccion grafica-panel h-100">
                                <div class="panel-seccion-titulo">
                                    <h5><i class="bi bi-pie-chart"></i>Distribuci&oacute;n del personal</h5>
                                </div>
                                <div class="grafica-pastel" data-valores="<%= totalUsuarios%>,<%= totalAlumnos%>,<%= totalDocentes%>">
                                    <div class="grafica-pastel-circulo"></div>
                                    <div class="grafica-leyenda">
                                        <div><span class="grafica-leyenda-color grafica-azul"></span>Usuarios <strong><%= totalUsuarios%></strong></div>
                                        <div><span class="grafica-leyenda-color grafica-morada"></span>Alumnos <strong><%= totalAlumnos%></strong></div>
                                        <div><span class="grafica-leyenda-color grafica-naranja"></span>Docentes <strong><%= totalDocentes%></strong></div>
                                    </div>
                                </div>
                                <p class="texto-info grafica-nota">Personas registradas en el sistema.</p>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="panel-seccion grafica-panel h-100">
                                <div class="panel-seccion-titulo">
                                    <h5><i class="bi bi-bar-chart"></i>Cat&aacute;logos acad&eacute;micos</h5>
                                </div>
                                <div class="grafica-barras">
                                    <div class="grafica-fila"><div class="grafica-etiqueta"><span>Carreras</span><strong><%= totalCarreras%></strong></div><div class="grafica-barra"><span class="grafica-barra-valor grafica-azul" style="--porcentaje: <%= totalCarreras * 100 / maxCatalogo%>%;"></span></div></div>
                                    <div class="grafica-fila"><div class="grafica-etiqueta"><span>Materias</span><strong><%= totalMaterias%></strong></div><div class="grafica-barra"><span class="grafica-barra-valor grafica-morada" style="--porcentaje: <%= totalMaterias * 100 / maxCatalogo%>%;"></span></div></div>
                                    <div class="grafica-fila"><div class="grafica-etiqueta"><span>Grupos</span><strong><%= totalGrupos%></strong></div><div class="grafica-barra"><span class="grafica-barra-valor grafica-naranja" style="--porcentaje: <%= totalGrupos * 100 / maxCatalogo%>%;"></span></div></div>
                                    <div class="grafica-fila"><div class="grafica-etiqueta"><span>Planes</span><strong><%= totalPlanes%></strong></div><div class="grafica-barra"><span class="grafica-barra-valor grafica-verde" style="--porcentaje: <%= totalPlanes * 100 / maxCatalogo%>%;"></span></div></div>
                                </div>
                                <p class="texto-info grafica-nota">Registros disponibles en los cat&aacute;logos principales.</p>
                            </div>
                        </div>
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
