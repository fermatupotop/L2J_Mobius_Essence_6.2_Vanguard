package ai.others;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jmobius.gameserver.network.serverpackets.ShortCutInit;

public class RegisterReplaceBySkill
{
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogin(OnPlayerLogin event)
	{
		
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		ThreadPool.schedule(() ->
		{
			player.sendPacket(new ShortCutInit(player));
			player.restoreAutoShortcutVisual();
		}, 1100);
		
	}
}
