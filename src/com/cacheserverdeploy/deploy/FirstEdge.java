package com.cacheserverdeploy.deploy;

import java.io.Serializable;

public class FirstEdge implements Serializable{
	/*
	 * 链路类
	 * @param id 链路id
	 * @param startPoint 起点
	 * @param endPoint 终点
	 * @param usedBand 已使用带宽。上行为正，下行为负。
	 * @param maxBand 当前边的最大负载带宽
	 * @param price 带宽单价
	 * 
	 * 
	 */
	public int id;
	public int startPoint;
	public int endPoint;
	public int usedBand;
	public int maxBand;
	public int price;

	public FirstEdge(int id, String parameters) { //构造方法
		this.id = id;
		String [] forInput = parameters.split(" ");
		this.startPoint = Integer.parseInt(forInput[0]);
		this.endPoint = Integer.parseInt(forInput[1]);
		this.maxBand = Integer.parseInt(forInput[2]);
		this.usedBand = 0 ;
		this.price = Integer.parseInt(forInput[3]);
	}
	
	
}
