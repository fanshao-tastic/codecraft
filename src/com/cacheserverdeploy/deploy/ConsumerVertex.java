package com.cacheserverdeploy.deploy;

import java.io.Serializable;

public class ConsumerVertex implements Comparable<ConsumerVertex>, Serializable{
	/**
	 * ���ѽڵ���
	 * @param id ���
	 * @param connectedVertex ����������ڵ���
	 * @param demand �������
	 */
	private static final long serialVersionUID = -6111779056521019663L;
	public int id;	
	public int connectedVertex; 
	public int demand;	
	
	public ConsumerVertex(String parameters) { 
		/**
		 * @param parameters ���������ļ����ַ������� �ַ������տո�ָ�
		 */
		
		String [] forInput = parameters.split(" ");
		this.id = Integer.parseInt(forInput[0]);	//��ȡ���ѽڵ�id
		this.connectedVertex = Integer.parseInt(forInput[1]);//��ȡ���ѽڵ����ӵ�����ڵ�
		this.demand = Integer.parseInt(forInput[2]);//��ȡ�������
	}

	@Override
	public int compareTo(ConsumerVertex another) {
		/**
		 * @param another ��һ�����ѽڵ�ʵ��
		 */
		//��дcompareTo���������Ը���������������ʱ���������ߵ������������
		return new Integer(this.demand).compareTo(new Integer(another.demand));
	}    	
}