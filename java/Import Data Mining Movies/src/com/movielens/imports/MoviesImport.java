package com.movielens.imports;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * 
 * @author sumit
 *
 */
public class MoviesImport extends AbstractImport {
	public void doImport(String filename) throws IOException {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			// read input file
			readFromFile(filename);
			// create file for logging errors
			writeToFile(getPath(filename) + ImportMain.MOVIES_ERRORS_FILE_DUMP);

			conn = ImportMain.getDBConnection();
			pstmt = conn.prepareStatement(ImportMain.MOVIES_INSERT_QUERRY);

			String[] genreLine = new String[3];
			String line = bfrReader.readLine();
			int linecnt = 0;

			while (null != line) {
				genreLine = line.split(ImportMain.DOUBLE_COLON);
				try {
					linecnt++;

					if ((null == genreLine[0]) || (null == genreLine[1])
							|| (null == genreLine[2])) {
						logError((new StringBuffer(Integer.toString(linecnt))
								.append(" : ").append(line)).toString());
					} else {
						pstmt.setInt(1, Integer.parseInt(genreLine[0]));
						pstmt.setString(2, genreLine[1]);
						pstmt.setString(3, genreLine[2]);
						pstmt.execute();
					}
				} catch (Exception ex) {
					logError((new StringBuffer(Integer.toString(linecnt))
							.append(" : ").append(line)).toString());
				}

				line = bfrReader.readLine();
			}
		} catch (Exception ex) {
			System.out.println("Problem while loading movies data "
					+ ex.getMessage());
		} finally {
			closeReader();
			closeWriter();
			ImportMain.releaseConnection(conn, pstmt);
		}
	}
}
