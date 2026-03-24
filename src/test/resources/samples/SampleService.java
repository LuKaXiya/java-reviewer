package samples;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class SampleService {

    public String execute(HttpServletRequest request, Model model) {
        return request.getRequestURI() + model.toString();
    }
}
