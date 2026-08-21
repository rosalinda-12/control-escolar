

function aplicarTema(tema)
{
    document.documentElement.setAttribute("data-theme", tema);
    localStorage.setItem("ce-tema", tema);

    document.querySelectorAll("[data-theme-toggle]").forEach(function (boton)
    {
        boton.setAttribute("aria-pressed", tema === "dark" ? "true" : "false");
    });

}

function inicializarBuscadoresDocentes()
{
    document.querySelectorAll(".docente-picker").forEach(function (picker)
    {
        var entrada = picker.querySelector(".asignacion-docente-select");
        var opciones = Array.from(picker.querySelectorAll(".docente-picker-option"));
        var oculto = picker.closest("form").querySelector(".docente-seleccionado-id");
        entrada.addEventListener("focus", function ()
        { picker.classList.add("is-open"); });
        entrada.addEventListener("input", function ()
        {
            oculto.value = "";
            picker.classList.add("is-open");
            var busqueda = normalizarTextoFiltro(entrada.value);
            opciones.forEach(function (opcion)
            {
                opcion.hidden = busqueda && !normalizarTextoFiltro(opcion.dataset.nombreDocente).includes(busqueda);
            });
        });
        opciones.forEach(function (opcion)
        {
            opcion.addEventListener("click", function ()
            {
                entrada.value = opcion.dataset.nombreDocente;
                oculto.value = opcion.dataset.idDocente;
                entrada.setCustomValidity("");
                picker.classList.remove("is-open");
            });
        });
        entrada.closest("form").addEventListener("submit", function (evento)
        {
            if (!oculto.value)
            {
                evento.preventDefault();
                entrada.setCustomValidity("Selecciona un docente de la lista.");
                entrada.reportValidity();
            } else
            {
                entrada.setCustomValidity("");
            }
        });
    });
    document.addEventListener("click", function (evento)
    {
        if (!evento.target.closest(".docente-picker")) document.querySelectorAll(".docente-picker.is-open").forEach(function (picker)
        { picker.classList.remove("is-open"); });
    });
}

function normalizarTextoFiltro(valor)
{
    return valor.toLocaleLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}

function compactarOpcionesDeModales()
{
    document.querySelectorAll(".modal select").forEach(function (select)
    {
        Array.from(select.options).forEach(function (opcion)
        {
            var textoCompleto = opcion.textContent.trim();
            if (textoCompleto.length <= 54)
            {
                return;
            }

            opcion.dataset.textoCompleto = textoCompleto;
            opcion.title = textoCompleto;
            opcion.textContent = textoCompleto.slice(0, 51) + "...";
        });

        function actualizarTitulo()
        {
            var opcionSeleccionada = select.options[select.selectedIndex];
            if (opcionSeleccionada)
            {
                select.title = opcionSeleccionada.dataset.textoCompleto || opcionSeleccionada.textContent.trim();
            }
        }

        select.addEventListener("change", actualizarTitulo);
        actualizarTitulo();
    });
}

function prepararModalNuevoDesdeEdicion(evento)
{
    var boton = evento.target.closest("[data-bs-toggle=\"modal\"][data-bs-target]");
    if (!boton || !window.location.search.includes("editar="))
    {
        return;
    }

    var textoBoton = boton.textContent.trim().toLocaleLowerCase();
    if (!/^(nuevo|agregar|registrar|crear|crea)\b/.test(textoBoton))
    {
        return;
    }

    var destino = boton.getAttribute("data-bs-target");
    if (!destino || !document.querySelector(destino))
    {
        return;
    }

    evento.preventDefault();
    evento.stopImmediatePropagation();

    var url = new URL(window.location.href);
    url.searchParams.delete("editar");
    url.searchParams.set("nuevo", "1");
    url.searchParams.set("modal", destino.substring(1));
    window.location.assign(url.toString());
}

