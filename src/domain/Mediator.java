package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import data_access.InputOutputOperations;
import utilities.*;

public class Mediator implements IMediator {

	private User currentUser;
	private Team currentTeam;
	private Channel currentChannel;
	private List<Team> teams;
	private List<User> users;
	private InputOutputOperations io;

	public Mediator() {
		setTeams(new ArrayList<Team>());
	}

	public Mediator(List<Team> teams, List<User> users, InputOutputOperations io) {
		setTeams(teams);
		setUsers(users);
		setIO(io);
	}

	public Mediator(Mediator mediator) {
		setTeams(mediator.getTeams());
	}

	@Override
	public List<Team> getTeams() {
		return this.teams;
	}

	private void setTeams(List<Team> teams) {
		this.teams = teams;
	}

	@Override
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<User> getAllStudents() {
		List<User> allStudents = new ArrayList<User>();
		for (Team team : getTeams()) {
			List<User> allStudentsInTeam = getAllStudentsInTeam(team.getTeamID());
			allStudents = Stream.concat(allStudents.stream(), allStudentsInTeam.stream()).collect(Collectors.toList());
		}
		return allStudents;
	}

	public List<User> getAllInstructors() {
		List<User> allInstructors = new ArrayList<User>();
		for (Team team : getTeams()) {
			List<User> allInstructorsInTeam = getAllInstructorsInTeam(team.getTeamID());
			allInstructors = Stream.concat(allInstructors.stream(), allInstructorsInTeam.stream())
					.collect(Collectors.toList());
		}
		return allInstructors;
	}

	public List<User> getAllAssistants() {
		List<User> allAssistants = new ArrayList<User>();
		for (Team team : getTeams()) {
			List<User> allAssistantsInTeam = getAllAssistantsInTeam(team.getTeamID());
			allAssistants = Stream.concat(allAssistants.stream(), allAssistantsInTeam.stream())
					.collect(Collectors.toList());
		}
		return allAssistants;
	}

	@Override
	public Team getTeam(String teamID) {
		for (Team team : getTeams()) {
			if (team.getTeamID().equals(teamID)) {
				return team;
			}
		}
		return null;
	}

	private void addTeam(Team team) {
		List<Team> teams = getTeams();
		teams.add(team);
	}

	@Override
	public Team createTeam(String teamName, String teamID) throws UnauthorizedUserOperationException {
		User currentUser = getCurrentUser();
		if (currentUser.getUserType() != UserType.INSTRUCTOR) {
			throw new UnauthorizedUserOperationException("Unauthorized.");
		}
		Team team = new Team(teamName, teamID, currentUser);
		addTeam(team);
		return team;
	}

	@Override
	public Team removeTeam(String teamID) throws UnauthorizedUserOperationException {
		Team teamToRemove = getTeam(teamID);
		List<Team> teams = getTeams();
		if (!teamToRemove.isUserOwner(getCurrentUser())) {
			throw new UnauthorizedUserOperationException("Unauthorized.");
		}
		teams.remove(teamToRemove);
		return teamToRemove;
	}

	@Override
	public Team addMemberToPrivateChannelOfTeam(Team team, int channelID, int memberID) {
		team.addMemberToPrivateChannel(channelID, memberID);
		return team;
	}

	@Override
	public Team removeMemberFromPrivateChannelOfTeam(Team team, int channelID, int memberID) {
		team.removeMemberFromPrivateChannel(channelID, memberID);
		return team;
	}

	@Override
	public Channel changeMeetingDateOfChannel(Channel channel, Date newMeetingDate) {
		((MeetingChannel) channel).setMeetingDate(newMeetingDate);
		return channel;
	}

	@Override
	public Channel addMeetingChannelToTeam(Team team, String channelName, Date meetingDate) {
		Channel channel = new MeetingChannel(channelName, meetingDate, team.getMembers());
		team.addMeetingChannel(channel);
		return channel;
	}

	@Override
	public Channel removeMeetingChannelFromTeam(Team team, int channelID) throws IllegalArgumentException {
		return team.removeMeetingChannel(channelID);
	}

	@Override
	public Channel addPrivateChannelToTeam(Team team, String channelName) {
		Channel channel = new PrivateChannel(channelName, getCurrentUser());
		team.addPrivateChannel(channel);
		return channel;
	}

	@Override
	public Channel removePrivateChannelFromTeam(Team team, int channelId) {
		return team.removePrivateChannel(channelId);
	}

	@Override
	public Team addMemberToChannelOfTeam(String teamID, int channelID, int userID) {
		Team team = getTeam(teamID);
		team.addMemberToChannel(channelID, userID);
		return team;
	}

	@Override
	public User removeMemberFromChannelOfTeam(String teamID, int channelID, int userID) {
		Team team = getTeam(teamID);
		return team.removeMemberFromChannel(channelID, userID);
	}

	@Override
	public Team addMemberToTeam(String teamID, User newMember) {
		Team team = getTeam(teamID);
		team.addMember(newMember);
		return team;
	}

