

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import kevin.project.Course;
import kevin.project.DocumentHandler;
import kevin.project.ParseService;
import kevin.project.ProgramRequirements;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kevin
 */
public class ParseServiceTest{
    ParseService parseService = new ParseService();
    DocumentHandler documentHandler;
    
    @Test
    public void testMmeMajor () throws IOException {
        
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("justinDars.html");
        String html = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        Document document = Jsoup.parse(html);
        ProgramRequirements programRequirements = parseService.parse(document);

        Assert.assertEquals("expected electives list to be size 8", 8, programRequirements.getElectives().size());
        Assert.assertEquals("expected coursesTaken list to be size 21", 21, programRequirements.getCoursesTaken().size());
        Assert.assertEquals("expected requirements list to be size 13", 13, programRequirements.getRequirements().size());
    }
    @Test
    public void testSeMajor () throws IOException {
        
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("devinDars.html");
        String html = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        Document document = Jsoup.parse(html);
        ProgramRequirements programRequirements = parseService.parse(document);
        
        Assert.assertEquals("expected electives list to be size 6", 6, programRequirements.getElectives().size()); //SE major should have 6 elective sections
        Assert.assertEquals("expected se electives courses list to be size 11", 11, programRequirements.getElectives().get(3).getElectiveCourses().size()); //the SE major electives should have 11 courses available
        Assert.assertEquals("expected coursesTaken list to be size 57", 57, programRequirements.getCoursesTaken().size());
        Assert.assertEquals("expected requirements list to be size 21", 21, programRequirements.getRequirements().size());
    }
    @Test
    public void testBlankFile () throws IOException {
        
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("blank.html");
        String html = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        Document document = Jsoup.parse(html);
        ProgramRequirements programRequirements = parseService.parse(document);
        
        Assert.assertEquals("expected electives list to be size 0", 0, programRequirements.getElectives().size());
        Assert.assertEquals("expected coursesTaken list to be size 0", 0, programRequirements.getCoursesTaken().size());
        Assert.assertEquals("expected requirements list to be size 0", 0, programRequirements.getRequirements().size());
    }
    
//    @Test
//    public void testGetValidCourseInformation () throws IOException {
//        String courseName = "SE490";
//        Course validCourse = parseService.getCourseInformation(courseName);
//        Assert.assertEquals("Expected prerequisites to be size 2", 2, validCourse.getPrerequisites().size());
//    }
//    
//    @Test
//    public void testGetInvalidCourseInformation () throws IOException {
//    	String courseName = "SE492";
//    	Course validCourse = parseService.getCourseInformation(courseName);
//    	
//    	Assert.assertEquals("Expected prerequisites to be empty", true, validCourse.getPrerequisites().isEmpty());
//    	Assert.assertEquals("Expected credits to be 3", 3, validCourse.getCredits());
//    	Assert.assertEquals("Expected semestersOffered size to be 3", 3, validCourse.getSemestersOffered().size());
//    }
    @Test
    public void testPartiallyFullfilledElective () throws IOException {
        System.out.println("Testing partially fulfilled elective file");
    	InputStream inputStream = ClassLoader.getSystemResourceAsStream("partiallyCompleteElective.html");
        String html = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        Document document = Jsoup.parse(html);
        ProgramRequirements programRequirements = parseService.parse(document);
        
        Assert.assertEquals("expected electives list to be size 4", 4, programRequirements.getElectives().size());
        Assert.assertEquals("expected coursesTaken list to be size 48", 48, programRequirements.getCoursesTaken().size());
        Assert.assertEquals("expected requirements list to be size 13", 13, programRequirements.getRequirements().size());
    }
    
    
    

}
