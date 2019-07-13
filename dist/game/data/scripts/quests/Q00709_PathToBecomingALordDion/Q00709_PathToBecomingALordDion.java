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
package quests.Q00709_PathToBecomingALordDion;

import org.l2jdevs.gameserver.instancemanager.CastleManager;
import org.l2jdevs.gameserver.instancemanager.FortManager;
import org.l2jdevs.gameserver.model.L2Clan;
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
import org.l2jdevs.gameserver.util.Util;
import org.l2jdevs.util.Rnd;

/**
 * Path to Becoming a Lord - Dion (709)
 * TODO: Support for TerritoryWars
 */
public class Q00709_PathToBecomingALordDion extends Quest
{
	private static final int Crosby = 35142;
	private static final int Rouke = 31418;
	private static final int Sophya = 30735;
	private static final int MandragoraRoot = 13849;
	private static final int BloodyAxeAide = 27392;
	private static final int Epaulette = 13850;
	private static final int[] OlMahums =
	{
		20208,
		20209,
		20210,
		20211,
		BloodyAxeAide
	};
	private static final int[] Manragoras =
	{
		20154,
		20155,
		20156
	};
	private static final int DionCastle = 2;
	
	public Q00709_PathToBecomingALordDion()
	{
		super(709, Q00709_PathToBecomingALordDion.class.getSimpleName(), "Path to Becoming a Lord - Dion");
		addStartNpc(Crosby);
		addTalkId(Crosby);
		addTalkId(Sophya);
		addTalkId(Rouke);
		questItemIds = new int[]
		{
			Epaulette,
			MandragoraRoot
		};
		addKillId(OlMahums);
		addKillId(Manragoras);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final QuestState qs = player.getQuestState(getName());
		final Castle castle = CastleManager.getInstance().getCastleById(DionCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		
		final L2PcInstance castleOwner = castle.getOwner().getLeader().getPlayerInstance();
		if (event.equals("35142-03.htm"))
		{
			qs.startQuest();
		}
		else if (event.equals("35142-06.htm"))
		{
			if (isLordAvailable(2, qs))
			{
				castleOwner.getQuestState(getName()).set("confidant", String.valueOf(qs.getPlayer().getObjectId()));
				castleOwner.getQuestState(getName()).setCond(3);
				qs.setState(State.STARTED);
			}
			else
			{
				htmltext = "35142-05a.htm";
			}
		}
		else if (event.equals("31418-03.htm"))
		{
			if (isLordAvailable(3, qs))
			{
				castleOwner.getQuestState(getName()).setCond(4);
			}
			else
			{
				htmltext = "35142-05a.htm";
			}
		}
		else if (event.equals("30735-02.htm"))
		{
			qs.set("cond", "6");
		}
		else if (event.equals("30735-05.htm"))
		{
			takeItems(player, Epaulette, 1);
			qs.set("cond", "8");
		}
		else if (event.equals("31418-05.htm"))
		{
			if (isLordAvailable(8, qs))
			{
				takeItems(player, MandragoraRoot, -1);
				castleOwner.getQuestState(getName()).setCond(9);
			}
		}
		else if (event.equals("35142-10.htm"))
		{
			if (castleOwner != null)
			{
				final NpcSay packet = new NpcSay(npc.getObjectId(), Say2.NPC_SHOUT, npc.getId(), NpcStringId.S1_HAS_BECOME_LORD_OF_THE_TOWN_OF_DION_LONG_MAY_HE_REIGN);
				packet.addStringParameter(player.getName());
				npc.broadcastPacket(packet);
				qs.exitQuest(true, true);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		final Castle castle = CastleManager.getInstance().getCastleById(DionCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		final L2PcInstance castleOwner = castle.getOwner().getLeader().getPlayerInstance();
		
		switch (npc.getId())
		{
			case Crosby:
			{
				if (qs.isCond(0))
				{
					if (castleOwner == qs.getPlayer())
					{
						if (!hasFort())
						{
							htmltext = "35142-01.htm";
						}
						else
						{
							htmltext = "35142-00.htm";
							qs.exitQuest(true);
						}
					}
					else if (isLordAvailable(2, qs))
					{
						if (castleOwner.calculateDistance(npc, false, false) <= 200)
						{
							htmltext = "35142-05.htm";
						}
						else
						{
							htmltext = "35142-05a.htm";
						}
					}
					else
					{
						htmltext = "35142-00a.htm";
						qs.exitQuest(true);
					}
				}
				else if (qs.isCond(1))
				{
					qs.set("cond", "2");
					htmltext = "35142-04.htm";
				}
				else if (qs.isCond(2) || qs.isCond(3))
				{
					htmltext = "35142-04a.htm";
				}
				else if (qs.isCond(4))
				{
					qs.set("cond", "5");
					htmltext = "35142-07.htm";
				}
				else if (qs.isCond(5))
				{
					htmltext = "35142-07.htm";
				}
				else if ((qs.getCond() > 5) && (qs.getCond() < 9))
				{
					htmltext = "35142-08.htm";
				}
				else if (qs.isCond(9))
				{
					htmltext = "35142-09.htm";
				}
				break;
			}
			case Rouke:
			{
				if ((qs.getState() == State.STARTED) && qs.isCond(0) && isLordAvailable(3, qs))
				{
					if (castleOwner.getQuestState(getName()).getInt("confidant") == qs.getPlayer().getObjectId())
					{
						htmltext = "31418-01.htm";
					}
				}
				else if ((qs.getState() == State.STARTED) && qs.isCond(0) && isLordAvailable(8, qs))
				{
					if (getQuestItemsCount(player, MandragoraRoot) >= 100)
					{
						htmltext = "31418-04.htm";
					}
					else
					{
						htmltext = "31418-04a.htm";
					}
				}
				else if ((qs.getState() == State.STARTED) && qs.isCond(0) && isLordAvailable(9, qs))
				{
					htmltext = "31418-06.htm";
				}
				break;
			}
			case Sophya:
			{
				if (qs.isCond(5))
				{
					htmltext = "30735-01.htm";
				}
				else if (qs.isCond(6))
				{
					htmltext = "30735-03.htm";
				}
				else if (qs.isCond(7))
				{
					htmltext = "30735-04.htm";
				}
				else if (qs.isCond(8))
				{
					htmltext = "30735-06.htm";
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
		
		if ((qs != null) && qs.isCond(6) && Util.contains(OlMahums, npc.getId()))
		{
			if ((npc.getId() != BloodyAxeAide) && (Rnd.get(9) == 0))
			{
				addSpawn(BloodyAxeAide, npc, true, 300000);
			}
			else if (npc.getId() == BloodyAxeAide)
			{
				giveItems(killer, Epaulette, 1);
				qs.setCond(7);
			}
		}
		if ((qs != null) && (qs.getState() == State.STARTED) && qs.isCond(0) && isLordAvailable(8, qs) && Util.contains(Manragoras, npc.getId()))
		{
			if (getQuestItemsCount(killer, MandragoraRoot) < 100)
			{
				giveItems(killer, MandragoraRoot, 1);
			}
		}
		return null;
	}
	
	private boolean isLordAvailable(int cond, QuestState qs)
	{
		final Castle castle = CastleManager.getInstance().getCastleById(DionCastle);
		final L2Clan owner = castle.getOwner();
		final L2PcInstance castleOwner = castle.getOwner().getLeader().getPlayerInstance();
		if (owner != null)
		{
			if ((castleOwner != null) && (castleOwner != qs.getPlayer()) && (owner == qs.getPlayer().getClan()) && (castleOwner.getQuestState(getName()) != null) && castleOwner.getQuestState(getName()).isCond(cond))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean hasFort()
	{
		for (Fort fortress : FortManager.getInstance().getForts())
		{
			if (fortress.getContractedCastleId() == DionCastle)
			{
				return true;
			}
		}
		return false;
	}
}