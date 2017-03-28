package com.cacheserverdeploy.deploy;

class Vertex { //节点类
	/**
	 * @param INFINITY 静态变量，用于表示无穷
	 * @param id 节点的标号
	 * @param known 表示该节点是否为已处理过的节点,用于迪杰特斯拉算法和广度优先遍历算法
	 * @param dv 整数数组，包含两个元素，第一个元素表示路径长度，第二个元素表示此路径中所有边的剩余带宽的最小值
	 * @param pv 路径上此节点的前一个节点
	 */
	
	private static final int INFINITY = 9999;
	public int id;
	public boolean known;
	public int [] dv;
	public int pv;
	
	public Vertex(int id, boolean known, int dv, int pv) {
		/**
		 * Vertex类的构造函数
		 */
		this.dv = new int [2];
		this.id = id;
		this.known = known;
		this.dv[0] = dv;
		this.dv[1] = INFINITY; //表示路径上最小带宽
		this.pv = pv;
	}
}
