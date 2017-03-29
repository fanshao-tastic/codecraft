package com.cacheserverdeploy.deploy;

import java.io.Serializable;

public class ConsumerVertex implements Comparable<ConsumerVertex>, Serializable{
	/**
	 * 消费节点类
	 * @param id 编号
	 * @param connectedVertex 相连的网络节点编号
	 * @param demand 需求带宽
	 */
	private static final long serialVersionUID = -6111779056521019663L;
	public int id;	
	public int connectedVertex; 
	public int demand;	
	
	public ConsumerVertex(String parameters) { 
		/**
		 * @param parameters 根据样例文件的字符串输入 字符串按照空格分割
		 */
		
		String [] forInput = parameters.split(" ");
		this.id = Integer.parseInt(forInput[0]);	//获取消费节点id
		this.connectedVertex = Integer.parseInt(forInput[1]);//获取消费节点连接的网络节点
		this.demand = Integer.parseInt(forInput[2]);//获取需求带宽
	}

	@Override
	public int compareTo(ConsumerVertex another) {
		/**
		 * @param another 另一个消费节点实例
		 */
		//重写compareTo方法，当对该类的数组进行排序时，按照两者的需求进行排序
		return new Integer(this.demand).compareTo(new Integer(another.demand));
	}    	
}