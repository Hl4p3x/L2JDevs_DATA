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
package handlers.usercommandhandlers;

import org.l2jdevs.gameserver.handler.IUserCommandHandler;
import org.l2jdevs.gameserver.model.L2CommandChannel;
import org.l2jdevs.gameserver.model.L2Party;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.network.SystemMessageId;
import org.l2jdevs.gameserver.network.serverpackets.SystemMessage;

/**
 * Channel Leave user command.
 * @author Chris, Zoey76
 */
public class ChannelLeave implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		96
	};
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if (id != COMMAND_IDS[0])
		{
			return false;
		}
		
		if (!activeChar.isInParty() || !activeChar.getParty().isLeader(activeChar))
		{
			activeChar.sendPacket(SystemMessageId.ONLY_PARTY_LEADER_CAN_LEAVE_CHANNEL);
			return false;
		}
		
		if (activeChar.getParty().isInCommandChannel())
		{
			final L2CommandChannel channel = activeChar.getParty().getCommandChannel();
			final L2Party party = activeChar.getParty();
			channel.removeParty(party);
			party.getLeader().sendPacket(SystemMessageId.LEFT_COMMAND_CHANNEL);
			
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_PARTY_LEFT_COMMAND_CHANNEL);
			sm.addPcName(party.getLeader());
			channel.broadcastPacket(sm);
			return true;
		}
		return false;
		
	}
}
