package view;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.StreamSupport;

import data_access.InputOutputOperations;
import domain.Channel;
import domain.IMediator;
import domain.Mediator;
import domain.MeetingChannel;
import domain.PrivateChannel;
import domain.Team;
import domain.User;
import utilities.*;

public class TeamsTECH {

	public void start() {
		InputOutputOperations inputOutputOperations = new InputOutputOperations();
		List<User> users = inputOutputOperations.fillUserList("xxx");
		List<Team> teams = inputOutputOperations.fillTeamList("ccc");
		IMediator med = new Mediator(teams, users, inputOutputOperations);
		inputOutputOperations.matchUsersAndTeams(med);
		inputOutputOperations.fillTeamChannels(med, "vvv");
		Scanner scanner = new Scanner(System.in);
		System.out.println("=========================================");
		System.out.println("=============== TeamsTECH ===============");
		System.out.println("=========================================\n");
		try {
			login(med, scanner);
			mainMenu(med, scanner);
		} catch (NotFoundException | PasswordIncorrectException e) {
			System.out.println("User Not Found or password is wrong");
			start();
		}
	}

	private void login(IMediator med, Scanner scanner) throws NotFoundException, PasswordIncorrectException {
		System.out.println("================= Login =================");
		System.out.print("Please enter your E-Mail adress: ");
		String email = scanner.next();
		System.out.print("Please enter your password: ");
		String password = scanner.next();
		User currentUser = med.getUserFromEmailAndPassword(email, password);
		System.out.println("\nWelcome " + currentUser.getName() + "!\n");
		System.out.println("What would you like to do?");
		med.setCurrentUser(currentUser);
		return;
	}

	private void mainMenu(IMediator med, Scanner scanner) {
		UserType userType = med.getCurrentUser().getUserType();
		System.out.println();
		System.out.println("1. Print the teams you're a member of.");
		System.out.println("2. Open a team.");
		System.out.println("3. Join a team.");
		System.out.println("4. Leave a team.");
		if (userType == UserType.TEACHING_ASSISTANT) {
			System.out.println("5. Delete a team.");
		} else if (userType == UserType.INSTRUCTOR) {
			System.out.println("5. Delete a team.");
			System.out.println("6. Create a team.");
		}
		System.out.println("9. Log out.");
		System.out.print("Please enter a number: ");
		int choice = -1;
		try {
			choice = scanner.nextInt();

		}catch (InputMismatchException e){
			scanner.nextLine();
			System.out.println("Please choose a valid option.\n");
			mainMenu(med, scanner);
		}
		System.out.println();
		switch (choice) {
		case 1:
			printTeams(med, scanner);
			break;
		case 2:
			openTeam(med, scanner);
			teamMenu(med, scanner);
			break;
		case 3:
			joinTeamMenu(med, scanner);
			break;
		case 4:
			leaveTeamMenu(med, scanner);
			break;
		case 5:
			deleteTeamMenu(med, scanner);
			break;
		case 6:
			createTeamMenu(med, scanner);
			break;
		case 9:
			med.getIO().outputUserCsv(med);
			med.getIO().outputTeamsCsv(med);
			med.getIO().outputChannelsCsv(med);
			start();
			break;
		default:
			System.out.println("Please choose a valid option.\n");
			mainMenu(med, scanner);
		}
	}

	private void leaveTeamMenu(IMediator med, Scanner scanner) {
		System.out.print("Please enter the ID of the team you want to leave: ");
		String teamToLeave = scanner.next();
		try {
			Team leftTeam = med.removeMemberFromTeam(teamToLeave, med.getCurrentUser().getUserID());
			System.out.println("Left team " + leftTeam.getTeamName() + ".\n");
			mainMenu(med, scanner);
		} catch (Exception e) {
			System.out.println("Invalid teamId\n");
			mainMenu(med, scanner);
		}
	}

	private void joinTeamMenu(IMediator med, Scanner scanner) {
		System.out.print("Please enter the ID of the team you want to join: ");
		String teamToJoin = scanner.next();
		Team team = med.findTeamById(teamToJoin);
		if (team != null) {
			Team joinedTeam = med.addMemberToTeam(teamToJoin, med.getCurrentUser());
			System.out.println("Joined team " + joinedTeam.getTeamName() + ".\n");
			mainMenu(med, scanner);
		} else
			System.out.println("Wrong team Id\n");
	}