	@Override
	public Team removeMemberFromTeam(String teamID, int userID) {
		getTeam(teamID).removeMember(userID);
		Team team = getTeam(teamID);
		return team;
	}

	@Override
	public Team elevateMemberToTeamOwnerOfTeam(String teamID, int userID) {
		Team team = getTeam(teamID);
		team.elevateMember(userID);
		return team;
	}

	@Override
	public List<User> getAllStudentsInTeam(String teamID) {
		return getAllUsersOfTypeInTeam(UserType.STUDENT, teamID);
	}

	@Override
	public List<User> getAllInstructorsInTeam(String teamID) {
		return getAllUsersOfTypeInTeam(UserType.INSTRUCTOR, teamID);
	}

	@Override
	public List<User> getAllAssistantsInTeam(String teamID) {
		return getAllUsersOfTypeInTeam(UserType.TEACHING_ASSISTANT, teamID);
	}

	private List<User> getAllUsersOfTypeInTeam(UserType type, String teamID) {
		Team team = getTeam(teamID);
		List<User> allUsersOfType = new ArrayList<User>();
		for (User user : team.getMembers()) {
			if (user.getUserType() == type) {
				allUsersOfType.add(user);
			}
		}
		return allUsersOfType;
	}

	@Override
	public List<User> fillUserList(String path) {
		return getIO().fillUserList(path);
	}

	@Override
	public Team findTeamById(String id) {
		for (Team team : teams) {
			if (team.getTeamID().equals(id))
				return team;
		}
		return null;
	}

	@Override
	public User findUserById(int id) {
		for (User user : users) {
			if (user.getUserID() == id)
				return user;
		}
		return null;
	}

	@Override
	public List<Team> getAllTeamsForUser(int userId) {
		User user = findUserById(userId);
		List<Team> userTeams = new ArrayList<>();
		if (user != null) {
			for (Team team : teams) {
				if (team.containsUser(user))
					userTeams.add(team);
			}
		}
		return userTeams;
	}

	@Override
	public User getUserFromEmailAndPassword(String email, String password)
			throws PasswordIncorrectException, NotFoundException {
		List<User> users = getUsers();
		for (User user : users) {
			if (user.getEmail().equals(email)) {
				if (user.getPassword().equals(password)) {
					return user;
				} else
					throw new PasswordIncorrectException();
			}
		}
		throw new NotFoundException();
	}

	@Override
	public InputOutputOperations getIO() {
		return io;
	}

	@Override
	public void setIO(InputOutputOperations io) {
		this.io = io;
	}

	@Override
	public User getCurrentUser() {
		return currentUser;
	}

	@Override
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	@Override
	public List<Team> getTeamsWithCurrentUser() {
		List<Team> teamsWithCurrentUser = new ArrayList<Team>();
		List<Team> teams = getTeams();
		for (Team team : teams) {
			if (team.containsUser(getCurrentUser())) {
				teamsWithCurrentUser.add(team);
			}
		}
		return teamsWithCurrentUser;
	}

	@Override
	public boolean teamContainsUser(User user, Team team) {
		return team.containsUser(user);
	}
	
	@Override
	public boolean channelContainsUser(User user, Channel channel) {
		return channel.containsMember(user);
	}

	@Override
	public boolean isUserOwnerOfTeam(User user, Team team) {
		return team.isUserOwner(user);
	}

	@Override
	public boolean isUserOwnerOfChannel(User user, Team team, int channelId) throws IllegalArgumentException {
		Channel channel = team.getChannel(channelId);
		if (channel == null)
			throw new IllegalArgumentException("Channel does not exist with given channel name");
		User owner = ((PrivateChannel) channel).getChannelOwner();
		return user.getUserID() == owner.getUserID();
	}

	@Override
	public List<Channel> channelsContainingUserInTeam(User user, Team team) {
		List<Channel> channels = team.getChannels();
		List<Channel> channelsToReturn = new ArrayList<Channel>();
		for (Channel channel : channels) {
			if (channel.getMembers().contains(user)) {
				channelsToReturn.add(channel);
			}
		}
		return channelsToReturn;
	}

	@Override
	public Team findChannelTeam(Channel channel) {
		for (Team team : teams) {
			if (team.getChannels().contains(channel))
				return team;
		}
		return null;
	}

	@Override
	public List<Channel> getAllChannelsExceptDefaultOnes() {
		List<Channel> channels = new ArrayList<>();
		for (Team team : teams) {
			channels.addAll(team.getChannels());
			channels.remove(team.getDefaultMeetingChannel());
		}
		return channels;
	}

	@Override
	public Team getCurrentTeam() {
		return currentTeam;
	}

	@Override
	public void setCurrentTeam(Team currentTeam) {
		this.currentTeam = currentTeam;
	}

	@Override
	public Channel getCurrentChannel() {
		return currentChannel;
	}

	@Override
	public void setCurrentChannel(Channel currentChannel) {
		this.currentChannel = currentChannel;
	}
}
