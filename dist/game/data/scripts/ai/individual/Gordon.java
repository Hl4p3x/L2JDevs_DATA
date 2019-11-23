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

import org.l2jdevs.gameserver.model.actor.L2Attackable;
import org.l2jdevs.gameserver.model.actor.L2Character;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;

import ai.npc.AbstractNpcAI;

/**
 * Gordon AI
 * @author TOFIZ, malyelfik
 */
public final class Gordon extends AbstractNpcAI
{
	private static final int GORDON = 29095;
	
	private Gordon()
	{
		super(Gordon.class.getSimpleName(), "ai/individual");
		addSpawnId(GORDON);
		addSeeCreatureId(GORDON);
	}
	
	public static void main(String[] args)
	{
		new Gordon();
	}
	
	@Override
	public String onSeeCreature(L2Npc npc, L2Character creature, boolean isSummon)
	{
		if (creature.isPlayer() && ((L2PcInstance) creature).isCursedWeaponEquipped())
		{
			addAttackDesire(npc, creature);
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		((L2Attackable) npc).setCanReturnToSpawnPoint(false);
		return super.onSpawn(npc);
	}
}