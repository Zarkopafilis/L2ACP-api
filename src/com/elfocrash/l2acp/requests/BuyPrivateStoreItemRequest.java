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

import java.util.HashSet;
import java.util.Set;

import net.sf.l2j.gameserver.model.ItemRequest;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance.StoreType;
import net.sf.l2j.gameserver.model.tradelist.TradeItem;
import net.sf.l2j.gameserver.model.tradelist.TradeList;


/**
 * @author Elfocrash
 * @author zarkopafilis
 */
public class BuyPrivateStoreItemRequest extends L2ACPRequest
{
	private int objectId, sellerId, count;
    private String buyerName;

	@Override
	public L2ACPResponse getResponse()
	{		
		L2PcInstance player = World.getInstance().getPlayer(buyerName);
		if(player == null){
			player = L2PcInstance.restore(Helpers.getPlayerIdByName(buyerName));
		}
	
		L2PcInstance seller = World.getInstance().getPlayer(sellerId);
		if (seller == null)
			return new L2ACPResponse(500, localeService.getString("requests.buy-sell.no-player"));
		
		if(seller.isInStoreMode() && seller.getStoreType() == StoreType.SELL){

			if (player.isCursedWeaponEquipped())
				return new L2ACPResponse(500, localeService.getString("requests.buy-sell.cursed-weapon"));
			
					
			TradeList storeList = seller.getSellList();
			if (storeList == null)
				return new L2ACPResponse(500, localeService.getString("requests.buy-sell.not-buying"));
			
			if (!player.getAccessLevel().allowTransaction())
			{
				return new L2ACPResponse(500, localeService.getString("requests.unauthorized"));
			}
			
			Set<ItemRequest> _items = new HashSet<>();
			int price = 0;
			
			for(TradeItem item : storeList.getItems()){
				if(item.getObjectId() == objectId){
					price = item.getPrice();
				}
			}
			
			_items.add(new ItemRequest(objectId, count, price));
			boolean flag = false;
			if(!player.isOnline())			{
				player.setOnlineStatus(true, false);
				flag = true;
			}
		
			int result = storeList.privateStoreBuy(player, _items);
			if(result > 0)
				return new L2ACPResponse(500, localeService.getString("requests.buy-sell.insufficient-items"));
			
			if(flag)
				player.setOnlineStatus(false, false);
			
			if (storeList.getItems().isEmpty())
			{
				seller.setStoreType(StoreType.NONE);
				seller.broadcastUserInfo();
			}
			return new L2ACPResponse(200, localeService.getString("requests.success"));
		}
		
		return new L2ACPResponse(500, localeService.getString("requests.error"));
	}
	
	@Override
	public void setContent(JsonObject content){
		super.setContent(content);
		objectId = content.get("objectId").getAsInt();
		sellerId = content.get("sellerId").getAsInt();
		count = content.get("count").getAsInt();
		buyerName = content.get("buyerName").getAsString();
	}
    
}
