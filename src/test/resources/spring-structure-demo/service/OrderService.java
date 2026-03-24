package demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public String saveOrder() {
        try {
            throw new IllegalStateException("boom");
        } catch (Exception ex) {
            log.error("save failed");
            return null;
        }
    }
}
