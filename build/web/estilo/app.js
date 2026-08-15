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
});

document.addEventListener("DOMContentLoaded", function ()
{
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
});
