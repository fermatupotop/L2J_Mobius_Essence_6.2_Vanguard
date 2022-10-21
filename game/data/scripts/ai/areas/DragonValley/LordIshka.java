package ai.areas.DragonValley;

import java.time.Duration;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.util.Broadcast;

import ai.AbstractNpcAI;

public class LordIshka extends AbstractNpcAI
{
	
	private static final int LORD_ISHKA = 18358;
	private static final Duration ISHKA_RESPAWN_DURATION = Duration.ofMinutes(120);
	
	private static final Location[] SPAWNS =
	{
		new Location(84492, 117479, -3000, 41157), // Near Tiphon
		new Location(83730, 111053, -3656, 41157),
		new Location(88093, 112321, -3288, 41157),
	};
	
	public LordIshka()
	{
		addSpawnId(LORD_ISHKA);
		addKillId(LORD_ISHKA);
	}
	
	@Override
	protected void onLoad()
	{
		
		ThreadPool.schedule(() ->
		{
			if (World.getInstance().getVisibleObjects().stream().noneMatch(it -> it.getId() == LORD_ISHKA))
			{
				addSpawn(LORD_ISHKA, getRandomEntry(SPAWNS));
			}
		}, ISHKA_RESPAWN_DURATION.toMillis());
		
		super.onLoad();
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		Broadcast.toAllOnlinePlayersOnScreen("Atingo was killed by " + killer);
		ThreadPool.schedule(() ->
		{
			if (World.getInstance().getVisibleObjects().stream().noneMatch(it -> it.getId() == LORD_ISHKA))
			{
				addSpawn(LORD_ISHKA, getRandomEntry(SPAWNS));
				
			}
		}, ISHKA_RESPAWN_DURATION.toMillis());
		
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		
		new LordIshka();
	}
	
}