package com.example.javareviewer.samples;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class SampleTransactionalController {

    @Transactional
    private String save() {
        try {
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
