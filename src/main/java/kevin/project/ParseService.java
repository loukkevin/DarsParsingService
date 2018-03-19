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
import static kevin.project.Elective.addSpaceToName;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;

@Component
public class ParseService {
String url;
List<Course> courseOk;
List<String> courseNotOk;
List<String> courseNotOkTitle;
List<Requirement> requirements;
List<Elective> electives;	

	/**
	 * @param args
	 * @throws IOException 
	 */
	public ProgramRequirements parse(Document document) throws IOException {
		long startTime = System.nanoTime();
//                File input = new File (url);
//                document = handler.createDocument(url);//local file testing 
//		//document = Jsoup.connect(url).get();//actual URL input
		System.out.println("DARS Parsing");
		courseTaken(document);

		displayCourseTaken(document);
		
		courseNotTaken(document);

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
        
        public Course getCourseInformation(String course) throws IOException {
        List<String> addSemesterOffered = new ArrayList();
        String catalogUrl = "https://catalog.stcloudstate.edu/Catalog/ViewCatalog.aspx?pageid=viewcatalog&catalogid=8&loaduseredits=True&search=true&keywords=";
        String temp = addSpaceToName(course);
        //Change courseName to Dept%20Number, ex: SE 490 --> SE%20490
        String[] courseNameSplit = temp.split(" ");
        String courseName = courseNameSplit[0] + "%20" + courseNameSplit[1]; //all course names will be split into 2

        //Add courseName to end of url
        catalogUrl = catalogUrl + courseName;
        System.out.println("Connecting to " + catalogUrl);

        //Query the catalog
        Document doc = Jsoup.connect(catalogUrl).get();
        int index = 0;
        Elements allElements = doc.getElementsMatchingOwnText("view details...");
        Element viewDetails = null;//this variable is reused many times
        for (Element element : allElements) {
            viewDetails = element;
        }
        if (viewDetails != null) {  //if null this means no results were found
            Attributes attributes = viewDetails.attributes();
            String detailsUrl = "https://catalog.stcloudstate.edu";

            for (Attribute attribute : attributes) {
                if (index == 1) {
                    detailsUrl = detailsUrl + attribute.getValue();
                    System.out.println("view details page url: " + attribute.getValue());
                }
                index++;
            }

            //Connect to the details page
            System.out.println("Connecting to catalog at: " + detailsUrl);
            Document detailsDoc = Jsoup.connect(detailsUrl).get();

            //really only getting one element in each list, could cause errors if more than one element contained these keywords
            Elements descriptions = detailsDoc.getElementsMatchingOwnText("Description:");
            Elements prerequisites = detailsDoc.getElementsMatchingOwnText("Prerequisites:");
            Elements semesterOffered = detailsDoc.getElementsMatchingOwnText("Semester Offered:");
            Elements credits = detailsDoc.getElementsMatchingOwnText("Credits:");

            String addDescription = "";
            for (Element description : descriptions) {

                viewDetails = description.nextElementSibling();
                addDescription = (viewDetails.ownText());
                System.out.println("Description:" + viewDetails.ownText());

            }
            List<String> addPrerequisites = new ArrayList();
            for (Element prerequisite : prerequisites) {//check for child nodes to detect if more than one prereq

                if (prerequisite.nextElementSibling().children().isEmpty()) {
                    viewDetails = prerequisite.nextElementSibling().child(0);
                    addPrerequisites.add(viewDetails.ownText());
                    System.out.println("Prereqs:" + viewDetails.ownText());
                } else {
                    List<String> prereqs = new ArrayList();
                    String semestersOffered = "";
                    viewDetails = prerequisite.nextElementSibling();
                    Elements listElements = viewDetails.children();
                    for (Element item : listElements) {
                        addPrerequisites.add(item.ownText());
                        System.out.println(item.ownText());
                    }
                }
            }

            
            for (Element semester : semesterOffered) {//check for child nodes to detect if more than one semester offered

                if (semester.nextElementSibling().children().isEmpty()) {//only one semester offered
                    viewDetails = semester.nextElementSibling();
                    addSemesterOffered.add(viewDetails.ownText());
                    System.out.println("Semesters offered:" + viewDetails.ownText());
                } else {//multiple semesters offered
                    viewDetails = semester.nextElementSibling();
                    Elements listElements = viewDetails.child(0).children();//unordered list is the child, list items are the children
                    for (Element item : listElements) {
                        addSemesterOffered.add(item.ownText());
                        System.out.println(item.ownText());
                    }
                }
            }
            int addCredits = 0;
            for (Element credit : credits) {

                viewDetails = credit.parent();//parent contains the number of credits due to styling tags
                String data = viewDetails.ownText();
                //Clean up white spaces, there might be a better way to do this
                String[] dataSplit = data.split(" |-");
                if (dataSplit.length == 1)
                addCredits = Integer.parseInt(dataSplit[0]);
                else if (dataSplit.length == 2)
                addCredits = Integer.parseInt(dataSplit[1]);
                System.out.println("Credits: " + viewDetails.ownText());

            }
            return new Course(course, addPrerequisites, addCredits, addDescription, addSemesterOffered);
        }
        else{
            addSemesterOffered.add("Fall");
            addSemesterOffered.add("Spring");
            addSemesterOffered.add("Summer");
            return new Course(course, null, 3, "no course information available, the system will automatically assign 3 credits to this course and allow scheduling in any semester. Check with an advisor to confirm this course's scheduling.", addSemesterOffered);
            
        }
    }
	private void courseNotTaken(Document document) throws IOException {
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
			String requiredTitle = title.ownText();//;replaceAll("[0-9]+|\\-|\\)","").trim();
                        //System.out.println(requiredTitle);
			courseNotOkTitle.add(requiredTitle);
                        
                        if (!title.ownText().contains("OR") && 
                                !title.previousElementSibling().ownText().contains("OR") && 
                                !title.ownText().contains("Complete")){  
                        Requirement req = new Requirement(title);
                        System.out.println(req.getTitle());
                        System.out.println(req.getRequiredCourse().getName());
                        if (req.getRequiredCourse().getName().length() > 4 && req.getRequiredCourse().getName().length() < 8)//to avoid adding too much garbage to arrayList
                        requirements.add(req);
                        }
                        
                        //Check if this is the first title element for an elective section, to only create one elective object per section
                        if (title.previousElementSibling() != null && !title.className().equals(title.previousElementSibling().className())){
                            while (title.nextElementSibling() != null && title.className().equals(title.nextElementSibling().className())){
                                title = title.nextElementSibling();
                                requiredTitle = requiredTitle + " " + title.ownText();
                            }
                            
                        if ((requiredTitle.contains("OR") || 
                             requiredTitle.contains("Complete") || requiredTitle.contains("Electives"))){
                            
                        Elective elective = new Elective(title, requiredTitle);
                        //System.out.println(elective.getTitle());
                        //System.out.println(elective.getCourse().getTitle);
                        electives.add(elective);
                            }
                        }//elective block
           
		}
	
	}

	/**
	 * 
	 */
	private void displayCourseTaken(Document document1) {
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
	private void courseTaken(Document document) {
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
