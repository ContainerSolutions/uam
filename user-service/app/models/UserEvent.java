package models;

import java.util.Date;

public class UserEvent {
	public Date datetime;
	public String application;
	public String action;
	public String executor;
	public Long requestNumber;

	public UserEvent() {}

	public UserEvent(Date datetime, String application, String action, String executor, Long requestNumber) {
		this.datetime = datetime;
		this.application = application;
		this.action = action;
		this.executor = executor;
		this.requestNumber = requestNumber;
	}
}
