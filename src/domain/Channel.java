package domain;

import java.util.ArrayList;
import java.util.List;

public abstract class Channel {

	private String name;
	private List<User> members;
	private int channelID;
	private static int channelIDCounter = 0;

	public Channel(String name) {
		setName(name);
		setMembers(new ArrayList<User>());
		setChannelID(channelIDCounter);
	}

	public Channel(String name, List<User> members) {
		setName(name);
		setMembers(members);
	}

	public Channel(Channel channel) {
		setMembers(channel.getMembers());
	}

	public List<User> getMembers() {
		return members;
	}

	public void setMembers(List<User> members) {
		this.members = members;
	}

	public int getChannelID() {
		return channelID;
	}

	public void setChannelID(int channelID) {
		channelIDCounter++;
		this.channelID = channelID;
	}

	public void addMember(User member) {
		if(!members.contains(member)){
			List<User> newList = getMembers();
			newList.add(member);
			setMembers(newList);
		}
	}

	public void removeMember(User member) {
		List<User> newList = getMembers();
		newList.remove(member);
		setMembers(newList);
	}

	public boolean containsMember(User user) {
		List<User> members = getMembers();
		for (User member : members) {
			if (member.getUserID() == user.getUserID()) {
				return true;
			}
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
