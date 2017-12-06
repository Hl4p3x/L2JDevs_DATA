/*
 * Copyright (C) 2008-2017 L2JDev Mods
 * 
 * This file is part of L2JDev Mods.
 * 
 * L2JDev Mods is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2JDev Mods is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package custom.L2JMods;

import com.u3games.l2jmods.ModsManager;

/**
 * This is the starter to load the Mods Manager
 * @author Zephyr
 */
public class L2JModsStarter
{
	private L2JModsStarter()
	{
		ModsManager.getInstance();
	}
	
	public static void main(String[] args)
	{
		new L2JModsStarter();
	}
}