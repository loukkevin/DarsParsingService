package kevin.project;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DarsController {
@Autowired ParseService parser;

@CrossOrigin(origins = "https://obscure-shelf-53073.herokuapp.com/")
@RequestMapping("/parse")
	    public ProgramRequirements parseDarsFile(@RequestParam(value="darsURL", required=true) String url) throws IOException {
	        return parser.parse(url);
	    }
            
@RequestMapping("/")
@ResponseBody
String home() {
    return "Dars Parsing Service";
    }
}
