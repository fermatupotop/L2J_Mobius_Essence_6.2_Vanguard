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
package handlers.bypasshandlers;

import java.util.StringTokenizer;
import java.util.logging.Level;

import org.l2jmobius.gameserver.handler.IBypassHandler;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.appearance.PlayerAppearance;
import org.l2jmobius.gameserver.model.itemcontainer.PlayerInventory;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author MrKirill1232
 */

public class ChangeColorTitle implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"ChangeColorTitle"
	};
	private static final Integer DefaultColorTitleInConfig = PlayerAppearance.DEFAULT_TITLE_COLOR;
	private static final String DefaultColorTitleConfig = DefaultColorTitleInConfig.toString();
	boolean isItemStock = false;
	int[] COLOR_TITLE_ITEM_ID = new int[]
	{
		13021,
		13307
	};
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{
		final PlayerInventory inventory = player.getInventory();
		int ResistFromStupid = 0;
		final String val = command.substring(16);
		final StringTokenizer st = new StringTokenizer(val);
		if (st.countTokens() == 2)
		{
			final String colorId = st.nextToken();
			final String TicketType = st.nextToken();
			final int TicketTypeInt = Integer.parseInt(TicketType);
			
			if (colorId.length() == 6)
			{
				try
				{
					Integer.parseInt(colorId, 16);
					ResistFromStupid = 0;
				}
				catch (NumberFormatException e)
				{
					ResistFromStupid = 1;
				}
			}
			else
			{
				ResistFromStupid = 1;
			}
			if (ResistFromStupid == 0)
			{
				final String newTitleColorHex = colorId.substring(4, 6) + colorId.substring(2, 4) + colorId.substring(0, 2);
				int newTitleColorDecimal = Integer.parseInt(newTitleColorHex, 16);
				
				// if ((player.getAppearance().getTitleColor()) == newTitleColorDecimal)
				// {
				// player.sendMessage("This color is already set!");
				// return false;
				// }
				for (int COLOR_TITLE_ITEM_ID : COLOR_TITLE_ITEM_ID)
				{
					if (inventory.getInventoryItemCount(COLOR_TITLE_ITEM_ID, 0) > 0)
					{
						if (TicketTypeInt == 2)
						{
							// player.destroyItemByItemId("You have successfully change color title.", 13021, 1, player, true);
							LOGGER.log(Level.INFO, "Custom change color title: Player " + player.getName() + " trying to change title to RGB HEX " + colorId + ", BGR HEX " + newTitleColorHex + ", with Decimal " + newTitleColorDecimal + ".");
						}
						else if (TicketTypeInt == 1)
						{
							player.destroyItemByItemId("You have successfully change color title.", 13307, 1, player, true);
							LOGGER.log(Level.INFO, "Change color title: Player " + player.getName() + " trying to change title to RGB HEX " + colorId + ", BGR HEX " + newTitleColorHex + ", with Decimal " + newTitleColorDecimal + ".");
						}
						else
						{
							LOGGER.log(Level.WARNING, "Change color title: UNAUTHORIZED Player " + player.getName() + " trying to change title to RGB HEX " + colorId + ", BGR HEX " + newTitleColorHex + ", with Decimal " + newTitleColorDecimal + ".");
							return false;
						}
						if (!newTitleColorHex.equals(DefaultColorTitleConfig))
						{
							player.getAppearance().setTitleColor(newTitleColorDecimal);
						}
						player.broadcastUserInfo();
						isItemStock = true;
						return true;
					}
				}
				if (!isItemStock)
				{
					player.sendMessage("Nice try!");
				}
				return false;
			}
			else
			{
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
				npcHtmlMessage.setHtml("<table border=0 cellpadding=0 cellspacing=10>\n" + "<tr>\n" + "<td valign=\"center\" align=\"center\" width=\"20\">\n" + "<font color=CCCC33>Enter new title color in HEX format</font>\n" + "</td></tr><tr>\n" + "<td valign=\"top\" align=\"center\" width=\"200\" height=1>\n" + "<edit var=\"TitleColorBox\" type=\"numer\" width=\"200\" height=\"12\" length=\"25\">\n" + "</td>\n" + "<td valign=\"top\" align=\"center\" width=\"20\">\n" + "<button value=\"Change\" action=\"bypass -h ChangeColorTitle $TitleColorBox 2\" width=64 height=22 back=\"l2ui_ch3.chatting_tab1\" fore=\"l2ui_ch3.chatting_tab2\">\n" + "</td></tr><tr>\n" + "<td valign=\"center\" align=\"center\" width=\"20\">\n" + "<font color=FF0000>INCORRECT TYPE, TRY AGAIN</font>\n" + "</td>\n" + "</tr></table>");
				player.sendPacket(npcHtmlMessage);
			}
		}
		return false;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}