/**
 * 
 */
package com.datamining.agglomerative.cluster;

import static com.datamining.dao.DBConnection.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.datamining.dao.DataDAO;

/**
 * @author sumit
 *
 */
public class ClusterComputerMain {

	private static final int LEVEL = 30;
	private int newNodeNumber = 0;

	private List<DataPoint> getUserData() throws IOException {
		DataDAO usersdao = new DataDAO();
		return usersdao.getDataPoints(getUserChoice());
	}

	private int getUserChoice() throws IOException {
		// TODO Auto-generated method stub
		return AgglomerativeClusterUtil.getUserChoice();
	}

	public void clusterUsers(int level) throws IOException {
		// first get the users data
		List<DataPoint> usersList = getUserData();

		System.out.println("Data Points Clustering Size::" + usersList.size());
		// find the pair of distance between these users
		Map<DataPoint, PairedDataNode> distanceMatrix = computeDistanceMatrix(usersList);

		// initialize the cluster node <Node>|null
		Map<DataPoint, List<DataPoint>> clusterNodesTree = new HashMap<DataPoint, List<DataPoint>>();// =
		// initClusterNodeTree(usersList);

		// perform computation
		clusterNodesTree = mergeAndComputClusters(level, distanceMatrix,
				clusterNodesTree);

		// print clusters node
		// display the result
		print(clusterNodesTree);
		// now use these information to determine the cluster similarity
		printIDs(clusterNodesTree);
	}

	private void printIDs(Map<DataPoint, List<DataPoint>> pClusterNodesTree)
			throws IOException {
		int clusterNumber = 1;
		FileWriter flWrite = new FileWriter("./queries_cluster.sql");
		StringBuffer strbuff = new StringBuffer();
		for (Map.Entry<DataPoint, List<DataPoint>> entry : pClusterNodesTree
				.entrySet()) {
			System.out.println("Cluster Number::" + clusterNumber);
			for (DataPoint dp : entry.getValue()) {
				strbuff.append(dp.getID() + ",");
			}

			System.out.println(strbuff.toString());
			flWrite.write(AgglomerativeClusterUtil.getSqlCode(
					AgglomerativeClusterUtil.getUserChoiceCluster(), strbuff
							.toString().substring(0, (strbuff.length() - 1))));
			flWrite.write("\n");
			clusterNumber++;
		}
		flWrite.close();

	}

	private void print(Map<DataPoint, List<DataPoint>> clusterNodesTree) {

		int numberOfDataPoints = 0;
		int totalDataPoints = 0;
		int clusterNumber = 1;
		for (Map.Entry<DataPoint, List<DataPoint>> tempTest : clusterNodesTree
				.entrySet()) {
			if (tempTest.getValue() == null) {
				numberOfDataPoints = 1;
			} else {
				numberOfDataPoints = tempTest.getValue().size();
			}

			totalDataPoints = numberOfDataPoints + totalDataPoints;

			System.out
					.println("--------------new CLuster --------- Node Number::: "
							+ tempTest.getKey().getID()
							+ "[ Cluster Number ::"
							+ clusterNumber
							+ "]"
							+ " ::: number of data points :::"
							+ numberOfDataPoints);

			System.out.println(tempTest.getValue() == null ? tempTest.getKey()
					: tempTest.getValue());
			System.out.flush();
			clusterNumber++;

		}
		System.out.println("Total Data points :::" + totalDataPoints);
		System.out.flush();

	}

	private Map<DataPoint, List<DataPoint>> mergeAndComputClusters(int pLevel,
			Map<DataPoint, PairedDataNode> pDistanceMatrix,
			Map<DataPoint, List<DataPoint>> pClusterNodesTree) {

		// variable required for intermediate nodes points

		Runtime runTime = Runtime.getRuntime();
		// iterate till the number of cluster nodes reached to the given number
		// of nodes
		while (pDistanceMatrix.size() > pLevel) {

			// find the minimum distance node
			DataPoint[] twoMinDistNode = getMin(pDistanceMatrix);

			// check if both user-id are not null, no element is found
			if (twoMinDistNode[0] == null && twoMinDistNode[1] == null) {
				System.err.println("No Minimum Found Error!!!");
				continue;
			}

			if (twoMinDistNode[0].equals(twoMinDistNode[1])) {
				System.err.println("two users are same::" + twoMinDistNode[0]);
				continue;
			}

			// safe conditions, achieved
			DataPoint newPoint = getNewPoint(this.newNodeNumber,
					twoMinDistNode[0], twoMinDistNode[1]);

			// removing part start--update of distance matrix
			// now remove the data points form the , usersMap
			//

			// now remove two points distance from the distance-matrix
			pDistanceMatrix.remove(twoMinDistNode[0]);
			pDistanceMatrix.remove(twoMinDistNode[1]);

			udateDistanceMatrix(pDistanceMatrix, newPoint);

			// Merging two nodes update the root Information
			List<DataPoint> leftNode = pClusterNodesTree.get(twoMinDistNode[0]);
			List<DataPoint> rightNode = pClusterNodesTree
					.get(twoMinDistNode[1]);

			// merge left and right child

			if (leftNode == null) {
				leftNode = new ArrayList<DataPoint>();
				leftNode.add(twoMinDistNode[0]);
			}

			if (rightNode == null) {
				rightNode = new ArrayList<DataPoint>();
				rightNode.add(twoMinDistNode[1]);
			}

			leftNode.addAll(rightNode);

			pClusterNodesTree.put(newPoint, leftNode);

			// remove old cluster nodes details
			pClusterNodesTree.remove(twoMinDistNode[0]);
			pClusterNodesTree.remove(twoMinDistNode[1]);
			// remove the children from the lists

			this.newNodeNumber++;

			// reset the data for garbage collection

			twoMinDistNode = null;

			if (runTime.freeMemory() < 1000) {
				System.gc();
			}

		}

		return pClusterNodesTree;
	}

