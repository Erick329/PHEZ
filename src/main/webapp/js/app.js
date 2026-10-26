// Funciones generales del sitio (sin JavaScript en línea: la política CSP lo bloquea)
document.addEventListener('DOMContentLoaded', function () {

    // Confirmación antes de enviar formularios con data-confirmar="mensaje"
    document.querySelectorAll('form[data-confirmar]').forEach(function (f) {
        f.addEventListener('submit', function (e) {
            var msg = f.getAttribute('data-confirmar');
            if (msg && !confirm(msg)) e.preventDefault();
        });
    });

    // Checkout: mostrar datos de tarjeta solo si se elige ese método
    var datos = document.getElementById('datosTarjeta');
    if (datos) {
        var actualizar = function () {
            var tarjeta = document.getElementById('mTarjeta').checked;
            datos.style.display = tarjeta ? '' : 'none';
            datos.querySelectorAll('input').forEach(function (i) { i.required = tarjeta; });
        };
        document.querySelectorAll('input[name="metodo"]').forEach(function (r) { r.addEventListener('change', actualizar); });
        actualizar();
        // Evita doble envío del pago
        document.getElementById('formCheckout').addEventListener('submit', function () {
            var b = document.getElementById('btnPagar');
            setTimeout(function () { b.disabled = true; b.textContent = 'Procesando pago...'; }, 0);
        });
    }
    // ===== Panel: barra lateral que se contrae (se recuerda en el navegador) =====
    var barra = document.getElementById('barraLateral');
    var btnContraer = document.getElementById('btnContraer');
    if (barra && btnContraer) {
        try { if (localStorage.getItem('barraContraida') === '1') document.body.classList.add('barra-contraida'); } catch (e) { }
        btnContraer.addEventListener('click', function () {
            var contraida = document.body.classList.toggle('barra-contraida');
            try { localStorage.setItem('barraContraida', contraida ? '1' : '0'); } catch (e) { }
        });
        // Contraída: al pasar el mouse se despliega encima del contenido
        barra.addEventListener('mouseenter', function () { barra.classList.add('desplegada'); });
        barra.addEventListener('mouseleave', function () {
            if (!barra.querySelector('.dropdown-menu.show')) barra.classList.remove('desplegada');
        });
        // Si se cierra el menú del usuario con el mouse fuera, se vuelve a contraer
        barra.addEventListener('hidden.bs.dropdown', function () {
            if (!barra.matches(':hover')) barra.classList.remove('desplegada');
        });
    }

    // ===== Inicio: flechas del carrusel de productos =====
    document.querySelectorAll('.flecha-tira').forEach(function (b) {
        b.addEventListener('click', function () {
            var tira = document.getElementById(b.getAttribute('data-tira'));
            if (tira) tira.scrollBy({ left: Number(b.getAttribute('data-dir')) * tira.clientWidth * 0.8, behavior: 'smooth' });
        });
    });

    // ===== Login: mostrar / ocultar contraseña =====
    document.querySelectorAll('[data-ver]').forEach(function (b) {
        b.addEventListener('click', function () {
            var input = document.getElementById(b.getAttribute('data-ver'));
            var ver = input.type === 'password';
            input.type = ver ? 'text' : 'password';
            b.innerHTML = ver ? '<i class="bi bi-eye-slash"></i>' : '<i class="bi bi-eye"></i>';
        });
    });

    // ===== Registro: mensajes propios para la contraseña =====
    var regPass = document.querySelector('form[action$="/registro"] input[name="password"]');
    var regConf = document.querySelector('form[action$="/registro"] input[name="confirmar"]');
    if (regPass && regConf) {
        var validarPassword = function () {
            var v = regPass.value;
            if (v.length < 8) regPass.setCustomValidity('La contraseña debe tener al menos 8 caracteres.');
            else if (!/[A-Za-z]/.test(v) || !/[0-9]/.test(v)) regPass.setCustomValidity('La contraseña debe tener letras y números.');
            else regPass.setCustomValidity('');
            regConf.setCustomValidity(regConf.value !== '' && regConf.value !== v ? 'Las contraseñas no coinciden.' : '');
        };
        regPass.addEventListener('input', validarPassword);
        regConf.addEventListener('input', validarPassword);
        validarPassword();
    }

    // ===== Dashboard: gráfica de ventas por día =====
    var datosDia = document.getElementById('datosVentasDia');
    if (datosDia && typeof Chart !== 'undefined') {
        var d = JSON.parse(datosDia.textContent);
        new Chart(document.getElementById('gVentasDia'), {
            data: {
                labels: d.etiquetas,
                datasets: [
                    { type: 'bar', label: 'Ventas (Q)', data: d.montos, backgroundColor: '#2F6DB5', borderRadius: 4, yAxisID: 'y' },
                    { type: 'line', label: 'Pedidos', data: d.pedidos, borderColor: '#F2B134', backgroundColor: '#F2B134', cubicInterpolationMode: 'monotone', yAxisID: 'y1' }
                ]
            },
            options: {
                maintainAspectRatio: false,
                interaction: { mode: 'index', intersect: false },
                plugins: { tooltip: { callbacks: { label: function (c) {
                    return c.dataset.yAxisID === 'y'
                        ? ' Ventas: Q ' + Number(c.raw).toLocaleString('es-GT', { minimumFractionDigits: 2 })
                        : ' Pedidos: ' + c.raw;
                } } } },
                scales: {
                    y: { beginAtZero: true, title: { display: true, text: 'Quetzales' } },
                    y1: { beginAtZero: true, position: 'right', grid: { drawOnChartArea: false }, ticks: { precision: 0 }, title: { display: true, text: 'Pedidos' } }
                }
            }
        });
    }
});
