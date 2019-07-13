/*
 * Copyright Â© 2004-2019 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q00716_PathToBecomingALordRune;

import java.util.ArrayList;
import java.util.List;

import org.l2jdevs.gameserver.instancemanager.CastleManager;
import org.l2jdevs.gameserver.instancemanager.FortManager;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.entity.Castle;
import org.l2jdevs.gameserver.model.entity.Fort;
import org.l2jdevs.gameserver.model.quest.Quest;
import org.l2jdevs.gameserver.model.quest.QuestState;
import org.l2jdevs.gameserver.model.quest.State;
import org.l2jdevs.gameserver.network.NpcStringId;
import org.l2jdevs.gameserver.network.clientpackets.Say2;
import org.l2jdevs.gameserver.network.serverpackets.NpcSay;

import quests.Q00021_HiddenTruth.Q00021_HiddenTruth;
import quests.Q00025_HidingBehindTheTruth.Q00025_HidingBehindTheTruth;

/**
 * Path to Becoming a Lord - Rune (716)
 * TODO: Support for TerritoryWars
 */
public class Q00716_PathToBecomingALordRune extends Quest
{
	private static final int Frederick = 35509;
	private static final int Agripel = 31348;
	private static final int Innocentin = 31328;
	
	private static final int RuneCastle = 8;
	private static List<Integer> Pagans = new ArrayList<>();
	static
	{
		for (int i = 22138; i <= 22176; i++)
		{
			Pagans.add(i);
		}
		for (int i = 22188; i <= 22195; i++)
		{
			Pagans.add(i);
		}
	}
	
