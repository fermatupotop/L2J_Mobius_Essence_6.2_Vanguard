package quests.Q10305_ChangesintheDragonValley2;

import java.util.HashSet;
import java.util.Set;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.network.NpcStringId;

/*
 * @author Valera
 */

public class Q10305_ChangesintheDragonValley2 extends Quest
{
	// NPCs
	private static final int ORVEN = 30857;
	
	// Monsters
	private static final int[] MONSTERS =
	{
		22307,
		22312,
		22306,
		22308,
		22309,
		22310,
		22311,
		22305,
	
	};
	
	// Items
	
	private static final ItemHolder SOE_HIGH_PRIEST_OVEN = new ItemHolder(91768, 1);
	private static final ItemHolder MAGIC_LAMP_CHARGING_POTION = new ItemHolder(91757, 5);
	private static final ItemHolder SAYHA_GUST = new ItemHolder(91712, 12);
	private static final ItemHolder BERSERKER_SCROLL = new ItemHolder(94777, 10);
	private static final ItemHolder SPIRIT_ORE = new ItemHolder(3031, 500);
	private static final ItemHolder CLOAK_OF_PROTECTION_5 = new ItemHolder(96624, 1);
	
	// Misc
	private static final int MIN_LEVEL = 76;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10305_ChangesintheDragonValley2()
	{
		super(10305);
		addStartNpc(ORVEN);
		addTalkId(ORVEN);
		addKillId(MONSTERS);
		addCondMinLevel(MIN_LEVEL, "no_lvl.html");
	}
	
	@Override
	public boolean checkPartyMember(Player member, Npc npc)
	{
		final QuestState qs = getQuestState(member, false);
		return ((qs != null) && qs.isStarted());
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		String htmltext = null;
		switch (event)
		{
			case "30857.htm":
			case "30857-01.htm":
			case "30857-02.htm":
			{
				htmltext = event;
				break;
			}
			case "30857-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "reward":
			{
				if (qs.isCond(2))
				{
					addExpAndSp(player, 40000000, 1080000);
					giveItems(player, MAGIC_LAMP_CHARGING_POTION);
					giveItems(player, SAYHA_GUST);
					giveItems(player, SPIRIT_ORE);
					giveItems(player, BERSERKER_SCROLL);
					giveItems(player, CLOAK_OF_PROTECTION_5);
					htmltext = "30857-05.html";
					qs.exitQuest(false, true);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			htmltext = "30857.htm";
		}
		else if (qs.isStarted())
		{
			if (qs.isCond(1))
			{
				final int killCount = qs.getInt(KILL_COUNT_VAR) + 1;
				if (killCount < 1000)
				{
					htmltext = "no_kill.html";
				}
				else
				{
					htmltext = "30857-01.htm";
				}
			}
			else if (qs.isCond(2))
			{
				htmltext = "30857-04.html";
			}
		}
		else if (qs.isCompleted())
		{
			htmltext = getAlreadyCompletedMsg(player);
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			final int killCount = qs.getInt(KILL_COUNT_VAR) + 1;
			if (killCount < 1000)
			{
				qs.set(KILL_COUNT_VAR, killCount);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				sendNpcLogList(killer);
			}
			else
			{
				qs.setCond(2, true);
				giveItems(killer, SOE_HIGH_PRIEST_OVEN);
				qs.unset(KILL_COUNT_VAR);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>();
			holder.add(new NpcLogListHolder(NpcStringId.DEFEAT_MONSTERS_IN_THE_DRAGON_VALLEY_EAST.getId(), true, qs.getInt(KILL_COUNT_VAR)));
			holder.add(new NpcLogListHolder(NpcStringId.LEVEL_70_ACCOMPLISHED, player.getLevel() > 70 ? 1 : 0));
			return holder;
			
		}
		return super.getNpcLogList(player);
	}
	
}
