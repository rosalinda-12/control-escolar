<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="modelo.Usuario"%>
<%@page import="modelo.Rol"%>
<%@page import="modelo.Carrera"%>
<%@page import="java.util.ArrayList"%>
<%
    ArrayList<Usuario> usuarios = (ArrayList<Usuario>) request.getAttribute("usuarios");
    ArrayList<Rol> roles = (ArrayList<Rol>) request.getAttribute("roles");
    ArrayList<Carrera> carreras = (ArrayList<Carrera>) request.getAttribute("carreras");

%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Usuarios</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="../estilo/styles.css">
    </head>
    <body>
        <%@ include file="menu_admin.jspf" %>

        <div class="container">
                    <div class="usuarios-heading mt-4">
                        <div><h2>Usuarios</h2>
                <p class="texto-info mb-0">
                    Cuentas del sistema y su rol. Desde aquí puedes agregar cuentas de Control Escolar o Subdirector.
                    Las cuentas de Alumno y Maestro se registran por su cuenta en la pantalla de
                    registro, usando el mismo correo con el que fue dada de alta en
                    Alumnos / Docentes / Subdirectores. Una vez que verifica su correo, aparece en
                    <a href="SSolicitudesRegistro">Solicitudes</a> para que la apruebes y pueda iniciar sesión.
                </p></div>
                <button type="button" class="btn btn-primary-formal" data-bs-toggle="modal" data-bs-target="#modalAgregarUsuario"><i class="bi bi-person-plus"></i>Agregar usuario</button>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>
            <% if (request.getAttribute("exito") != null) { %>
            <div class="mensaje-exito"><i class="bi bi-check-circle me-1"></i><%= request.getAttribute("exito")%></div>
            <% } %>

            <div class="barra-filtros" data-filtros-tabla="#tbodyUsuarios">
                <div class="campo-filtro">
                    <label for="filtroEstatusUsuarios">Estatus</label>
                    <select id="filtroEstatusUsuarios" class="form-select form-select-sm" data-filtro-campo="estatus">
                        <option value="Activo" selected>Activos (actuales)</option>
                        <option value="">Todos</option>
                        <option value="Inactivo">Inactivos</option>
                    </select>
                </div>
                <div class="campo-filtro campo-filtro-texto">
                    <label for="filtroTextoUsuarios">Buscar</label>
                    <input type="text" id="filtroTextoUsuarios" class="form-control form-control-sm" data-filtro-texto placeholder="Nombre o correo...">
                </div>
                <span class="filtro-contador" data-filtro-contador></span>
            </div>
            <div class="tabla-formal-wrap mt-3">
                <table class="table table-formal align-middle">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Correo</th>
                            <th>Rol</th>
                            <th>Carrera</th>
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="tbodyUsuarios">
                        <% for (Usuario u : usuarios) { %>
                        <tr data-fila-filtrable data-estatus="<%= "Inactivo".equals(u.getEstatusRegistro()) ? "Inactivo" : "Activo"%>">
                            <td><%= u.getNombres()%> <%= u.getApellidoPaterno()%></td>
                            <td><%= u.getCorreo()%></td>
                            <td>
                                <form method="post" action="SUsuarios" class="d-flex align-items-center gap-1">
                                    <input type="hidden" name="accion" value="CambiarRol">
                                    <input type="hidden" name="idUsuario" value="<%= u.getIdUsuario()%>">
                                    <% if (u.isAdministradorPrincipal()) { %>
                                    <span class="badge text-bg-dark">Administrador</span>
                                    <% } else { %>
                                    <select name="selRol" class="form-select form-select-sm selRolUsuario" style="min-width:150px" onchange="alternarCarrera(this)">
                                        <% for (Rol r : roles) { if (r.isEsAdministradorPrincipal()) continue; %>
                                        <option value="<%= r.getIdRol()%>" <%= r.getIdRol() == u.getIdRol() ? "selected" : ""%>
                                                data-subdirector="<%= "Subdirector".equals(r.getNombreRol())%>">
                                            <%= r.getNombreRol()%>
                                        </option>
                                        <% } %>
                                    </select>
                                    <select name="selCarrera" class="form-select form-select-sm selCarreraUsuario"
                                            style="min-width:150px; <%= u.esSubdirector() ? "" : "display:none"%>">
                                        <option value="">-- Carrera --</option>
                                        <% for (Carrera c : carreras) { %>
                                        <option value="<%= c.getIdCarrera()%>" <%= u.getIdCarrera() != null && u.getIdCarrera() == c.getIdCarrera() ? "selected" : ""%>>
                                            <%= c.getNombreCarrera()%>
                                        </option>
                                        <% } %>
                                    </select>
                                    <button type="submit" class="btn btn-sm btn-outline-formal">Guardar</button>
                                    <% } %>
                                </form>
                            </td>
                            <td><%= u.getIdCarrera() != null && u.esSubdirector() ? buscarNombreCarrera(carreras, u.getIdCarrera()) : "—"%></td>
                            <td>
                                <% if ("Inactivo".equals(u.getEstatusRegistro())) { %>
                                <span class="badge text-bg-secondary">Inactivo</span>
                                <% } else { %>
                                <span class="badge text-bg-success"><%= u.getEstatusRegistro()%></span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <% if (!u.isAdministradorPrincipal() && u.getIdUsuario() != usuarioSesion.getIdUsuario()
                                        && !"Inactivo".equals(u.getEstatusRegistro())) { %>
                                <form method="post" action="SUsuarios" class="d-inline"
                                      onsubmit="return confirm('¿Eliminar/desactivar esta cuenta?');">
                                    <input type="hidden" name="accion" value="Eliminar">
                                    <input type="hidden" name="idUsuario" value="<%= u.getIdUsuario()%>">
                                    <button type="submit" class="btn btn-sm btn-danger-formal btn-icon-formal" title="Eliminar usuario" aria-label="Eliminar usuario"><i class="bi bi-trash3"></i></button>
                                </form>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <div class="mensaje-exito mt-3" data-filtro-vacio style="display:none;">Ningún registro coincide con los filtros seleccionados.</div>
            </div>
        </div>

        <div class="modal fade modal-formal" id="modalAgregarUsuario" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <form method="post" action="SUsuarios">
                        <div class="modal-header"><h5 class="modal-title">Agregar usuario</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
                        <div class="modal-body">
                            <input type="hidden" name="accion" value="Agregar">
                            <div class="row g-3">
                                <div class="col-md-6"><label for="tfNombresNuevo" class="form-label">Nombres</label><input id="tfNombresNuevo" name="tfNombresNuevo" class="form-control" required></div>
                                <div class="col-md-6"><label for="tfApellidoPaternoNuevo" class="form-label">Apellido paterno</label><input id="tfApellidoPaternoNuevo" name="tfApellidoPaternoNuevo" class="form-control" required></div>
                                <div class="col-md-6"><label for="tfApellidoMaternoNuevo" class="form-label">Apellido materno</label><input id="tfApellidoMaternoNuevo" name="tfApellidoMaternoNuevo" class="form-control"></div>
                                <div class="col-md-6"><label for="tfCorreoNuevo" class="form-label">Correo</label><input id="tfCorreoNuevo" type="email" name="tfCorreoNuevo" class="form-control" required></div>
                                <div class="col-md-6"><label for="tfContrasenaNuevo" class="form-label">Contraseña inicial</label><input id="tfContrasenaNuevo" type="password" name="tfContrasenaNuevo" minlength="8" class="form-control" required></div>
                                <div class="col-md-6"><label for="selRolNuevo" class="form-label">Rol</label><select id="selRolNuevo" name="selRolNuevo" class="form-select" onchange="alternarCarreraNueva(this)" required><% for (Rol r : roles) { if ("Control Escolar".equals(r.getNombreRol()) || "Subdirector".equals(r.getNombreRol())) { %><option value="<%= r.getIdRol()%>" data-subdirector="<%= "Subdirector".equals(r.getNombreRol())%>"><%= r.getNombreRol()%></option><% } } %></select></div>
                                <div class="col-12" id="carreraNuevaWrap"><label for="selCarreraNuevo" class="form-label">Carrera del subdirector</label><select id="selCarreraNuevo" name="selCarreraNuevo" class="form-select"><option value="">Selecciona una carrera</option><% for (Carrera c : carreras) { %><option value="<%= c.getIdCarrera()%>"><%= c.getNombreCarrera()%></option><% } %></select></div>
                            </div>
                            <div class="form-text mt-3">La cuenta quedará aprobada y deberá cambiar su contraseña al iniciar sesión.</div>
                        </div>
                        <div class="modal-footer"><button type="submit" class="btn btn-primary-formal"><i class="bi bi-person-plus"></i>Agregar cuenta</button></div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
        <script src="../estilo/app.js"></script>
        <script>
            function alternarCarrera(select) {
                var opcion = select.options[select.selectedIndex];
                var esSubdirector = opcion.getAttribute("data-subdirector") === "true";
                var contenedor = select.closest("form") ? select.closest("form").querySelector(".selCarreraUsuario") : null;
                if (contenedor) {
                    contenedor.style.display = esSubdirector ? "" : "none";
                }
            }
            function alternarCarreraNueva(select) {
                var opcion = select.options[select.selectedIndex];
                var carrera = document.getElementById("carreraNuevaWrap");
                var selector = document.getElementById("selCarreraNuevo");
                var esSubdirector = opcion.getAttribute("data-subdirector") === "true";
                carrera.style.display = esSubdirector ? "" : "none";
                selector.required = esSubdirector;
            }
            alternarCarreraNueva(document.getElementById("selRolNuevo"));
            <% if (request.getAttribute("abrirAgregar") != null) { %>
            new bootstrap.Modal(document.getElementById("modalAgregarUsuario")).show();
            <% } %>
        </script>
            </main>
    </div>
</div>
    </body>
</html>
<%!
    private String buscarNombreCarrera(ArrayList<Carrera> carreras, int idCarrera)
    {
        for (Carrera c : carreras)
        {
            if (c.getIdCarrera() == idCarrera)
            {
                return c.getNombreCarrera();
            }
        }
        return "—";
    }
%>