	private void printTeams(IMediator med, Scanner scanner) {
		List<Team> teams = med.getTeamsWithCurrentUser();
		System.out.println("Teams you're a member of: ");
		for (Team team : teams) {
			System.out.println(team.getTeamID() + ": " + team.getTeamName());
		}
		System.out.println();
		mainMenu(med, scanner);
	}

	private void deleteTeamMenu(IMediator med, Scanner scanner) {
		UserType userType = med.getCurrentUser().getUserType();
		if (userType == UserType.STUDENT) {
			System.out.println("Please choose a valid option.\n");
			mainMenu(med, scanner);
		}
		System.out.print("Please enter the ID of the team you want to delete: ");
		String teamToDelete = scanner.next();
		Team removedTeam = med.findTeamById(teamToDelete);
		try {
			removedTeam = med.removeTeam(teamToDelete);
			System.out.println("Deleted team " + removedTeam.getTeamName() + ".");
		} catch (UnauthorizedUserOperationException e) {
			System.out.println("You don't have permission to do that. Please choose a different option.\n");
		} catch (NullPointerException e) {
			System.out.println("Wrong team id\n");
		}
		mainMenu(med, scanner);
	}

	private void createTeamMenu(IMediator med, Scanner scanner) {
		UserType userType = med.getCurrentUser().getUserType();
		if (userType == UserType.STUDENT || userType == UserType.TEACHING_ASSISTANT) {
			System.out.println("Please choose a valid option.\n");
			mainMenu(med, scanner);
		}
		System.out.print("Please enter a team ID: ");
		String teamID = scanner.next();
		scanner.nextLine();
		System.out.print("Please enter a team name: ");
		String teamName = scanner.nextLine();
		try {
			Team createdTeam = med.createTeam(teamName, teamID);
			System.out.println(
					"Created team " + createdTeam.getTeamName() + " with the ID " + createdTeam.getTeamID() + ".\n");
		} catch (UnauthorizedUserOperationException e) {
			System.out.println("You don't have permission to do that. Please choose a different option.\n");
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			System.out.println();
			mainMenu(med, scanner);
		}
		mainMenu(med, scanner);
	}

	private void printChannels(IMediator med, Scanner scanner) {
		Team team = med.getCurrentTeam();
		List<Channel> channels = med.channelsContainingUserInTeam(med.getCurrentUser(), team);
		System.out.println("Channels you're a member of:");
		for (Channel channel : channels) {
			System.out.println(channel.getChannelID() + ": " + channel.getName());
		}
		System.out.println();
		teamMenu(med, scanner);
	}

	private void printMembers(IMediator med, Scanner scanner) {
		Team team = med.getCurrentTeam();
		List<User> members = team.getMembers();
		System.out.println("Members:");
		for (User user : members) {
			System.out.println(user.getUserID() + ": " + user.getName());
		}
		System.out.println();
		teamMenu(med, scanner);
	}

	private void printMembersOfChannel(IMediator med, Scanner scanner) {
		Channel channel = med.getCurrentChannel();
		List<User> members = channel.getMembers();
		System.out.println("Members:");
		for (User user : members) {
			System.out.println(user.getUserID() + ": " + user.getName());
		}
		System.out.println();
		channelMenu(med, scanner);
	}

