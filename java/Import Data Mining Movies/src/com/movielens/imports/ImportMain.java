package com.movielens.imports;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
/**
 * 
 * @author sumit
 *
 */
public class ImportMain {

	public static final String MOVIES_ERRORS_FILE_DUMP = "movies_errors_dump.txt";
	public static final String USERS_ERRORS_FILE_DUMP = "users_errors_dump.txt";
	public static final String RATINGS_ERRORS_FILE_DUMP = "ratings_errors_dump.txt";
	public static final String GENRES_ERRORS_FILE_DUMP = "ratings_errors_dump.txt";
	
	public static final String MOVIES_INSERT_QUERRY = "INSERT INTO MOVIES (movieid, title, genres) VALUES (?,?,?)";
	public static final String USERS_INSERT_QUERRY = "INSERT INTO USERS (userid, gender, age, occupation, zipcode) VALUES (?, ?, ?, ?, ?)";
	public static final String RATINGS_INSERT_QUERRY = "INSERT INTO RATINGS (userid, movieid, rating, timestamp) VALUES (?,?,?,?)";
	public static final String GENRES_INSERT_QUERRY = "INSERT INTO GENRES (movieid, genre) VALUES (?,?)";

	public static final String DOUBLE_COLON = "::";

	static Properties propFile = null;
	
	public static void readAndParseProperties(String pFilename)
			throws FileNotFoundException, IOException {
		propFile = new Properties();
		propFile.load(new FileInputStream(pFilename));
	}
	
	public static void importAll() throws IOException {
		String moviesDatafile = propFile
				.getProperty("data.import.moviesfile.location");
		if (null != moviesDatafile) {
			MoviesImport me = new MoviesImport();
			me.doImport(moviesDatafile);
		}

		System.out.println("Movies data Loaded");

		if (null != moviesDatafile) {
			GenresImport ge = new GenresImport();
			ge.doImport(moviesDatafile);
		}

		System.out.println("Genres data Loaded ");

		String usersfile = propFile
				.getProperty("data.import.usersfile.location");
		if (null != usersfile) {
			UsersImport ue = new UsersImport();
			ue.doImport(usersfile);
		}

		System.out.println("Loaded users data into Database ");

		/*
		 * String ratingsfile =
		 * prop.getProperty("data.import.ratingsfile.location"); if (null !=
		 * ratingsfile) { RatingsExport re = new RatingsExport();
		 * re.doExport(ratingsfile); }
		 * 
		 * System.out.println("Loaded ratings data into Database ");
		 */
	}

	public static Connection getDBConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(
					propFile.getProperty("db.servername"),
					propFile.getProperty("db.username"),
					propFile.getProperty("db.password"));

		} catch (SQLException se) {
			System.out.println("Cannot Connection to database  "
					+ se.getMessage());
		}
		return conn;
	}

	public static void releaseConnection( Connection con,Statement stmt) {
		try {
			if (null != stmt) {
				stmt.close();
			}
			if (null != con) {
				con.close();
			}
		} catch (SQLException se) {
			System.out.println("Cannot Release Connection " + se.getMessage());
		}
	}

	public static void main(String[] args) throws IOException {

		if (args.length < 1) {
			System.out
					.println("Usage: \njava ImportMain <Please Specify Path to Config File>");
			return;
		}

		String dataFile = args[0];

		System.out.println("Reading or Parsing Configuration file: " + args[0]);

		if (null != dataFile) {
			readAndParseProperties(dataFile);

			System.out.println("Initializing DataBase");

			importAll();

			System.out.println("Load Complete.");
		}
	}

}
