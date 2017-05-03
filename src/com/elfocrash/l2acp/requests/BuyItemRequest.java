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

import com.elfocrash.l2acp.models.BuyListItem;
import com.elfocrash.l2acp.responses.L2ACPResponse;
import com.elfocrash.l2acp.util.Helpers;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

/**
 * @author Elfocrash
 * @author zarkopafilis
 */
public class BuyItemRequest extends L2ACPRequest
{
	private String username, accountName;
	private int itemId, itemCount, enchant, price;

	@Override
	public L2ACPResponse getResponse()
	{
		ArrayList<BuyListItem> items = Helpers.getDonateItemList();
		boolean valid = false;
		for(BuyListItem listItem : items){
			if(listItem.ItemId == itemId && listItem.ItemCount == itemCount && listItem.Price == price && listItem.Enchant == enchant)
				valid = true;
		}
		if(!valid){
			return new L2ACPResponse(500, localeService.getString("requests.buy-sell.error"));
		}
		
		L2PcInstance player = World.getInstance().getPlayer(username);
		if(player == null){
			player = L2PcInstance.restore(Helpers.getPlayerIdByName(username));
		}
		
		if(Helpers.getDonatePoints(accountName) > price){
			if(enchant > 0){
				ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), itemId);
			
				item.setEnchantLevel(enchant);
				player.addItem("Buy item", item, player, true);
			}else if(itemCount > 0){
				player.addItem("Buy item", itemId, itemCount, player, true);
			}
			Helpers.removeDonatePoints(accountName, price);
			return new L2ACPResponse(200, localeService.getString("requests.success"));
		}	
		return new L2ACPResponse(500, localeService.getString("requests.insufficient-donate-points"));
	}
	
	@Override
	public void setContent(JsonObject content){
		super.setContent(content);
		accountName = content.get("accountName").getAsString();
		username = content.get("username").getAsString();
		itemId = content.get("itemId").getAsInt();
		itemCount = content.get("itemCount").getAsInt();
		enchant = content.get("enchant").getAsInt();
		price = content.get("price").getAsInt();
	}
}
