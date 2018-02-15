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
package events.AngelCat;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.event.LongTimeEvent;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Angel Cat event.
 * @author U3Games
 */
public class AngelCat extends LongTimeEvent
{
	// Values
	private static final int ANGEL_CAT = 4308;
	private static final int REWARD_ID = 21726;
	private static final int REWARD_AMOUNT = 1;
	private static final int REUSE_GIT_TIME = 24;
	
	// Mist
	private static final String REUSE = AngelCat.class.getSimpleName() + "_reuse";
	
	private AngelCat()
	{
		super(AngelCat.class.getSimpleName(), "events");
		addFirstTalkId(ANGEL_CAT);
		addStartNpc(ANGEL_CAT);
		addTalkId(ANGEL_CAT);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		switch (event)
		{
			case "gift":
			{
				final long reuse = player.getVariables().getLong(REUSE, 0);
				if (reuse > System.currentTimeMillis())
				{
					long remainingTime = (reuse - System.currentTimeMillis()) / 1000;
					int hours = (int) (remainingTime / 3600);
					int minutes = (int) ((remainingTime % 3600) / 60);
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.AVAILABLE_AFTER_S1_S2_HOURS_S3_MINUTES);
					sm.addItemName(REWARD_ID);
					sm.addInt(hours);
					sm.addInt(minutes);
					player.sendPacket(sm);
				}
				else
				{
					player.addItem("AngelCat-Gift", REWARD_ID, REWARD_AMOUNT, npc, true);
					player.getVariables().set(REUSE, System.currentTimeMillis() + (REUSE_GIT_TIME * 3600000));
				}
				
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return npc.getId() + ".htm";
	}
	
	public static void main(String[] args)
	{
		new AngelCat();
	}
}