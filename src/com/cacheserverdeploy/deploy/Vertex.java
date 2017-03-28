package com.cacheserverdeploy.deploy;

class Vertex { //�ڵ���
	/**
	 * @param INFINITY ��̬���������ڱ�ʾ����
	 * @param id �ڵ�ı��
	 * @param known ��ʾ�ýڵ��Ƿ�Ϊ�Ѵ�����Ľڵ�,���ڵϽ���˹���㷨�͹�����ȱ����㷨
	 * @param dv �������飬��������Ԫ�أ���һ��Ԫ�ر�ʾ·�����ȣ��ڶ���Ԫ�ر�ʾ��·�������бߵ�ʣ��������Сֵ
	 * @param pv ·���ϴ˽ڵ��ǰһ���ڵ�
	 */
	
	private static final int INFINITY = 9999;
	public int id;
	public boolean known;
	public int [] dv;
	public int pv;
	
	public Vertex(int id, boolean known, int dv, int pv) {
		/**
		 * Vertex��Ĺ��캯��
		 */
		this.dv = new int [2];
		this.id = id;
		this.known = known;
		this.dv[0] = dv;
		this.dv[1] = INFINITY; //��ʾ·������С����
		this.pv = pv;
	}
}
