package com.cacheserverdeploy.deploy;

import java.io.Serializable;

class Edge implements Serializable {
	/**
	 * @param id 边的编号
	 * @param startPoint 起点
	 * @param endPoint 终点
	 * @param upBand 上行带宽
	 * @param downBand 下行带宽
	 * @param price 价格
	 * @param band 总带宽
	 */
	private static final long serialVersionUID = -759628232779267634L;
	public int id;
	public int startPoint;
	public int endPoint;
	public int upBand;
	public int downBand;
	public int price;
	public int band;

	public Edge(int id, int startPoint, int endPoint, int band, int price) { 
		//Edge的构造函数
		this.id = id;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.upBand = band;
		this.downBand = band;
		this.band = band;
		this.price = price;
	}

	public Edge(int id, String parameters) {
		/**
		 * Edge的另一个构造函数
		 * @param id Edge的编号
		 * @param parameters 根据实例文件输入一行，各参数用空格分割，分别是起点、终点、带宽、单价
		 */
		this.id = id;
		String [] forInput = parameters.split(" ");
		this.startPoint = Integer.parseInt(forInput[0]);
		this.endPoint = Integer.parseInt(forInput[1]);
		this.upBand = Integer.parseInt(forInput[2]);
		this.downBand = this.upBand;
		this.band = this.downBand;
		this.price = Integer.parseInt(forInput[3]);
	}
	
}
