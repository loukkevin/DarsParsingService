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
        courseTitle = courseTitle.replaceAll(" ","");
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
