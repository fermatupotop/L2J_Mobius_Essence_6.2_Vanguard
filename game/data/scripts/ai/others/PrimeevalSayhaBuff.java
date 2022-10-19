package ai.others;

import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.skill.Skill;

import ai.AbstractNpcAI;

public class PrimeevalSayhaBuff extends AbstractNpcAI
{
	// Buff
	private static final Skill HAWK_BUFF = SkillData.getInstance().getSkill(50123, 1);
	
	// NPC
	private static final int[] MINIONS =
	{
		21962,
		21963,
		21964,
		21966,
		21968,
		21969,
		22056,
		22057,
		22058,
		21971,
		21974,
		21976,
		22059,
		21978,
	};
	
	private PrimeevalSayhaBuff()
	{
		addKillId(MINIONS);
	}
	
	@Override
	public String onKill(Npc npc, Player attacker, boolean isSummon)
	{
		
		if (!attacker.isAffectedBySkill(50123))
		{
			
			HAWK_BUFF.applyEffects(attacker, attacker);
		}
		return super.onKill(npc, attacker, isSummon);
	}
	
	public static void main(String[] args)
	{
		new PrimeevalSayhaBuff();
	}
}