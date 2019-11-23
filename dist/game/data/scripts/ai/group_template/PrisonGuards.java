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
package ai.group_template;

import org.l2jdevs.gameserver.model.L2Object;
import org.l2jdevs.gameserver.model.actor.L2Attackable;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.holders.SkillHolder;
import org.l2jdevs.gameserver.model.skills.Skill;
import org.l2jdevs.gameserver.network.NpcStringId;
import org.l2jdevs.gameserver.network.clientpackets.Say2;

import ai.npc.AbstractNpcAI;

/**
 * Prison Guards AI.
 * @author St3eT
 */
public final class PrisonGuards extends AbstractNpcAI
{
	// NPCs
	private static final int GUARD_HEAD = 18367; // Prison Guard
	private static final int GUARD = 18368; // Prison Guard
	// Item
	private static final int STAMP = 10013; // Race Stamp
	// Skills
	private static final int TIMER = 5239; // Event Timer
	private static final SkillHolder STONE = new SkillHolder(4578); // Petrification
	private static final SkillHolder SILENCE = new SkillHolder(4098, 9); // Silence
	
	private PrisonGuards()
	{
		super(PrisonGuards.class.getSimpleName(), "ai/group_template");
		addAttackId(GUARD_HEAD, GUARD);
		addSpawnId(GUARD_HEAD, GUARD);
		addNpcHateId(GUARD);
		addSkillSeeId(GUARD);
		addSpellFinishedId(GUARD_HEAD, GUARD);
	}
	
	public static void main(String[] args)
	{
		new PrisonGuards();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("CLEAR_STATUS"))
		{
			npc.setScriptValue(0);
		}
		else if (event.equals("CHECK_HOME"))
		{
			if ((npc.calculateDistance(npc.getSpawn().getLocation(), false, false) > 10) && !npc.isInCombat() && !npc.isDead())
			{
				npc.teleToLocation(npc.getSpawn().getLocation());
			}
			startQuestTimer("CHECK_HOME", 30000, npc, null);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isSummon)
	{
		if (npc.getId() == GUARD_HEAD)
		{
			if (player.isAffectedBySkill(TIMER))
			{
				if ((getRandom(100) < 10) && (npc.calculateDistance(player, true, false) < 100))
				{
					if ((getQuestItemsCount(player, STAMP) <= 3) && npc.isScriptValue(0))
					{
						giveItems(player, STAMP, 1);
						npc.setScriptValue(1);
						startQuestTimer("CLEAR_STATUS", 600000, npc, null);
					}
				}
			}
			else
			{
				npc.setTarget(player);
				npc.doCast(STONE);
				broadcastNpcSay(npc, Say2.ALL, NpcStringId.ITS_NOT_EASY_TO_OBTAIN);
			}
		}
		else
		{
			if (!player.isAffectedBySkill(TIMER) && (npc.calculateDistance(npc.getSpawn().getLocation(), false, false) < 2000))
			{
				npc.setTarget(player);
				npc.doCast(STONE);
				broadcastNpcSay(npc, Say2.ALL, NpcStringId.YOURE_OUT_OF_YOUR_MIND_COMING_HERE);
			}
		}
		return super.onAttack(npc, player, damage, isSummon);
	}
	
	@Override
	public boolean onNpcHate(L2Attackable mob, L2PcInstance player, boolean isSummon)
	{
		return player.isAffectedBySkill(TIMER);
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, L2Object[] targets, boolean isSummon)
	{
		if (!caster.isAffectedBySkill(TIMER))
		{
			npc.setTarget(caster);
			npc.doCast(SILENCE);
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc.getId() == GUARD_HEAD)
		{
			npc.setIsImmobilized(true);
			npc.setIsInvul(true);
		}
		else
		{
			npc.setIsNoRndWalk(true);
			cancelQuestTimer("CHECK_HOME", npc, null);
			startQuestTimer("CHECK_HOME", 30000, npc, null);
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, Skill skill)
	{
		if ((skill == SILENCE.getSkill()) || (skill == STONE.getSkill()))
		{
			((L2Attackable) npc).clearAggroList();
			npc.setTarget(npc);
		}
		return super.onSpellFinished(npc, player, skill);
	}
}
