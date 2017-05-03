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

import net.sf.l2j.gameserver.LoginServerThread;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

/*
 * @author Elfocrash
 * @author zarkopafilis
 */
public class PunishRequest extends L2ACPRequest {

	private int punishId, time = 0;
	private String playerName;

	@Override
	public L2ACPResponse getResponse() {
		
		L2PcInstance player = World.getInstance().getPlayer(playerName);
		if(player == null){
			player = L2PcInstance.restore( Helpers.getPlayerIdByName(playerName));
		}
		
		switch(punishId){
			case 1: // ban account
				try{
					player.setPunishLevel(L2PcInstance.PunishLevel.ACC, time);
				}catch(Exception e){
					LoginServerThread.getInstance().sendAccessLevel(playerName, -100);
				}
					
				return new L2ACPResponse(200, localeService.getString("requests.punish.account-ban"));//"Account successfully banned"
			case 2: // ban char
					player.setPunishLevel(L2PcInstance.PunishLevel.CHAR, time);
				return new L2ACPResponse(200, localeService.getString("requests.punish.char-ban"));//"Successfully banned"
			case 3: // ban chat
					player.setPunishLevel(L2PcInstance.PunishLevel.CHAT, time);
				return new L2ACPResponse(200, localeService.getString("requests.punish.chat-ban"));//"Successfully chat banned"
			case 4: // ban jail
					player.setPunishLevel(L2PcInstance.PunishLevel.JAIL, time);
				return new L2ACPResponse(200,localeService.getString("requests.punish.jail"));//"Successfully jailed"
			case 5: // unban account
					LoginServerThread.getInstance().sendAccessLevel(Helpers.getAccountName(player.getName()), 0);
				return new L2ACPResponse(200, localeService.getString("requests.punish.account-unban"));//"Account unbanned"
			case 6: // unban char
				Helpers.changeCharAccessLevel(null, playerName, 0);
				return new L2ACPResponse(200, localeService.getString("requests.punish.char-unban"));//"Character unbanned"
			case 7: // unban chat
				try{
					if (player.isChatBanned())
					{
						player.setPunishLevel(L2PcInstance.PunishLevel.NONE, 0);
						return new L2ACPResponse(200, localeService.getString("requests.punish.chat-unban"));//"Chat ban has been lifted"
					}
					else
						return new L2ACPResponse(500,localeService.getString("requests.punish.chat-unban-no-ban"));//"User isn't currently chat banned"
				}catch(Exception e){
					Helpers.banChatOfflinePlayer(playerName, 0, false);
				}
					
				//break;
			case 8:
				player.setPunishLevel(L2PcInstance.PunishLevel.NONE, 0);
				return new L2ACPResponse(200, localeService.getString("requests.punish.unjail"));//"Character unjailed"
			case 9:
				if(player.isOnline())
				{
					player.logout();
					return new L2ACPResponse(200, localeService.getString("requests.punish.kick"));//"Character kicked"
				}
				return new L2ACPResponse(5000, localeService.getString("requests.punish.player-offline"));//"Character wasn't online"
				
		}
		
		return new L2ACPResponse(200,localeService.getString("requests.ok"));
	}
	
	
	@Override
	public void setContent(JsonObject content){
		super.setContent(content);
		
		punishId = content.get("punishId").getAsInt();
		playerName = content.get("playerName").getAsString();
		time = content.get("time").getAsInt();
	}
}