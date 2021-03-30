package domain;

import java.util.List;
import java.util.Random;

import utilities.UserType;

public class User {

	private UserType userType;
	private String name;
	private int userID;
	private String email;
	private String password;
	private List<String> teamIDs;

	public User(UserType userType, String name) {
		setUserType(userType);
		setName(name);
		generateAndSetUserID();
		generateAndSetEmail(userType, name);
		generateAndSetPassword();
	}

	public User(UserType userType, String name, int userId) {
		setUserType(userType);
		setName(name);
		setUserID(userId);
		generateAndSetEmail(userType, name);
		generateAndSetPassword();
	}

	public User(User user) {
		setUserType(user.getUserType());
		setName(user.getName());
		setUserID(user.getUserID());
		setEmail(user.getEmail());
		setPassword(user.getPassword());
		setTeamIDs(user.getTeamIDs());
	}

	private void generateAndSetUserID() {
		Random random = new Random();
		int id = random.nextInt(999) + 1;
		setUserID(id);
	}

	private void generateAndSetEmail(UserType userType, String name) {
		String[] splitStr = name.split("\\s+");
		String email = "";
		for (String s : splitStr) {
			email += s.toLowerCase();
		}
		email += "@" + userType.domain;
		setEmail(email);
	}

	private void generateAndSetPassword() {
		int leftLimit = 48;
		int rightLimit = 122;
		int targetStringLength = 4;
		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		setPassword(generatedString);
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name.length() < 3) {
			throw new IllegalArgumentException("Name cannot be shorter than 3 characters.");
		}
		if (name.length() > 50) {
			throw new IllegalArgumentException("Name cannot be longer than 50 characters.");
		}
		this.name = name.toUpperCase();

	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		if (userID < 1) {
			throw new IllegalArgumentException("User ID cannot be less than 1.");
		}
		if (userID > 999) {
			throw new IllegalArgumentException("User ID cannot be more than 999.");
		}
		this.userID = userID;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if (!email.matches("^(.+)@(.+)$")) {
			throw new IllegalArgumentException("E-Mail address is not valid.");
		}
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (password.length() != 4) {
			throw new IllegalArgumentException("Password has to be 4 characters.");
		}
		this.password = password;
	}

	public List<String> getTeamIDs() {
		return teamIDs;
	}

	public void setTeamIDs(List<String> teamIDs) {
		this.teamIDs = teamIDs;
	}

	public void removeTeamId(String teamId) {
		for (String id : teamIDs) {
			if (id.equals(teamId))
				teamIDs.remove(id);
		}
	}

}
