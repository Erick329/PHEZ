<%-- 
    Document   : clientes
    Created on : 26 oct 2026, 0:36:02
    Author     : erick
--%>
<%@ include file="/WEB-INF/vistas/admin/_encabezado.jspf" %>
<h1 class="h3 fw-bold mb-3">Clientes y usuarios</h1>
<div class="caja table-responsive">
    <table class="table table-sm align-middle mb-0">
        <thead><tr><th>Nombre</th><th>Correo</th><th>Teléfono</th><th>Rol</th><th class="text-center">Pedidos</th><th>Registro</th><th>Estado</th><th class="text-end">Acciones</th></tr></thead>
        <tbody>
        <c:forEach var="user" items="${usuarios}">
            <tr><td><c:out value="${user.nombreCompleto}"/></td><td><c:out value="${user.email}"/></td><td><c:out value="${user.telefono}"/></td>
                <td><span class="badge ${user.rol == 'CLIENTE' ? 'text-bg-light border' : 'text-bg-primary'}">${user.rol}</span></td>
                <td class="text-center">${user.totalPedidos}</td>
                <td><fmt:formatDate value="${user.fechaRegistro}" pattern="dd/MM/yyyy"/></td>
                <td><c:choose>
                    <c:when test="${not user.activo}"><span class="badge text-bg-secondary">Inactivo</span></c:when>
                    <c:when test="${user.bloqueado}"><span class="badge text-bg-danger">Bloqueado</span></c:when>
                    <c:otherwise><span class="badge text-bg-success">Activo</span></c:otherwise></c:choose></td>
                <td class="text-end text-nowrap">
                    <c:if test="${user.idUsuario != sessionScope.usuario.idUsuario}">

                        <%-- Desbloquear --%>
                        <c:if test="${user.bloqueado}">
                            <form method="post" action="${raiz}/admin/clientes" class="d-inline">
                                <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                                <input type="hidden" name="id" value="${user.idUsuario}">
                                <button name="accion" value="desbloquear" class="btn btn-sm btn-outline-warning" title="Desbloquear"><i class="bi bi-unlock"></i></button>
                            </form>
                        </c:if>

                        <%-- Activar / desactivar --%>
                        <form method="post" action="${raiz}/admin/clientes" class="d-inline">
                            <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                            <input type="hidden" name="id" value="${user.idUsuario}">
                            <button name="accion" value="${user.activo ? 'desactivar' : 'activar'}" class="btn btn-sm ${user.activo ? 'btn-outline-danger' : 'btn-outline-success'}" title="${user.activo ? 'Desactivar' : 'Activar'}">
                                <i class="bi ${user.activo ? 'bi-person-x' : 'bi-person-check'}"></i></button>
                        </form>

                        <%-- Opciones solo para ADMIN --%>
                        <c:if test="${sessionScope.usuario.admin}">

                            <%-- Restablecer contraseña --%>
                            <form method="post" action="${raiz}/admin/clientes" class="d-inline">
                                <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                                <input type="hidden" name="id" value="${user.idUsuario}">
                                <button name="accion" value="restablecer" class="btn btn-sm btn-outline-secondary" title="Restablecer contraseña"
                                        onclick="return confirm('¿Generar una contraseña temporal para este usuario?')"><i class="bi bi-key"></i></button>
                            </form>

                            <%-- Cambiar rol --%>
                            <form method="post" action="${raiz}/admin/clientes" class="d-inline-flex gap-1 ms-1 align-middle">
                                <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                                <input type="hidden" name="id" value="${user.idUsuario}">
                                <input type="hidden" name="accion" value="rol">
                                <select name="idRol" class="form-select form-select-sm" style="width:auto">
                                    <option value="1" ${user.rol == 'ADMIN'    ? 'selected' : ''}>ADMIN</option>
                                    <option value="2" ${user.rol == 'VENDEDOR' ? 'selected' : ''}>VENDEDOR</option>
                                    <option value="3" ${user.rol == 'CLIENTE'  ? 'selected' : ''}>CLIENTE</option>
                                </select>
                                <button class="btn btn-sm btn-outline-primary" title="Cambiar rol"><i class="bi bi-arrow-repeat"></i></button>
                            </form>

                        </c:if>
                    </c:if>
                </td></tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@ include file="/WEB-INF/vistas/admin/_pie.jspf" %>