package utilities;

public enum UserType {
	INSTRUCTOR("iyte.edu.tr"), STUDENT("std.iyte.edu.tr"), TEACHING_ASSISTANT("iyte.edu.tr");

	public final String domain;

	private UserType(String domain) {
		this.domain = domain;
	}

	public static UserType getUserTypeFromString(String type) {
		if (type.equalsIgnoreCase("Student"))
			return UserType.STUDENT;
		else if (type.equalsIgnoreCase("Assistant") || type.equalsIgnoreCase("Teaching Assistant"))
			return UserType.TEACHING_ASSISTANT;
		else
			return UserType.INSTRUCTOR;
	}
}
