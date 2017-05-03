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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.elfocrash.l2acp.models.TradeItemAcp;
import com.elfocrash.l2acp.responses.GetBuyPrivateStoreItemsResponse;
import com.elfocrash.l2acp.responses.L2ACPResponse;
import com.google.gson.JsonObject;

import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance.StoreType;

/*
 * @author Elfocrash
 * @author zarkopafilis
 */
public class GetBuyPrivateStoreItemsRequest extends L2ACPRequest {
	private List<TradeItemAcp> _items = new ArrayList<>();
	
	@Override
	public L2ACPResponse getResponse() {

		World.getInstance().getPlayers().stream().filter(player -> player.isInStoreMode() && player.getStoreType() == StoreType.BUY).forEach(player -> {
			player.getSellList().updateItems();

			_items.addAll(player.getBuyList().getItems().stream().map(item -> new TradeItemAcp(item.getObjectId(), item.getItem().getItemId(), item.getEnchant(), item.getCount(), item.getPrice(), player.getName(), player.getObjectId())).collect(Collectors.toList()));
		});
		
		return new GetBuyPrivateStoreItemsResponse(200, localeService.getString("requests.ok"), _items.toArray(new TradeItemAcp[_items.size()]));
	}
	
	
	@Override
	public void setContent(JsonObject content){
		super.setContent(content);
	}
}