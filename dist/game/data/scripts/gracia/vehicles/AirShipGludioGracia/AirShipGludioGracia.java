/*
 * Copyright © 2004-2019 L2J DataPack
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
package gracia.vehicles.AirShipGludioGracia;

import com.l2jserver.gameserver.instancemanager.AirShipManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.VehiclePathPoint;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2AirShipInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;

import gracia.vehicles.AirShipController;

/**
 * @author DS, Sacrifice
 */
public final class AirShipGludioGracia extends AirShipController
{
	private static final int[] CONTROLLERS =
	{
		32607, // Keucereus Alliance Base Controller
		32609 // Warf of Gludio Airship's Controller
	};
	
	private static final int GLUDIO_DOCK_ID = 10; // Warf of Gludio Airship's
	private static final int GRACIA_DOCK_ID = 11; // Keucereus Alliance Base
	
	protected static final Location OUST_GLUDIO = new Location(-149379, 255246, -80);
	protected static final Location OUST_GRACIA = new Location(-186563, 243590, 2608);
	
	protected static final VehiclePathPoint[] GLUDIO_TO_WARPGATE =
	{
		new VehiclePathPoint(-151202, 252556, 231),
		new VehiclePathPoint(-160403, 256144, 222),
		new VehiclePathPoint(-167874, 256731, -509, 0, 41035) // teleport: x, y, z, speed=0, heading
	};
	
	protected static final VehiclePathPoint[] GRACIA_TO_WARPGATE =
	{
		new VehiclePathPoint(-187801, 244997, 2672),
		new VehiclePathPoint(-188520, 245932, 2465),
		new VehiclePathPoint(-189932, 245243, 1682),
		new VehiclePathPoint(-191192, 242969, 1523),
		new VehiclePathPoint(-190408, 239088, 1706),
		new VehiclePathPoint(-187475, 237113, 2768),
		new VehiclePathPoint(-184673, 238433, 2802),
		new VehiclePathPoint(-184524, 241119, 2816),
		new VehiclePathPoint(-182129, 243385, 2733),
		new VehiclePathPoint(-179440, 243651, 1337),
		new VehiclePathPoint(-174538, 246185, 39),
		new VehiclePathPoint(-172595, 247737, 398),
		new VehiclePathPoint(-171822, 250061, 425),
		new VehiclePathPoint(-169763, 254815, 282),
		new VehiclePathPoint(-168067, 256626, 343),
		new VehiclePathPoint(-157261, 255664, 221, 0, 64781) // teleport: x, y, z, speed=0, heading
	};
	
	protected static final VehiclePathPoint[] WARPGATE_TO_GLUDIO =
	{
		new VehiclePathPoint(-153414, 255385, 221),
		new VehiclePathPoint(-149548, 258172, 221),
		new VehiclePathPoint(-146884, 257097, 221),
		new VehiclePathPoint(-146672, 254239, 221),
		new VehiclePathPoint(-147855, 252712, 206),
		new VehiclePathPoint(-149378, 252552, 198)
	};
	
	protected static final VehiclePathPoint[] WARPGATE_TO_GRACIA =
	{
		new VehiclePathPoint(-169763, 254815, 282),
		new VehiclePathPoint(-171822, 250061, 425),
		new VehiclePathPoint(-172595, 247737, 398),
		new VehiclePathPoint(-174538, 246185, 39),
		new VehiclePathPoint(-179440, 243651, 1337),
		new VehiclePathPoint(-182601, 243957, 2739),
		new VehiclePathPoint(-184952, 245122, 2694),
		new VehiclePathPoint(-186936, 244563, 2617)
	};
	
	protected volatile int _cycle;
	
	protected volatile L2AirShipInstance _ship;
	
	private boolean _foundAtcGludio;
	private boolean _foundAtcGracia;
	
	private L2Npc _atcGludio;
	private L2Npc _atcGracia;
	
