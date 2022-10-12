package ai.others;

import org.l2jmobius.gameserver.data.xml.CategoryData;
import org.l2jmobius.gameserver.enums.CategoryType;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLevelChanged;
import org.l2jmobius.gameserver.model.holders.ItemHolder;

import ai.AbstractNpcAI;

public class ThirdClassTpOrven extends AbstractNpcAI
{
	private static final ItemHolder SOE_HIGH_PRIEST_OVEN = new ItemHolder(91768, 1);
	private static final int MIN_LEVEL = 76;
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLevelChanged(OnPlayerLevelChanged event)
	{
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		if ((player.getLevel() >= MIN_LEVEL) && CategoryData.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, player.getClassId().getId()))
		{
			giveItems(player, SOE_HIGH_PRIEST_OVEN);
		}
		return;
		
	}
	
	public static void main(String[] args)
	{
		new ThirdClassTpOrven();
	}
	
}
