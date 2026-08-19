<%@page import="modelo.Usuario"%>
<%@page import="jakarta.servlet.RequestDispatcher"%>
<%
    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
    Object uriOriginal = request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
    String uriActual = uriOriginal != null ? uriOriginal.toString() : request.getRequestURI();
    String rutaBase = request.getContextPath();

    String iniciales = "" + (usuarioSesion.getNombres() != null && !usuarioSesion.getNombres().isEmpty() ? usuarioSesion.getNombres().charAt(0) : '?')
            + (usuarioSesion.getApellidoPaterno() != null && !usuarioSesion.getApellidoPaterno().isEmpty() ? usuarioSesion.getApellidoPaterno().charAt(0) : "");
%>
<script>(function(){try{var t=localStorage.getItem("ce-tema");if(!t){t=(window.matchMedia&&window.matchMedia("(prefers-color-scheme: dark)").matches)?"dark":"light";}document.documentElement.setAttribute("data-theme",t);}catch(e){}})();</script>
<div class="app-shell">
    <aside class="sidebar">
        <a class="sidebar-brand" href="<%= rutaBase %>/maestro/SPanel">
            <span class="sidebar-brand-icon"><i class="bi bi-shield-check"></i></span>
            <span class="sidebar-brand-text">
                <span>CONTROL ESCOLAR</span>
                <span class="sidebar-brand-sub">Portal Docente</span>
            </span>
        </a>

        <nav class="sidebar-nav">
            <a class="sidebar-link <%= uriActual.contains("SPanel") ? "active" : ""%>" href="<%= rutaBase %>/maestro/SPanel">
                <i class="bi bi-house-door"></i>Inicio
            </a>
            <a class="sidebar-link <%= uriActual.contains("SCalificaciones") ? "active" : ""%>" href="<%= rutaBase %>/maestro/SCalificaciones">
                <i class="bi bi-journal-check"></i>Mis grupos y calificaciones
            </a>
        </nav>

        <div class="sidebar-footer">
            <a class="sidebar-link" href="<%= rutaBase %>/SLogout"><i class="bi bi-box-arrow-right"></i>Cerrar sesión</a>
        </div>
    </aside>

    <div class="main-wrap">
        <header class="topbar">
            <button class="topbar-toggle" type="button" data-toggle-sidebar aria-label="Abrir menú">
                <i class="bi bi-list"></i>
            </button>
            <div class="topbar-search">
                <i class="bi bi-search"></i>
                <input type="text" data-buscador placeholder="Buscar alumno o grupo...">
                <kbd>Ctrl+K</kbd>
            </div>
            <div class="topbar-actions">
                <button type="button" class="icon-btn theme-toggle" data-theme-toggle data-tooltip="Cambiar tema" aria-pressed="false">
                    <i class="bi bi-moon-stars-fill"></i>
                    <i class="bi bi-sun-fill"></i>
                </button>
                <div class="dropdown">
                    <button class="topbar-user btn" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                        <div class="topbar-user-avatar"><%= iniciales.toUpperCase()%></div>
                        <div class="topbar-user-info">
                            <span class="topbar-user-name"><%= usuarioSesion.getNombres()%></span>
                            <span class="topbar-user-role">Docente</span>
                        </div>
                        <i class="bi bi-chevron-down topbar-user-caret"></i>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li class="px-3 py-1 texto-info">Sesión iniciada como<br><strong><%= usuarioSesion.getCorreo()%></strong></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="<%= rutaBase %>/SLogout"><i class="bi bi-box-arrow-right me-2"></i>Cerrar sesión</a></li>
                    </ul>
                </div>
            </div>
        </header>
        <main class="contenido">
