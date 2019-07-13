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
package quests.Q00714_PathToBecomingALordSchuttgart;

import org.l2jdevs.gameserver.instancemanager.CastleManager;
import org.l2jdevs.gameserver.instancemanager.FortManager;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.entity.Castle;
import org.l2jdevs.gameserver.model.entity.Fort;
import org.l2jdevs.gameserver.model.quest.Quest;
import org.l2jdevs.gameserver.model.quest.QuestState;
import org.l2jdevs.gameserver.network.NpcStringId;
import org.l2jdevs.gameserver.network.clientpackets.Say2;
import org.l2jdevs.gameserver.network.serverpackets.NpcSay;

import quests.Q00114_ResurrectionOfAnOldManager.Q00114_ResurrectionOfAnOldManager;
import quests.Q00120_PavelsLastResearch.Q00120_PavelsLastResearch;
import quests.Q00121_PavelTheGiant.Q00121_PavelTheGiant;

/**
 * Path to Becoming a Lord - Schuttgart (714)
 * TODO: Support for TerritoryWars
 */
public class Q00714_PathToBecomingALordSchuttgart extends Quest
{
	private static final int August = 35555;
	private static final int Newyear = 31961;
	private static final int Yasheni = 31958;
	private static final int GolemShard = 17162;
	
	private static final int SchuttgartCastle = 9;
	
	public Q00714_PathToBecomingALordSchuttgart()
	{
		super(714, Q00714_PathToBecomingALordSchuttgart.class.getSimpleName(), "Path to Becoming a Lord - Schuttgart");
		addStartNpc(August);
		addTalkId(August);
		addTalkId(Newyear);
		addTalkId(Yasheni);
		for (int i = 22801; i < 22812; i++)
		{
			addKillId(i);
		}
		questItemIds = new int[]
		{
			GolemShard
		};
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = player.getQuestState(getName());
		final Castle castle = CastleManager.getInstance().getCastleById(SchuttgartCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		
		if (event.equals("35555-03.htm"))
		{
			qs.startQuest();
		}
		else if (event.equals("35555-05.htm"))
		{
			qs.setCond(2);
		}
		else if (event.equals("31961-03.htm"))
		{
			qs.setCond(3);
		}
		else if (event.equals("31958-02.htm"))
		{
			qs.setCond(5);
		}
		else if (event.equals("35555-08.htm"))
		{
			if (castle.getOwner().getLeader().getPlayerInstance() != null)
			{
				final NpcSay packet = new NpcSay(npc.getObjectId(), Say2.NPC_SHOUT, npc.getId(), NpcStringId.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_SCHUTTGART_MAY_THERE_BE_GLORY_IN_THE_TERRITORY_OF_SCHUTTGART);
				packet.addStringParameter(player.getName());
				npc.broadcastPacket(packet);
				qs.exitQuest(true, true);
			}
		}
		return event;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		final Castle castle = CastleManager.getInstance().getCastleById(SchuttgartCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		final L2PcInstance castleOwner = castle.getOwner().getLeader().getPlayerInstance();
		
		switch (npc.getId())
		{
			case August:
			{
				if (qs.isCond(0))
				{
					if (castleOwner == qs.getPlayer())
					{
						if (!hasFort())
						{
							htmltext = "35555-01.htm";
						}
						else
						{
							htmltext = "35555-00.htm";
							qs.exitQuest(true);
						}
					}
					else
					{
						htmltext = "35555-00a.htm";
						qs.exitQuest(true);
					}
				}
				else if (qs.isCond(1))
				{
					htmltext = "35555-04.htm";
				}
				else if (qs.isCond(2))
				{
					htmltext = "35555-06.htm";
				}
				else if (qs.isCond(7))
				{
					htmltext = "35555-07.htm";
				}
				break;
			}
			case Newyear:
			{
				if (qs.isCond(2))
				{
					htmltext = "31961-01.htm";
				}
				else if (qs.isCond(3))
				{
					final QuestState q1 = qs.getPlayer().getQuestState(Q00114_ResurrectionOfAnOldManager.class.getSimpleName());
					final QuestState q2 = qs.getPlayer().getQuestState(Q00120_PavelsLastResearch.class.getSimpleName());
					final QuestState q3 = qs.getPlayer().getQuestState(Q00121_PavelTheGiant.class.getSimpleName());
					if ((q3 != null) && q3.isCompleted())
					{
						if ((q1 != null) && q1.isCompleted())
						{
							if ((q2 != null) && q2.isCompleted())
							{
								qs.setCond(4);
								htmltext = "31961-04.htm";
							}
							else
							{
								htmltext = "31961-04a.htm";
							}
						}
						else
						{
							htmltext = "31961-04b.htm";
						}
					}
					else
					{
						htmltext = "31961-04c.htm";
					}
				}
				break;
			}
			case Yasheni:
			{
				if (qs.isCond(4))
				{
					htmltext = "31958-01.htm";
				}
				else if (qs.isCond(5))
				{
					htmltext = "31958-03.htm";
				}
				else if (qs.isCond(6))
				{
					takeItems(player, GolemShard, -1);
					qs.setCond(7);
					htmltext = "31958-04.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public final String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		final QuestState qs = killer.getQuestState(getName());
		if ((qs != null) && qs.isCond(5))
		{
			if (getQuestItemsCount(killer, GolemShard) < 300)
			{
				giveItems(killer, GolemShard, 1);
			}
			if (getQuestItemsCount(killer, GolemShard) >= 300)
			{
				qs.setCond(6);
			}
		}
		return null;
	}
	
	private boolean hasFort()
	{
		for (Fort fortress : FortManager.getInstance().getForts())
		{
			if (fortress.getContractedCastleId() == SchuttgartCastle)
			{
				return true;
			}
		}
		return false;
	}
}