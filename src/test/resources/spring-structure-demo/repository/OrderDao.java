package demo.repository;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDao {

    public String load(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
