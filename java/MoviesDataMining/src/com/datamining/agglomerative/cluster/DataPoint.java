package com.datamining.agglomerative.cluster;

/**
 * 
 * @author sumit
 *
 */
public class DataPoint {

	private int ID;
	// age,rating
	private float clusterAttributeOne;
	// genre,occupation
	private float clusterAttributeTwo;

	public DataPoint() {

	}

	public DataPoint(int newUserId, float occupation, float age) {
		this.setID(newUserId);
		this.setClusterAttributeTwo(occupation);
		this.setClusterAttributeOne(age);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof DataPoint) {
			DataPoint usrOther = ((DataPoint) obj);
			return this.getID() == usrOther.getID()
					&& this.getClusterAttributeOne() == usrOther
							.getClusterAttributeOne()
					&& this.getClusterAttributeTwo() == usrOther
							.getClusterAttributeTwo();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.getID() + "," + this.getClusterAttributeOne() + "," + this
				.getClusterAttributeTwo()).hashCode();
	}

	@Override
	public String toString() {
		String str = null;
		if (AgglomerativeClusterUtil.getUserChoiceCluster() == 1) {
			str = "User-Id::" + this.getID() + " Occupation:: "
					+ this.getClusterAttributeTwo() + " Age:: "
					+ this.getClusterAttributeOne() + "\n";
		} else {
			str = "Movies-Id::" + this.getID() + " Genre:: "
					+ this.getClusterAttributeTwo() + " Rating:: "
					+ this.getClusterAttributeOne() + "\n";
		}
		return str;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public float getClusterAttributeOne() {
		return clusterAttributeOne;
	}

	public void setClusterAttributeOne(float clusterAttributeOne) {
		this.clusterAttributeOne = clusterAttributeOne;
	}

	public float getClusterAttributeTwo() {
		return clusterAttributeTwo;
	}

	public void setClusterAttributeTwo(float clusterAttributeTwo) {
		this.clusterAttributeTwo = clusterAttributeTwo;
	}
}
