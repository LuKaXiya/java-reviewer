package samples;

import org.springframework.stereotype.Controller;

@Controller
public class SampleController {

    private final UserRepository userRepository;

    public SampleController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void handle() {
        try {
            System.out.println("demo");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
