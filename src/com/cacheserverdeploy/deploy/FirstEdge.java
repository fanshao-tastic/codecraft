package com.cacheserverdeploy.deploy;

import java.io.Serializable;

public class FirstEdge implements Serializable{
	/*
	 * ��·��
	 * @param id ��·id
	 * @param startPoint ���
	 * @param endPoint �յ�
	 * @param usedBand ��ʹ�ô�������Ϊ��������Ϊ����
	 * @param maxBand ��ǰ�ߵ�����ش���
	 * @param price ������
	 * 
	 * 
	 */
	public int id;
	public int startPoint;
	public int endPoint;
	public int usedBand;
	public int maxBand;
	public int price;

	public FirstEdge(int id, String parameters) { //���췽��
		this.id = id;
		String [] forInput = parameters.split(" ");
		this.startPoint = Integer.parseInt(forInput[0]);
		this.endPoint = Integer.parseInt(forInput[1]);
		this.maxBand = Integer.parseInt(forInput[2]);
		this.usedBand = 0 ;
		this.price = Integer.parseInt(forInput[3]);
	}
	
	
}
