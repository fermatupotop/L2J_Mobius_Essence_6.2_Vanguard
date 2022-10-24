package ai.areas.IvoryTowerCrator;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;

import ai.AbstractNpcAI;

public class IvoryTowerCrator extends AbstractNpcAI
{
	
	private static final int[] MONSTERS =
	{
		20563,
		20564,
		20565,
		20566,
		20567,
	};
	
	// guard
	private static final int GUARD_BUTCHER = 22101;
	
	private IvoryTowerCrator()
	{
		
		addKillId(MONSTERS);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		
		if (getRandom(100) < 50)
		{
			final Npc spawnBanshee = addSpawn(GUARD_BUTCHER, npc, false, 300000);
			final Playable attacker = isSummon ? killer.getServitors().values().stream().findFirst().orElse(killer.getPet()) : killer;
			addAttackPlayerDesire(spawnBanshee, attacker);
			npc.deleteMe();
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new IvoryTowerCrator();
	}
	
}