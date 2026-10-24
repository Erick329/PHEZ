package com.phez.controlador.admin;

import com.phez.controlador.BaseServlet;
import com.phez.repositorio.PedidoRepositorio;
import com.phez.repositorio.ProductoRepositorio;
import com.phez.repositorio.ReporteRepositorio;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import com.phez.repositorio.LoteRepositorio;

@WebServlet("/admin/dashboard")
public class DashboardServlet extends BaseServlet {

    private final ReporteRepositorio reporte = new ReporteRepositorio();
    private final PedidoRepositorio pedido = new PedidoRepositorio();
    private final ProductoRepositorio producto = new ProductoRepositorio();
    private final LoteRepositorio lote = new LoteRepositorio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LocalDate hoy = LocalDate.now();
        try {
            req.setAttribute("hoy", reporte.resumen(hoy, hoy));
            req.setAttribute("mes", reporte.resumen(hoy.minusDays(29), hoy));
            req.setAttribute("recientes", pedido.listar(null, 8));
            req.setAttribute("bajoStock", producto.bajoStock());
            req.setAttribute("porVencer", lote.proximosAVencer(15));
            req.setAttribute("porAtender", pedido.listar("PAGADO", 50).size() + pedido.listar("PENDIENTE", 50).size());
            vista(req, resp, "admin/dashboard", "Dashboard");
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }
}
        