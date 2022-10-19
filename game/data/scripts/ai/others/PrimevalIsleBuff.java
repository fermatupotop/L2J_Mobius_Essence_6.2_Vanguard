package ai.others;

import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.skill.Skill;

import ai.AbstractNpcAI;

public class PrimevalIsleBuff extends AbstractNpcAI
{
	// Buff
	private static final Skill BURNING_BUFF = SkillData.getInstance().getSkill(48054, 1);
	
	// NPC
	private static final int MINION = 21978; // Tyrannosauruses
	
	private PrimevalIsleBuff()
	{
		addAttackId(MINION);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill)
	{
		if ((npc.getId() == MINION) && !attacker.isAffectedBySkill(48054))
		{
			
			BURNING_BUFF.applyEffects(attacker, attacker);
		}
		
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	public static void main(String[] args)
	{
		new PrimevalIsleBuff();
	}
}
