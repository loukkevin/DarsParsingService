/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kevin.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kevin
 */
@Component
public class DocumentHandler {
    public Document createDocument (String url) throws IOException{
       try{
           return Jsoup.connect(url).get();
       }catch (IOException e) {
            e.printStackTrace();
       }
       return null;
    }
}
