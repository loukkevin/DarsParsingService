package kevin.project;

import java.util.ArrayList;
import java.util.List;

public class ProgramRequirements {
	List<Requirement> requirements = new ArrayList<Requirement>();
	List<Elective> electives = new ArrayList<Elective>();
	List<Course> coursesTaken = new ArrayList<Course>();


	
	public ProgramRequirements(List<Requirement> requirements, List<Elective> electives, List<Course> coursesTaken) {
		super();
		this.requirements = requirements;
		this.electives = electives;
		this.coursesTaken = coursesTaken;
	}
	public List<Course> getCoursesTaken() {
		return coursesTaken;
	}
	public void setCoursesTaken(List<Course> coursesTaken) {
		this.coursesTaken = coursesTaken;
	}
	public List<Requirement> getRequirements() {
		return requirements;
	}
	public void setRequirements(List<Requirement> requirements) {
		this.requirements = requirements;
	}
	public List<Elective> getElectives() {
		return electives;
	}
	public void setElectives(List<Elective> electives) {
		this.electives = electives;
	}
	
	
	
	
}
