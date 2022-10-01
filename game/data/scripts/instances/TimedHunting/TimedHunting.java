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
package instances.TimedHunting;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.ai.AttackableAI;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.data.xml.TimedHuntingZoneData;
import org.l2jmobius.gameserver.enums.ShortcutType;
import org.l2jmobius.gameserver.enums.SkillFinishType;
import org.l2jmobius.gameserver.enums.TeleportWhereType;
import org.l2jmobius.gameserver.instancemanager.InstanceManager;
import org.l2jmobius.gameserver.model.Shortcut;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.holders.TimedHuntingZoneHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jmobius.gameserver.network.serverpackets.ShortCutInit;
import org.l2jmobius.gameserver.network.serverpackets.ShortCutRegister;
import org.l2jmobius.gameserver.network.serverpackets.huntingzones.TimedHuntingZoneExit;

import instances.AbstractInstance;

/**
 * @author Berezkin Nikolay, Mobius
 */
public class TimedHunting extends AbstractInstance
{
	// NPCs (on official server random pick of NPC's)
	private static final int KATE = 34120; // Dragon Valley 80-99
	private static final int DEEKHIN = 34121; // Cemetery 50-59
	private static final int BUNCH = 34122; // Giant's Cave 90+
	private static final int AYAN = 34123; // Sel Mahum Base 85-99
	private static final int JOON = 34124; // Sea of Spores 40-49
	private static final int PANJI = 34125; // Plains of Glory 60-69
	private static final int DEBBIE = 34126; // War-Torn Plains 70-79
	// Skill
	private static final int BUFF = 59829;
	// Misc
	private static final int[] TEMPLATES =
	{
		208, // Sea of Spores
		209, // Cemetery
		210, // Plains of Glory
		211, // War-Torn Plains
		212, // Dragon Valley
		213, // Sel Mahum Base
		215, // Giant's Cave
	};
	private static final Map<Integer, Integer> SKILL_REPLACEMENTS = new HashMap<>();
	static
	{
		SKILL_REPLACEMENTS.put(3, 45199); // Power Strike
		SKILL_REPLACEMENTS.put(16, 45200); // Mortal Blow
		SKILL_REPLACEMENTS.put(56, 45201); // Power Shot
		SKILL_REPLACEMENTS.put(29, 45202); // Iron Punch
		SKILL_REPLACEMENTS.put(5, 45203); // Double Sonic Slash
		SKILL_REPLACEMENTS.put(261, 45204); // Triple Sonic Slash
		SKILL_REPLACEMENTS.put(19, 45205); // Double Shot
		SKILL_REPLACEMENTS.put(190, 45206); // Fatal Strike
		SKILL_REPLACEMENTS.put(263, 45207); // Deadly Blow
		SKILL_REPLACEMENTS.put(280, 45208); // Burning Fist
		SKILL_REPLACEMENTS.put(284, 45209); // Hurricane Assault
		SKILL_REPLACEMENTS.put(343, 45210); // Lethal Shot
		SKILL_REPLACEMENTS.put(344, 45211); // Lethal Blow
		SKILL_REPLACEMENTS.put(400, 45212); // Tribunal
		SKILL_REPLACEMENTS.put(401, 45213); // Judgment
		SKILL_REPLACEMENTS.put(984, 45215); // Shield Strike
		SKILL_REPLACEMENTS.put(1632, 45216); // Deadly Strike
		SKILL_REPLACEMENTS.put(45187, 45217); // Guard Crush
		SKILL_REPLACEMENTS.put(1230, 45218); // Prominence
		SKILL_REPLACEMENTS.put(1235, 45219); // Hydro Blast
		SKILL_REPLACEMENTS.put(1239, 45220); // Hurricane
		SKILL_REPLACEMENTS.put(1220, 45221); // Blaze
		SKILL_REPLACEMENTS.put(1175, 45222); // Aqua Swirl
		SKILL_REPLACEMENTS.put(1178, 45223); // Twister
		SKILL_REPLACEMENTS.put(1028, 45224); // Might of Heaven
		SKILL_REPLACEMENTS.put(1245, 45225); // Steal Essence
		SKILL_REPLACEMENTS.put(45155, 45227); // Soul Impulse
		SKILL_REPLACEMENTS.put(45161, 45228); // Soul Piercing
		SKILL_REPLACEMENTS.put(45163, 45229); // Soul Spark
		SKILL_REPLACEMENTS.put(45168, 45230); // Twin Shot
		SKILL_REPLACEMENTS.put(1148, 45231); // Death Spike
		SKILL_REPLACEMENTS.put(1234, 45232); // Vampiric Claw
		SKILL_REPLACEMENTS.put(1031, 45261); // Divine Strike
		SKILL_REPLACEMENTS.put(45241, 45262); // Divine Beam
		SKILL_REPLACEMENTS.put(45247, 45263); // Vampiric Touch
		SKILL_REPLACEMENTS.put(1090, 45265); // Life Drain
		SKILL_REPLACEMENTS.put(777, 45266); // Demolition Impact
		SKILL_REPLACEMENTS.put(45249, 45267); // Earth Tremor
		SKILL_REPLACEMENTS.put(348, 45268); // Spoil Crush
		SKILL_REPLACEMENTS.put(45303, 45360); // Wipeout
		SKILL_REPLACEMENTS.put(36, 45386); // Spinning Slasher
		SKILL_REPLACEMENTS.put(47011, 47015); // Freezing Wound
		SKILL_REPLACEMENTS.put(47801, 47891); // Piercing
		SKILL_REPLACEMENTS.put(47805, 47893); // Wild Scratch
		SKILL_REPLACEMENTS.put(45377, 47435); // Ethereal Strike
	}
	
