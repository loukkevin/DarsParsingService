package kevin.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import java.time.Clock;
import static java.time.Clock.systemDefaultZone;
import org.apache.commons.lang.StringEscapeUtils;

@Component
public class ParseService {
String url;
Document document; 
List<Course> courseOk;
List<String> courseNotOk;
List<String> courseNotOkTitle;
List<Requirement> requirements;
List<Elective> electives;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public ProgramRequirements parse(String url) throws IOException {
		long startTime = System.nanoTime();
                File input = new File (url);
                document = Jsoup.parse(input, "UTF-8");//local file testing 
		//doc = Jsoup.connect(url).get();//actual URL input
		System.out.println("DARS Parsing");
		courseTaken();

		displayCourseTaken();
		
		courseNotTaken();

		ProgramRequirements progReqs = new ProgramRequirements(requirements,electives,courseOk);
                String requirementsJson = new Gson().toJson(requirements);
                String electivesJson = new Gson().toJson(electives);
            
                System.out.println("requirementsJson:\n" + requirementsJson);
                System.out.println("electivesJson:\n" + electivesJson);
                
                long endTime = System.nanoTime();

                long duration = (endTime - startTime)/1000000;
                System.out.println(duration);
		return progReqs;
	}
        
	private void courseNotTaken() throws IOException {
                requirements = new ArrayList();
                electives = new ArrayList();
		courseNotOk = new ArrayList();
		courseNotOkTitle = new ArrayList();
		Elements notTakenTitle = document.select("span[class=auditLineType_17_noSubrequirementTLine]");
		Elements notTaken = document.select("span[class=auditLineType_29_noSubrequirementAcceptCourses]");
		System.out.println("********************");
		System.out.println("\nCourses not taken");
		System.out.println("********************");
		for(Element course:notTaken) {
			String cNotTaken = course.text();
			courseNotOk.add(cNotTaken);	
		}
		
		for(Element title:notTakenTitle) {
			String requiredTitle = title.text();//;replaceAll("[0-9]+|\\-|\\)","").trim();
                        //System.out.println(requiredTitle);
			courseNotOkTitle.add(requiredTitle);
                        
                        if (!title.ownText().contains("OR") && 
                                !title.previousElementSibling().ownText().contains("OR") && 
                                !title.ownText().contains("Complete")){  
                        Requirement req = new Requirement(title);
                        System.out.println(req.getTitle());
                        System.out.println(req.getRequiredCourse().getName());
                        if (req.getRequiredCourse().getName().length() > 4 && req.getRequiredCourse().getName().length() < 8)//to avoid adding garbage to arrayList
                        requirements.add(req);
                        }
                        if ((title.ownText().contains("OR") || title.ownText().contains("Complete")) && !title.className().equals(title.previousElementSibling().className())){
                        Elective elective = new Elective(title);
                        //System.out.println(elective.getTitle());
                        //System.out.println(elective.getCourse().getTitle);
                        electives.add(elective);
                        }
		}
	
	}

	/**
	 * 
	 */
	private void displayCourseTaken() {
		for(int i = 0; i<courseOk.size(); i++) {		
			if(courseOk.get(i).equals("NAT:ELEC")) {
			courseOk.remove(i);
			} else if(courseOk.get(i).equals("CHEDAS:")) {
			courseOk.remove(i);}
			else if(courseOk.get(i).equals("NAT:TECH")) {
				courseOk.remove(i);}
			else {
				System.out.println(courseOk.get(i).getName());
			}
		}//en outforloop
	}

	/**
	 *  Course Taken
	 */
	private void courseTaken() {
		courseOk = new ArrayList();
		Elements ctable = document.select("span[class=auditLineType_22_okSubrequirementCourses]");
		System.out.println("********************");
		System.out.println("Courses that have been taken");
		System.out.println("********************");
		for(Element course:ctable) {
			String cTaken = course.text().substring(5,14).replaceAll(" ", "");
			//System.out.println(cTaken);
			Course newCourse = new Course(cTaken,null,0,"description",null);
			courseOk.add(newCourse);	
		}
	}

}
