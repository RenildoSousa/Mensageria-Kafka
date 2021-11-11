package br.com.renildo.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FraudeDetectorService {
    public static void main(String[] args) {
        var fraudeService = new FraudeDetectorService();
        try (var service = new KafkaService<>(FraudeDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER", fraudeService::parse, Order.class, Map.of())) {
            service.run();
        }
    }

    private final KafkaDispatcher<Order> orderKafkaDispatcher = new KafkaDispatcher<>();

    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException {
        System.out.println("______________________________________");
        System.out.println("Processando tópico, verivicando fraude");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        var order = record.value();

        if (isFraude(order)) {
            System.out.println("Está compra é uma fraude!!!");
            orderKafkaDispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getUserId(), order);
        }
        else {
            System.out.println("Aprovado: " + order);
            orderKafkaDispatcher.send("ECOMMERCE_ORDER_APROVED", order.getUserId(), order);
        }
    }

    private boolean isFraude(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) > 0;
    }
}
