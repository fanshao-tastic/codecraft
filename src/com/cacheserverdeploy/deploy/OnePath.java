package com.cacheserverdeploy.deploy;

public class OnePath {
	/**
	 * ��ʾ·��Ƭ��
	 * @param startPoint ���id
	 * @param endPoint �յ�id
	 * @param band ռ�ô���
	 */
	public int startPoint;
	public int endPoint;
	public int band;
	
	public OnePath(int startPoint, int endPoint, int band) { //OnePath��Ĺ��캯��
		/**
		 *���캯��
		 */
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.band = band;
	}
}