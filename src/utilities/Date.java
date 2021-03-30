package utilities;

public class Date {

	private DayOfTheWeek day;
	private int hour;
	private int minute;

	public Date() {
		this.day = DayOfTheWeek.MONDAY;
		this.hour = 12;
		this.minute = 0;
	}

	public Date(String str) {
		// PARSE STRING HERE
		String[] splitStr = str.split("\\s+");
		setDay(stringToDate(splitStr).getDay());
		setHour(stringToDate(splitStr).getHour());
		setMinute(stringToDate(splitStr).getMinute());
	}

	public Date(DayOfTheWeek day, int hour, int minute) {
		this.day = day;
		this.hour = hour;
		this.minute = minute;
	}

	public Date(Date date) {
		this.day = date.getDay();
		this.hour = date.getHour();
		this.minute = date.getMinute();
	}

	public DayOfTheWeek getDay() {
		return day;
	}

	public void setDay(DayOfTheWeek day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		if (hour < 0) {
			throw new IllegalArgumentException("Hour cannot be less than 0");
		}
		if (hour > 23) {
			throw new IllegalArgumentException("Hour cannot be more than 23");
		}
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		if (minute < 0) {
			throw new IllegalArgumentException("Minute cannot be less than 0");
		}
		if (minute > 59) {
			throw new IllegalArgumentException("Minute cannot be more than 59");
		}
		this.minute = minute;
	}

	@Override
	public String toString() {
		String hourString = String.valueOf(hour);
		if (hour < 10) {
			hourString = "0" + hourString;
		}
		String minuteString = String.valueOf(minute);
		if (minute < 10) {
			minuteString = "0" + minuteString;
		}
		return day.name() + " " + hourString + ":" + minuteString;
	}

	public static Date stringToDate(String[] s) {
		String day = s[0];
		String time = s[1];
		DayOfTheWeek dayOfTheWeek;
		if (day.equals("Monday"))
			dayOfTheWeek = DayOfTheWeek.MONDAY;
		else if (day.equals("Tuesday"))
			dayOfTheWeek = DayOfTheWeek.TUESDAY;
		else if (day.equals("Wednesday"))
			dayOfTheWeek = DayOfTheWeek.WEDNESDAY;
		else if (day.equals("Thursday"))
			dayOfTheWeek = DayOfTheWeek.THURSDAY;
		else if (day.equals("Friday"))
			dayOfTheWeek = DayOfTheWeek.FRIDAY;
		else if (day.equals("Saturday"))
			dayOfTheWeek = DayOfTheWeek.SATURDAY;
		else
			dayOfTheWeek = DayOfTheWeek.SUNDAY;

		String[] splitStr = time.split(":");
		int hour = Integer.parseInt(splitStr[0]);
		int minute = Integer.parseInt(splitStr[1]);

		Date date = new Date(dayOfTheWeek, hour, minute);
		return date;
	}
}
