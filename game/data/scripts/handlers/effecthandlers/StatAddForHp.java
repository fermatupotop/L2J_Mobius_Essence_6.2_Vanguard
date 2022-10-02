package handlers.effecthandlers;

import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.stats.Stat;

/**
 * @author Mobius
 */
public class StatAddForHp extends AbstractEffect
{
	private final int _hp;
	private final Stat _stat;
	private final double _amount;
	
	public StatAddForHp(StatSet params)
	{
		_hp = params.getInt("hp", 0);
		_stat = params.getEnum("stat", Stat.class);
		_amount = params.getDouble("amount", 0);
	}
	
	@Override
	public void pump(Creature effected, Skill skill)
	{
		if (effected.getMaxHp() >= _hp)
		{
			effected.getStat().mergeAdd(_stat, _amount);
		}
	}
}
