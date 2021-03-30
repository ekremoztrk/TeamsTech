package domain;

import java.util.List;

public class PrivateChannel extends Channel {

	private User channelOwner;

	public PrivateChannel(String name, User channelOwner) {
		super(name);
		setChannelOwner(channelOwner);
	}

	public PrivateChannel(String name, User channelOwner, List<User> members) {
		super(name, members);
		setChannelOwner(channelOwner);
	}

	public PrivateChannel(PrivateChannel privateChannel) {
		super(privateChannel.getName(), privateChannel.getMembers());
		setChannelOwner(privateChannel.getChannelOwner());
	}

	public User getChannelOwner() {
		return channelOwner;
	}

	public void setChannelOwner(User channelOwner) {
		this.channelOwner = channelOwner;
	}

}
