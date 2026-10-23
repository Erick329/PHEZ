<%-- 
    Document   : recuperar
    Created on : 23 oct 2026, 16:05:57
    Author     : erick
--%>

<%@ include file="/WEB-INF/vistas/otros/encabezado.jspf" %>
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="bg-white rounded-4 shadow-sm p-4 p-md-5">
                <h1 class="h4 fw-bold mb-2">Recuperar contraseña</h1>
                <p class="text-muted small">Ingrese su correo para restablecer la contraseña.</p>
                <form method="post" action="${raiz}/recuperar">
                    <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                    <input name="email" type="email" class="form-control mb-3" placeholder="correo@ejemplo.com" required>
                    <button class="btn btn-verde w-100">Enviar</button>
                </form>
                <p class="text-center small mt-3 mb-0"><a href="${raiz}/login">Volver a iniciar sesión</a></p>
            </div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/vistas/otros/pie.jspf" %>