	public TimedHunting()
	{
		super(TEMPLATES);
		addFirstTalkId(KATE, DEEKHIN, BUNCH, AYAN, JOON, PANJI, DEBBIE);
		addInstanceLeaveId(TEMPLATES);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (event.startsWith("ENTER"))
		{
			final int zoneId = Integer.parseInt(event.split(" ")[1]);
			final TimedHuntingZoneHolder huntingZone = TimedHuntingZoneData.getInstance().getHuntingZone(zoneId);
			if (huntingZone == null)
			{
				return null;
			}
			
			if (huntingZone.isSoloInstance())
			{
				enterInstance(player, npc, huntingZone.getInstanceId());
			}
			else
			{
				Instance world = null;
				for (Instance instance : InstanceManager.getInstance().getInstances())
				{
					if (instance.getTemplateId() == huntingZone.getInstanceId())
					{
						world = instance;
						break;
					}
				}
				
				if (world == null)
				{
					world = InstanceManager.getInstance().createInstance(huntingZone.getInstanceId(), player);
				}
				
				player.teleToLocation(huntingZone.getEnterLocation(), world);
			}
		}
		else if (event.startsWith("FINISH"))
		{
			player.teleToLocation(TeleportWhereType.TOWN, null);
			finishInstance(player);
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if (player.getInstanceWorld().getParameters().getBoolean("TimedHuntingTaskFinished", false))
		{
			return npc.getId() + "-finished.html";
		}
		
		if (!player.getInstanceWorld().getParameters().getBoolean("PlayerEnter", false))
		{
			player.getInstanceWorld().setParameter("PlayerEnter", true);
			player.getInstanceWorld().setDuration(10);
			replaceNormalSkills(player);
			startEvent(player);
		}
		
		npc.setTarget(player);
		if (!player.getEffectList().isAffectedBySkill(BUFF))
		{
			npc.doCast(new SkillHolder(BUFF, 1).getSkill());
		}
		
		return super.onFirstTalk(npc, player);
	}
	
	@Override
	protected void onEnter(Player player, Instance instance, boolean firstEnter)
	{
		super.onEnter(player, instance, firstEnter);
		instance.setParameter("PlayerIsOut", false);
		if (!firstEnter)
		{
			replaceNormalSkills(player);
			startEvent(player);
		}
	}
	
	private void replaceNormalSkills(Player player)
	{
		// Replace normal skills.
		for (Entry<Integer, Integer> entry : SKILL_REPLACEMENTS.entrySet())
		{
			final int normalSkillId = entry.getKey().intValue();
			player.addReplacedSkill(normalSkillId);
			final Skill knownSkill = player.getKnownSkill(normalSkillId);
			if (knownSkill == null)
			{
				continue;
			}
			
			final int transcendentSkillId = entry.getValue().intValue();
			player.addSkill(SkillData.getInstance().getSkill(transcendentSkillId, knownSkill.getLevel(), knownSkill.getSubLevel()), false);
			for (Shortcut shortcut : player.getAllShortCuts())
			{
				if ((shortcut.getType() == ShortcutType.SKILL) && (shortcut.getId() == normalSkillId))
				{
					final Shortcut newShortcut = new Shortcut(shortcut.getSlot(), shortcut.getPage(), ShortcutType.SKILL, transcendentSkillId, shortcut.getLevel(), shortcut.getSubLevel(), shortcut.getCharacterType());
					player.deleteShortCut(shortcut.getSlot(), shortcut.getPage());
					player.registerShortCut(newShortcut);
					player.sendPacket(new ShortCutRegister(newShortcut));
				}
			}
			
			player.removeSkill(knownSkill, false);
		}
		player.sendSkillList();
		ThreadPool.schedule(() -> player.sendPacket(new ShortCutInit(player)), 1100);
	}
	
	@Override
	public void onInstanceLeave(Player player, Instance instance)
	{
		if (instance.getParameters().getBoolean("TimedHuntingTaskFinished", false))
		{
			instance.setParameter("TimedHuntingTaskFinished", false);
		}
		player.sendPacket(new ExSendUIEvent(player, true, false, 600, 0, NpcStringId.TIME_LEFT));
		player.sendPacket(new TimedHuntingZoneExit(108)); // Training Zone id.
		
		player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, BUFF);
		instance.setParameter("PlayerIsOut", true);
		
		// Restore normal skills.
		for (Entry<Integer, Integer> entry : SKILL_REPLACEMENTS.entrySet())
		{
			final int transcendentSkillId = entry.getValue().intValue();
			final Skill knownSkill = player.getKnownSkill(transcendentSkillId);
			if (knownSkill == null)
			{
				continue;
			}
			
			final int normalSkillId = entry.getKey().intValue();
			player.removeReplacedSkill(normalSkillId);
			player.addSkill(SkillData.getInstance().getSkill(normalSkillId, knownSkill.getLevel(), knownSkill.getSubLevel()), false);
			for (Shortcut shortcut : player.getAllShortCuts())
			{
				if ((shortcut.getType() == ShortcutType.SKILL) && (shortcut.getId() == transcendentSkillId))
				{
					final Shortcut newShortcut = new Shortcut(shortcut.getSlot(), shortcut.getPage(), ShortcutType.SKILL, normalSkillId, shortcut.getLevel(), shortcut.getSubLevel(), shortcut.getCharacterType());
					player.deleteShortCut(shortcut.getSlot(), shortcut.getPage());
					player.registerShortCut(newShortcut);
					player.sendPacket(new ShortCutRegister(newShortcut));
				}
			}
			
			player.removeSkill(knownSkill, false);
		}
		player.sendSkillList();
		ThreadPool.schedule(() -> player.sendPacket(new ShortCutInit(player)), 1100);
	}
	
