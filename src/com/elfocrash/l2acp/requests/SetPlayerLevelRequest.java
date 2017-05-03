/*
 * Copyright (C) 2017  Nick Chapsas
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 2 of the License, or (at your option) any later
 * version.
 * 
 * L2ACP is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.elfocrash.l2acp.requests;

import com.elfocrash.l2acp.responses.L2ACPResponse;
import com.elfocrash.l2acp.util.Helpers;
import com.google.gson.JsonObject;

import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.Experience;

/*
 * @author Elfocrash
 * @author zarkopafilis
 */
public class SetPlayerLevelRequest extends L2ACPRequest {

    private String playerName;
    private int level;
    
	@Override
	public L2ACPResponse getResponse() {
		
		L2PcInstance player = World.getInstance().getPlayer(playerName);
		if(player == null)
			player = L2PcInstance.restore( Helpers.getPlayerIdByName(playerName));
		
		if(player == null)
			return new L2ACPResponse(500,localeService.getString("requests.error"));
		
		try
		{

			byte lvl = Byte.parseByte(String.valueOf(level));
			if (lvl >= 1 && lvl <= Experience.MAX_LEVEL)
			{
				long pXp = player.getExp();
				long tXp = Experience.LEVEL[lvl];
				
				if (pXp > tXp)
					player.removeExpAndSp(pXp - tXp, 0);
				else if (pXp < tXp)
					player.addExpAndSp(tXp - pXp, 0);
				
				player.broadcastUserInfo();
				player.store();
			}
			else
			{
				throw new NumberFormatException();
			}
		}
		catch (NumberFormatException e)
		{
			return new L2ACPResponse(500, localeService.getString("requests.set-level.not-specified").replace("{}", "" + Experience.MAX_LEVEL));
		}
		
		
		return new L2ACPResponse(200, localeService.getString("requests.ok"));
		
	}
	
	
	@Override
	public void setContent(JsonObject content){
		super.setContent(content);
		
		playerName = content.get("playerName").getAsString();
		level = content.get("level").getAsInt();
	}
}