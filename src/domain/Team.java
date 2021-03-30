package domain;

import java.util.ArrayList;
import java.util.List;

public class Team {

	private String teamName;
	private String teamID;
	private List<User> teamOwners;
	private List<User> members;
	private Channel defaultMeetingChannel;
	private List<Channel> channels;

	public Team(String teamName, String teamID, User creator) {
		setTeamName(teamName);
		setTeamID(teamID);

		List<User> members = new ArrayList<User>();
		members.add(creator);
		setMembers(members);

		List<User> teamOwners = new ArrayList<User>();
		teamOwners.add(creator);
		setTeamOwners(teamOwners);

		channels = new ArrayList<>();
		Channel defaultMeetingChannel = new MeetingChannel("General");
		setDefaultMeetingChannel(defaultMeetingChannel);

		List<Channel> channels = new ArrayList<Channel>();
		channels.add(defaultMeetingChannel);
		setChannels(channels);
	}

	public Team(String teamName, String teamID, List<User> teamOwners, Channel defaultMeetingChannel,
			List<Channel> channels, List<User> members) {
		setTeamName(teamName);
		setTeamID(teamID);
		setTeamOwners(teamOwners);
		setChannels(channels);
		setDefaultMeetingChannel(defaultMeetingChannel);
		setMembers(members);
	}

	public Team(Team team) {
		setTeamName(team.getTeamName());
		setTeamID(team.getTeamID());
		setTeamOwners(team.getTeamOwners());
		setDefaultMeetingChannel(team.getDefaultMeetingChannel());
		setChannels(team.getChannels());
		setMembers(team.getMembers());
	}

	public void addMember(User member) {
		List<User> members = getMembers();
		members.add(member);
		addMemberToChannel(defaultMeetingChannel.getChannelID(), member.getUserID());
	}

	public User removeMember(int userID) {
		for (User owner : getTeamOwners()) {
			if (owner.getUserID() == userID) {
				getTeamOwners().remove(owner);
			}
		}
		for (User member : getMembers()) {
			if (member.getUserID() == userID) {
				getMembers().remove(member);
				return member;
			}
		}
		return null;
	}

	public User elevateMember(int userID) {
		User member = getMember(userID);
		List<User> teamOwners = getTeamOwners();
		teamOwners.add(member);
		return member;
	}

	public void addMemberToChannel(int channelID, int userID) {
		Channel channel = getChannel(channelID);
		User member = getMember(userID);
		channel.getMembers().add(member);
	}

	public User removeMemberFromChannel(int channelID, int userID) {
		Channel channel = getChannel(channelID);
		User member = getMember(userID);
		channel.getMembers().remove(member);
		return member;
	}

	public void addMemberToPrivateChannel(int channelID, int memberID) {
		Channel channel = getChannel(channelID);
		if (channel.getClass().isAssignableFrom(PrivateChannel.class)) {
			User member = getMember(memberID);
			channel.addMember(member);
		} else {
			// exception
		}
	}

	public Channel removeMemberFromPrivateChannel(int channelID, int memberID) {
		Channel channel = getChannel(channelID);
		if (channel.getClass().isAssignableFrom(PrivateChannel.class)) {
			User member = getMember(memberID);
			channel.removeMember(member);
		} else {
			// exception
		}
		return channel;
	}

	public void addMeetingChannel(Channel newChannel) {
		for(User member:members)
			newChannel.addMember(member);
		newChannel.setMembers(getMembers());
		channels.add(newChannel);
	}

	public Channel removeMeetingChannel(int channelId) throws IllegalArgumentException {
		Channel channel = getChannel(channelId);
		if (channel == null)
			throw new IllegalArgumentException("Channel does not exist with given channel name");
		getChannels().remove(channel);
		return channel;
	}

	public void addPrivateChannel(Channel newChannel) {
		channels.add(newChannel);
	}

	public Channel removePrivateChannel(int channelId) {
		Channel channel = getChannel(channelId);
		getChannels().remove(channel);
		return channel;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		if (teamName.length() < 3) {
			throw new IllegalArgumentException("Team Name cannot be shorter than 3 characters");
		}
		if (teamName.length() > 255) {
			throw new IllegalArgumentException("Team Name cannot be longer than 255 characters.");
		}
		this.teamName = teamName;
	}

	public String getTeamID() {
		return teamID;
	}

	public void setTeamID(String teamID) {
		if (teamID.length() < 3) {
			throw new IllegalArgumentException("Team ID cannot be shorter than 3 characters.");
		}
		if (teamID.length() > 8) {
			throw new IllegalArgumentException("Team ID cannot be longer than 8 characters.");
		}
		this.teamID = teamID;
	}

	public List<User> getTeamOwners() {
		return teamOwners;
	}

	public void setTeamOwners(List<User> teamOwners) {
		this.teamOwners = teamOwners;
	}

	public MeetingChannel getDefaultMeetingChannel() {
		return (MeetingChannel) defaultMeetingChannel;
	}

	public void setDefaultMeetingChannel(Channel defaultMeetingChannel) {
		this.channels.add(defaultMeetingChannel);
		this.defaultMeetingChannel = defaultMeetingChannel;
	}

	public Channel getChannel(int channelID) {
		List<Channel> channels = getChannels();
		for (Channel channel : channels) {
			if (channel.getChannelID() == channelID) {
				return channel;
			}
		}
		return null;
	}

	public Channel getChannel(String channelName) {
		List<Channel> channels = getChannels();
		for (Channel channel : channels) {
			if (channel.getName().equals(channelName)) {
				return channel;
			}
		}
		return null;
	}

	public List<Channel> getChannels() {
		return channels;
	}

	public void setChannels(List<Channel> channels) {

		this.channels = channels;
	}

	public User getMember(int userID) {
		List<User> members = getMembers();
		for (User member : members) {
			if (member.getUserID() == userID) {
				return member;
			}
		}
		return null;
	}

	public List<User> getMembers() {
		return members;
	}

	public void setMembers(List<User> members) {
		this.members = members;
	}

	public boolean containsUser(User user) {
		for (User teamUser : members) {
			if (teamUser.equals(user))
				return true;
		}
		for (User teamUser : teamOwners) {
			if (teamUser.equals(user))
				return true;
		}
		return false;
	}

	public boolean isUserOwner(User user) {
		List<User> owners = getTeamOwners();
		for (User owner : owners) {
			if (owner.getUserID() == user.getUserID()) {
				return true;
			}
		}
		return false;
	}
}
