/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package events.GoldenWheel;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.instancemanager.events.PaybackManager;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogout;
import org.l2jmobius.gameserver.model.events.impl.item.OnMultisellBuyItem;
import org.l2jmobius.gameserver.model.holders.ItemChanceHolder;
import org.l2jmobius.gameserver.model.quest.LongTimeEvent;
import org.l2jmobius.gameserver.network.serverpackets.payback.PaybackUILauncher;
import org.w3c.dom.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Index
 * @implNote link - <a href="https://l2central.info/essence/events_and_promos/654.html?lang=en">Event Page</a>
 */
public class GoldenWheel extends LongTimeEvent implements IXmlReader
{
	// Item
	private int coin = 94834; // Golden Wheel's Coin
	// Misc
	private static final List<Long> _multisells = new ArrayList<>();

	private GoldenWheel()
	{
		if (isEventPeriod())
		{
			load();
			PaybackManager.getInstance().init();
			PaybackManager.getInstance().addLocalString("ru", "minLevel", "Ваш уровень слишком низкий для участия в этом событии");
			PaybackManager.getInstance().addLocalString("ru", "maxLevel", "Ваш уровень слишком большой для участия в этом событии");
			PaybackManager.getInstance().addLocalString("en", "minLevel", "Your level so low for be participant in this event");
			PaybackManager.getInstance().addLocalString("en", "maxLevel", "Your level so high for be participant in this event");
		}
	}

	public void reloadRewards()
	{
		for (Player player : World.getInstance().getPlayers())
		{
			player.sendPacket(new PaybackUILauncher(false));
		}
		PaybackManager.getInstance().resetField();
		load();
		for (Player player : World.getInstance().getPlayers())
		{
			player.sendPacket(new PaybackUILauncher(true));
		}
	}

	@Override
	public synchronized void load()
	{
		parseDatapackFile("data/scripts/events/GoldenWheel/rewards.xml");
	}

	@Override
	public void parseDocument(Document doc, File f)
	{
		PaybackManager manager = PaybackManager.getInstance();
		manager.setEndTime(_eventPeriod.getEndDate().getTime());
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "params", paramNode ->
			{
				StatSet paramSet = new StatSet(parseAttributes(paramNode));
				coin = paramSet.getInt("coinId", 94834);
				manager.setCoinID(coin);
				manager.setMinLevel(paramSet.getInt("minLevel"));
				manager.setMaxLevel(paramSet.getInt("maxLevel"));
			});
			forEach(listNode, "multisells", multisellNode ->
			{
				String[] multisells = multisellNode.getTextContent().trim().split(";");
				for (String id : multisells)
				{
					_multisells.add(Long.parseLong(id));
				}
				manager.addToMultisells(_multisells);
			});
			forEach(listNode, "payback", paybackNode ->
			{
				StatSet paramSet = new StatSet(parseAttributes(paybackNode));
				final int id = paramSet.getInt("id");
				final long count = paramSet.getLong("count");
				final AtomicReference<List<ItemChanceHolder>> rewards = new AtomicReference<>(new ArrayList<>());
				forEach(paybackNode, "item", itemNode ->
				{
					StatSet itemSet = new StatSet(parseAttributes(itemNode));
					final double chance = itemSet.getDouble("chance", 100);
					rewards.get().add(new ItemChanceHolder(itemSet.getInt("id"), chance, itemSet.getLong("count"), (byte) itemSet.getInt("enchantLevel", 0)));
				});
				manager.addRewardsToHolder(id, count, rewards.get());
			});
		});
	}

	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogin(OnPlayerLogin event)
	{
		final Player player = event.getPlayer();
		if (!isEventPeriod() || player == null)
		{
			return;
		}
		PaybackManager.getInstance().addPlayerToList(player);
		player.sendPacket(new PaybackUILauncher(true));
	}

	@RegisterEvent(EventType.ON_PLAYER_LOGOUT)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogout(OnPlayerLogout event)
	{
		final Player player = event.getPlayer();
		if (!isEventPeriod() || player == null)
		{
			return;
		}
		PaybackManager.getInstance().removePlayerFromList(player.getObjectId());
	}

	@RegisterEvent(EventType.ON_MULTISELL_BUY_ITEM)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onMultisellBuyItem(OnMultisellBuyItem event)
	{
		final Player player = event.getPlayer();
		if (!isEventPeriod() || player == null)
		{
			return;
		}
		PaybackManager manager = PaybackManager.getInstance();
		if (manager.getMultisells().contains(event.getMultisellId()))
		{
			for (ItemChanceHolder materials : event.getResourceItems())
			{
				if (materials.getId() == coin)
				{
					long consumed = manager.getPlayerConsumedProgress(player.getObjectId());
					manager.changePlayerConsumedProgress(player.getObjectId(),  consumed + event.getAmount() * materials.getCount());
					manager.storePlayerProgress(player);
					break;
				}
			}
		}
	}

	public static void main(String[] args)
	{
		new GoldenWheel();
	}
}
