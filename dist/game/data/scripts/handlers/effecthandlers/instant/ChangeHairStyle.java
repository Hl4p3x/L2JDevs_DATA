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
package handlers.effecthandlers.instant;

import org.l2jdevs.gameserver.model.StatsSet;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.conditions.Condition;
import org.l2jdevs.gameserver.model.effects.AbstractEffect;
import org.l2jdevs.gameserver.model.skills.BuffInfo;

/**
 * Change Hair Style effect implementation.
 * @author Zoey76
 */
public final class ChangeHairStyle extends AbstractEffect
{
	private final int _value;
	
	public ChangeHairStyle(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
	{
		super(attachCond, applyCond, set, params);
		
		_value = params.getInt("value", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info)
	{
		if ((info.getEffector() == null) || (info.getEffected() == null) || !info.getEffector().isPlayer() || !info.getEffected().isPlayer() || info.getEffected().isAlikeDead())
		{
			return;
		}
		
		final L2PcInstance player = info.getEffector().getActingPlayer();
		player.getAppearance().setHairStyle(_value);
		player.broadcastUserInfo();
	}
}
