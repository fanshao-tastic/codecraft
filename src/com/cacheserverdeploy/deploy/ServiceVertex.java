package com.cacheserverdeploy.deploy;

import java.util.List;



public class ServiceVertex {
	/*
	 * 瀹氫箟涓�涓湇鍔″櫒鑺傜偣绫�
	 * @param location  鏈嶅姟鍣ㄨ妭鐐圭殑浣嶇疆銆傚嵆鍦ㄥ摢涓綉缁滆妭鐐逛笂
	 * @param serviceVertexId 鏈嶅姟鍣ㄨ妭鐐圭殑Id
	 *  锛堟敞鎰忓湪鐢熸垚鏈嶅姟鍣ㄨ妭鐐圭殑鏃跺�欙紝缃戠粶鑺傜偣鐨勫甫瀹芥洿鏂板簲璇ユ槸宸茬粡瀹屾垚鐨勶級
	 * @param connectedVertex 涓庢湇鍔″櫒鑺傜偣鎯宠繛鎺ョ殑缃戠粶鑺傜偣涓庤竟
	 * @param demand  杩欎釜鏈嶅姟鍣ㄩ渶瑕佽緭鍑虹殑甯﹀闇�姹�
	 * 杩欎釜绫绘湁寰呰繘涓�姝ュ畬鍠�
	 * 褰撴妸鏈嶅姟鍣ㄨ妭鐐逛綔涓烘秷璐硅妭鐐硅繘琛屾柊涓�杞殑閫掑綊鏃讹紝搴旇鍒犻櫎鏈嶅姟鍣ㄨ妭鐐逛笌鍏舵湇鍔＄殑娑堣垂鑺傜偣涔嬮棿鐨勮矾寰勩��
	 * @param
	 * 
	 */
	public int location;
	public List<int[]> connectedVertex;
	public int demand;
	
	
	 ServiceVertex(FirstGraph graph , int vertexId , int demand){		
		this.location = vertexId;
		connectedVertex = graph.struct[vertexId];
		this.demand = demand;
	}
	
}
