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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.elfocrash.l2acp.responses.L2ACPResponse;
import com.google.gson.JsonObject;

import net.sf.l2j.L2DatabaseFactory;

/**
 * @author Elfocrash
 * @author zarkopafilis
 */
public class DonateRequest extends L2ACPRequest {

	private String accountName, transactionId, verificationSign;
	private int amount;

	@Override
	public L2ACPResponse getResponse() {

		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT count(*) as Count FROM l2acp_donations WHERE accountName=? and transactionid=? and verificationSign=?"))
		{
			ps.setString(1, accountName);
			ps.setString(2, transactionId);
			ps.setString(3, verificationSign);
			try (ResultSet rset = ps.executeQuery())
			{
				if (rset.next())
				{
					int count = rset.getInt("Count");
					if(count > 0)
						return new L2ACPResponse(500, localeService.getString("requests.error"));
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return new L2ACPResponse(500, localeService.getString("requests.error"));
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("insert into l2acp_donations (accountName,amount,transactionid,verificationSign,timestamp) values (?,?,?,?,?)"))
		{
			ps.setString(1, accountName);
			ps.setInt(2, amount);
			ps.setString(3, transactionId);
			ps.setString(4, verificationSign);
			ps.setLong(5, System.currentTimeMillis());
			ps.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return new L2ACPResponse(500, localeService.getString("requests.error"));
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("update accounts set donatepoints=(donatepoints + ?) WHERE login=?"))
		{
			ps.setInt(1, amount);
			ps.setString(2, accountName);
			ps.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return new L2ACPResponse(500, localeService.getString("requests.error"));
		}

		return new L2ACPResponse(200, localeService.getString("requests.ok"));
	}
	
	
	@Override
	public void setContent(JsonObject content){
		super.setContent(content);
		
		accountName = content.get("accountName").getAsString();
		amount = content.get("amount").getAsInt();
		transactionId = content.get("transactionId").getAsString();
		verificationSign = content.get("verificationSign").getAsString();
	}
}