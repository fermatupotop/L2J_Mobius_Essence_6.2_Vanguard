package quests.Q10303_SymbolOfHubris;

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

public class Q10303_SymbolOfHubris extends Quest
{
	// NPCs
	private static final int ORVEN = 30857;
	
	// Monsters
	private static final int[] MONSTERS =
	{
		21989,
		21990,
		21991,
		21992,
		21993,
		21994,
		21995,
		21996,
		21997,
		21998,
		21999,
	};
	
	// Items
	
	private static final ItemHolder SOE_HIGH_PRIEST_OVEN = new ItemHolder(91768, 1);
	private static final ItemHolder MAGIC_LAMP_CHARGING_POTION = new ItemHolder(91757, 5);
	private static final ItemHolder SAYHA_GUST = new ItemHolder(91712, 10);
	private static final ItemHolder BERSERKER_SCROLL = new ItemHolder(94777, 10);
	private static final ItemHolder SPIRIT_ORE = new ItemHolder(3031, 500);
	
	// Misc
	private static final int MIN_LEVEL = 76;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10303_SymbolOfHubris()
	{
		super(10303);
		addStartNpc(ORVEN);
		addTalkId(ORVEN);
		addKillId(MONSTERS);
		addCondMinLevel(MIN_LEVEL, "no_lvl.html");
		setQuestNameNpcStringId(NpcStringId.KILL_MONSTERS_IN_THE_TOWER_OF_INSOLENCE);
		
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
					addExpAndSp(player, 100000000, 2700000);
					giveItems(player, MAGIC_LAMP_CHARGING_POTION);
					giveItems(player, SAYHA_GUST);
					giveItems(player, SPIRIT_ORE);
					giveItems(player, BERSERKER_SCROLL);
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
			holder.add(new NpcLogListHolder(NpcStringId.KILL_MONSTERS_IN_THE_TOWER_OF_INSOLENCE.getId(), true, qs.getInt(KILL_COUNT_VAR)));
			return holder;
			
		}
		return super.getNpcLogList(player);
	}
	
}
