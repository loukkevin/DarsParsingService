package kevin.project;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * s
 *
 * @author Kevin
 */
public class Elective {

    String title;
    List<Course> electiveCourses;
    int numOfCredits = 0;
    int numOfCourses = 0;

    public List<Course> getElectiveCourses() {
        return electiveCourses;
    }

    public void setElectiveCourses(List<Course> electiveCourses) {
        this.electiveCourses = electiveCourses;
    }

    public int getNumOfCredits() {
        return numOfCredits;
    }

    public void setNumOfCredits(int numOfCredits) {
        this.numOfCredits = numOfCredits;
    }

    public int getNumOfCourses() {
        return numOfCourses;
    }

    public void addElectiveCourse(Course course) {
        electiveCourses.add(course);
    }

    public void setNumOfCourses(int numOfCourses) {
        this.numOfCourses = numOfCourses;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Elective(Element titleElement) throws IOException {
        Element electiveTitle = titleElement;
        Element electiveCoursesText = titleElement.nextElementSibling();
        this.title = titleElement.ownText();
        this.electiveCourses = new ArrayList();

        String electiveTitleClassName = electiveTitle.className();
        boolean isCourseElement = false;
        while (isCourseElement == false) {//Check to see if the title is contained in more than one element
            if (electiveTitleClassName.equals(electiveCoursesText.className())) {
                title = title.concat(" " + electiveCoursesText.ownText());
                electiveCoursesText = electiveCoursesText.nextElementSibling();

            } else {
                isCourseElement = true;
            }
        }

        //determine if the elective has a minimum credit requirement or a minimum courses requirement
        boolean done = false;

        if (title.contains("credits")) {
            String[] temp = title.split(" ");
            String temp2;
            int index = 0;
            while (!done) {
                temp2 = temp[index];
                if (temp2.contains("credits")) {
                    this.numOfCredits = Integer.parseInt(temp[index - 1]);
                    done = true;
                }
                index++;
            }
        } else if (title.contains("courses")) {
            done = false;
            String[] temp = title.split(" ");
            String temp2;
            int index = 0;
            while (!done) {
                temp2 = temp[index];
                if (temp2.contains("courses")) {
                    this.numOfCourses = Integer.parseInt(temp[index - 1]);
                    done = true;
                }
                index++;
            }
        } else {
            this.numOfCourses = 1;
        }

        //Retrieve courses and transform the data to be uniformly formatted
        String electiveCoursesString = electiveCoursesText.ownText().replaceAll("SELECT FROM: ", "");
        if (electiveCoursesText.className().equals(electiveCoursesText.nextElementSibling().className())) {
            electiveCoursesString += (" " + electiveCoursesText.nextElementSibling().ownText());
        }

        System.out.println(title);
        String[] coursesTypeString = electiveCoursesString.split("[0-9|,| |(|)]+");
        String[] coursesStringSplit = electiveCoursesString.split("[,| |(|)]+");
        List<String> courseTypeList = new ArrayList();
        List<String> courses = new ArrayList();
        for (int i = 0; i < coursesTypeString.length; i++) {
            courseTypeList.add(coursesTypeString[i]);
            // System.out.println(coursesTypeString[i]);
        }
        int index = 0;
        String courseType = coursesTypeString[index];
        String value;
        for (int i = 0; i < coursesStringSplit.length; i++) {
            value = coursesStringSplit[i];
            if (value.contains(courseType)) {//first course of type
                if (value.length() > 4) {
                    System.out.println(value);
                    getCourseInformation(value);

                }
            } else if (!value.contains(courseType)) {//remaining
                String courseTypeAndNumber = courseType + value;
                if (courseTypeAndNumber.length() > 4) {
                    System.out.println(courseTypeAndNumber);
                    getCourseInformation(courseTypeAndNumber);
                }
            }
            //check to see if next value contains the next course type
            if (index < (coursesTypeString.length - 1) && coursesStringSplit[i + 1].contains(coursesTypeString[index + 1])) {
                index++;
                courseType = coursesTypeString[index];
            }
        }

        //Get the information for all the courses
        Iterator coursesIterator = electiveCourses.iterator();
        while (courses.iterator().hasNext()) {
            Course course = (Course) coursesIterator.next();
            //add a space between department and course number

        }

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
                if (dataSplit.length == 1)
                addCredits = Integer.parseInt(dataSplit[0]);
                else if (dataSplit.length == 2)
                addCredits = Integer.parseInt(dataSplit[1]);
                System.out.println("Credits: " + viewDetails.ownText());

            }
            addElectiveCourse(new Course(course, addPrerequisites, addCredits, addDescription, addSemesterOffered));
        }
    }

    public String getTitle() {
        return title;
    }

}
