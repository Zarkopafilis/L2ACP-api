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

import com.elfocrash.l2acp.models.PlayerInfo;
import com.elfocrash.l2acp.responses.GetPlayerInfoResponse;
import com.elfocrash.l2acp.responses.L2ACPResponse;
import com.elfocrash.l2acp.util.Helpers;
import com.google.gson.JsonObject;

import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.ClassId;

/**
 * @author Elfocrash
 * @author zarkopafilis
 */
public class GetPlayerInfoRequest extends L2ACPRequest {

	private String username;
	
	@Override
	public L2ACPResponse getResponse() {
		L2PcInstance player = World.getInstance().getPlayer(username);
		if(player == null){
			player = L2PcInstance.restore(Helpers.getPlayerIdByName(username));
		}
		PlayerInfo playerInfo = new PlayerInfo();
		playerInfo.Name = player.getName();
		playerInfo.Title = player.getTitle();
		playerInfo.Level = player.getLevel();
		playerInfo.Pvp = player.getPvpKills();
		playerInfo.Pk = player.getPkKills();
		ClassId classId = player.getClassId();
		while(classId.getParent() != null){
			classId = classId.getParent();
		}
		playerInfo.Race = classId.getId();
		playerInfo.Sex = player.getAppearance().getSex().ordinal();
		playerInfo.ClanName = player.getClan() != null ? player.getClan().getName() : "No clan";
		playerInfo.AllyName = player.getClan() != null && player.getClan().getAllyId() != -1 ? player.getClan().getAllyName() : "No ally";
		playerInfo.Hero = player.isHero();
		playerInfo.Nobless = player.isNoble();
		playerInfo.Time = player.getUptime();
		return new GetPlayerInfoResponse(200, localeService.getString("requests.ok"), playerInfo);
	}
	
	
	@Override
	public void setContent(JsonObject content){
		super.setContent(content);
		
		username = content.get("username").getAsString();
	}
}
