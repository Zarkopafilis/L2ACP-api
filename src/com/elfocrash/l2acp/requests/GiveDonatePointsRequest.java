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

/*
 * @author Elfocrash
 * @author zarkopafilis
 */
public class GiveDonatePointsRequest extends L2ACPRequest {

    private String playerName;
    private int points;
    
	@Override
	public L2ACPResponse getResponse() {
		
		String accountName = Helpers.getAccountName(playerName);
		if(accountName != null && accountName.length() > 0){
			Helpers.addDonatePoints(accountName, points);
			
			return new L2ACPResponse(200, localeService.getString("requests.ok"));
		}
		return new L2ACPResponse(500, localeService.getString("requests.error"));
	}
	
	
	@Override
	public void setContent(JsonObject content){
		super.setContent(content);
		
		playerName = content.get("playerName").getAsString();
		points = content.get("points").getAsInt();
	}
}