function abrirModalNuevoDesdeUrl()
{
    var url = new URL(window.location.href);
    var nombreModal = url.searchParams.get("modal");
    if (url.searchParams.get("nuevo") !== "1" || !nombreModal)
    {
        return;
    }

    var modal = document.getElementById(nombreModal);
    if (modal && window.bootstrap)
    {
        bootstrap.Modal.getOrCreateInstance(modal).show();
        url.searchParams.delete("nuevo");
        url.searchParams.delete("modal");
        window.history.replaceState({}, document.title, url.toString());
    }
}

document.addEventListener("click", prepararModalNuevoDesdeEdicion, true);

document.addEventListener("DOMContentLoaded", function ()
{
    inicializarBuscadoresDocentes();
    compactarOpcionesDeModales();
    abrirModalNuevoDesdeUrl();

    document.querySelectorAll(".mensaje-error, .mensaje-exito").forEach(function (mensaje)
    {
        mensaje.setAttribute("role", "alert");
        if (!mensaje.querySelector("[data-cerrar-mensaje]"))
        {
            var cerrar = document.createElement("button");
            cerrar.type = "button";
            cerrar.className = "mensaje-cerrar";
            cerrar.setAttribute("data-cerrar-mensaje", "true");
            cerrar.setAttribute("aria-label", "Cerrar mensaje");
            cerrar.innerHTML = "<i class=\"bi bi-x-lg\"></i>";
            cerrar.addEventListener("click", function ()
            { mensaje.remove(); });
            mensaje.appendChild(cerrar);
        }
    });

    document.querySelectorAll("[data-theme-toggle]").forEach(function (boton)
    {
        boton.addEventListener("click", function ()
        {
            var actual = document.documentElement.getAttribute("data-theme") === "dark" ? "dark" : "light";
            aplicarTema(actual === "dark" ? "light" : "dark");
        });
    });

    document.querySelectorAll("[data-busqueda-tiempo-real]").forEach(function (formulario)
    {
        var campo = formulario.querySelector("input");
        var temporizador;
        var destino = document.querySelector("#resultadoInscripciones");

        function buscar()
        {
            if (!campo || !destino)
            {
                return;
            }

            var parametros = new URLSearchParams();
            if (campo.value.trim() !== "")
            {
                parametros.set(campo.name, campo.value.trim());
            }

            fetch(formulario.action + (parametros.toString() ? "?" + parametros.toString() : ""))
                    .then(function (respuesta)
                    { return respuesta.text(); })
                    .then(function (html)
                    {
                        var documento = new DOMParser().parseFromString(html, "text/html");
                        var nuevoResultado = documento.querySelector("#resultadoInscripciones");
                        if (nuevoResultado)
                        {
                            destino.innerHTML = nuevoResultado.innerHTML;
                            campo.focus();
                        }
                    });
        }

        if (campo)
        {
            campo.addEventListener("input", function ()
            {
                clearTimeout(temporizador);
                temporizador = setTimeout(buscar, 350);
            });
        }

        formulario.addEventListener("submit", function (evento)
        {
            evento.preventDefault();
            clearTimeout(temporizador);
            buscar();
        });

        var limpiar = formulario.querySelector("[data-busqueda-limpiar]");
        if (limpiar)
        {
            limpiar.addEventListener("click", function ()
            {
                campo.value = "";
                clearTimeout(temporizador);
                buscar();
                campo.focus();
            });
        }
    });

    document.addEventListener("change", function (evento)
    {
        var grupo = evento.target.closest(".group-option input[name='idGrupo']");
        if (!grupo)
        {
            return;
        }

        var formulario = grupo.closest("form");
        var etiqueta = grupo.closest(".group-option").querySelector(".group-option-top strong");
        var resumen = formulario ? formulario.querySelector("[data-grupo-seleccionado]") : null;
        if (etiqueta && resumen)
        {
            resumen.textContent = etiqueta.textContent.trim();
        }
    });

    document.querySelectorAll('form[method="post"], form[method="POST"]').forEach(function (formulario)
    {
        if (formulario.hasAttribute("onsubmit") || formulario.hasAttribute("data-confirm"))
        {
            return;
        }

        var accion = formulario.getAttribute("action") || "";
        if (/SLogin|SVerificar|SOlvideContrasena|SNuevaContrasena/.test(accion))
        {
            return;
        }

        formulario.addEventListener("submit", function (evento)
        {
            var boton = evento.submitter || formulario.querySelector("button[type=submit]");
            var operacion = boton ? boton.textContent.trim().toLowerCase() : "esta acción";
            if (!window.confirm("¿Confirmar " + operacion + "?"))
            {
                evento.preventDefault();
            }
        });
    });

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

    document.querySelectorAll("table.table-formal tbody").forEach(function (cuerpoTabla)
    {
        var filas = Array.prototype.slice.call(cuerpoTabla.querySelectorAll(":scope > tr"));
        var tamanoPagina = 10;
        if (filas.length <= tamanoPagina)
        {
            return;
        }

        var paginaActual = 1;
        var contenedorTabla = cuerpoTabla.closest(".tabla-formal-wrap") || cuerpoTabla.closest("table").parentElement;
        var paginacion = document.createElement("div");
        paginacion.className = "paginacion-registros";
        contenedorTabla.appendChild(paginacion);
        filas.forEach(function (fila)
        { fila.setAttribute("data-paginacion-visible", "true"); });

        function actualizarPaginacion()
        {
            var filasVisibles = filas.filter(function (fila)
            { return fila.getAttribute("data-paginacion-visible") !== "false"; });
            var totalPaginas = Math.max(1, Math.ceil(filasVisibles.length / tamanoPagina));
            paginaActual = Math.min(paginaActual, totalPaginas);
            filas.forEach(function (fila)
            { fila.style.display = "none"; });
            filasVisibles.slice((paginaActual - 1) * tamanoPagina, paginaActual * tamanoPagina).forEach(function (fila)
            { fila.style.display = ""; });

            paginacion.innerHTML = "";
            var anterior = document.createElement("button");
            anterior.type = "button";
            anterior.className = "btn btn-sm btn-outline-formal";
            anterior.innerHTML = '<i class="bi bi-chevron-left"></i><span class="visually-hidden">Anterior</span>';
            anterior.disabled = paginaActual === 1;
            anterior.addEventListener("click", function ()
            { paginaActual--; actualizarPaginacion(); });

            var indicador = document.createElement("span");
            indicador.className = "paginacion-indicador";
            indicador.textContent = "Página " + paginaActual + " de " + totalPaginas;

            var siguiente = document.createElement("button");
            siguiente.type = "button";
            siguiente.className = "btn btn-sm btn-outline-formal";
            siguiente.innerHTML = '<i class="bi bi-chevron-right"></i><span class="visually-hidden">Siguiente</span>';
            siguiente.disabled = paginaActual === totalPaginas;
            siguiente.addEventListener("click", function ()
            { paginaActual++; actualizarPaginacion(); });
            paginacion.append(anterior, indicador, siguiente);
        }

        cuerpoTabla._paginacion =
        { reiniciar: function ()
        { paginaActual = 1; actualizarPaginacion(); } };
        actualizarPaginacion();
    });

    document.querySelectorAll(".grafica-pastel").forEach(function (grafica)
    {
        var valores = grafica.getAttribute("data-valores").split(",").map(Number);
        var total = valores.reduce(function (suma, valor)
        { return suma + valor; }, 0);
        if (total > 0)
        {
            var colores = ["var(--accent)", "var(--accent-2)", "var(--color-info)"];
            var acumulado = 0;
            var segmentos = valores.map(function (valor, indice)
            {
                var inicio = acumulado;
                acumulado += valor * 100 / total;
                return colores[indice % colores.length] + " " + inicio + "% " + acumulado + "%";
            });
            grafica.querySelector(".grafica-pastel-circulo").style.background = "conic-gradient(" + segmentos.join(", ") + ")";
        }
    });

    document.querySelectorAll("[data-filtros-tabla]").forEach(function (barra)
    {
        var contenedor = document.querySelector(barra.getAttribute("data-filtros-tabla"));
        if (!contenedor)
        {
            return;
        }

        var selects = barra.querySelectorAll("select[data-filtro-campo]");
        var buscadorTabla = barra.querySelector("[data-filtro-texto]");

        var botonLimpiarFiltros = document.createElement("button");
        botonLimpiarFiltros.type = "button";
        botonLimpiarFiltros.className = "btn btn-sm btn-outline-formal filtro-limpiar-auto";
        botonLimpiarFiltros.innerHTML = "<i class=\"bi bi-eraser me-1\"></i>Limpiar";
        botonLimpiarFiltros.addEventListener("click", function ()
        {
            selects.forEach(function (select)
            { select.value = ""; });
            if (buscadorTabla) buscadorTabla.value = "";
            aplicarFiltrosTabla();
        });
        barra.appendChild(botonLimpiarFiltros);

        function aplicarFiltrosTabla()
        {
            var filas = contenedor.querySelectorAll("[data-fila-filtrable]");
            var visibles = 0;

            filas.forEach(function (fila)
            {
                var visible = true;

                selects.forEach(function (select)
                {
                    if (select.value !== "")
                    {
                        var campo = select.getAttribute("data-filtro-campo");
                        var valorFila = fila.getAttribute("data-" + campo);
                        var valoresFila = (valorFila || "").split("|").map(normalizarTextoFiltro);
                        if (!valoresFila.includes(normalizarTextoFiltro(select.value)))
                        {
                            visible = false;
                        }
                    }
                });

                if (visible && buscadorTabla && buscadorTabla.value.trim() !== "")
                {
                    var texto = normalizarTextoFiltro(buscadorTabla.value.trim());
                    if (!normalizarTextoFiltro(fila.textContent).includes(texto))
                    {
                        visible = false;
                    }
                }

                fila.style.display = visible ? "" : "none";
                fila.setAttribute("data-paginacion-visible", visible ? "true" : "false");
                if (visible)
                {
                    visibles++;
                }
            });

            var mensajeVacio = contenedor.parentElement.querySelector("[data-filtro-vacio]");
            if (mensajeVacio)
            {
                mensajeVacio.style.display = visibles === 0 ? "" : "none";
            }

            var contador = barra.querySelector("[data-filtro-contador]");
            if (contador)
            {
                contador.textContent = visibles + (visibles === 1 ? " registro" : " registros");
            }

            if (contenedor._paginacion)
            {
                contenedor._paginacion.reiniciar();
            }
        }

        selects.forEach(function (select)
        {
            select.addEventListener("change", aplicarFiltrosTabla);
        });

        if (buscadorTabla)
        {
            buscadorTabla.addEventListener("input", aplicarFiltrosTabla);
        }

        var botonLimpiar = barra.querySelector("[data-filtro-limpiar]");
        if (botonLimpiar)
        {
            botonLimpiar.addEventListener("click", function ()
            {
                selects.forEach(function (select)
                {
                    select.value = "";
                });
                if (buscadorTabla)
                {
                    buscadorTabla.value = "";
                }
                aplicarFiltrosTabla();
            });
        }

        aplicarFiltrosTabla();
    });

    document.querySelectorAll(".mensaje-exito[data-auto-ocultar], .mensaje-error[data-auto-ocultar]").forEach(function (mensaje)
    {
        setTimeout(function ()
        {
            mensaje.style.transition = "opacity 300ms ease";
            mensaje.style.opacity = "0";
            setTimeout(function ()
            { mensaje.remove(); }, 320);
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

    if ((evento.ctrlKey || evento.metaKey) && evento.key.toLowerCase() === "k")
    {
        var buscadorRapido = document.querySelector("[data-buscador]");
        if (buscadorRapido)
        {
            evento.preventDefault();
            buscadorRapido.focus();
        }
    }

    if ((evento.ctrlKey || evento.metaKey) && evento.shiftKey && evento.key.toLowerCase() === "l")
    {
        var actual = document.documentElement.getAttribute("data-theme") === "dark" ? "dark" : "light";
        aplicarTema(actual === "dark" ? "light" : "dark");
    }
});

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
        setTimeout(function ()
        { aviso.remove(); }, 320);
    }, 4200);
}
