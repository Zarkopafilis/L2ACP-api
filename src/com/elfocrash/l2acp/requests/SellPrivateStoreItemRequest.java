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
public class SellPrivateStoreItemRequest extends L2ACPRequest
{
	private int objectId, buyerId, count;
    private String sellerName;

	@Override
	public L2ACPResponse getResponse()
	{		
		L2PcInstance player = World.getInstance().getPlayer(sellerName);
		if(player == null){
			player = L2PcInstance.restore(Helpers.getPlayerIdByName(sellerName));
		}
	
		L2PcInstance buyer = World.getInstance().getPlayer(buyerId);
		if (buyer == null)
			return new L2ACPResponse(500, localeService.getString("requests.buy-sell.no-player"));
		
		if(buyer.isInStoreMode() && buyer.getStoreType() == StoreType.BUY){
			
			
			if (player.isCursedWeaponEquipped())
				return new L2ACPResponse(500, localeService.getString("requests.buy-sell.cursed-weapon"));
			
			TradeList storeList = buyer.getBuyList();
			if (storeList == null)
				return new L2ACPResponse(500, localeService.getString("requests.buy-sell.not-buying"));
			
			if (!player.getAccessLevel().allowTransaction())
			{
				return new L2ACPResponse(500, localeService.getString("requests.unauthorized"));
			}
			
			ItemRequest[] _items = new ItemRequest[1];
			int itemId = 0;
			int price = 0;
			
			for(TradeItem item : storeList.getItems()){
				if(item.getObjectId() == objectId){
					itemId = item.getItem().getItemId();
					price = item.getPrice();
				}
			}
			
			_items[0] = new ItemRequest(objectId, itemId, (int) count, price);
			boolean flag = false;
			if(!player.isOnline())			{
				player.setOnlineStatus(true, false);
				flag = true;
			}
			
			if (!storeList.privateStoreSell(player, _items))
			{
				return new L2ACPResponse(500, localeService.getString("requests.buy-sell.insufficient-items"));
			}
			if(flag)
				player.setOnlineStatus(false, false);
			
			if (storeList.getItems().isEmpty())
			{
				buyer.setStoreType(StoreType.NONE);
				buyer.broadcastUserInfo();
			}
			return new L2ACPResponse(200, localeService.getString("requests.ok"));
		
			
		}
		
		return new L2ACPResponse(500, localeService.getString("requests.error"));
	}
	
	@Override
	public void setContent(JsonObject content){
		super.setContent(content);
		objectId = content.get("objectId").getAsInt();
		buyerId = content.get("buyerId").getAsInt();
		count = content.get("count").getAsInt();
		sellerName = content.get("sellerName").getAsString();
	}
}
