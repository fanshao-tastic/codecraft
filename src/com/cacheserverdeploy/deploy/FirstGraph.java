package com.cacheserverdeploy.deploy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

public class FirstGraph implements Serializable {
	/*
	 * 图类
	 * @param struct 保存图的结构，存储形式为邻接表
	 * @param netVertexNum 网络节点数目
	 * @param edgeList 图中边的链表
	 * 
	 */
	public ArrayList<int []> [] struct; //ArrayList数组，下标为节点id，ArrayList中的元素是数对,第一个数为节点id,第二个数为链路id
	public int netVertexNum;
	public FirstEdge[] edgeList;
	public int[] weights;
	public Set<Integer> connected;
	public int[][] array;
	
	FirstGraph(int netVertexNum, FirstEdge [] edgeList, Set<Integer> connected ){ //构造方法
		this.edgeList = edgeList;
		this.struct = new ArrayList [netVertexNum];
		this.connected = connected;
		this.netVertexNum = netVertexNum;
		this.array = new int[netVertexNum][netVertexNum];
		for(int i = 0; i < netVertexNum; i++) {
			struct[i] = new ArrayList<int []>();
		}
		
		for(int i = 0; i < edgeList.length; i++) {
			FirstEdge thisEdge = edgeList[i];
			int [] group1 = {thisEdge.startPoint, thisEdge.id};
			int [] group2 = {thisEdge.endPoint, thisEdge.id};
			struct[thisEdge.startPoint].add(group2);
			struct[thisEdge.endPoint].add(group1);
			array[thisEdge.startPoint][thisEdge.endPoint] =1;
			array[thisEdge.endPoint][thisEdge.startPoint] =1;
		}
		while(true) {
			boolean flag = true;
			for(int  i = 0; i < this.struct.length; i++) {
				ArrayList<int []> list = this.struct[i];
				if(list.size() == 1 && !connected.contains(i)) {
					flag = false;
					int x = list.get(0)[0];
					list.remove(0);
					for(int j = 0 ; j < this.struct[x].size(); j++) {
						if(this.struct[x].get(j)[0] == x) {
							this.struct[x].remove(j);
							break;
						}
					}
					
				}
			}
			if(flag) {
				break;
			}
			
		}
		this.weights = new int [netVertexNum];
		for(int i = 0; i < netVertexNum; i++) {
			ArrayList<int []> list = this.struct[i];
			if(list.size() == 0) {
				this.weights[i]= 0;
				continue;
			}
			int count = list.size();
			int sumBand = 0;
			int sumFee = 0;
			for(int j = 0;j < count; j++) {
				sumBand += edgeList[list.get(j)[1]].maxBand;
				sumFee += edgeList[list.get(j)[1]].maxBand * edgeList[list.get(j)[1]].price;
			}
			this.weights[i] = (int) (count * sumBand * 100 / sumFee);
		}
	}
	
	public  FirstGraph deepClone(FirstGraph graph){ 
		 if(graph == null){ 
		  return null; 
		 } 
		 try { 
		  ByteArrayOutputStream byteOut = new ByteArrayOutputStream(); 
		  FirstGraph cloneGraph = null; 
		  ObjectOutputStream out = new ObjectOutputStream(byteOut); 
		  out.writeObject(graph); 
		  ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray()); 
		  ObjectInputStream in = new ObjectInputStream(byteIn); 
		  cloneGraph= (FirstGraph)in.readObject(); 
		  return cloneGraph; 
		 } catch (Exception e) { 
		  throw new RuntimeException(e); 
		 } 
		}
	
}

