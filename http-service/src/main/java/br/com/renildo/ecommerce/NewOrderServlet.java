package br.com.renildo.ecommerce;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {
    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();


    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var email = req.getParameter("email");

            var orderId = UUID.randomUUID().toString();
            var amount = new BigDecimal(req.getParameter("amount"));

            var order = new Order(orderId, amount, email);

            orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

            var emailCode = "Bem vindo! Sua ordem está sendo processada!";
            emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);

            System.out.println("Nova compra realizada com sucesso !!");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("Nova compra realizada ");
        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }
    }
}
