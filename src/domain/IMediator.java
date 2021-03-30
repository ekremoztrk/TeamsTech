package domain;

import java.util.List;

import data_access.InputOutputOperations;
import utilities.Date;
import utilities.NotFoundException;
import utilities.PasswordIncorrectException;
import utilities.UnauthorizedUserOperationException;

public interface IMediator {
	public Team getTeam(String teamID);

	public Team createTeam(String teamName, String teamID) throws UnauthorizedUserOperationException;

	public Team removeTeam(String teamID) throws UnauthorizedUserOperationException;

	public Team addMemberToPrivateChannelOfTeam(Team team, int channelID, int memberID);

	public Team removeMemberFromPrivateChannelOfTeam(Team team, int channelID, int memberID);

	public Channel changeMeetingDateOfChannel(Channel channel, Date newMeetingDate);

	public Channel addMeetingChannelToTeam(Team team, String channelName, Date meetingDate);

	public Channel removeMeetingChannelFromTeam(Team team, int channelId) throws IllegalArgumentException;

	public Channel addPrivateChannelToTeam(Team team, String channelName);

	public Channel removePrivateChannelFromTeam(Team team, int channelId);

	public Team addMemberToChannelOfTeam(String teamID, int channelID, int userID);

	public User removeMemberFromChannelOfTeam(String teamID, int channelID, int userID);

	public Team addMemberToTeam(String teamID, User newMember);

	public Team removeMemberFromTeam(String teamID, int userID);

	public Team elevateMemberToTeamOwnerOfTeam(String teamID, int userID);

	public List<User> getAllStudentsInTeam(String teamID);

	public List<User> getAllInstructorsInTeam(String teamID);

	public List<User> getAllAssistantsInTeam(String teamID);

	public List<User> fillUserList(String path);

	public Team findTeamById(String id);

	public User findUserById(int id);

	public List<Team> getAllTeamsForUser(int userId);

	public List<User> getUsers();

	public List<Team> getTeams();

	public User getCurrentUser();

	public void setCurrentUser(User currentUser);

	public Team getCurrentTeam();

	public void setCurrentTeam(Team currentTeam);

	public Channel getCurrentChannel();

	public void setCurrentChannel(Channel currentChannel);

	public InputOutputOperations getIO();

	public void setIO(InputOutputOperations io);

	public User getUserFromEmailAndPassword(String email, String password)
			throws NotFoundException, PasswordIncorrectException;

	public List<Team> getTeamsWithCurrentUser();

	public boolean teamContainsUser(User user, Team team);
	
	public boolean channelContainsUser(User user, Channel channel);
		
	public boolean isUserOwnerOfTeam(User user, Team team);

	public boolean isUserOwnerOfChannel(User user, Team team, int channelId) throws IllegalArgumentException;

	public List<Channel> channelsContainingUserInTeam(User user, Team team);

	public List<Channel> getAllChannelsExceptDefaultOnes();

	public Team findChannelTeam(Channel channel);
}
