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
            <div class="mt-4">
                <h2>Usuarios</h2>
                <p class="texto-info mb-0">
                    Cuentas del sistema y su rol. Aquí no se crean cuentas nuevas: cada persona
                    (Alumno, Maestro o Subdirector) se autoregistra por su cuenta en la pantalla de
                    registro, usando el mismo correo con el que fue dada de alta en
                    Alumnos / Docentes / Subdirectores. Una vez que verifica su correo, aparece en
                    <a href="SSolicitudesRegistro">Solicitudes</a> para que la apruebes y pueda iniciar sesión.
                </p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje-error"><i class="bi bi-exclamation-triangle me-1"></i><%= request.getAttribute("error")%></div>
            <% } %>
            <% if (request.getAttribute("exito") != null) { %>
            <div class="mensaje-exito"><i class="bi bi-check-circle me-1"></i><%= request.getAttribute("exito")%></div>
            <% } %>

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
                    <tbody>
                        <% for (Usuario u : usuarios) { %>
                        <tr>
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
                                    <button type="submit" class="btn btn-sm btn-danger-formal">Eliminar</button>
                                </form>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
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