	private DataPoint getNewPoint(int newNodeNumber2, DataPoint user1,
			DataPoint user2) {
		float averageAge = (user1.getClusterAttributeOne() + user2
				.getClusterAttributeOne()) / 2;
		float occupation = (user1.getClusterAttributeTwo() + user2
				.getClusterAttributeTwo()) / 2;

		DataPoint usrTemp = new DataPoint(newNodeNumber, occupation, averageAge);

		return usrTemp;
	}

	private void udateDistanceMatrix(
			Map<DataPoint, PairedDataNode> pDistanceMatrix, DataPoint newPoint) {
		float distance = 0;
		PairedDataNode tempDataNode = null;

		for (DataPoint userTemp : pDistanceMatrix.keySet()) {

			tempDataNode = pDistanceMatrix.get(userTemp);
			distance = AgglomerativeClusterUtil.getDistance(userTemp, newPoint);

			if (tempDataNode == null) {
				pDistanceMatrix.put(userTemp, new PairedDataNode(newPoint,
						distance));
			} else if (!pDistanceMatrix.containsKey(tempDataNode.getCurrent())
					|| (pDistanceMatrix.get(userTemp).getDistance() > distance)) {
				// update the distance node, otherwise keep it's information
				tempDataNode.setCurrent(newPoint);
				tempDataNode.setDistance(distance);
			}

		}
		// put the new point in the list
		pDistanceMatrix.put(newPoint, null);

	}

	private DataPoint[] getMin(Map<DataPoint, PairedDataNode> pDistanceMatrix) {

		DataPoint[] userMinFound = new DataPoint[2];
		float minimumDistance = Float.MAX_VALUE;
		PairedDataNode pDistPairNode = null;

		for (DataPoint userTemp : pDistanceMatrix.keySet()) {
			pDistPairNode = pDistanceMatrix.get(userTemp);
			if (pDistPairNode != null
					&& minimumDistance > pDistPairNode.getDistance()) {
				userMinFound[0] = userTemp;
				userMinFound[1] = pDistPairNode.getCurrent();
				minimumDistance = pDistPairNode.getDistance();
			}
		}// end of for loop

		return userMinFound;
	}

	/**
	 * 
	 * @param usersList
	 * @return
	 */
	/*
	 * private Map<User, List<User>> initClusterNodeTree(List<User> usersList) {
	 * 
	 * Map<User, List<User>> clusterNodesTree = new HashMap<User, List<User>>();
	 * for (User tempUser : usersList) { clusterNodesTree.put(tempUser, null); }
	 * //
	 * System.out.println(clusterNodesTree.get(99)+":::"+clusterNodesTree.get(
	 * 100)); return clusterNodesTree; }
	 */

	private Map<DataPoint, PairedDataNode> computeDistanceMatrix(
			List<DataPoint> usersList) {
		Map<DataPoint, PairedDataNode> distanceMatrix = new HashMap<DataPoint, PairedDataNode>();
		// add all distances-pairs to priority queue

		int sizeUserList = usersList.size();
		DataPoint minUser = null;

		float distanceMin = Float.MAX_VALUE;

		for (int i = 0; i < sizeUserList; i++) {

			if (this.newNodeNumber < usersList.get(i).getID()) {
				this.newNodeNumber = usersList.get(i).getID();
			}

			for (int j = i + 1; j < sizeUserList; j++) {
				float dist = AgglomerativeClusterUtil.getDistance(
						usersList.get(i), usersList.get(j));
				if (distanceMin > dist) {
					distanceMin = dist;
					minUser = usersList.get(j);
				}
			}
			// if(distanceMatrix.containsKey(usersList.get(i))){
			// System.out.println(usersList.get(i)+""+usersList.get(i).hashCode()+""+distanceMatrix.get(usersList.get(i)).equals(usersList.get(i))+"--"+distanceMatrix.get(usersList.get(i)).getCurrent());
			// }
			// here the distance will be minimum
			distanceMatrix.put(usersList.get(i), minUser == null ? null
					: new PairedDataNode(minUser, distanceMin));

			minUser = null;
			distanceMin = Float.MAX_VALUE;
		}

		System.gc();

		this.newNodeNumber++;

		return distanceMatrix;
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws IOException {
		connectTODB(args);
		ClusterComputerMain ac = new ClusterComputerMain();
		ac.clusterUsers(LEVEL);

	}

	public static void connectTODB(String[] args) throws FileNotFoundException,
			IOException {
		if (args.length < 1) {
			System.out
					.println("Usage: \njava ExportMain <Path to Config File>");
			return;
		}

		String propFile = args[0];
		System.out.println("Loading Database Configuration Informaton from: "
				+ args[0]);

		if (null != propFile) {
			properties = new Properties();
			properties.load(new FileInputStream(propFile));
		}
	}

}