	private void createChannelMenu(IMediator med, Scanner scanner, String type) {
		Team team = med.getCurrentTeam();
		if (type == "private") {
			System.out.print("Please enter the name of the private channel you want to create: ");
			scanner.nextLine();
			String privateChannelName = scanner.nextLine();
			Channel createdPrivateChannel = med.addPrivateChannelToTeam(team, privateChannelName);
			med.addMemberToPrivateChannelOfTeam(team, createdPrivateChannel.getChannelID(), med.getCurrentUser().getUserID());
			System.out.println(
					"Channel " + createdPrivateChannel.getName() + " created in team " + team.getTeamName() + ".\n");
		} else if (type == "meeting") {
			if (med.isUserOwnerOfTeam(med.getCurrentUser(), team)) {
				System.out.print("Please enter the name of the meeting channel you want to create: ");
				scanner.nextLine();
				String meetingChannelName = scanner.nextLine();
				System.out.print("Please enter the meeting date (e.g. Monday 13:30): ");
				String meetingDateString = scanner.nextLine();
				try {
					Date meetingDate = new Date(meetingDateString);
					Channel createdMeetingChannel = med.addMeetingChannelToTeam(team, meetingChannelName, meetingDate);
					System.out.println("Channel " + createdMeetingChannel.getName() + " created in team "
							+ team.getTeamName() + ".\n");
				} catch (Exception e) {
					System.out.println("Wrong input\n");
					teamMenu(med, scanner);
				}

			} else {
				System.out.print("You don't have permission to create a meeting channel.\n");
			}
		}
		teamMenu(med, scanner);
	}

	private void deleteChannelMenu(IMediator med, Scanner scanner, String type) {
		Team team = med.getCurrentTeam();
		try {
			if (type == "private") {
				System.out.print("Please enter the name of the private channel you want to delete: ");

				int channelId = scanner.nextInt();
				if (med.isUserOwnerOfChannel(med.getCurrentUser(), team, channelId)) {
					Channel removedPrivateChannel = med.removePrivateChannelFromTeam(team, channelId);
					System.out.println("Channel " + removedPrivateChannel.getName() + " removed from team "
							+ team.getTeamName() + ".\n");
				} else {
					System.out.println("You are not the owner of this channel.\n");
				}
			} else if (type == "meeting") {
				if (med.isUserOwnerOfTeam(med.getCurrentUser(), team)) {
					System.out.print("Please enter the name of the meeting channel you want to delete: ");
					int channelId = scanner.nextInt();
					try {
						Channel removedMeetingChannel = med.removeMeetingChannelFromTeam(team, channelId);
						System.out.println("Channel " + removedMeetingChannel.getName() + " removed from team "
								+ team.getTeamName() + ".\n");
					} catch (Exception e) {
						System.out.println(e.getMessage());
						teamMenu(med, scanner);
					}

				} else {
					System.out.print("You don't have permission to delete a meeting channel.\n");
				}
			}
			teamMenu(med, scanner);
		}catch (Exception e){
			scanner.nextLine();
			System.out.println("Wrong input");
			teamMenu(med, scanner);

		}

	}

	private void elevateUserMenu(IMediator med, Scanner scanner) {
		Team team = med.getCurrentTeam();
		try {
			System.out.print("Enter the ID of the user you want to make team owner: ");
			int id = scanner.nextInt();
			User user = med.findUserById(id);
			if (user != null) {
				if (med.isUserOwnerOfTeam(med.getCurrentUser(), team)
						&& user.getUserType().equals(UserType.TEACHING_ASSISTANT)) {
					med.elevateMemberToTeamOwnerOfTeam(team.getTeamID(), id);
					System.out.println(user.getName() + " elevated to team owner status.\n");
				} else {
					System.out.println("You don't have permission to do that.\n");
				}
			}
			teamMenu(med, scanner);
		}catch (InputMismatchException e){
			scanner.nextLine();
			System.out.println("Wrong Input.\n");
			teamMenu(med, scanner);
		}
	}

	private void openTeam(IMediator med, Scanner scanner) {
		System.out.print("Please enter the ID of the team you want to open: ");
		String teamID = scanner.next();
		Team team = med.getTeam(teamID);
		if (team == null) {
			System.out.println("Incorrect ID.\n");
			mainMenu(med, scanner);
		}
		if (!med.teamContainsUser(med.getCurrentUser(), team)) {
			System.out.println("You are not a member of this team.\n");
			mainMenu(med, scanner);
		}
		System.out.println("\nWelcome to team " + team.getTeamName() + ".\n");
		med.setCurrentTeam(team);
		return;
	}

