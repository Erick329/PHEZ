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
import com.phez.modelo.Reporte;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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
            req.setAttribute("jsonVentasDia", ventasPorDia(hoy.minusDays(13), hoy));
            vista(req, resp, "admin/dashboard", "Dashboard");
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }

    private String ventasPorDia(LocalDate desde, LocalDate hasta) throws SQLException {
        Map<String, Reporte> porFecha = new HashMap<>();
        for (Reporte r : reporte.ventasPorPeriodo(desde, hasta)) {
            porFecha.put(r.getEtiqueta(), r);          // etiqueta = "yyyy-MM-dd"
        }
        DateTimeFormatter corto = DateTimeFormatter.ofPattern("dd/MM");
        StringBuilder etiquetas = new StringBuilder(), montos = new StringBuilder(), pedidos = new StringBuilder();
        for (LocalDate d = desde; !d.isAfter(hasta); d = d.plusDays(1)) {
            Reporte r = porFecha.get(d.toString());
            if (etiquetas.length() > 0) { etiquetas.append(','); montos.append(','); pedidos.append(','); }
            etiquetas.append('"').append(d.format(corto)).append('"');
            montos.append(r == null || r.getMonto() == null ? "0" : r.getMonto().toPlainString());
            pedidos.append(r == null ? 0 : r.getCantidad());
        }
        return "{\"etiquetas\":[" + etiquetas + "],\"montos\":[" + montos + "],\"pedidos\":[" + pedidos + "]}";
    }
}
