package ai.others;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerMoveRequest;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.util.Broadcast;

import ai.AbstractNpcAI;
import handlers.admincommandhandlers.AdminSkill;

public class BeforeStart extends AbstractNpcAI
{
	
	public static boolean isCharCreateOnly = true;
	private final SkillHolder deBuff = new SkillHolder(4262, 1);
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogin(OnPlayerLogin event)
	{
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.isGM())
		{
			return;
		}
		
		if (isCharCreateOnly)
		{
			showOnScreenMsg(player, NpcStringId.MOM_DAD_HELP, ExShowScreenMessage.TOP_CENTER, 10000, player.getName());
			player.doCast(deBuff.getSkill());
			Broadcast.toAllOnlinePlayers(String.valueOf(isCharCreateOnly));
			Broadcast.toAllOnlinePlayers(String.valueOf(AdminSkill.startWaytrel));
			return;
		}
		
	}
	
	@RegisterEvent(EventType.ON_PLAYER_MOVE_REQUEST)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void StartWaytrelGame(OnPlayerMoveRequest event)
	{
		final Player player = event.getPlayer();
		
		if (player == null)
		{
			return;
		}
		
		if (player.isGM())
		{
			return;
		}
		if (AdminSkill.getWaytrel())
		{
			Broadcast.toAllOnlinePlayers(String.valueOf(BeforeStart.isCharCreateOnly));
			showOnScreenMsg(player, NpcStringId.MOM_DAD_HELP, ExShowScreenMessage.TOP_CENTER, 10000, player.getName());
			player.stopAllEffects();
			return;
		}
		Broadcast.toAllOnlinePlayers(String.valueOf(BeforeStart.isCharCreateOnly));
		Broadcast.toAllOnlinePlayers(String.valueOf(AdminSkill.getWaytrel()));
		
	}
	
	// public static void main(String[] args)
	// {
	// new BeforeStart();
	// }
}
