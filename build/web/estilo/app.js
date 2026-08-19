/* ---------------------------------------------------------------------
   Modo oscuro
   El atributo data-theme ya se aplica lo antes posible con un script
   inline al inicio de cada shell (menu_*.jspf/.jsp) para evitar el
   parpadeo. Aquí solo queda cablear el botón y mantenerlo sincronizado.
   --------------------------------------------------------------------- */
function aplicarTema(tema)
{
    document.documentElement.setAttribute("data-theme", tema);
    localStorage.setItem("ce-tema", tema);

    document.querySelectorAll("[data-theme-toggle]").forEach(function (boton)
    {
        boton.setAttribute("aria-pressed", tema === "dark" ? "true" : "false");
    });
}

document.addEventListener("DOMContentLoaded", function ()
{
    document.querySelectorAll("[data-theme-toggle]").forEach(function (boton)
    {
        boton.addEventListener("click", function ()
        {
            var actual = document.documentElement.getAttribute("data-theme") === "dark" ? "dark" : "light";
            aplicarTema(actual === "dark" ? "light" : "dark");
        });
    });

    /* -----------------------------------------------------------------
       Atajos de teclado
       ----------------------------------------------------------------- */
    var botonSidebar = document.querySelector("[data-toggle-sidebar]");
    var barraLateral = document.querySelector(".sidebar");

    if (botonSidebar && barraLateral)
    {
        botonSidebar.addEventListener("click", function ()
        {
            barraLateral.classList.toggle("abierto");
        });

        document.addEventListener("click", function (evento)
        {
            var dentroDeSidebar = barraLateral.contains(evento.target);
            var esBoton = botonSidebar.contains(evento.target);

            if (!dentroDeSidebar && !esBoton)
            {
                barraLateral.classList.remove("abierto");
            }
        });
    }

    var filas = document.querySelectorAll("[data-fila-navegable]");

    filas.forEach(function (fila, indice)
    {
        fila.addEventListener("keydown", function (evento)
        {
            if (evento.key === "ArrowDown" && filas[indice + 1])
            {
                filas[indice + 1].focus();
            }

            if (evento.key === "ArrowUp" && filas[indice - 1])
            {
                filas[indice - 1].focus();
            }
        });
    });

    var buscador = document.querySelector("[data-buscador]");
    if (buscador)
    {
        buscador.addEventListener("input", function ()
        {
            var filtro = buscador.value.trim().toLowerCase();
            var filasTabla = document.querySelectorAll("[data-fila-buscable]");

            filasTabla.forEach(function (fila)
            {
                var coincide = fila.textContent.toLowerCase().includes(filtro);
                fila.style.display = coincide ? "" : "none";
            });
        });
    }

    /* Marca automáticamente los mensajes de éxito/error para que
       desaparezcan solos después de unos segundos (menos ruido visual). */
    document.querySelectorAll(".mensaje-exito[data-auto-ocultar], .mensaje-error[data-auto-ocultar]").forEach(function (mensaje)
    {
        setTimeout(function ()
        {
            mensaje.style.transition = "opacity 300ms ease";
            mensaje.style.opacity = "0";
            setTimeout(function () { mensaje.remove(); }, 320);
        }, 4000);
    });
});

document.addEventListener("keydown", function (evento)
{
    if (evento.key === "Escape")
    {
        var modalAbierto = document.querySelector(".modal.show");
        if (modalAbierto)
        {
            var instancia = bootstrap.Modal.getInstance(modalAbierto);
            if (instancia)
            {
                instancia.hide();
            }
        }
    }

    if (evento.key === "/" && document.activeElement.tagName !== "INPUT" && document.activeElement.tagName !== "TEXTAREA")
    {
        var buscador = document.querySelector("[data-buscador]");
        if (buscador)
        {
            evento.preventDefault();
            buscador.focus();
        }
    }

    /* Ctrl/Cmd + K también enfoca el buscador, como anuncia el atajo
       que se muestra junto a la barra de búsqueda. */
    if ((evento.ctrlKey || evento.metaKey) && evento.key.toLowerCase() === "k")
    {
        var buscadorRapido = document.querySelector("[data-buscador]");
        if (buscadorRapido)
        {
            evento.preventDefault();
            buscadorRapido.focus();
        }
    }

    /* Ctrl/Cmd + Shift + L alterna el modo oscuro desde cualquier lugar. */
    if ((evento.ctrlKey || evento.metaKey) && evento.shiftKey && evento.key.toLowerCase() === "l")
    {
        var actual = document.documentElement.getAttribute("data-theme") === "dark" ? "dark" : "light";
        aplicarTema(actual === "dark" ? "light" : "dark");
    }
});

/* ---------------------------------------------------------------------
   Notificaciones tipo "toast", reutilizables desde cualquier página:
   mostrarToast("Se guardó correctamente", "exito")
   --------------------------------------------------------------------- */
function mostrarToast(mensaje, tipo)
{
    var contenedor = document.querySelector(".toast-contenedor");
    if (!contenedor)
    {
        contenedor = document.createElement("div");
        contenedor.className = "toast-contenedor";
        document.body.appendChild(contenedor);
    }

    var icono = tipo === "error" ? "bi-exclamation-triangle" : "bi-check-circle";
    var aviso = document.createElement("div");
    aviso.className = "toast-premium " + (tipo === "error" ? "error" : "exito");
    aviso.innerHTML = '<i class="bi ' + icono + '"></i><span>' + mensaje + "</span>";
    contenedor.appendChild(aviso);

    setTimeout(function ()
    {
        aviso.style.transition = "opacity 300ms ease, transform 300ms ease";
        aviso.style.opacity = "0";
        aviso.style.transform = "translateX(20px)";
        setTimeout(function () { aviso.remove(); }, 320);
    }, 4200);
}
