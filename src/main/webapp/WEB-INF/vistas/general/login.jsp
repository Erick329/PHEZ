<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="raiz" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar sesión | Venta de Alimentos Phez</title>
    <link href="${raiz}/lib/bootstrap/bootstrap.min.css" rel="stylesheet">
    <link href="${raiz}/lib/bootstrap-icons/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${raiz}/css/estilos.css?v=2" rel="stylesheet">
</head>
<body class="pagina-login">
    <div class="login-decoracion" aria-hidden="true"><span>🥑</span><span>🌽</span><span>🥛</span><span>🍅</span><span>🍞</span><span>🥕</span></div>

    <main class="login-contenedor">
        <a href="${raiz}/inicio" class="login-logo text-decoration-none">
            <span class="login-logo-icono"><i class="bi bi-basket2-fill"></i></span>
            <span class="login-logo-texto"><b>Phez</b><small>ALIMENTOS</small></span>
        </a>

        <div class="login-tarjeta">
            <h1 class="h4 text-center mb-4">Ingreso</h1>
            <c:if test="${not empty error}"><div class="alert alert-danger small py-2"><c:out value="${error}"/></div></c:if>
            <c:if test="${not empty sessionScope.flashOk}">
                <div class="alert alert-success small py-2"><c:out value="${sessionScope.flashOk}"/></div>
                <c:remove var="flashOk" scope="session"/>
            </c:if>

            <form method="post" action="${raiz}/login" id="formLogin">
                <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                <input type="hidden" name="next" value="<c:out value='${next}'/>">

                <div class="campo-linea mb-3">
                    <input id="email" name="email" type="email" placeholder=" " required autofocus value="<c:out value='${email}'/>">
                    <label for="email">Correo electrónico *</label>
                </div>
                <div class="campo-linea mb-2">
                    <input id="password" name="password" type="password" placeholder=" " required>
                    <label for="password">Contraseña *</label>
                    <button type="button" class="ver-password" data-ver="password" aria-label="Mostrar contraseña"><i class="bi bi-eye"></i></button>
                </div>

                <div class="text-center my-3"><a href="${raiz}/recuperar" class="small enlace-login">¿Olvidaste tu contraseña?</a></div>
                <div class="text-center small text-muted">¿No tienes cuenta? <a href="${raiz}/registro?next=<c:out value='${next}'/>" class="enlace-login">Crear una cuenta</a></div>

                <button class="login-boton" type="submit" aria-label="Ingresar" title="Ingresar"><i class="bi bi-chevron-right"></i></button>
            </form>
        </div>

        <a href="${raiz}/inicio" class="login-volver"><i class="bi bi-arrow-left"></i> Volver a la tienda</a>
    </main>

    <script src="${raiz}/lib/bootstrap/bootstrap.bundle.min.js"></script>
    <script src="${raiz}/js/app.js?v=2"></script>
</body>
</html>
