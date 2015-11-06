package com.movielens.imports;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * 
 * @author sumit
 *
 */
public class UsersImport extends AbstractImport {
	
	public void doImport(String filename) throws IOException {
		PreparedStatement pstmtObj = null;
		Connection connObj = null;

		try {

			readFromFile(filename);

			writeToFile(getPath(filename) + ImportMain.USERS_ERRORS_FILE_DUMP);

			connObj = ImportMain.getDBConnection();
			pstmtObj = connObj.prepareStatement(ImportMain.USERS_INSERT_QUERRY);

			String[] usersLine = new String[5];
			String lineText = bfrReader.readLine();
			int lineCounter = 0;

			while (null != lineText) {
				usersLine = lineText.split(ImportMain.DOUBLE_COLON);
				try {
					lineCounter++;

					if ((null == usersLine[0]) || (null == usersLine[1])
							|| (null == usersLine[2]) || (null == usersLine[3])
							|| (null == usersLine[4])) {
						logError((new StringBuffer(Integer.toString(lineCounter))
								.append(" : ").append(lineText)).toString());
					} else {
						pstmtObj.setInt(1, Integer.parseInt(usersLine[0]));
						pstmtObj.setString(2, usersLine[1]);
						pstmtObj.setInt(3, Integer.parseInt(usersLine[2]));
						pstmtObj.setInt(4, Integer.parseInt(usersLine[3]));
						pstmtObj.setString(5, usersLine[4]);
						pstmtObj.execute();
					}
				} catch (Exception ex) {
					logError((new StringBuffer(Integer.toString(lineCounter))
							.append(" : ").append(lineText)).toString());
				}

				lineText = bfrReader.readLine();
			}
		} catch (Exception ex) {
			System.out.println("Error in loading users Infromation::: "
					+ ex.getMessage());
		} finally {
			closeReader();
			closeWriter();
			ImportMain.releaseConnection(connObj, pstmtObj);
		}
	}
}
