package com.phez.controlador.admin;

import com.phez.controlador.BaseServlet;
import com.phez.modelo.Pedido;
import com.phez.modelo.Producto;
import com.phez.modelo.Reporte;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet("/admin/dashboard")
public class DashboardServlet extends BaseServlet {

    private final Reporte reporte = new Reporte();
    private final Pedido pedido = new Pedido();
    private final Producto producto = new Producto();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LocalDate hoy = LocalDate.now();
        try {
            req.setAttribute("hoy", reporte.resumen(hoy, hoy));
            req.setAttribute("mes", reporte.resumen(hoy.minusDays(29), hoy));
            req.setAttribute("recientes", pedido.listar(null, 8));
            req.setAttribute("bajoStock", producto.bajoStock());
            req.setAttribute("porVencer", new com.phez.modelo.Lote().proximosAVencer(15));
            req.setAttribute("porAtender", pedido.listar("PAGADO", 50).size() + pedido.listar("PENDIENTE", 50).size());
            vista(req, resp, "admin/dashboard", "Dashboard");
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }
}
        