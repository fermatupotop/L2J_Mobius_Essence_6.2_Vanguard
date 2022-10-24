package ai.others;

import java.time.Duration;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.util.Broadcast;

import ai.AbstractNpcAI;

public class AlligatorIslandChest extends AbstractNpcAI
{
	
	// NPC
	private static final int CHEST = 18406;
	
	// Reward
	private static final int RC_STONE = 93080;
	
	// Location
	private static final Location[] SPAWNS =
	{
		new Location(83928, 94232, -3453, 41157),
	};
	
	// Misc
	private static final Duration CHEST_RESPAWN_DURATION = Duration.ofMinutes(60);
	
	public AlligatorIslandChest()
	{
		
		addSpawnId(CHEST);
		addKillId(CHEST);
	}
	
	@Override
	protected void onLoad()
	{
		ThreadPool.schedule(() ->
		{
			if (World.getInstance().getVisibleObjects().stream().noneMatch(it -> it.getId() == CHEST))
			{
				addSpawn(CHEST, getRandomEntry(SPAWNS));
				Broadcast.toAllOnlinePlayers("Shining Treasure Chest was spawn");
			}
		}, CHEST_RESPAWN_DURATION.toMillis());
		super.onLoad();
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		ThreadPool.schedule(() ->
		{
			if (World.getInstance().getVisibleObjects().stream().noneMatch(it -> it.getId() == CHEST))
			{
				addSpawn(CHEST, getRandomEntry(SPAWNS));
				Broadcast.toAllOnlinePlayers("Shining Treasure Chest was spawn");
			}
		}, CHEST_RESPAWN_DURATION.toMillis());
		
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new AlligatorIslandChest();
	}
}