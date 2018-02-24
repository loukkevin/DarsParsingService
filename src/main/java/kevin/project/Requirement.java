package kevin.project;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Kevin
 */
public class Requirement {

    String title;
    Course requiredCourse;

    public Requirement(Element titleElement) throws IOException {
        Element requirementCourse = titleElement.nextElementSibling();
        this.title = titleElement.ownText();
        String requirementTitleClassName = titleElement.className();
        boolean isCourseElement = false;
        while (isCourseElement == false) {//Check to see if the title is contained in more than one element
            if (requirementTitleClassName.equals(requirementCourse.className())) {
                title.concat(requirementCourse.ownText());
                requirementCourse = requirementCourse.nextElementSibling();
            } else {
                isCourseElement = true;
            }
        }
        String courseTitle = requirementCourse.ownText().replaceAll("SELECT FROM: ", "");
        this.requiredCourse = new Course(courseTitle, null, 3, courseTitle,null);
        //getCourseInformation(this.requiredCourse);

    }

      public static String addSpaceToName(String courseName) {
        String[] courseNameSplit = courseName.split("(?=[0-9])");
        int index = 0;
        for (String split : courseNameSplit) {
            if (index == 0) {
                courseName = split;
            } else if (index == 1) {
                courseName = courseName + " " + split;
            } else {
                courseName = courseName + split;
            }
            index++;
        }

        return (courseName);

    }

    public void getCourseInformation(String course) throws IOException {
        String catalogUrl = "https://catalog.stcloudstate.edu/Catalog/ViewCatalog.aspx?pageid=viewcatalog&catalogid=8&loaduseredits=True&search=true&keywords=";

        //Change courseName to Dept%20Number, ex: SE 490 --> SE%20490
        String[] courseNameSplit = course.split(" ");
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

            List<String> addSemesterOffered = new ArrayList();
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
                addCredits = Integer.parseInt(dataSplit[0]);
                System.out.println("Credits: " + viewDetails.ownText());

            }
            setRequiredCourse(new Course(course, addPrerequisites, addCredits, addDescription, addSemesterOffered));
        }
    }

    public Course getRequiredCourse() {
        return requiredCourse;
    }

    public void setRequiredCourse(Course requiredCourse) {
        this.requiredCourse = requiredCourse;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
