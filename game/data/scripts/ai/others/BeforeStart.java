package ai.others;

import java.util.Date;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.util.Broadcast;

import ai.AbstractNpcAI;

public class BeforeStart extends AbstractNpcAI
{
	
	private final SkillHolder deBuff = new SkillHolder(4262, 1);
	
	Date dateBeforeStart = new Date();
	Date dateStart = new Date(1665169814000L);
	
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
		
		if (dateStart.before(dateBeforeStart))
		{
			showOnScreenMsg(player, NpcStringId.MOM_DAD_HELP, ExShowScreenMessage.TOP_CENTER, 10000, player.getName());
			player.doCast(deBuff.getSkill());
			Broadcast.toAllOnlinePlayers(String.valueOf(dateBeforeStart));
		}
		return;
		
	}
	
	// @RegisterEvent(EventType.ON_PLAYER_MOVE_REQUEST)
	// @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	// public void StartWaytrelGame(OnPlayerMoveRequest event)
	// {
	//
	// final Player player = event.getPlayer();
	// Date actualTime = new Date();
	//
	// if (player == null)
	// {
	// return;
	// }
	//
	// if (player.isGM())
	// {
	// return;
	// }
	// if (dateStart.before(actualTime))
	// {
	// showOnScreenMsg(player, NpcStringId.MOM_DAD_HELP, ExShowScreenMessage.TOP_CENTER, 10000, player.getName());
	// player.stopAllEffects();
	// return;
	// }
	//
	// Broadcast.toAllOnlinePlayers(String.valueOf(actualTime));
	// Broadcast.toAllOnlinePlayers(String.valueOf(dateStart));
	//
	// }
	
	public static void main(String[] args)
	{
		new BeforeStart();
	}
}
