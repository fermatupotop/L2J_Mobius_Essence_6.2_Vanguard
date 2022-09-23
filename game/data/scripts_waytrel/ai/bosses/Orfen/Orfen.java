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
package ai.bosses.Orfen;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.ai.CtrlIntention;
import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.instancemanager.GrandBossManager;
import org.l2jmobius.gameserver.instancemanager.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.Spawn;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.GrandBoss;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.PlaySound;

import ai.AbstractNpcAI;

/**
 * Orfen's AI
 * @author Emperorc
 */
public class Orfen extends AbstractNpcAI
{
	
	private static final NpcStringId[] TEXT =
	{
		NpcStringId.S1_STOP_KIDDING_YOURSELF_ABOUT_YOUR_OWN_POWERLESSNESS,
		NpcStringId.S1_YOU_WILL_LEARN_WHAT_THE_TRUE_FEAR_IS,
		NpcStringId.YOU_RE_REALLY_STUPID_TO_HAVE_CHALLENGED_ME_S1_GET_READY,
		NpcStringId.S1_DO_YOU_THINK_THAT_S_GOING_TO_WORK
	};
	
	private static final int ORFEN = 29014;
	// private static final int ORFEN_X = 66839;
	// private static final int ORFEN_Y = 27422;
	// private static final int ORFEN_Z = -3748;
	// private static final int ORFEN_HEADING = 7835;
	// private static final int RAIKEL = 29015;
	// private static final int RAIKEL_LEOS = 29016;
	// private static final int RIBA = 29017;
	// private static final int RIBA_IREN = 29018;
	
	private static final int ORFEN_X = 66844;
	private static final int ORFEN_Y = 27433;
	private static final int ORFEN_Z = -3748;
	
	private static final byte ALIVE = 0;
	private static final byte DEAD = 1;
	
	private static final SkillHolder PARALYSIS = new SkillHolder(4064, 1);
	private static final SkillHolder BLOW = new SkillHolder(4067, 4);
	private static final SkillHolder ORFEN_HEAL = new SkillHolder(4516, 1);
	
	private static boolean _isTeleported;
	private static Set<Attackable> _minions = ConcurrentHashMap.newKeySet();
	private static ZoneType _zone;
	
	private Orfen()
	{
		final int[] mobs =
		{
			ORFEN
		};
		registerMobs(mobs);
		_isTeleported = false;
		_zone = ZoneManager.getInstance().getZoneById(12013);
		final StatSet info = GrandBossManager.getInstance().getStatSet(ORFEN);
		final int status = GrandBossManager.getInstance().getStatus(ORFEN);
		if (status == DEAD)
		{
			// load the unlock date and time for Orfen from DB
			final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
			// if Orfen is locked until a certain time, mark it so and start the unlock timer
			// the unlock time has not yet expired.
			if (temp > 0)
			{
				startQuestTimer("orfen_unlock", temp, null, null);
			}
			else
			{
				final GrandBoss orfen = (GrandBoss) addSpawn(ORFEN, ORFEN_X, ORFEN_Y, ORFEN_Z, 0, false, 0);
				GrandBossManager.getInstance().setStatus(ORFEN, ALIVE);
				spawnBoss(orfen);
			}
		}
		else
		{
			int locX = info.getInt("loc_x");
			int locY = info.getInt("loc_y");
			int locZ = info.getInt("loc_z");
			final int heading = info.getInt("heading");
			final double hp = info.getDouble("currentHP");
			final double mp = info.getDouble("currentMP");
			if (!_zone.isInsideZone(locX, locY, locZ))
			{
				locX = ORFEN_X;
				locY = ORFEN_Y;
				locZ = ORFEN_Z;
			}		
			final GrandBoss orfen = (GrandBoss) addSpawn(ORFEN, locX, locY, locZ, heading, false, 0);
			orfen.setCurrentHpMp(hp, mp);
			spawnBoss(orfen);
		}
	}
	
	public void setSpawnPoint(Npc npc, int index)
	{
		((Attackable) npc).clearAggroList();
		npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
		final Spawn spawn = npc.getSpawn();
	}
	
	public void spawnBoss(GrandBoss npc)
	{
		GrandBossManager.getInstance().addBoss(npc);
		npc.broadcastPacket(new PlaySound(1, "BS01_A", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
		startQuestTimer("check_orfen_pos", 10000, npc, null, true);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (event.equalsIgnoreCase("orfen_unlock"))
		{
			final GrandBoss orfen = (GrandBoss) addSpawn(ORFEN, ORFEN_X, ORFEN_Y, ORFEN_Z, 0, false, 0);
			GrandBossManager.getInstance().setStatus(ORFEN, ALIVE);
			spawnBoss(orfen);
		}
		else if (event.equalsIgnoreCase("check_orfen_pos"))
		{
			if ((_isTeleported && (npc.getCurrentHp() > (npc.getMaxHp() * 0.95))) || (!_zone.isInsideZone(npc) && !_isTeleported))
			{
				setSpawnPoint(npc, getRandom(3) + 1);
				_isTeleported = false;
			}
			else if (_isTeleported && !_zone.isInsideZone(npc))
			{
				setSpawnPoint(npc, 0);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSkillSee(Npc npc, Player caster, Skill skill, WorldObject[] targets, boolean isSummon)
	{
		if (npc.getId() == ORFEN)
		{
			final Creature originalCaster = isSummon ? caster.getServitors().values().stream().findFirst().orElse(caster.getPet()) : caster;
			if ((skill.getEffectPoint() > 0) && (getRandom(5) == 0) && npc.isInsideRadius2D(originalCaster, 1000))
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, TEXT[getRandom(4)], caster.getName());
				npc.setTarget(originalCaster);
				npc.doCast(PARALYSIS.getSkill());
			}
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		final int npcId = npc.getId();
		if (npcId == ORFEN)
		{
				npc.broadcastSay(ChatType.NPC_GENERAL, TEXT[getRandom(3)], attacker.getName());
				npc.setTarget(attacker);
				npc.doCast(PARALYSIS.getSkill());
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (npc.getId() == ORFEN)
		{
			npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
			GrandBossManager.getInstance().setStatus(ORFEN, DEAD);
			// Calculate Min and Max respawn times randomly.
			long respawnTime = Config.ORFEN_SPAWN_INTERVAL + getRandom(-Config.ORFEN_SPAWN_RANDOM, Config.ORFEN_SPAWN_RANDOM);
			respawnTime *= 3600000;
			startQuestTimer("orfen_unlock", respawnTime, null, null);
			// also save the respawn time so that the info is maintained past reboots
			final StatSet info = GrandBossManager.getInstance().getStatSet(ORFEN);
			info.set("respawn_time", System.currentTimeMillis() + respawnTime);
			GrandBossManager.getInstance().setStatSet(ORFEN, info);
			cancelQuestTimer("check_orfen_pos", npc, null);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new Orfen();
	}
}