	public Q00716_PathToBecomingALordRune()
	{
		super(716, Q00716_PathToBecomingALordRune.class.getSimpleName(), "Path to Becoming a Lord - Rune");
		addStartNpc(Frederick);
		addTalkId(Frederick);
		addTalkId(new int[]
		{
			Agripel,
			Innocentin
		});
		for (int i : Pagans)
		{
			addKillId(i);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = player.getQuestState(getName());
		if (qs == null)
		{
			return null;
		}
		final Castle castle = CastleManager.getInstance().getCastleById(RuneCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		final L2PcInstance castleOwner = castle.getOwner().getLeader().getPlayerInstance();
		if (event.equals("35509-03.htm"))
		{
			qs.startQuest();
		}
		else if (event.equals("31348-03.htm"))
		{
			qs.setCond(3);
		}
		else if (event.equals("35509-08.htm"))
		{
			castleOwner.getQuestState(Q00716_PathToBecomingALordRune.class.getSimpleName()).set("confidant", String.valueOf(qs.getPlayer().getObjectId()));
			castleOwner.getQuestState(Q00716_PathToBecomingALordRune.class.getSimpleName()).setCond(5);
			qs.setState(State.STARTED);
		}
		else if (event.equals("31328-03.htm"))
		{
			if ((castleOwner != null) && (castleOwner != qs.getPlayer()) && (castleOwner.getQuestState(Q00716_PathToBecomingALordRune.class.getSimpleName()) != null) && (castleOwner.getQuestState(Q00716_PathToBecomingALordRune.class.getSimpleName()).isCond(5)))
			{
				castleOwner.getQuestState(Q00716_PathToBecomingALordRune.class.getSimpleName()).setCond(6);
			}
		}
		else if (event.equals("31348-08.htm"))
		{
			qs.setCond(8);
		}
		return event;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		final Castle castle = CastleManager.getInstance().getCastleById(RuneCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		final L2PcInstance castleOwner = castle.getOwner().getLeader().getPlayerInstance();
		
		switch (npc.getId())
		{
			case Frederick:
			{
				if (qs.isCond(0))
				{
					if (castleOwner == qs.getPlayer())
					{
						if (!hasFort())
						{
							htmltext = "35509-01.htm";
						}
						else
						{
							htmltext = "35509-00.htm";
							qs.exitQuest(true);
						}
					}
					else if ((castleOwner != null) && (castleOwner != qs.getPlayer()) && (castleOwner.getQuestState(Q00716_PathToBecomingALordRune.class.getSimpleName()) != null) && (castleOwner.getQuestState(Q00716_PathToBecomingALordRune.class.getSimpleName()).isCond(4)))
					{
						if (castleOwner.calculateDistance(npc, false, false) <= 200)
						{
							htmltext = "35509-07.htm";
						}
						else
						{
							htmltext = "35509-07a.htm";
						}
					}
					else if (qs.getState() == State.STARTED)
					{
						htmltext = "35509-00b.htm";
					}
					else
					{
						htmltext = "35509-00a.htm";
						qs.exitQuest(true);
					}
				}
				else if (qs.isCond(1))
				{
					final QuestState hidingBehindTheTruth = qs.getPlayer().getQuestState(Q00025_HidingBehindTheTruth.class.getSimpleName());
					final QuestState hiddenTruth = qs.getPlayer().getQuestState(Q00021_HiddenTruth.class.getSimpleName());
					if ((hidingBehindTheTruth != null) && hidingBehindTheTruth.isCompleted() && (hiddenTruth != null) && hiddenTruth.isCompleted())
					{
						qs.setCond(2);
						htmltext = "35509-04.htm";
					}
					else
					{
						htmltext = "35509-03.htm";
					}
				}
				else if (qs.isCond(2))
				{
					htmltext = "35509-04a.htm";
				}
				else if (qs.isCond(3))
				{
					qs.setCond(4);
					htmltext = "35509-05.htm";
				}
				else if (qs.isCond(4))
				{
					htmltext = "35509-06.htm";
				}
				else if (qs.isCond(5))
				{
					htmltext = "35509-09.htm";
				}
				else if (qs.isCond(6))
				{
					qs.setCond(7);
					htmltext = "35509-10.htm";
				}
				else if (qs.isCond(7))
				{
					htmltext = "35509-11.htm";
				}
				else if (qs.isCond(8))
				{
					if (castleOwner != null)
					{
						final NpcSay packet = new NpcSay(npc.getObjectId(), Say2.NPC_SHOUT, npc.getId(), NpcStringId.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_RUNE_MAY_THERE_BE_GLORY_IN_THE_TERRITORY_OF_RUNE);
						packet.addStringParameter(player.getName());
						npc.broadcastPacket(packet);
						htmltext = "35509-12.htm";
						qs.exitQuest(true, true);
					}
				}
				break;
			}
			case Agripel:
			{
				if (qs.isCond(2))
				{
					htmltext = "31348-01.htm";
				}
				else if (qs.isCond(7))
				{
					if ((qs.get("paganCount") != null) && (qs.getInt("paganCount") >= 100))
					{
						htmltext = "31348-07.htm";
					}
					else
					{
						htmltext = "31348-04.htm";
					}
				}
				else if (qs.isCond(8))
				{
					htmltext = "31348-09.htm";
				}
				break;
			}
			case Innocentin:
			{
				if ((qs.getState() == State.STARTED) && qs.isCond(0))
				{
					if ((castleOwner != null) && (castleOwner != qs.getPlayer()) && (castleOwner.getQuestState(Q00716_PathToBecomingALordRune.class.getSimpleName()) != null) && (castleOwner.getQuestState(Q00716_PathToBecomingALordRune.class.getSimpleName()).isCond(5)))
					{
						if (castleOwner.getQuestState(Q00716_PathToBecomingALordRune.class.getSimpleName()).getInt("confidant") == qs.getPlayer().getObjectId())
						{
							htmltext = "31328-01.htm";
						}
						else
						{
							htmltext = "31328-00.htm";
						}
					}
					else
					{
						htmltext = "31328-00a.htm";
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		final QuestState qs = killer.getQuestState(getName());
		if (qs == null)
		{
			return null;
		}
		if (qs.getState() != State.STARTED)
		{
			return null;
		}
		final Castle castle = CastleManager.getInstance().getCastleById(RuneCastle);
		final L2PcInstance castleOwner = castle.getOwner().getLeader().getPlayerInstance();
		if ((qs.getState() == State.STARTED) && qs.isCond(0))
		{
			if ((castleOwner != null) && (castleOwner != qs.getPlayer()) && (castleOwner.getQuestState(Q00716_PathToBecomingALordRune.class.getSimpleName()) != null) && (castleOwner.getQuestState(Q00716_PathToBecomingALordRune.class.getSimpleName()).isCond(7)))
			{
				if (castleOwner.getQuestState(Q00716_PathToBecomingALordRune.class.getSimpleName()).get("paganCount") != null)
				{
					castleOwner.getQuestState(Q00716_PathToBecomingALordRune.class.getSimpleName()).set("paganCount", String.valueOf(castleOwner.getQuestState(Q00716_PathToBecomingALordRune.class.getSimpleName()).getInt("paganCount") + 1));
				}
				else
				{
					castleOwner.getQuestState(Q00716_PathToBecomingALordRune.class.getSimpleName()).set("paganCount", "1");
				}
			}
		}
		return null;
	}
	
	private boolean hasFort()
	{
		for (Fort fortress : FortManager.getInstance().getForts())
		{
			if (fortress.getContractedCastleId() == RuneCastle)
			{
				return true;
			}
		}
		return false;
	}
}