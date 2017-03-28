package com.cacheserverdeploy.deploy;

import java.io.Serializable;

class Edge implements Serializable {
	/**
	 * @param id �ߵı��
	 * @param startPoint ���
	 * @param endPoint �յ�
	 * @param upBand ���д���
	 * @param downBand ���д���
	 * @param price �۸�
	 * @param band �ܴ���
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
		//Edge�Ĺ��캯��
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
		 * Edge����һ�����캯��
		 * @param id Edge�ı��
		 * @param parameters ����ʵ���ļ�����һ�У��������ÿո�ָ�ֱ�����㡢�յ㡢��������
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
