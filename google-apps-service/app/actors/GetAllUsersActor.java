package actors;

import akka.actor.UntypedActor;
import akka.actor.ActorRef;
//local code review (vtegza): Breake model to domain specific. ex: UserActor, GroupActor, etc @ 29.01.16
public class GetAllUsersActor extends UntypedActor
{

	private ActorRef target = null ;

	@Override
	public void onReceive(Object msg)
	{
		if (msg.equals("hello"))
		{
			getSender().tell("world", getSelf());
			if (target != null)
				target.forward(msg, getContext());
		}
		else if (msg instanceof ActorRef)
		{
			target = (ActorRef) msg;
			getSender().tell("done", getSelf());
		}

	}

	public static class AllUsers
	{

	}

	public static class AllGroups
	{

	}

	public static class GetUserGroups
	{

		private final String user;

		public GetUserGroups(String user)
		{
			this.user = user;
		}

		public String getUser()
		{
			return user;
		}

	}
}