	public AirShipGludioGracia()
	{
		super(-1, AirShipGludioGracia.class.getSimpleName(), "gracia/vehicles");
		addStartNpc(CONTROLLERS);
		addFirstTalkId(CONTROLLERS);
		addTalkId(CONTROLLERS);
		_cycle = 0;
		_ship = AirShipManager.getInstance().getNewAirShip(-149378, 252552, 198, 33837);
		_foundAtcGludio = false;
		_foundAtcGracia = false;
		_atcGludio = null;
		_atcGracia = null;
		_ship.setOustLoc(OUST_GLUDIO);
		_ship.setInDock(GLUDIO_DOCK_ID);
		_ship.registerEngine(new RunAirShip());
		_ship.runEngine(60000);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (player.isTransformed())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_TRANSFORMED);
			return null;
		}
		else if (player.isParalyzed())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_PETRIFIED);
			return null;
		}
		else if (player.isDead() || player.isFakeDeath())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_DEAD);
			return null;
		}
		else if (player.isFishing())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_FISHING);
			return null;
		}
		else if (player.isInCombat())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_BATTLE);
			return null;
		}
		else if (player.isInDuel())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_A_DUEL);
			return null;
		}
		else if (player.isSitting())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_SITTING);
			return null;
		}
		else if (player.isCastingNow())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_CASTING);
			return null;
		}
		else if (player.isCursedWeaponEquipped())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_A_CURSED_WEAPON_IS_EQUIPPED);
			return null;
		}
		else if (player.isCombatFlagEquipped())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_HOLDING_A_FLAG);
			return null;
		}
		else if (player.hasSummon() || player.isMounted())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_A_PET_OR_A_SERVITOR_IS_SUMMONED);
			return null;
		}
		else if (_ship.isInDock() && _ship.isInsideRadius(player, 600, true, false))
		{
			_ship.addPassenger(player);
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return npc.getId() + ".html";
	}
	
	@Override
	public boolean unload(boolean removeFromList)
	{
		if (_ship != null)
		{
			_ship.oustPlayers();
			_ship.deleteMe();
		}
		return super.unload(removeFromList);
	}
	
	private L2Npc findController()
	{
		// Check objects around the ship
		for (L2Object obj : L2World.getInstance().getVisibleObjects(_ship, 600))
		{
			if (obj.isNpc())
			{
				for (int id : CONTROLLERS)
				{
					if (obj.getId() == id)
					{
						return (L2Npc) obj;
					}
				}
			}
		}
		return null;
	}
	
	protected void broadcastInGludio(NpcStringId npcString)
	{
		if (!_foundAtcGludio)
		{
			_foundAtcGludio = true;
			_atcGludio = findController();
		}
		if (_atcGludio != null)
		{
			_atcGludio.broadcastPacket(new NpcSay(_atcGludio.getObjectId(), Say2.NPC_SHOUT, _atcGludio.getId(), npcString));
		}
	}
	
	protected void broadcastInGracia(NpcStringId npcStringId)
	{
		if (!_foundAtcGracia)
		{
			_foundAtcGracia = true;
			_atcGracia = findController();
		}
		if (_atcGracia != null)
		{
			_atcGracia.broadcastPacket(new NpcSay(_atcGracia.getObjectId(), Say2.NPC_SHOUT, _atcGracia.getId(), npcStringId));
		}
	}
	
	private final class RunAirShip implements Runnable
	{
		protected RunAirShip()
		{
		}
		
		@Override
		public void run()
		{
			try
			{
				switch (_cycle)
				{
					case 0:
					{
						broadcastInGludio(NpcStringId.THE_REGULARLY_SCHEDULED_AIRSHIP_THAT_FLIES_TO_THE_GRACIA_CONTINENT_HAS_DEPARTED);
						_ship.setInDock(0);
						_ship.executePath(GLUDIO_TO_WARPGATE);
						break;
					}
					case 1:
					{
						_ship.setOustLoc(OUST_GRACIA);
						_ship.runEngine(5000);
						break;
					}
					case 2:
					{
						_ship.executePath(WARPGATE_TO_GRACIA);
						break;
					}
					case 3:
					{
						broadcastInGracia(NpcStringId.THE_REGULARLY_SCHEDULED_AIRSHIP_HAS_ARRIVED_IT_WILL_DEPART_FOR_THE_ADEN_CONTINENT_IN_1_MINUTE);
						_ship.setInDock(GRACIA_DOCK_ID);
						_ship.oustPlayers();
						_ship.runEngine(60000);
						break;
					}
					case 4:
					{
						broadcastInGracia(NpcStringId.THE_REGULARLY_SCHEDULED_AIRSHIP_THAT_FLIES_TO_THE_ADEN_CONTINENT_HAS_DEPARTED);
						_ship.setInDock(0);
						_ship.executePath(GRACIA_TO_WARPGATE);
						break;
					}
					case 5:
					{
						_ship.setOustLoc(OUST_GLUDIO);
						_ship.runEngine(5000);
						break;
					}
					case 6:
					{
						_ship.executePath(WARPGATE_TO_GLUDIO);
						break;
					}
					case 7:
					{
						broadcastInGludio(NpcStringId.THE_REGULARLY_SCHEDULED_AIRSHIP_HAS_ARRIVED_IT_WILL_DEPART_FOR_THE_GRACIA_CONTINENT_IN_1_MINUTE);
						_ship.setInDock(GLUDIO_DOCK_ID);
						_ship.oustPlayers();
						_ship.runEngine(60000);
						break;
					}
				}
				_cycle++;
				if (_cycle > 7)
				{
					_cycle = 0;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}