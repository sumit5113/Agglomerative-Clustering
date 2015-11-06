package com.movielens.imports;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * 
 * @author sumit
 *
 */
public class RatingsImport extends AbstractImport {
	public void doImport(String filename) throws IOException {
		PreparedStatement pstmt = null;
		Connection conn = null;

		try {
			// read input file
			readFromFile(filename);
			// create file for logging errors
			writeToFile(getPath(filename) + ImportMain.RATINGS_ERRORS_FILE_DUMP);

			conn = ImportMain.getDBConnection();
			pstmt = conn.prepareStatement(ImportMain.RATINGS_INSERT_QUERRY);

			String[] ratingsLine = new String[4];
			String lineText = bfrReader.readLine();
			int lineCounter = 0;

			while (null != lineText) {
				ratingsLine = lineText.split(ImportMain.DOUBLE_COLON);
				try {
					lineCounter++;

					if ((null == ratingsLine[0]) || (null == ratingsLine[1])
							|| (null == ratingsLine[2])
							|| (null == ratingsLine[3])) {
						logError((new StringBuffer(Integer.toString(lineCounter))
								.append(" : ").append(lineText)).toString());
					} else {
						pstmt.setInt(1, Integer.parseInt(ratingsLine[0]));
						pstmt.setInt(2, Integer.parseInt(ratingsLine[1]));
						pstmt.setInt(3, Integer.parseInt(ratingsLine[2]));
						pstmt.setInt(4, Integer.parseInt(ratingsLine[3]));
						pstmt.execute();
					}
				} catch (Exception ex) {
					logError((new StringBuffer(Integer.toString(lineCounter))
							.append(" : ").append(lineText)).toString());
				}

				lineText = bfrReader.readLine();
			}
		} catch (Exception ex) {
			System.out.println("Error in Loading Ratings Information "
					+ ex.getMessage());
		} finally {
			closeReader();
			closeWriter();
			ImportMain.releaseConnection(conn, pstmt);
		}
	}
}