	private void teamMenu(IMediator med, Scanner scanner) {
		Team team = med.getCurrentTeam();
		System.out.println("What would you like to do?");
		System.out.println("0. Open channel.");
		System.out.println("1. Print all channels you're a member of.");
		System.out.println("2. Print all members of team.");
		System.out.println("3. Create a private channel.");
		System.out.println("4. Delete a private channel.");
		if (med.isUserOwnerOfTeam(med.getCurrentUser(), team)) {
			System.out.println("5. Create a meeting channel.");
			System.out.println("6. Delete a meeting channel.");
			System.out.println("7. Elevate a user to team owner status.");
		}
		System.out.println("8. Go back to the main menu.");
		System.out.println("9. Log out.");
		System.out.print("Please enter a number: ");
		int choice = -1;
		try {
			choice = scanner.nextInt();
		}catch (InputMismatchException e){
			scanner.nextLine();
			System.out.println("Please choose a valid option.\n");
			mainMenu(med, scanner);
		}
		System.out.println();
		switch (choice) {
		case 0:
			openChannel(med, scanner);
			channelMenu(med, scanner);
			break;
		case 1:
			printChannels(med, scanner);
			break;
		case 2:
			printMembers(med, scanner);
			break;
		case 3:
			createChannelMenu(med, scanner, "private");
			break;
		case 4:
			deleteChannelMenu(med, scanner, "private");
			break;
		case 5:
			createChannelMenu(med, scanner, "meeting");
			break;
		case 6:
			deleteChannelMenu(med, scanner, "meeting");
			break;
		case 7:
			elevateUserMenu(med, scanner);
			break;
		case 8:
			mainMenu(med, scanner);
			break;
		case 9:
			med.getIO().outputUserCsv(med);
			med.getIO().outputTeamsCsv(med);
			med.getIO().outputChannelsCsv(med);
			start();
			break;
		default:
			System.out.println("Please choose a valid option.\n");
			mainMenu(med, scanner);
		}
	}

	private void openChannel(IMediator med, Scanner scanner) {
		Team team = med.getCurrentTeam();
		System.out.print("Please enter the name of the channel you want to open: ");
		scanner.nextLine();
		String channelName = scanner.nextLine();
		Channel channel = team.getChannel(channelName);
		if (channel == null) {
			System.out.println("Incorrect name.\n");
			teamMenu(med, scanner);
		}
		if (!channel.containsMember(med.getCurrentUser())) {
			System.out.println("You are not a member of this channel.\n");
			teamMenu(med, scanner);
		}
		System.out.println("\nWelcome to channel " + channel.getName() + ".\n");
		med.setCurrentChannel(channel);
	}

	private void addMemberToChannelMenu(IMediator med, Scanner scanner) {
		Team team = med.getCurrentTeam();
		Channel channel = med.getCurrentChannel();
		try {
			System.out.print("Enter the ID of the user you want to make channel member: ");
			int id = scanner.nextInt();
			User user = med.findUserById(id);
			if (!med.teamContainsUser(user, team)) {
				System.out.println("User is not a member of team " + team.getTeamName() + ".\n");
				channelMenu(med, scanner);
			}
			if (med.channelContainsUser(user, channel)) {
				System.out.println("User is already a member of channel " + channel.getName() + ".\n");
				channelMenu(med, scanner);
			}
			med.addMemberToPrivateChannelOfTeam(team, channel.getChannelID(), id);
			System.out.println("User " + user.getName() + " added to channel " + channel.getName() + ".\n");
			channelMenu(med, scanner);
		}catch (InputMismatchException e){
			scanner.nextLine();
			System.out.println("Wrong input\n");

			channelMenu(med, scanner);

		}

	}

	private void removeMemberFromChannelMenu(IMediator med, Scanner scanner) {
		Team team = med.getCurrentTeam();
		Channel channel = med.getCurrentChannel();
		try {
			System.out.print("Enter the ID of the user you want to remove from channel: ");
			int id = scanner.nextInt();
			User user = med.findUserById(id);
			if (!med.teamContainsUser(user, team)) {
				System.out.println("User is not a member of team " + team.getTeamName() + ".\n");
				channelMenu(med, scanner);
			}
			if (!med.channelContainsUser(user, channel)) {
				System.out.println("User is not a member of channel " + channel.getName() + ".\n");
				channelMenu(med, scanner);
			}
			med.removeMemberFromPrivateChannelOfTeam(team, channel.getChannelID(), id);
			System.out.println("User " + user.getName() + " removed from channel " + channel.getName() + ".\n");
			channelMenu(med, scanner);
		}catch (InputMismatchException e){
			scanner.nextLine();
			System.out.println("Wrong Input\n");
			channelMenu(med, scanner);
		}

	}

