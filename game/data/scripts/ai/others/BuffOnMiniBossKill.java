package ai.others;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.skill.Skill;

import ai.AbstractNpcAI;

public class BuffOnMiniBossKill extends AbstractNpcAI
{
	private static final int[] MINI_BOSSES =
	{
		21037, // Nova Beast (Cruma 65)
		21038, // Nova Giant (Cruma 70)
		27114, // Field of Massacre
		27037,
		27024,
	};
	
	private BuffOnMiniBossKill()
	{
		
		addAttackId(MINI_BOSSES);
		addKillId(MINI_BOSSES);
	}
	
	private final static int min_damage = 0;
	private final SkillHolder miniBuff = new SkillHolder(55298, 1);
	private static final Map<Npc, Map<Integer, Integer>> MINIBOSS_HITS = new ConcurrentHashMap<>(new ConcurrentHashMap<>());
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill)
	{
		
		if (MINIBOSS_HITS.getOrDefault(npc, null) == null)
		{
			MINIBOSS_HITS.put(npc, new ConcurrentHashMap<>());
		}
		
		MINIBOSS_HITS.get(npc).replace(attacker.getObjectId(), MINIBOSS_HITS.get(npc).getOrDefault(attacker.getObjectId(), 0) + damage);
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		
		for (Player killers : World.getInstance().getVisibleObjects(npc, Player.class))
		{
			if (MINIBOSS_HITS.containsKey(npc) && (min_damage >= MINIBOSS_HITS.get(npc).getOrDefault(killers.getObjectId(), 0)) && (npc.getTemplate().getLevel() > (killers.getLevel() - 15)) && (npc.getTemplate().getLevel() < (killers.getLevel() + 15)))
			{
				killers.doCast(miniBuff.getSkill());
			}
		}
		
		MINIBOSS_HITS.get(npc).clear();
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new BuffOnMiniBossKill();
	}
	
}
