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
package events.MerryChristmas;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.LongTimeEvent;

/**
 * @author Mobius
 */
public class MerryChristmas extends LongTimeEvent
{
	// NPC
	private static final int SANTAGIRL = 34282;
	// Item
	private static final int REWARD = 95912;
	// Misc
	private static final String REWARD_VAR = "SANTAGIRL_REWARD_VAR";
	private static final Object REWARD_LOCK = new Object();
	
	private MerryChristmas()
	{
		addFirstTalkId(SANTAGIRL);
		addTalkId(SANTAGIRL);
		addSpawnId(SANTAGIRL);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "34282-02.html":
			case "34282-03.html":
			{
				return event;
			}
			case "reward":
			{
				if (player.getLevel() < 20)
				{
					return "34282-04.html";
				}
				
				synchronized (REWARD_LOCK)
				{
					final long currentTime = System.currentTimeMillis();
					if (player.getVariables().getLong(REWARD_VAR, 0) < currentTime)
					{
						player.getVariables().set(REWARD_VAR, currentTime + 86400000); // 24 hours
						giveItems(player, REWARD, 1);
					}
					else
					{
						return "34282-05.html";
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "34282-01.html";
	}
	
	public static void main(String[] args)
	{
		new MerryChristmas();
	}
}
