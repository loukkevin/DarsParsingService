package kevin.project;

import java.util.ArrayList;
import java.util.List;

public class Course {
	String name;
	List<String> prerequisites = new ArrayList();
	int credits;
	String description;
        List<String> semestersOffered = new ArrayList();

    public void setSemestersOffered(List<String> semestersOffered) {
        this.semestersOffered = semestersOffered;
    }

    public List<String> getSemestersOffered() {
        return semestersOffered;
    }
	public Course(String name, List<String> prerequisites, int credits, String description, List<String> semestersOffered) {
		super();
		this.name = name;
		this.prerequisites = prerequisites;
		this.credits = credits;
		this.description = description;
                this.semestersOffered = semestersOffered;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getPrerequisites() {
		return prerequisites;
	}
	public void setPrerequisites(List<String> prerequisites) {
		this.prerequisites = prerequisites;
	}
	public int getCredits() {
		return credits;
	}
	public void setCredits(int credits) {
		this.credits = credits;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
        public void addPrerequisite (String prereq){
            this.prerequisites.add(prereq);
        }
        public void addSemesterOffered (String semesterOffered){
            this.prerequisites.add(semesterOffered);
        }
	
	
}
