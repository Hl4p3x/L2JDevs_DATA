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
package quests.Q00713_PathToBecomingALordAden;

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

/**
 * Path to Becoming a Lord - Aden (713)
 * TODO: Support for TerritoryWars
 */
public class Q00713_PathToBecomingALordAden extends Quest
{
	private static final int Logan = 35274;
	private static final int Orven = 30857;
	private static final int[] Orcs =
	{
		20669,
		20665
	};
	
	private static final int AdenCastle = 5;
	
	public Q00713_PathToBecomingALordAden()
	{
		super(713, Q00713_PathToBecomingALordAden.class.getSimpleName(), "Path to Becoming a Lord - Aden");
		addStartNpc(Logan);
		addTalkId(Logan);
		addTalkId(Orven);
		addKillId(Orcs);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = player.getQuestState(getName());
		final Castle castle = CastleManager.getInstance().getCastleById(AdenCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		
		if (event.equals("35274-02.htm"))
		{
			qs.startQuest();
		}
		else if (event.equals("30857-03.htm"))
		{
			qs.setCond(2);
		}
		else if (event.equals("35274-05.htm"))
		{
			if (castle.getOwner().getLeader().getPlayerInstance() != null)
			{
				final NpcSay packet = new NpcSay(npc.getObjectId(), Say2.NPC_SHOUT, npc.getId(), NpcStringId.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_ADEN_MAY_THERE_BE_GLORY_IN_THE_TERRITORY_OF_ADEN);
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
		final Castle castle = CastleManager.getInstance().getCastleById(AdenCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		final L2PcInstance castleOwner = castle.getOwner().getLeader().getPlayerInstance();
		
		switch (npc.getId())
		{
			case Logan:
			{
				if (qs.isCond(0))
				{
					if (castleOwner == qs.getPlayer())
					{
						if (!hasFort())
						{
							htmltext = "35274-01.htm";
						}
						else
						{
							htmltext = "35274-00.htm";
							qs.exitQuest(true);
						}
					}
					else
					{
						htmltext = "35274-00a.htm";
						qs.exitQuest(true);
					}
				}
				else if (qs.isCond(1))
				{
					htmltext = "35274-03.htm";
				}
				else if (qs.isCond(7))
				{
					htmltext = "35274-04.htm";
				}
				break;
			}
			case Orven:
			{
				if (qs.isCond(1))
				{
					htmltext = "30857-01.htm";
				}
				else if (qs.isCond(2))
				{
					htmltext = "30857-04.htm";
				}
				else if (qs.isCond(4))
				{
					htmltext = "30857-05.htm";
				}
				else if (qs.isCond(5))
				{
					qs.setCond(7);
					htmltext = "30857-06.htm";
				}
				else if (qs.isCond(7))
				{
					htmltext = "30857-06.htm";
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
		if ((qs != null) && qs.isCond(4))
		{
			if (qs.getInt("mobs") < 100)
			{
				qs.set("mobs", String.valueOf(qs.getInt("mobs") + 1));
			}
			else
			{
				qs.setCond(5);
			}
		}
		return null;
	}
	
	private boolean hasFort()
	{
		for (Fort fortress : FortManager.getInstance().getForts())
		{
			if (fortress.getContractedCastleId() == AdenCastle)
			{
				return true;
			}
		}
		return false;
	}
}