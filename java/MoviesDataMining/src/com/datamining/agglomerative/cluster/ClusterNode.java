package com.datamining.agglomerative.cluster;

import java.util.List;

/**
 * 
 * @author sumit
 *
 * @param <T>
 */
public class ClusterNode<T> {

	private T nodeData;
	private List<ClusterNode<T>> left;
	private List<ClusterNode<T>> right;

	public ClusterNode(T data) {
		nodeData = data;
	}

	public T getNodeData() {
		return nodeData;
	}

	public void setNodeData(T nodeData) {
		this.nodeData = nodeData;
	}

	public List<ClusterNode<T>> getLeft() {
		return left;
	}

	public void setLeft(List<ClusterNode<T>> left) {
		this.left = left;
	}

	public List<ClusterNode<T>> getRight() {
		return right;
	}

	public void setRight(List<ClusterNode<T>> right) {
		this.right = right;
	}

}
