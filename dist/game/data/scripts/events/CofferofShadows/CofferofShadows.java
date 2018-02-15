/*
 * Copyright (C) 2004-2018 L2J DataPack
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
package events.CofferofShadows;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.event.LongTimeEvent;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Event: Coffer of Shadows.
 * @URL 404 Error: Page Not Found.
 * @author U3Games
 */
public class CofferofShadows extends LongTimeEvent
{
	// Values
	private static final int MANAGER_EVENT = 32091;
	private static final int REWARD_ID = 8659;
	private static final int REWARD_AMOUNT = 1;
	private static final int PRICE = 50000;
	
	private CofferofShadows()
	{
		super(CofferofShadows.class.getSimpleName(), "events");
		addFirstTalkId(MANAGER_EVENT);
		addStartNpc(MANAGER_EVENT);
		addTalkId(MANAGER_EVENT);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (player.getAdena() < PRICE)
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.DO_YOU_WANT_TO_PAY_S1_ADENA);
			sm.addInt(PRICE);
			player.sendPacket(sm);
			player.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
		}
		else
		{
			takeItems(player, Inventory.ADENA_ID, PRICE);
			giveItems(player, REWARD_ID, REWARD_AMOUNT);
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new CofferofShadows();
	}
}