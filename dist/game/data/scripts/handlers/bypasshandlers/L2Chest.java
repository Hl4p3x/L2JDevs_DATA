/*
 * Copyright © 2020 L2RogueLike
 *
 * This file is part of L2JDevs/L2RogueLike fork.
 *
 * L2JDevs & L2RogueLike is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JDevs & L2RogueLike is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.bypasshandlers;

import org.l2jdevs.gameserver.handler.IBypassHandler;
import org.l2jdevs.gameserver.model.L2Object;
import org.l2jdevs.gameserver.model.L2World;
import org.l2jdevs.gameserver.model.actor.L2Character;
import org.l2jdevs.gameserver.model.actor.L2Npc;
// import org.l2jdevs.gameserver.model.actor.instance.L2ChestInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2ChestInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class L2Chest implements IBypassHandler {
    private static final Logger LOG = LoggerFactory.getLogger(L2Chest.class);
    private static final String[] COMMANDS = {
        "L2Chest"
    };

    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }

    @Override
    /**
     * @param cmd server bypass command, such as "L2Chest 18266 open" and so. See L2ChestInstance.openChest
     * @param _target always null?
     */
    public boolean useBypass(final String cmd, final L2PcInstance pc, final L2Character _target) {
        //final StringTokenizer st = new StringTokenizer(command);
        String[] vals = cmd.trim().split("\\s+");
        LOG.error("chest useBypass cmd = " + cmd);
        final L2ChestInstance target;
        {
            L2Object t;
            try {
                t = L2World.getInstance().findObject(Integer.parseInt(vals[1]));
            } catch (Exception ex) {
                t = null;
            }
            if(t instanceof L2ChestInstance)
                target = (L2ChestInstance)t;
            else
                    return false;
        }
        showL2ChestWindow(pc, vals, target);
        return false;
    }

    /**
     * Open a L2Chest window on client with the text of the L2NpcInstance.<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Get the text of the selected HTML file in function of the npcId and of the page number</li>
     * <li>Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance</li>
     * <li>Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet</li><BR>
     *
     * @param pc     The L2PcInstance that talk with the L2NpcInstance
     * @param cmd    command split by tokens
     * @param chest  affected L2World object
     */
    public static final void showL2ChestWindow(final L2PcInstance pc, final String[] cmd, final L2ChestInstance chest) {
        // fixme: test stub
        StringBuilder msg = new StringBuilder("<html><title>L2Chest handler</title><body>");
        msg.append("Object ID ");
        msg.append(chest.getId());
        msg.append("<br1/>");
        msg.append("Actions:<br1/><table>");
        msg.append("<tr><td align=\"center\"><button value=\"Open\" width=120 height=25 action=\"bypass L2Chest ");
        msg.append(" open\"/></td></tr>");
        msg.append("<tr><td align=\"center\"><button value=\"Check for traps\" width=120 height=25 action=\"bypass L2Chest ");
        msg.append(" untrap\"/></td></tr>");
        msg.append("<tr><td align=\"center\"><button value=\"Leave\" width=120 height=25 action=\"bypass L2Chest ");
        msg.append(" leave\"/></td></tr>");
        msg.append("</table><br1/>Server command was: <br>");
        msg.append(cmd);
        msg.append("</body></html>");
        Util.sendHtml(pc, msg.toString());
    }
}
