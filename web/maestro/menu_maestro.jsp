<%@page import="modelo.Usuario"%>
<%@page import="jakarta.servlet.RequestDispatcher"%>
<%
    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
    Object uriOriginal = request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
    String uriActual = uriOriginal != null ? uriOriginal.toString() : request.getRequestURI();
    String contexto = request.getContextPath();
%>
<nav class="navbar navbar-expand-lg navbar-formal">
    <div class="container-fluid">
        <a class="navbar-brand" href="<%= contexto %>/maestro/SPanel"><i class="bi bi-mortarboard me-2"></i>Control Escolar</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#menuMaestro">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="menuMaestro">
            <div class="navbar-nav flex-wrap">
                <a class="nav-link <%= uriActual.contains("SPanel") ? "active" : ""%>" href="<%= contexto %>/maestro/SPanel">Panel</a>
                <a class="nav-link <%= uriActual.contains("SCalificaciones") ? "active" : ""%>" href="<%= contexto %>/maestro/SCalificaciones">Mis grupos y calificaciones</a>
                <a class="nav-link" href="<%= contexto %>/SLogout"><i class="bi bi-box-arrow-right me-1"></i>Salir</a>
            </div>
        </div>
        <span class="texto-info text-white ms-3 d-none d-lg-inline"><%= usuarioSesion.getNombres()%></span>
    </div>
</nav>