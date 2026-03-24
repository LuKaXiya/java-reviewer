package demo.controller;

import demo.repository.OrderRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public int submit() {
        int total = 0;
        for (int index = 0; index < 10; index++) {
            total += index;
        }
        return total + orderRepository.count();
    }
}
