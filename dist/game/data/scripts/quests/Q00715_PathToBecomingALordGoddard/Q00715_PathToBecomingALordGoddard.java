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
package quests.Q00715_PathToBecomingALordGoddard;

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
 * Path to Becoming a Lord - Goddard (715)
 * TODO: Support for TerritoryWars
 */
public class Q00715_PathToBecomingALordGoddard extends Quest
{
	private static final int Alfred = 35363;
	
	private static final int WaterSpiritAshutar = 25316;
	private static final int FireSpiritNastron = 25306;
	
	private static final int GoddardCastle = 7;
	
	public Q00715_PathToBecomingALordGoddard()
	{
		super(715, Q00715_PathToBecomingALordGoddard.class.getSimpleName(), "Path to Becoming a Lord - Goddard");
		addStartNpc(Alfred);
		addTalkId(Alfred);
		addKillId(WaterSpiritAshutar);
		addKillId(FireSpiritNastron);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = player.getQuestState(getName());
		final Castle castle = CastleManager.getInstance().getCastleById(GoddardCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		
		if (event.equals("35363-03.htm"))
		{
			qs.startQuest();
		}
		else if (event.equals("35363-04a.htm"))
		{
			qs.setCond(3);
		}
		else if (event.equals("35363-04b.htm"))
		{
			qs.setCond(2);
		}
		else if (event.equals("35363-08.htm"))
		{
			if (castle.getOwner().getLeader().getPlayerInstance() != null)
			{
				final NpcSay packet = new NpcSay(npc.getObjectId(), Say2.NPC_SHOUT, npc.getId(), NpcStringId.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_GODDARD_MAY_THERE_BE_GLORY_IN_THE_TERRITORY_OF_GODDARD);
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
		final Castle castle = CastleManager.getInstance().getCastleById(GoddardCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		final L2PcInstance castleOwner = castle.getOwner().getLeader().getPlayerInstance();
		
		if (qs.isCond(0))
		{
			if (castleOwner == qs.getPlayer())
			{
				if (!hasFort())
				{
					htmltext = "35363-01.htm";
				}
				else
				{
					htmltext = "35363-00.htm";
					qs.exitQuest(true);
				}
			}
			else
			{
				htmltext = "35363-00a.htm";
				qs.exitQuest(true);
			}
		}
		else if (qs.isCond(1))
		{
			htmltext = "35363-03.htm";
		}
		else if (qs.isCond(2))
		{
			htmltext = "35363-05b.htm";
		}
		else if (qs.isCond(3))
		{
			htmltext = "35363-05a.htm";
		}
		else if (qs.isCond(4))
		{
			qs.setCond(6);
			htmltext = "35363-06b.htm";
		}
		else if (qs.isCond(5))
		{
			qs.setCond(7);
			htmltext = "35363-06a.htm";
		}
		else if (qs.isCond(6))
		{
			htmltext = "35363-06b.htm";
		}
		else if (qs.isCond(7))
		{
			htmltext = "35363-06a.htm";
		}
		else if (qs.isCond(8) || qs.isCond(9))
		{
			htmltext = "35363-07.htm";
		}
		return htmltext;
	}
	
	@Override
	public final String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final QuestState qs = player.getQuestState(getName());
		if (qs == null)
		{
			return null;
		}
		if (qs.isCond(2) && (npc.getId() == FireSpiritNastron))
		{
			qs.setCond(4);
		}
		else if (qs.isCond(3) && (npc.getId() == WaterSpiritAshutar))
		{
			qs.setCond(5);
		}
		
		if (qs.isCond(6) && (npc.getId() == WaterSpiritAshutar))
		{
			qs.setCond(9);
		}
		else if (qs.isCond(7) && (npc.getId() == FireSpiritNastron))
		{
			qs.setCond(8);
		}
		return null;
	}
	
	private boolean hasFort()
	{
		for (Fort fortress : FortManager.getInstance().getForts())
		{
			if (fortress.getContractedCastleId() == GoddardCastle)
			{
				return true;
			}
		}
		return false;
	}
}