	private void changeMeetingDateMenu(IMediator med, Scanner scanner) {
		Channel channel = med.getCurrentChannel();
		System.out.print("Please enter the meeting date (e.g. Monday 13:30): ");
		scanner.nextLine();
		String meetingDateString = scanner.nextLine();
		try {
			Date meetingDate = new Date(meetingDateString);
			med.changeMeetingDateOfChannel(channel, meetingDate);
			System.out
					.println("Meeting date of channel " + channel.getName() + " changed to " + meetingDate.toString());
			System.out.println();
		} catch (Exception e) {
			System.out.println("Invalid meeting date.\n");
		}
		channelMenu(med, scanner);
	}

	private void printMeetingDate(IMediator med, Scanner scanner) {
		Channel channel = med.getCurrentChannel();
		System.out.println("Meeting date for channel " + channel.getName() + " is " + ((MeetingChannel) channel).getMeetingDate().toString());
		System.out.println();
		channelMenu(med, scanner);
	}
	
	private void channelMenu(IMediator med, Scanner scanner) {
		Channel channel = med.getCurrentChannel();
		System.out.println("What would you like to do?");
		System.out.println("1. Print all members of channel.");
		if (channel.getClass().isAssignableFrom(PrivateChannel.class)) {
			if (((PrivateChannel) channel).getChannelOwner().getUserID() == med.getCurrentUser().getUserID()) {
				System.out.println("2. Add a member to channel.");
				System.out.println("3. Remove a member from channel.");
			}
		}
		if (channel.getClass().isAssignableFrom(MeetingChannel.class)) {
			System.out.println("2. Show the meeting date.");
			System.out.println("3. Change the meeting date.");
		}
		System.out.println("7. Go back to the team menu.");
		System.out.println("8. Go back to the main menu.");
		System.out.println("9. Log out.");
		System.out.print("Please enter a number: ");
		int choice = -1;
		try {
			choice = scanner.nextInt();

		}catch (InputMismatchException e){
			scanner.nextLine();
			System.out.println("Wrong Input.\n");
			teamMenu(med, scanner);
		}
		System.out.println();
		switch (choice) {
		case 1:
			printMembersOfChannel(med, scanner);
			break;
		case 2:
			if (channel.getClass().isAssignableFrom(PrivateChannel.class)) {
				if (((PrivateChannel) channel).getChannelOwner().getUserID() == med.getCurrentUser().getUserID()) {
					addMemberToChannelMenu(med, scanner);
				} else {
					System.out.println("You are not the owner of this channel.\n");
					channelMenu(med, scanner);
				}
			} else if (channel.getClass().isAssignableFrom(MeetingChannel.class)) {
				printMeetingDate(med, scanner);
			} else {
				System.out.println("Invalid channel type.\n");
				channelMenu(med, scanner);
			}
			break;
		case 3:
			if (channel.getClass().isAssignableFrom(PrivateChannel.class)) {
				if (((PrivateChannel) channel).getChannelOwner().getUserID() == med.getCurrentUser().getUserID()) {
					removeMemberFromChannelMenu(med, scanner);
				} else {
					System.out.println("You are not the owner of this channel.\n");
					channelMenu(med, scanner);
				}
			} else if (channel.getClass().isAssignableFrom(MeetingChannel.class)) {
				changeMeetingDateMenu(med, scanner);
			} else {
				System.out.println("Invalid channel type.\n");
				channelMenu(med, scanner);
			}
			break;
		case 7:
			teamMenu(med, scanner);
			break;
		case 8:
			mainMenu(med, scanner);
			break;
		case 9:
			med.getIO().outputUserCsv(med);
			med.getIO().outputTeamsCsv(med);
			med.getIO().outputChannelsCsv(med);
			start();
			break;
		default:
			System.out.println("Please choose a valid option.\n");
			mainMenu(med, scanner);
		}
	}
}