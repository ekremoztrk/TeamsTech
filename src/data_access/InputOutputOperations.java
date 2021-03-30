package data_access;

import domain.*;
import utilities.Date;
import utilities.UserType;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class InputOutputOperations {

	public List<User> fillUserList(String path) {
		List<User> users = new ArrayList<>();
		List<List<String>> rows = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader("usersNew.csv"))) {
			String line = br.readLine();
			List<String> columnNames = new ArrayList<>();
			for (String s : line.split(","))
				columnNames.add(s);
			rows.add(columnNames);

			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				UserType type = UserType.getUserTypeFromString(values[0]);
				String name = values[1];
				User user;
				if (values.length < 3 || values[2].equals(""))
					user = new User(type, name);
				else{
					user = new User(type, name, Integer.parseInt(values[2]));
					solveConflictForUserWithSpecificId(users,user);
				}

				if (values.length > 4 && !values[4].equals(""))
					user.setPassword(values[4]);
				users.add(user);
				List<String> userString = createUserString(user);
				for (int i = 5; i < values.length; i++)
					userString.add(values[i]);
				rows.add(userString);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		outputRows(rows, "usersNew.csv");
		return users;
	}

	public List<Team> fillTeamList(String path) {
		List<Team> teams = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader("teamsNew.csv"))) {
			String line;
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");

				String name = values[0];
				String teamId = values[1];

				String channelName = values[2];
				String date = values[3];
				String[] splitStr = date.split("\\s+");
				Date meetingDate = Date.stringToDate(splitStr);
				Channel channel = new MeetingChannel(channelName, meetingDate);

				List<User> owners = new ArrayList<>();
				List<User> members = new ArrayList<>();
				List<Channel> channels = new ArrayList<>();

				Team team = new Team(name, teamId, owners, channel, channels, members);
				teams.add(team);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return teams;
	}

	public void fillTeamChannels(IMediator mediator, String path) {
		try (BufferedReader br = new BufferedReader(new FileReader("channelsNew.csv"))) {
			String line;
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");

				String teamId = values[0];
				Team team = mediator.findTeamById(teamId);
				if (team != null) {
					String channelName = values[1];
					String meetingDate = values[2];
					String channelType = values[3];
					String[] userIds = values[4].split(";");
					List<String> ids = new ArrayList<>();
					if (userIds.length > 0) {
						ids = extractIds(userIds);
					}
					List<User> members = new ArrayList<>();
					for (String id : ids) {
						User member = mediator.findUserById(Integer.parseInt(id));
						if (member != null)
							members.add(member);
					}
					if (channelType.equals("MEETING")) {
						Channel channel = new MeetingChannel(channelName);
						if (meetingDate != "") {
							String[] meetingDateSplit = meetingDate.split("\\s+");
							if (meetingDateSplit.length > 1) {
								Date date = Date.stringToDate(meetingDateSplit);
								((MeetingChannel) channel).setMeetingDate(date);
							}
						}
						channel.setMembers(members);
						team.addMeetingChannel(channel);
					} else if (channelType.equals("PRIVATE")) {
						String ownerId = values[5];
						User channelOwner = mediator.findUserById(Integer.parseInt(ownerId));
						if (channelOwner != null) {
							Channel channel = new PrivateChannel(channelName, channelOwner);
							channel.setMembers(members);
							team.addPrivateChannel(channel);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void matchUsersAndTeams(IMediator mediator) {
		matchMembersAndTeams(mediator);
		matchOwnersAndTeams(mediator);
	}

	private void matchMembersAndTeams(IMediator mediator) {
		try (BufferedReader br = new BufferedReader(new FileReader("usersNew.csv"))) {
			String line;
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				int userId = Integer.parseInt(values[2]);
				User user = mediator.findUserById(userId);
				if (values.length > 5) {
					for (int i = 5; i < values.length; i++) {
						Team team = mediator.getTeam(values[i]);
						if (user.getUserType().equals(UserType.INSTRUCTOR)) {
							mediator.addMemberToTeam(team.getTeamID(), user);
							mediator.addMemberToChannelOfTeam(team.getTeamID(),
									team.getDefaultMeetingChannel().getChannelID(), user.getUserID());
						} else {
							mediator.addMemberToTeam(team.getTeamID(), user);
							mediator.addMemberToChannelOfTeam(team.getTeamID(),
									team.getDefaultMeetingChannel().getChannelID(), user.getUserID());
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void matchOwnersAndTeams(IMediator mediator) {
		try (BufferedReader br = new BufferedReader(new FileReader("teamsNew.csv"))) {
			String line;
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				if (values.length > 4) {
					String[] userIds = values[4].split(";");
					List<String> ids = new ArrayList<>();
					if (userIds.length > 0) {
						ids = extractIds(userIds);
					}
					for (String id : ids) {
						User owner = mediator.findUserById(Integer.parseInt(id));
						Team team = mediator.findTeamById(values[1]);
						if (!team.getTeamOwners().contains(owner))
							mediator.elevateMemberToTeamOwnerOfTeam(team.getTeamID(), owner.getUserID());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void outputUserCsv(IMediator mediator) {
		List<List<String>> rows = createInitialRows(
				new String[] { "User Type", "User Name", "User ID", "Email", "Password", "Team ID" });
		List<User> users = mediator.getUsers();
		for (User user : users) {
			List<String> rowData = createUserString(user);
			List<Team> teams = mediator.getAllTeamsForUser(user.getUserID());
			String teamsString = "";
			for (Team team : teams)
				teamsString += team.getTeamID() + ",";
			if (teamsString.length() > 1)
				teamsString = teamsString.substring(0, teamsString.length() - 1);
			rowData.add(teamsString);
			rows.add(rowData);
		}
		outputRows(rows, "usersNew.csv");
	}

	public void outputTeamsCsv(IMediator mediator) {
		List<List<String>> rows = createInitialRows(
				new String[] { "Team Name", "Team ID", "Default Channel", "Default Meeting Day and Time", "Owners" });
		List<Team> teams = mediator.getTeams();
		for (Team team : teams) {
			List<String> rowData = new ArrayList<>();
			rowData.add(team.getTeamName());
			rowData.add(team.getTeamID());
			rowData.add(team.getDefaultMeetingChannel().getName());
			rowData.add(team.getDefaultMeetingChannel().getMeetingDate().toString());
			List<User> owners = team.getTeamOwners();

			String ids = "" + '"';
			for (User user : owners) {
				ids += user.getUserID() + ";";
			}
			ids = ids.substring(0, ids.length() - 1);
			ids += '"';
			rowData.add(ids);
			rows.add(rowData);
		}
		outputRows(rows, "teamsNew.csv");
	}

	public void outputChannelsCsv(IMediator mediator) {
		List<List<String>> rows = createInitialRows(new String[] { "Team ID", "Meeting Channel", "Meeting Day and Time",
				"Type", "Participant ID", "Owner" });
		List<Channel> channels = mediator.getAllChannelsExceptDefaultOnes();
		for (Channel channel : channels) {
			List<String> rowData = new ArrayList<>();
			rowData.add(mediator.findChannelTeam(channel).getTeamID());
			rowData.add(channel.getName());
			if (channel instanceof MeetingChannel) {
				if ((((MeetingChannel) channel).getMeetingDate() != null))
					rowData.add(((MeetingChannel) channel).getMeetingDate().toString());
				else
					rowData.add("");
				rowData.add("MEETING");
				String ids = "" + '"';
				for (User user : channel.getMembers())
					ids += user.getUserID() + ";";
				ids = ids.substring(0, ids.length() - 1);
				ids += '"';
				rowData.add(ids);
				rowData.add("");
			}
			if (channel instanceof PrivateChannel) {
				rowData.add("");
				rowData.add("PRIVATE");
				String ids = "" + '"';
				for (User user : channel.getMembers())
					ids += user.getUserID() + ";";
				ids = ids.substring(0, ids.length() - 1);
				ids += '"';
				rowData.add(ids);
				rowData.add(String.valueOf(((PrivateChannel) channel).getChannelOwner().getUserID()));
			}
			rows.add(rowData);
		}
		outputRows(rows, "channelsNew.csv");
	}

	private List<List<String>> createInitialRows(String[] rowNames) {
		List<List<String>> rows = new ArrayList<>();
		List<String> columnNames = Arrays.asList(rowNames);
		rows.add(columnNames);
		return rows;
	}

	private List<String> createUserString(User user) {
		List<String> userString = new ArrayList<>();
		if (user.getUserType().equals(UserType.TEACHING_ASSISTANT))
			userString.add("Teaching Assistant");
		else
			userString.add(String.valueOf(user.getUserType()));
		userString.add(user.getName());
		userString.add(String.valueOf(user.getUserID()));
		userString.add(user.getEmail());
		userString.add(user.getPassword());
		return userString;
	}

	private void outputRows(List<List<String>> rows, String fileName) {
		FileWriter csvWriter = null;
		try {
			csvWriter = new FileWriter(fileName);
			for (List<String> rowData : rows) {
				csvWriter.append(String.join(",", rowData));
				csvWriter.append("\n");
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<String> extractIds(String[] userIds) {
		List<String> ids = new ArrayList<>();
		if (userIds.length > 0) {
			for (String s : userIds) {
				s = s.replace(String.valueOf('"'), "");
				s = s.replace(" ", "");
				ids.add(s);
			}
		}
		return ids;
	}

	private int generateUserID(List<User> users){
		Random random = new Random();
		int id = random.nextInt(999) + 1;
		while (id<1000){
			if(isUserIdValid(users,id))
				return id;
			id +=1;
		}
		return generateUserID(users);
	}
	private User solveConflictForUserWithSpecificId(List<User> users,User user){
		if(isUserIdValid(users,user.getUserID()))
			return user;
		User userWithSameId = findUserById(users, user.getUserID());
		int newId = generateUserID(users);
		userWithSameId.setUserID(newId);
		return user;
	}
	public boolean isUserIdValid(List<User> users,int id) {
		for(User user: users){
			if(user.getUserID() ==id)
				return false;
		}
		return true;
	}
	private User findUserById(List<User> users,int id){
		for(User user: users){
			if(user.getUserID() ==id)
				return user;
		}
		return null;
	}
}