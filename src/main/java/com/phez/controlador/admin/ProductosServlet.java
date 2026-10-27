package com.phez.controlador.admin;

import com.phez.controlador.BaseServlet;
import com.phez.repositorio.CategoriaRepositorio;
import com.phez.repositorio.ProductoRepositorio;
import com.phez.modelo.Producto;
import com.phez.util.Validador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/productos")
public class ProductosServlet extends BaseServlet {

    private final ProductoRepositorio productoRepositorio = new ProductoRepositorio();
    private final CategoriaRepositorio categoriaRepositorio = new CategoriaRepositorio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = Validador.limpiar(req.getParameter("accion"));
        try {
            if (accion.equals("nuevo") || accion.equals("editar")) {
                Producto p = accion.equals("editar")
                        ? productoRepositorio.buscar(Validador.entero(req.getParameter("id"), 0)) : new Producto();
                if (p == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                if (p.getIdProducto() == 0) p.setStockMinimo(5);
                mostrarFormulario(req, resp, p, null);
                return;
            }
            int categoria = Validador.entero(req.getParameter("categoria"), 0);
            String q = Validador.limpiar(req.getParameter("q"));
            req.setAttribute("productos", productoRepositorio.listar(categoria, q, false));
            req.setAttribute("categorias", categoriaRepositorio.listar(false));
            req.setAttribute("categoriaSel", categoria);
            req.setAttribute("q", q);
            vista(req, resp, "admin/productos", "Productos");
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = Validador.limpiar(req.getParameter("accion"));
        try {
            if (accion.equals("estado")) {
                int id = Validador.entero(req.getParameter("id"), 0);
                boolean activo = "1".equals(req.getParameter("activo"));
                productoRepositorio.cambiarStatus(id, activo ? "ACTIVO" : "INACTIVO");
                exito(req, activo ? "Producto activado." : "Producto desactivado (ya no aparece en el catálogo).");
                redirigir(req, resp, "/admin/productos");
                return;
            }

            // Guardar (nuevo o edición)
            Producto p = new Producto();
            p.setIdProducto(Validador.entero(req.getParameter("id"), 0));
            p.setCodigo(Validador.limpiar(req.getParameter("codigo")).toUpperCase());
            p.setNombre(Validador.limpiar(req.getParameter("nombre")));
            p.setIdCategoria(Validador.entero(req.getParameter("idCategoria"), 0));
            p.setDescripcion(Validador.limpiar(req.getParameter("descripcion")));
            BigDecimal precio = Validador.decimal(req.getParameter("precio"));
            p.setPrecio(precio == null ? BigDecimal.ZERO : precio);
            p.setStockMinimo(Validador.entero(req.getParameter("stockMinimo"), -1));
            p.setStatus(req.getParameter("activo") != null ? "ACTIVO" : "INACTIVO");

            List<String> errores = new ArrayList<>();
            if (!p.getCodigo().matches("[A-Z0-9-]{3,20}")) errores.add("El código debe tener de 3 a 20 letras, números o guiones.");
            if (Validador.vacio(p.getNombre()) || p.getNombre().length() > 100) errores.add("Ingrese el nombre (máximo 100 caracteres).");
            if (p.getIdCategoria() <= 0) errores.add("Seleccione una categoría.");
            if (precio == null || precio.signum() <= 0 || precio.compareTo(new BigDecimal("99999")) > 0) errores.add("Ingrese un precio válido mayor que cero.");
            if (p.getStockMinimo() < 0) errores.add("Ingrese el stock mínimo (0 o más).");
            if (Validador.vacio(p.getDescripcion()) || p.getDescripcion().length() > 255) errores.add("Ingrese la descripción (máximo 255 caracteres).");

            if (!errores.isEmpty()) {
                mostrarFormulario(req, resp, p, errores);
                return;
            }
            boolean nuevo = p.getIdProducto() == 0;
            try {
                productoRepositorio.guardar(p);
            } catch (SQLIntegrityConstraintViolationException e) {
                errores.add("Ya existe un producto con el código " + p.getCodigo() + ".");
                mostrarFormulario(req, resp, p, errores);
                return;
            }
            if (nuevo) {
                // El stock se maneja por lotes: después de crear el producto se registra su primer lote
                exito(req, "Producto \"" + p.getNombre() + "\" creado. Ahora registre su primer lote (entrada de inventario).");
                redirigir(req, resp, "/admin/inventario?producto=" + p.getIdProducto());
            } else {
                exito(req, "Producto \"" + p.getNombre() + "\" guardado correctamente.");
                redirigir(req, resp, "/admin/productos");
            }
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }

    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp, Producto p, List<String> errores)
            throws SQLException, ServletException, IOException {
        req.setAttribute("producto", p);
        req.setAttribute("errores", errores);
        req.setAttribute("categorias", categoriaRepositorio.listar(false));
        vista(req, resp, "admin/producto-form", p.getIdProducto() == 0 ? "Nuevo producto" : "Editar producto");
    }
}
