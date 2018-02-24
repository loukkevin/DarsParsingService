package kevin.project;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
public class Application {

@RequestMapping("/")
@ResponseBody
String home() {
    return "Dars Parsing Service";
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
