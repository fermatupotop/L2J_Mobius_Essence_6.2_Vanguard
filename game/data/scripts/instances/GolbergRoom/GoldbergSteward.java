package instances.GolbergRoom;

import java.time.Duration;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.util.Broadcast;

import ai.AbstractNpcAI;

public class GoldbergSteward extends AbstractNpcAI
{
	
	private static final int STEWARD = 18358;
	private static final Duration STEWARD_RESPAWN_DURATION = Duration.ofMinutes(2);
	
	private static final Location[] SPAWNS =
	{
		new Location(102607, 118708, -3692, 41157), // DV 82
		new Location(-53726, 137680, -2780, 41157), // Mahum
		new Location(186893, 55256, -4575, 41157), // Giant Cave
	};
	
	public GoldbergSteward()
	{
		addSpawnId(STEWARD);
		addKillId(STEWARD);
	}
	
	@Override
	protected void onLoad()
	{
		
		ThreadPool.schedule(() ->
		{
			if (World.getInstance().getVisibleObjects().stream().noneMatch(it -> it.getId() == STEWARD))
			{
				addSpawn(STEWARD, getRandomEntry(SPAWNS));
				Broadcast.toAllOnlinePlayersOnScreen("Goldberg Steward was spawn somewhere.");
			}
		}, STEWARD_RESPAWN_DURATION.toMillis());
		
		super.onLoad();
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		Broadcast.toAllOnlinePlayersOnScreen("Goldberg Steward was killed by " + killer);
		
		ThreadPool.schedule(() ->
		{
			if (World.getInstance().getVisibleObjects().stream().noneMatch(it -> it.getId() == STEWARD))
			{
				addSpawn(STEWARD, getRandomEntry(SPAWNS));
				Broadcast.toAllOnlinePlayersOnScreen("Goldberg Steward was spawn somewhere.");
			}
		}, STEWARD_RESPAWN_DURATION.toMillis());
		
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		
		new GoldbergSteward();
	}
	
}