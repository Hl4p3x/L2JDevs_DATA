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
package handlers.admincommandhandlers;

import org.l2jdevs.gameserver.MonsterRace;
import org.l2jdevs.gameserver.ThreadPoolManager;
import org.l2jdevs.gameserver.enums.audio.Music;
import org.l2jdevs.gameserver.enums.audio.Sound;
import org.l2jdevs.gameserver.handler.IAdminCommandHandler;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.network.SystemMessageId;
import org.l2jdevs.gameserver.network.serverpackets.DeleteObject;
import org.l2jdevs.gameserver.network.serverpackets.MonRaceInfo;
import org.l2jdevs.gameserver.network.serverpackets.PlaySound;
import org.l2jdevs.gameserver.network.serverpackets.SystemMessage;

/**
 * This class handles following admin commands: - invul = turns invulnerability on/off
 * @version $Revision: 1.1.6.4 $ $Date: 2007/07/31 10:06:00 $
 */
public class AdminMonsterRace implements IAdminCommandHandler
{
	// private static Logger _log = Logger.getLogger(AdminMonsterRace.class.getName());
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_mons"
	};
	
	protected static int state = -1;
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equalsIgnoreCase("admin_mons"))
		{
			handleSendPacket(activeChar);
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void handleSendPacket(L2PcInstance activeChar)
	{
		/*
		 * -1 0 to initialize the race 0 15322 to start race 13765 -1 in middle of race -1 0 to end the race 8003 to 8027
		 */
		
		int[][] codes =
		{
			{
				-1,
				0
			},
			{
				0,
				15322
			},
			{
				13765,
				-1
			},
			{
				-1,
				0
			}
		};
		MonsterRace race = MonsterRace.getInstance();
		
		if (state == -1)
		{
			state++;
			race.newRace();
			race.newSpeeds();
			MonRaceInfo spk = new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds());
			activeChar.sendPacket(spk);
			activeChar.broadcastPacket(spk);
		}
		else if (state == 0)
		{
			state++;
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_RACE_START);
			sm.addInt(0);
			activeChar.sendPacket(sm);
			PlaySound SRace = Music.S_RACE.getPacket();
			activeChar.sendPacket(SRace);
			activeChar.broadcastPacket(SRace);
			PlaySound SRace2 = Sound.ITEMSOUND2_RACE_START.getPacket();
			activeChar.sendPacket(SRace2);
			activeChar.broadcastPacket(SRace2);
			MonRaceInfo spk = new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds());
			activeChar.sendPacket(spk);
			activeChar.broadcastPacket(spk);
			
			ThreadPoolManager.getInstance().scheduleGeneral(new RunRace(codes, activeChar), 5000);
		}
		
	}
	
	class RunRace implements Runnable
	{
		
		private final int[][] codes;
		private final L2PcInstance activeChar;
		
		public RunRace(int[][] pCodes, L2PcInstance pActiveChar)
		{
			codes = pCodes;
			activeChar = pActiveChar;
		}
		
		@Override
		public void run()
		{
			// int[][] speeds1 = MonsterRace.getInstance().getSpeeds();
			// MonsterRace.getInstance().newSpeeds();
			// int[][] speeds2 = MonsterRace.getInstance().getSpeeds();
			/*
			 * int[] speed = new int[8]; for (int i=0; i<8; i++) { for (int j=0; j<20; j++) { //_log.info("Adding "+speeds1[i][j] +" and "+ speeds2[i][j]); speed[i] += (speeds1[i][j]*1);// + (speeds2[i][j]*1); } _log.info("Total speed for "+(i+1)+" = "+speed[i]); }
			 */
			
			MonRaceInfo spk = new MonRaceInfo(codes[2][0], codes[2][1], MonsterRace.getInstance().getMonsters(), MonsterRace.getInstance().getSpeeds());
			activeChar.sendPacket(spk);
			activeChar.broadcastPacket(spk);
			ThreadPoolManager.getInstance().scheduleGeneral(new RunEnd(activeChar), 30000);
		}
	}
	
	private static class RunEnd implements Runnable
	{
		private final L2PcInstance activeChar;
		
		public RunEnd(L2PcInstance pActiveChar)
		{
			activeChar = pActiveChar;
		}
		
		@Override
		public void run()
		{
			DeleteObject obj = null;
			for (int i = 0; i < 8; i++)
			{
				obj = new DeleteObject(MonsterRace.getInstance().getMonsters()[i]);
				activeChar.sendPacket(obj);
				activeChar.broadcastPacket(obj);
			}
			state = -1;
		}
	}
}
