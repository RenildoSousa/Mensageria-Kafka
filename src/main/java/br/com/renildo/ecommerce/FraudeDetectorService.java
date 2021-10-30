package br.com.renildo.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class FraudeDetectorService {
    public static void main(String[] args) {
        var fraudeService = new FraudeDetectorService();
        try (var service = new KafkaService(EmailService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER", fraudeService::parse)) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, String> record) {
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
        System.out.println("Não foi encontrado nenhum indicio de fraude, pedido processado");
    }
}
