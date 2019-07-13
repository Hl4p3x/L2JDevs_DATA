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
package quests.Q00708_PathToBecomingALordGludio;

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
import org.l2jdevs.util.Rnd;

/**
 * Path to Becoming a Lord - Gludio (708)
 * TODO: Support for TerritoryWars
 */
public class Q00708_PathToBecomingALordGludio extends Quest
{
	private static final int Sayres = 35100;
	private static final int Pinter = 30298;
	private static final int Bathis = 30332;
	private static final int HeadlessKnight = 20280;
	
	private static final int HeadlessKnightsArmor = 13848;
	
	private static final int[] Mobs =
	{
		20045,
		20051,
		20099,
		HeadlessKnight
	};
	
	private static final int GludioCastle = 1;
	
	public Q00708_PathToBecomingALordGludio()
	{
		super(708, Q00708_PathToBecomingALordGludio.class.getSimpleName(), "Path to Becoming a Lord - Gludio");
		addStartNpc(Sayres);
		addTalkId(Sayres);
		addTalkId(Pinter);
		addTalkId(Bathis);
		addKillId(Mobs);
		questItemIds = new int[]
		{
			HeadlessKnightsArmor
		};
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final QuestState qs = player.getQuestState(getName());
		
		final Castle castle = CastleManager.getInstance().getCastleById(GludioCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		final L2PcInstance castleOwner = castle.getOwner().getLeader().getPlayerInstance();
		if (event.equals("35100-03.htm"))
		{
			qs.startQuest();
		}
		else if (event.equals("35100-05.htm"))
		{
			qs.setCond(2);
		}
		else if (event.equals("35100-08.htm"))
		{
			if (isLordAvailable(2, qs))
			{
				castleOwner.getQuestState(getName()).set("confidant", String.valueOf(qs.getPlayer().getObjectId()));
				castleOwner.getQuestState(getName()).setCond(3);
				qs.setState(State.STARTED);
			}
			else
			{
				htmltext = "35100-05a.htm";
			}
		}
		else if (event.equals("30298-03.htm"))
		{
			if (isLordAvailable(3, qs))
			{
				castleOwner.getQuestState(getName()).setCond(4);
			}
			else
			{
				htmltext = "30298-03a.htm";
			}
		}
		else if (event.equals("30332-02.htm"))
		{
			qs.setCond(6);
		}
		else if (event.equals("30332-05.htm"))
		{
			takeItems(player, HeadlessKnightsArmor, 1);
			qs.setCond(8);
			npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_SHOUT, npc.getId(), NpcStringId.LISTEN_YOU_VILLAGERS_OUR_LIEGE_WHO_WILL_SOON_BECOME_A_LORD_HAS_DEFEATED_THE_HEADLESS_KNIGHT_YOU_CAN_NOW_REST_EASY));
		}
		else if (event.equals("30298-05.htm"))
		{
			if (isLordAvailable(8, qs))
			{
				takeItems(player, 1867, 100);
				takeItems(player, 1865, 100);
				takeItems(player, 1869, 100);
				takeItems(player, 1879, 50);
				castleOwner.getQuestState(getName()).setCond(9);
			}
			else
			{
				htmltext = "30298-03a.htm";
			}
		}
		else if (event.equals("35100-12.htm"))
		{
			if (castleOwner != null)
			{
				final NpcSay packet = new NpcSay(npc.getObjectId(), Say2.NPC_SHOUT, npc.getId(), NpcStringId.S1_HAS_BECOME_LORD_OF_THE_TOWN_OF_GLUDIO_LONG_MAY_HE_REIGN);
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
		final Castle castle = CastleManager.getInstance().getCastleById(GludioCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		final L2PcInstance castleOwner = castle.getOwner().getLeader().getPlayerInstance();
		
		switch (npc.getId())
		{
			case Sayres:
			{
				if (qs.isCond(0))
				{
					if (castleOwner == qs.getPlayer())
					{
						if (!hasFort())
						{
							htmltext = "35100-01.htm";
						}
						else
						{
							htmltext = "35100-00.htm";
							qs.exitQuest(true);
						}
					}
					else if (isLordAvailable(2, qs))
					{
						if (castleOwner.calculateDistance(npc, false, false) <= 200)
						{
							htmltext = "35100-07.htm";
						}
						else
						{
							htmltext = "35100-05a.htm";
						}
					}
					else if (qs.getState() == State.STARTED)
					{
						htmltext = "35100-08a.htm";
					}
					else
					{
						htmltext = "35100-00a.htm";
						qs.exitQuest(true);
					}
				}
				else if (qs.isCond(1))
				{
					htmltext = "35100-04.htm";
				}
				else if (qs.isCond(2))
				{
					htmltext = "35100-06.htm";
				}
				else if (qs.isCond(4))
				{
					qs.set("cond", "5");
					htmltext = "35100-09.htm";
				}
				else if (qs.isCond(5))
				{
					htmltext = "35100-10.htm";
				}
				else if ((qs.getCond() > 5) && (qs.getCond() < 9))
				{
					htmltext = "35100-08.htm";
				}
				else if (qs.isCond(9))
				{
					htmltext = "35100-11.htm";
				}
				break;
			}
			case Pinter:
			{
				if ((qs.getState() == State.STARTED) && qs.isCond(0) && isLordAvailable(3, qs))
				{
					if (castleOwner.getQuestState(getName()).getInt("confidant") == qs.getPlayer().getObjectId())
					{
						htmltext = "30298-01.htm";
					}
				}
				else if ((qs.getState() == State.STARTED) && qs.isCond(0) && isLordAvailable(8, qs))
				{
					if ((getQuestItemsCount(player, 1867) >= 100) && (getQuestItemsCount(player, 1865) >= 100) && (getQuestItemsCount(player, 1869) >= 100) && (getQuestItemsCount(player, 1879) >= 50))
					{
						htmltext = "30298-04.htm";
					}
					else
					{
						htmltext = "30298-04a.htm";
					}
				}
				else if ((qs.getState() == State.STARTED) && qs.isCond(0) && isLordAvailable(9, qs))
				{
					htmltext = "30298-06.htm";
				}
				break;
			}
			case Bathis:
			{
				if (qs.isCond(5))
				{
					htmltext = "30332-01.htm";
				}
				else if (qs.isCond(6))
				{
					htmltext = "30332-03.htm";
				}
				else if (qs.isCond(7))
				{
					htmltext = "30332-04.htm";
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
		
		if ((qs != null) && qs.isCond(6))
		{
			if ((npc.getId() != HeadlessKnight) && (Rnd.get(9) == 0))
			{
				addSpawn(HeadlessKnight, npc, true, 300000);
			}
			else if (npc.getId() == HeadlessKnight)
			{
				giveItems(killer, HeadlessKnightsArmor, 1);
				qs.setCond(7);
			}
		}
		return null;
	}
	
	private boolean isLordAvailable(int cond, QuestState qs)
	{
		final Castle castle = CastleManager.getInstance().getCastleById(GludioCastle);
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
			if (fortress.getContractedCastleId() == GludioCastle)
			{
				return true;
			}
		}
		return false;
	}
}