	private void startEvent(Player player)
	{
		// Start instance tasks.
		if (!player.getInstanceWorld().getParameters().getBoolean("TimedHuntingTaskFinished", false))
		{
			final Instance instance = player.getInstanceWorld();
			player.sendPacket(new ExSendUIEvent(player, false, false, Math.min(600, (int) (instance.getRemainingTime() / 1000)), 0, NpcStringId.TIME_LEFT));
			
			final ScheduledFuture<?> spawnTask = ThreadPool.scheduleAtFixedRate(() ->
			{
				if (!instance.getParameters().getBoolean("PlayerIsOut", false) && (instance.getAliveNpcCount() < 60))
				{
					if (getRandom(3) == 0)
					{
						for (Npc npc : player.getInstanceWorld().spawnGroup("treasures"))
						{
							if (npc.isAttackable())
							{
								((AttackableAI) npc.getAI()).setGlobalAggro(0);
								((Attackable) npc).addDamageHate(player, 0, 9999);
							}
						}
					}
					
					if (getRandom(6) == 0)
					{
						for (Npc npc : player.getInstanceWorld().spawnGroup("guardian"))
						{
							if (npc.isAttackable())
							{
								((AttackableAI) npc.getAI()).setGlobalAggro(0);
								((Attackable) npc).addDamageHate(player, 0, 9999);
							}
						}
						
					}
					
					else
					{
						if (getRandom(3) == 0)
						{
							for (Npc npc : player.getInstanceWorld().spawnGroup("treasures"))
							{
								if (npc.isAttackable())
								{
									((AttackableAI) npc.getAI()).setGlobalAggro(0);
									((Attackable) npc).addDamageHate(player, 0, 9999);
								}
							}
							
						}
						for (Npc npc : player.getInstanceWorld().spawnGroup("monsters"))
						{
							if (npc.isAttackable())
							{
								((AttackableAI) npc.getAI()).setGlobalAggro(0);
								((Attackable) npc).addDamageHate(player, 0, 9999);
							}
						}
					}
				}
			}, 0, 10000);
			
			ThreadPool.schedule(() ->
			{
				instance.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
				instance.getParameters().set("TimedHuntingTaskFinished", true);
				if (spawnTask != null)
				{
					spawnTask.cancel(false);
				}
			}, instance.getRemainingTime() - 30000);
			
			ThreadPool.schedule(() -> instance.finishInstance(), instance.getRemainingTime());
		}
	}
	
	public static void main(String[] args)
	{
		new TimedHunting();
	}
}
