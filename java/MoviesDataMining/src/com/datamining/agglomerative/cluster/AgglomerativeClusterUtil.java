package com.datamining.agglomerative.cluster;

/**
 * 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AgglomerativeClusterUtil {

	private static int choiceUser = 1;

	public static int getUserChoiceCluster() {
		return choiceUser;
	}

	/**
	 * 
	 * @param u1
	 * @param u2
	 * @return
	 */
	public static float getDistance(DataPoint u1, DataPoint u2) {
		float tempAgeDiff = (u1.getClusterAttributeOne() - u2
				.getClusterAttributeOne());
		float tempOccDiff = (u1.getClusterAttributeTwo() - u2
				.getClusterAttributeTwo());

		return (float) Math.sqrt(tempAgeDiff * tempAgeDiff + tempOccDiff
				* tempOccDiff);
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public static int getUserChoice() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		try {
			usagePrint(true);
			String inputChoice = reader.readLine();

			switch (inputChoice.trim()) {
			case "1":
			case "2":
				choiceUser = Integer.valueOf(inputChoice.trim());
				return choiceUser;
			default:
				System.out.println("Please Enter a valid choice.");
				usagePrint(false);
				System.exit(0);
			}
		} finally {
			reader.close();
		}

		return 0;
	}

	private static void usagePrint(boolean promptUser) {
		System.out.println("Please Enter one of the followig choices::");
		System.out.println("1.Cluster User Data on Age and Profession.[1]");
		System.out.println("2.Cluster Movies Data on Rating and Genre.[2]");
		if (promptUser)
			System.out.print("Enter choice[1 or 2]::");
	}

	public static String getSqlCode(int choice, String ids) {
		switch (choice) {
		default:
		case 1:
			return "select avg(r_m.rating) as avg_rating,g_m.genre,u_m.gender,"
					+ "count(u_m.gender) as no_of_user,max(r_m.rating) as max_rating,"
					+ "min(r_m.rating) as min_rating, max(u_m.age) as max_age,"
					+ "min(u_m.age) min_age"
					+ "from ratings r_m,genres g_m,users u_m"
					+ "where r_m.movieid =g_m.movieid" + "and r_m.userid in("
					+ ids + ")" + "and r_m.userid=u_m.userid"
					+ "group by u_m.gender,g_m.genre"
					+ "order by g_m.genre,u_m.gender;";
		case 2:
			return "select u_m.age,u_m.occupation,u_m.gender,count(*) as no_of_users from users u_m"
					+ "where u_m.userid in ("
					+ "select r_m.userid from ratings r_m where"
					+ "r_m.movieid in ("
					+ ids
					+ "))"
					+ "group by u_m.age,u_m.occupation,u_m.gender"
					+ "order by u_m.age,u_m.occupation;";
		}

	}
}
