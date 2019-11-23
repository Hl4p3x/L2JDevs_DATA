/*
 * Copyright © 2004-2019 L2JDevs
 * 
 * This file is part of L2JDevs.
 * 
 * L2JDevs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2JDevs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.individual;

import org.l2jdevs.gameserver.ai.CtrlIntention;
import org.l2jdevs.gameserver.model.Location;
import org.l2jdevs.gameserver.model.actor.L2Attackable;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.holders.SkillHolder;
import org.l2jdevs.gameserver.model.itemcontainer.Inventory;
import org.l2jdevs.gameserver.network.NpcStringId;
import org.l2jdevs.gameserver.network.clientpackets.Say2;

import ai.npc.AbstractNpcAI;

/**
 * Frightened Ragna Orc AI.
 * @author Gladicek, malyelfik
 */
public final class FrightenedRagnaOrc extends AbstractNpcAI
{
	// NPC ID
	private static final int MOB_ID = 18807;
	// Chances
	private static final int ADENA = 10000;
	private static final int CHANCE = 1000;
	private static final int ADENA2 = 1000000;
	private static final int CHANCE2 = 10;
	// Skill
	private static final SkillHolder SKILL = new SkillHolder(6234);
	
	private FrightenedRagnaOrc()
	{
		super(FrightenedRagnaOrc.class.getSimpleName(), "ai/individual");
		addAttackId(MOB_ID);
		addKillId(MOB_ID);
	}
	
	public static void main(String[] args)
	{
		new FrightenedRagnaOrc();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "say":
			{
				if (npc.isDead() || !npc.isScriptValue(1))
				{
					cancelQuestTimer("say", npc, null);
					return null;
				}
				final NpcStringId msg = getRandomBoolean() ? NpcStringId.I_DONT_WANT_TO_FIGHT : NpcStringId.IS_THIS_REALLY_NECESSARY;
				broadcastNpcSay(npc, Say2.NPC_ALL, msg);
				break;
			}
			case "reward":
			{
				if (!npc.isDead() && npc.isScriptValue(2))
				{
					if (getRandom(100000) < CHANCE2)
					{
						final NpcStringId msg = getRandomBoolean() ? NpcStringId.TH_THANKS_I_COULD_HAVE_BECOME_GOOD_FRIENDS_WITH_YOU : NpcStringId.ILL_GIVE_YOU_10000000_ADENA_LIKE_I_PROMISED_I_MIGHT_BE_AN_ORC_WHO_KEEPS_MY_PROMISES;
						broadcastNpcSay(npc, Say2.NPC_ALL, msg);
						npc.setScriptValue(3);
						npc.doCast(SKILL);
						for (int i = 0; i < 10; i++)
						{
							npc.dropItem(player, Inventory.ADENA_ID, ADENA2);
						}
					}
					else if (getRandom(100000) < CHANCE)
					{
						final NpcStringId msg = getRandomBoolean() ? NpcStringId.TH_THANKS_I_COULD_HAVE_BECOME_GOOD_FRIENDS_WITH_YOU : NpcStringId.SORRY_BUT_THIS_IS_ALL_I_HAVE_GIVE_ME_A_BREAK;
						broadcastNpcSay(npc, Say2.NPC_ALL, msg);
						npc.setScriptValue(3);
						npc.doCast(SKILL);
						for (int i = 0; i < 10; i++)
						{
							((L2Attackable) npc).dropItem(player, Inventory.ADENA_ID, ADENA);
						}
					}
					else
					{
						final NpcStringId msg = getRandomBoolean() ? NpcStringId.THANKS_BUT_THAT_THING_ABOUT_10000000_ADENA_WAS_A_LIE_SEE_YA : NpcStringId.YOURE_PRETTY_DUMB_TO_BELIEVE_ME;
						broadcastNpcSay(npc, Say2.NPC_ALL, msg);
					}
					startQuestTimer("despawn", 1000, npc, null);
				}
				break;
			}
			case "despawn":
			{
				npc.setRunning();
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location((npc.getX() + getRandom(-800, 800)), (npc.getY() + getRandom(-800, 800)), npc.getZ(), npc.getHeading()));
				npc.deleteMe();
				break;
			}
		}
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (npc.isScriptValue(0))
		{
			npc.setScriptValue(1);
			startQuestTimer("say", (getRandom(5) + 3) * 1000, npc, null, true);
		}
		else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.2)) && npc.isScriptValue(1))
		{
			startQuestTimer("reward", 10000, npc, attacker);
			broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.WAIT_WAIT_STOP_SAVE_ME_AND_ILL_GIVE_YOU_10000000_ADENA);
			npc.setScriptValue(2);
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final NpcStringId msg = getRandomBoolean() ? NpcStringId.UGH_A_CURSE_UPON_YOU : NpcStringId.I_REALLY_DIDNT_WANT_TO_FIGHT;
		broadcastNpcSay(npc, Say2.NPC_ALL, msg);
		cancelQuestTimer("say", npc, null);
		cancelQuestTimer("reward", npc, player);
		return super.onKill(npc, player, isSummon);
	}
}