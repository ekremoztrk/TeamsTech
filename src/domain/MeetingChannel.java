package domain;

import java.util.List;

import utilities.Date;

public class MeetingChannel extends Channel {

	private Date meetingDate;

	public MeetingChannel(String name) {
		super(name);
		setMeetingDate(new Date());
	}

	public MeetingChannel(String name, Date date) {
		super(name);
		setMeetingDate(date);
	}

	public MeetingChannel(String name, Date meetingDate, List<User> members) {
		super(name, members);
		setMeetingDate(meetingDate);
	}

	public MeetingChannel(MeetingChannel meetingChannel) {
		super(meetingChannel.getName(), meetingChannel.getMembers());
		setMeetingDate(meetingChannel.getMeetingDate());
	}

	public Date getMeetingDate() {
		return meetingDate;
	}

	public void setMeetingDate(Date meetingDate) {
		this.meetingDate = meetingDate;
	}

}
