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
package handlers.voicedcommandhandlers;

import java.text.SimpleDateFormat;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.handler.IVoicedCommandHandler;
import org.l2jmobius.gameserver.instancemanager.PremiumManager;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;

public class Premium implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"pa"
	};
	
    @Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
    {
		if (command.startsWith("pa") && Config.PREMIUM_SYSTEM_ENABLED)
		{
			final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			final long endDate = PremiumManager.getInstance().getPremiumExpiration(activeChar.getAccountName());
			if (endDate == 0)
		{
			activeChar.sendMessage("You dont have Premium Account.");
			return false;
		}
		else
		{
			activeChar.sendMessage("Your account will now have premium status until " + format.format(endDate) + ".");
			return false;
		}
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}