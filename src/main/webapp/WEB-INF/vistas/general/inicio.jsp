<%@ include file="/WEB-INF/vistas/otros/encabezado.jspf" %>
<c:set var="volverA" value="/inicio"/>
<c:forEach var="cat" items="${menuCategorias}">
    <c:if test="${cat.icono == '🥕'}"><c:set var="idFrutas" value="${cat.idCategoria}"/></c:if>
    <c:if test="${cat.icono == '🥛'}"><c:set var="idLacteos" value="${cat.idCategoria}"/></c:if>
</c:forEach>
<div class="container">

    <div id="carruselInicio" class="carousel slide carrusel-inicio rounded-4 overflow-hidden shadow-sm mb-4" data-bs-ride="carousel" data-bs-interval="5000">
        <div class="carousel-indicators">
            <button type="button" data-bs-target="#carruselInicio" data-bs-slide-to="0" class="active" aria-current="true" aria-label="Promoción 1"></button>
            <button type="button" data-bs-target="#carruselInicio" data-bs-slide-to="1" aria-label="Promoción 2"></button>
            <button type="button" data-bs-target="#carruselInicio" data-bs-slide-to="2" aria-label="Promoción 3"></button>
            <button type="button" data-bs-target="#carruselInicio" data-bs-slide-to="3" aria-label="Promoción 4"></button>
        </div>
        <div class="carousel-inner">
            <div class="carousel-item active slide-1">
                <div class="slide-contenido">
                    <div>
                        <span class="etiqueta-slide">Temporada</span>
                        <h2 class="display-6 fw-bold">Frutas y verduras frescas</h2>
                        <p class="lead mb-3">Seleccionadas cada mañana de productores locales.</p>
                        <a href="${raiz}/catalogo${empty idFrutas ? '' : '?categoria='.concat(idFrutas)}" class="btn btn-light btn-lg rounded-pill px-4">Comprar aquí</a>
                    </div>
                    <div class="slide-arte">🥑🍅🥕</div>
                </div>
            </div>
            <div class="carousel-item slide-2">
                <div class="slide-contenido">
                    <div>
                        <span class="etiqueta-slide">Envíos</span>
                        <h2 class="display-6 fw-bold">Envío gratis desde Q 200</h2>
                        <p class="lead mb-3">Entregas en Portales y municipios cercanos.</p>
                        <a href="${raiz}/catalogo" class="btn btn-light btn-lg rounded-pill px-4">Ver catálogo</a>
                    </div>
                    <div class="slide-arte">🚚📦</div>
                </div>
            </div>
            <div class="carousel-item slide-3">
                <div class="slide-contenido">
                    <div>
                        <span class="etiqueta-slide">Lácteos</span>
                        <h2 class="display-6 fw-bold">Leche, quesos y yogur</h2>
                        <p class="lead mb-3">Frescos y siempre con la fecha de vencimiento a la vista.</p>
                        <a href="${raiz}/catalogo${empty idLacteos ? '' : '?categoria='.concat(idLacteos)}" class="btn btn-light btn-lg rounded-pill px-4">Comprar aquí</a>
                    </div>
                    <div class="slide-arte">🥛🧀</div>
                </div>
            </div>
            <div class="carousel-item slide-4">
                <div class="slide-contenido">
                    <div>
                        <span class="etiqueta-slide">Compra segura</span>
                        <h2 class="display-6 fw-bold">Crea tu cuenta y compra en minutos</h2>
                        <p class="lead mb-3">Paga con tarjeta, transferencia o en efectivo al recibir.</p>
                        <c:choose>
                            <c:when test="${empty u}"><a href="${raiz}/registro" class="btn btn-light btn-lg rounded-pill px-4">Crear cuenta</a></c:when>
                            <c:otherwise><a href="${raiz}/cliente/pedidos" class="btn btn-light btn-lg rounded-pill px-4">Ver mis pedidos</a></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="slide-arte">🛒🔒</div>
                </div>
            </div>
        </div>
        <button class="carousel-control-prev" type="button" data-bs-target="#carruselInicio" data-bs-slide="prev">
            <span class="carousel-control-prev-icon" aria-hidden="true"></span><span class="visually-hidden">Anterior</span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#carruselInicio" data-bs-slide="next">
            <span class="carousel-control-next-icon" aria-hidden="true"></span><span class="visually-hidden">Siguiente</span>
        </button>
    </div>

    <h2 class="h5 fw-bold mb-3">Compra por categoría</h2>
    <div class="d-flex flex-wrap justify-content-center justify-content-md-start gap-3 gap-md-4 mb-4">
        <c:forEach var="cat" items="${menuCategorias}">
            <a href="${raiz}/catalogo?categoria=${cat.idCategoria}" class="circulo-categoria text-decoration-none text-center">
                <span class="circulo icono-cat-${cat.idCategoria}">${cat.icono}</span>
                <span class="d-block small fw-semibold text-dark mt-2"><c:out value="${cat.nombre}"/></span>
            </a>
        </c:forEach>
    </div>

    <div class="d-flex justify-content-between align-items-center mb-2">
        <h2 class="h5 fw-bold mb-0">Productos destacados</h2>
        <div class="d-flex gap-2">
            <button class="btn btn-sm btn-outline-primario rounded-circle flecha-tira" type="button" data-tira="tiraProductos" data-dir="-1" aria-label="Anterior"><i class="bi bi-chevron-left"></i></button>
            <button class="btn btn-sm btn-outline-primario rounded-circle flecha-tira" type="button" data-tira="tiraProductos" data-dir="1" aria-label="Siguiente"><i class="bi bi-chevron-right"></i></button>
            <a href="${raiz}/catalogo" class="btn btn-sm btn-primario rounded-pill px-3">Ver todo</a>
        </div>
    </div>
    <div id="tiraProductos" class="tira-productos pb-2">
        <c:forEach var="p" items="${destacados}">
            <div class="tira-item"><%@ include file="/WEB-INF/vistas/otros/tarjeta-producto.jspf" %></div>
        </c:forEach>
    </div>

    <div class="row g-3 mt-4 text-center">
        <div class="col-md-4"><div class="p-3 bg-white rounded-3 shadow-sm h-100"><i class="bi bi-truck fs-2 text-primario"></i><h3 class="h6 mt-2">Envío gratis desde Q 200</h3><p class="small text-muted mb-0">Entregas en Portales y municipios cercanos.</p></div></div>
        <div class="col-md-4"><div class="p-3 bg-white rounded-3 shadow-sm h-100"><i class="bi bi-shield-check fs-2 text-primario"></i><h3 class="h6 mt-2">Compra 100 % segura</h3><p class="small text-muted mb-0">Tus datos viajan cifrados y no guardamos tu tarjeta.</p></div></div>
        <div class="col-md-4"><div class="p-3 bg-white rounded-3 shadow-sm h-100"><i class="bi bi-basket fs-2 text-primario"></i><h3 class="h6 mt-2">Productos de calidad</h3><p class="small text-muted mb-0">Seleccionados de productores locales.</p></div></div>
    </div>
</div>
<%@ include file="/WEB-INF/vistas/otros/pie.jspf" %>
