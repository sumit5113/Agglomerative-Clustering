package com.datamining.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.datamining.agglomerative.cluster.DataPoint;

/**
 * 
 * @author sumit
 *
 */
public class DataDAO {

	public List<DataPoint> getDataPoints(int type) throws IOException {
		Connection connObj = null;
		Statement stmtObj = null;
		List<DataPoint> usersList = new ArrayList<DataPoint>();

		try {
			connObj = DBConnection.getConnection();
			stmtObj = connObj.createStatement();
			String querry = GET_USERS;//default query to fire
			if (type == 2) {
				querry = GET_MOVIES_GENRE;
			}
			ResultSet rs = stmtObj.executeQuery(querry);

			while (rs.next()) {
				DataPoint user = new DataPoint();

				user.setID(rs.getInt(1));
				user.setClusterAttributeOne(rs.getInt(2));// age,rating
				user.setClusterAttributeTwo(rs.getInt(3));// occupation,genreid

				usersList.add(user);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Error In retrieving data::" + ex.getMessage());
		} finally {
			DBConnection.cleanupConnection(stmtObj, connObj);
		}
		return usersList;
	}

	private static final String GET_USERS = "SELECT userid, age, occupation FROM users";
	private static final String GET_MOVIES_GENRE = "select distinct m.MovieID,r.Rating,g.genreid  from MOVIES m, RATINGS r,genrestypes g  where m.MovieID = r.MovieID and"
			+ " m.genres like concat('%',g.Genre,'%') group by m.MovieID,g.Genre, r.Rating LIMIT 100000";

}
