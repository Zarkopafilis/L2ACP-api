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

import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

/**
 * @author Elfocrash
 * @author zarkopafilis
 */
public class GiveItemRequest extends L2ACPRequest
{
	private String username;
	private int itemId, itemCount, enchant;

	@Override
	public L2ACPResponse getResponse()
	{
		
		L2PcInstance player = World.getInstance().getPlayer(username);
		if(player == null){
			player = L2PcInstance.restore(Helpers.getPlayerIdByName(username));
		}
		
		if(enchant > 0 && itemCount > 1){
			return new L2ACPResponse(500, localeService.getString("requests.error"));
		}
		
		if(enchant > 0){
			ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), itemId);
		
			item.setEnchantLevel(enchant);
			player.addItem("Give item", item, player, true);
		}else if(itemCount > 0){
			player.addItem("Give item", itemId, itemCount, player, true);
		}
		return new L2ACPResponse(200, localeService.getString("requests.ok"));
	}
	
	@Override
	public void setContent(JsonObject content){
		super.setContent(content);
		username = content.get("username").getAsString();
		itemId = content.get("itemId").getAsInt();
		itemCount = content.get("itemCount").getAsInt();
		enchant = content.get("enchant").getAsInt();
	}
}
