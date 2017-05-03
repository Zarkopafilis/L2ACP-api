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
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.l2j.L2DatabaseFactory;

/**
 * @author Elfocrash
 * @author zarkopafilis
 */
public class RegisterRequest extends L2ACPRequest
{
	private String username, password;

	@Override
	public L2ACPResponse getResponse()
	{
		String query = "SELECT login, password, access_level, lastServer FROM accounts WHERE login=?";
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(query))
		{
			ps.setString(1, username);
			try (ResultSet rset = ps.executeQuery())
			{
				if (rset.next())
				{
						return new L2ACPResponse(500, localeService.getString("requests.register.account-exists").replace("{}", username));
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return new L2ACPResponse(500, localeService.getString("requests.error"));
		}
		
		
		String query2 = "INSERT INTO accounts (login, password, lastactive, access_level) values (?, ?, ?, ?)";
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement ps = con.prepareStatement(query2);
			ps.setString(1, username);
			ps.setString(2, password);
			ps.setLong(3, System.currentTimeMillis());
			ps.setInt(4, 0);
			ps.execute();
			ps.close();
			return new L2ACPResponse(200, localeService.getString("requests.ok"));
		}
		catch (Exception e)
		{
			return new L2ACPResponse(500, localeService.getString("requests.ok"));
		}
	}
	
	@Override
	public void setContent(JsonObject content){
		super.setContent(content);
		
		username = content.get("username").getAsString();
		password = content.get("password").getAsString();
	}
}
