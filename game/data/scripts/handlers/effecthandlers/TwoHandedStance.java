package handlers.effecthandlers;

import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.stats.Stat;

/**
 * @author NasSeKa
 */
public class TwoHandedStance extends AbstractEffect
{
	private final double _amount;
	
	public TwoHandedStance(StatSet params)
	{
		_amount = params.getDouble("amount", 0);
	}
	
	@Override
	public void pump(Creature effected, Skill skill)
	{
		effected.getStat().mergeAdd(Stat.PHYSICAL_ATTACK, (_amount * effected.getShldDef()) / 100);
	}
}