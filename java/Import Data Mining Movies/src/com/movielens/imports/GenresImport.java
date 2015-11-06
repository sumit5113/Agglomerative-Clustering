package com.movielens.imports;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * 
 * @author sumit
 *
 */
public class GenresImport extends AbstractImport {

	public void doImport(String filename) throws IOException {

		Connection dbConnectObj = null;
		PreparedStatement prpStmtObj = null;

		try {

			readFromFile(filename);
			writeToFile(getPath(filename) + ImportMain.GENRES_ERRORS_FILE_DUMP);

			dbConnectObj = ImportMain.getDBConnection();
			prpStmtObj = dbConnectObj
					.prepareStatement(ImportMain.GENRES_INSERT_QUERRY);

			String[] movieLine = new String[3];
			String lineText = bfrReader.readLine();
			int lineCounter = 0;
			String[] genresList = null;
			int i = 0;
			while (null != lineText) {
				movieLine = lineText.split(ImportMain.DOUBLE_COLON);
				try {
					lineCounter++;

					if ((null == movieLine[0]) || (null == movieLine[1])
							|| (null == movieLine[2])) {
						logError((new StringBuffer(
								Integer.toString(lineCounter)).append(" : ")
								.append(lineText)).toString());
					} else {
						prpStmtObj.setInt(1, Integer.parseInt(movieLine[0]));
						genresList = movieLine[2].split("\\|");
						i = 0;

						while (null != genresList[i]) {
							prpStmtObj.setString(2, genresList[i]);
							i++;
							prpStmtObj.execute();
						}
					}
				} catch (Exception ex) {
					logError((new StringBuffer(Integer.toString(lineCounter))
							.append(" : ").append(lineText)).toString());
				}

				lineText = bfrReader.readLine();
			}
		} catch (Exception ex) {
			System.out.println("Error Occured in Loading movies data "
					+ ex.getMessage());
		} finally {
			closeReader();
			closeWriter();
			ImportMain.releaseConnection(dbConnectObj, prpStmtObj);
		}
	}
}
