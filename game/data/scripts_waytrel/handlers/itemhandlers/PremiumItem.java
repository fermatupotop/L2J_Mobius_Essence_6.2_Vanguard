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
package handlers.itemhandlers;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import org.l2jmobius.gameserver.handler.IItemHandler;
import org.l2jmobius.gameserver.instancemanager.PremiumManager;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.instance.Item;

/**
 * @author Nasseka
 */
public class PremiumItem implements IItemHandler
{
    private static int PREMIUM_ITEM = 94441; //Add item ID here.
    private static int PREMIUM_TIME = 30; // How much days to add
    
    @Override
    public boolean useItem(Playable playable, Item item, boolean forceUse)
    {
        final Player player = playable.getActingPlayer();
        if (player == null)
        {
            return false;
        }
        
        if (player.getInventory().getInventoryItemCount(PREMIUM_ITEM, -1) > 0)
        {
            player.destroyItemByItemId("Premium", PREMIUM_ITEM, 1, player, true);
            PremiumManager.getInstance().addPremiumTime(player.getAccountName(), PREMIUM_TIME, TimeUnit.DAYS);
            player.sendMessage("Your account will now have premium status until " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(PremiumManager.getInstance().getPremiumExpiration(player.getAccountName())) + ".");
            return true;
        }
        
        return false;
    }
}