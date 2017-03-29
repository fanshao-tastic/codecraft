package com.cacheserverdeploy.deploy;

public class OnePath {
	/**
	 * 表示路径片段
	 * @param startPoint 起点id
	 * @param endPoint 终点id
	 * @param band 占用带宽
	 */
	public int startPoint;
	public int endPoint;
	public int band;
	
	public OnePath(int startPoint, int endPoint, int band) { //OnePath类的构造函数
		/**
		 *构造函数
		 */
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.band = band;
	}
}