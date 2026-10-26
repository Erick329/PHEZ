package com.phez.controlador.cliente;

import com.phez.controlador.BaseServlet;
import com.phez.repositorio.ProductoRepositorio;
import com.phez.modelo.Carrito;
import com.phez.modelo.ItemCarrito;
import com.phez.modelo.Pedido;
import com.phez.modelo.Producto;
import com.phez.modelo.Usuario;
import com.phez.servicio.CorreoService;
import com.phez.servicio.PedidoService;
import com.phez.util.ReglasDeNegocio;
import com.phez.util.Validador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet("/cliente/checkout")
public class CheckoutServlet extends BaseServlet {
    
    private static final List<String> METODOS =Arrays.asList("TARJETA","TRANSFERENCIA","EFECTIVO");
    
    private final PedidoService pedidoService = new PedidoService();
    private final ProductoRepositorio productoRepositorio = new ProductoRepositorio();
    private final CorreoService correoService = new CorreoService ();
    
    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Carrito carrito = carrito(req);
        if (carrito.isVacio()) {
            error (req, "su carrito está vacío.");
            redirigir(req, resp, "/catalogo");
            return;
        }
        try {
            List<String> avisos = new ArrayList<>();
            for (ItemCarrito item : carrito.getItems()) {
                Producto actual = productoRepositorio.buscar(item.getProducto().getIdProducto());
                if (actual == null || !actual.isActivo()) {
                    carrito.eliminar(item.getProducto().getIdProducto());
                    avisos.add(item.getProducto().getNombre() + " ya no está disponible y se quitó del carrito.");
                } else {
                    item.setProducto(actual);
                    if (actual.getStock() < item.getCantidad()) {
                        avisos.add("Solo quedan " + actual.getStock() + " unidades de " + actual.getNombre() + ". Ajuste la cantidad en el carrito.");
                    }
                }
            }
            req.setAttribute("avisos", avisos);
            req.setAttribute("direccion", usuario(req).getDireccion());
            req.setAttribute("metodo", "TARJETA");
            vista(req, resp, "cliente/checkout", "Finalizar compra");
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario u = usuario(req);
        Carrito carrito = carrito(req);
        String direccion = Validador.limpiar(req.getParameter("direccion"));
        String metodo = Validador.limpiar(req.getParameter("metodo"));

        String error = null;
        if (direccion.length() < 10 || direccion.length() > 200) error = "Ingrese una dirección de envío completa (10 a 200 caracteres).";
        else if (!METODOS.contains(metodo)) error = "Seleccione un método de pago.";

        if (error == null) {
            try {
                Pedido pedido = pedidoService.crearPedido(u, carrito, direccion, metodo,
                        req.getParameter("tarjeta"), req.getParameter("vencimiento"), req.getParameter("cvv"),
                        u.getIdUsuario());
                carrito.vaciar();
                correoService.enviarConfirmacion(u, pedido);
                exito(req, "¡Gracias por su compra! Su pedido #" + pedido.getIdPedido() + " fue registrado.");
                redirigir(req, resp, "/cliente/pedido?id=" + pedido.getIdPedido() + "&nuevo=1");
                return;
            } catch (ReglasDeNegocio e) {
                error = e.getMessage();
            } catch (SQLException e) {
                errorBD(req, resp, e);
                return;
            }
        }
        req.setAttribute("error", error);
        req.setAttribute("direccion", direccion);
        req.setAttribute("metodo", metodo);
        vista(req, resp, "cliente/checkout", "Finalizar compra");
    }
}






