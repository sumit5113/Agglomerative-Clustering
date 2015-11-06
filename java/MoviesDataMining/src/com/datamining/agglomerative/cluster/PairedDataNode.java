package com.datamining.agglomerative.cluster;
/**
 * 
 * @author sumit
 *
 */

public class PairedDataNode {
	private DataPoint current;

	public DataPoint getCurrent() {
		return current;
	}

	public void setCurrent(DataPoint current) {
		this.current = current;
	}

	private float distance;

	PairedDataNode(DataPoint currentUser, float distance) {
		this.current = currentUser;
		this.distance = distance;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}
}
