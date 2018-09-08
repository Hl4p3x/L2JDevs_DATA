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
package handlers.targethandlers;

import static com.l2jserver.gameserver.model.skills.targets.L2TargetType.ENEMY_ONLY;
import static com.l2jserver.gameserver.network.SystemMessageId.INCORRECT_TARGET;

import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2AdventurerInstance;
import com.l2jserver.gameserver.model.actor.instance.L2AuctioneerInstance;
import com.l2jserver.gameserver.model.actor.instance.L2CastleDoormenInstance;
import com.l2jserver.gameserver.model.actor.instance.L2ClanHallDoormenInstance;
import com.l2jserver.gameserver.model.actor.instance.L2ClanHallManagerInstance;
import com.l2jserver.gameserver.model.actor.instance.L2DawnPriestInstance;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2DoormenInstance;
import com.l2jserver.gameserver.model.actor.instance.L2DungeonGatekeeperInstance;
import com.l2jserver.gameserver.model.actor.instance.L2DuskPriestInstance;
import com.l2jserver.gameserver.model.actor.instance.L2FortDoormenInstance;
import com.l2jserver.gameserver.model.actor.instance.L2FortLogisticsInstance;
import com.l2jserver.gameserver.model.actor.instance.L2FortManagerInstance;
import com.l2jserver.gameserver.model.actor.instance.L2GuardInstance;
import com.l2jserver.gameserver.model.actor.instance.L2MerchantInstance;
import com.l2jserver.gameserver.model.actor.instance.L2NpcBufferInstance;
import com.l2jserver.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2ObservationInstance;
import com.l2jserver.gameserver.model.actor.instance.L2OlympiadManagerInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PetManagerInstance;
import com.l2jserver.gameserver.model.actor.instance.L2RaceManagerInstance;
import com.l2jserver.gameserver.model.actor.instance.L2SepulcherNpcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2SignsPriestInstance;
import com.l2jserver.gameserver.model.actor.instance.L2TamedBeastInstance;
import com.l2jserver.gameserver.model.actor.instance.L2TeleporterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2TerrainObjectInstance;
import com.l2jserver.gameserver.model.actor.instance.L2TrainerInstance;
import com.l2jserver.gameserver.model.actor.instance.L2VillageMasterDElfInstance;
import com.l2jserver.gameserver.model.actor.instance.L2VillageMasterDwarfInstance;
import com.l2jserver.gameserver.model.actor.instance.L2VillageMasterFighterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2VillageMasterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2VillageMasterKamaelInstance;
import com.l2jserver.gameserver.model.actor.instance.L2VillageMasterMysticInstance;
import com.l2jserver.gameserver.model.actor.instance.L2VillageMasterOrcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2VillageMasterPriestInstance;
import com.l2jserver.gameserver.model.actor.instance.L2WarehouseInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.L2TargetType;
import com.l2jserver.gameserver.model.zone.ZoneId;

/**
 * Enemy Only target type handler.
 * @author Zoey76
 * @since 2.6.0.0
 */
public class EnemyOnly implements ITargetTypeHandler
{
	@Override
	public L2Object[] getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		switch (skill.getAffectScope())
		{
			case SINGLE:
			{
				if (target == null)
				{
					return EMPTY_TARGET_LIST;
				}
				
				final L2PcInstance player = activeChar.getActingPlayer();
				if (target.isDead() //
					|| (skill.getTargetType() == L2TargetType.SELF) //
					|| (target instanceof L2AdventurerInstance) //
					|| (target instanceof L2AuctioneerInstance) //
					|| (target instanceof L2CastleDoormenInstance) //
					|| (target instanceof L2ClanHallDoormenInstance) //
					|| (target instanceof L2ClanHallManagerInstance) //
					|| (target instanceof L2DoorInstance) //
					|| (target instanceof L2DoormenInstance) //
					|| (target instanceof L2DungeonGatekeeperInstance) //
					|| (target instanceof L2DuskPriestInstance) //
					|| (target instanceof L2DawnPriestInstance) //
					|| (target instanceof L2FortDoormenInstance) //
					|| (target instanceof L2FortLogisticsInstance) //
					|| (target instanceof L2FortManagerInstance) //
					|| (target instanceof L2GuardInstance) //
					|| (target instanceof L2MerchantInstance) //
					|| (target instanceof L2NpcBufferInstance) //
					|| (target instanceof L2NpcInstance) //
					|| (target instanceof L2ObservationInstance) //
					|| (target instanceof L2OlympiadManagerInstance) //
					|| (target instanceof L2PetManagerInstance) //
					|| (target instanceof L2RaceManagerInstance) //
					|| (target instanceof L2SepulcherNpcInstance) //
					|| (target instanceof L2SignsPriestInstance) //
					|| (target instanceof L2TamedBeastInstance) //
					|| (target instanceof L2TeleporterInstance) //
					|| (target instanceof L2TerrainObjectInstance) //
					|| (target instanceof L2TrainerInstance) //
					|| (target instanceof L2VillageMasterDElfInstance) //
					|| (target instanceof L2VillageMasterDwarfInstance) //
					|| (target instanceof L2VillageMasterFighterInstance) //
					|| (target instanceof L2VillageMasterInstance) //
					|| (target instanceof L2VillageMasterKamaelInstance) //
					|| (target instanceof L2VillageMasterMysticInstance) //
					|| (target instanceof L2VillageMasterOrcInstance) //
					|| (target instanceof L2VillageMasterPriestInstance) //
					|| (target instanceof L2WarehouseInstance) //
					|| (!target.isAttackable() //
						&& (player != null) //
						&& player.isInPartyWith(target) //
						&& player.isInClanWith(target) //
						&& player.isInAllyWith(target) //
						&& player.isInCommandChannelWith(target) //
						&& player.isOnSameSiegeSideWith(target) //
						&& !(player.isInsideZone(ZoneId.PVP) && target.isInsideZone(ZoneId.PVP)) //
						&& !player.isAttackable() //
						&& !player.isInOlympiadMode() //
						&& !player.isAtWarWith(target) //
						&& !player.checkIfPvP(target) //
						&& !player.checkIfPvP(target)))
				{
					activeChar.sendPacket(INCORRECT_TARGET);
					return EMPTY_TARGET_LIST;
				}
				
				return new L2Character[]
				{
					target
				};
			}
		}
		return EMPTY_TARGET_LIST;
	}
	
	@Override
	public Enum<L2TargetType> getTargetType()
	{
		return ENEMY_ONLY;
	}
}