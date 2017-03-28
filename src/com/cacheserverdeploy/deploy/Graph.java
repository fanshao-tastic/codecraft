package com.cacheserverdeploy.deploy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph implements Serializable{
	/**
	 * 图类
	 * @param struct 保存图的结构，存储形式为邻接表
	 * @param netVertexNum 网络节点数目
	 * @param edgeList 边集
	 * @param serverList 服务器表
	 * @param consumerList 消费节点
	 * @param maxCount 最大服务器个数
	 * @param minCount 最少服务器的个数
	 */
	private static final long serialVersionUID = 872390113109L;
	public Map<Integer, Integer> [] struct; //ArrayList数组，下标为节点id，ArrayList中的元素是数对,第一个数为节点id,第二个数为链路id
	public int netVertexNum;
	public List<Edge> edgeList;
	public List<Integer> serverList;
	public List<ConsumerVertex> consumerList;
	//public Map<Integer,Integer> nodeValueList;
	public int maxCount;
	public int minCount;
	
	public Graph(int netVertexNum, Edge [] edgeList, List<ConsumerVertex> consumerList, List<Integer> serverList) { //构造方法
		this.struct = new HashMap [netVertexNum + 2];
		this.edgeList = new ArrayList<Edge>();
		this.serverList = serverList;
		this.consumerList = consumerList;
		this.maxCount = consumerList.size();
		//this.nodeValueList = setNodeValueList(netVertexNum,edgeList);
		
		this.minCount = 0;
		for(int i = 0; i < edgeList.length;i++) {
			this.edgeList.add(edgeList[i]);//加入原始的边
		}
 		this.netVertexNum = netVertexNum + 2;//网络节点数目+2，新建两个虚拟的网络节点，一个表示总服务器，一个表示总消费节点
		//初始化过程
		for(int i = 0; i < netVertexNum + 2; i++) {
			struct[i] = new LinkedHashMap<Integer, Integer>();
		}
		
		for(int i = 0; i < edgeList.length; i++) {
			Edge thisEdge = edgeList[i];
			struct[thisEdge.startPoint].put(thisEdge.endPoint, i);
			struct[thisEdge.endPoint].put(thisEdge.startPoint, i);
		}
		
		Set<Integer> set = new HashSet<Integer>();//新建一个Set，利用contains函数查找与消费节点相连的网络节点
		for(int i = 0; i < consumerList.size(); i++) {
			set.add(consumerList.get(i).connectedVertex);
		}
		
		int [] serverBand = new int [serverList.size()];//服务器节点的输出能力,若该点和消费节点连接，输出能力会加入消费节点的需求

		for(int i = 0; i < serverList.size(); i++) {
			int temp = getOuputAbility(i);
			if(set.contains(serverList.get(i))) {
				for(int j = 0; j < consumerList.size(); j++) {
					if(consumerList.get(j).connectedVertex == serverList.get(i)) {
						temp += consumerList.get(j).demand;
						break;
					}
				}
			}
			serverBand[i] = temp;
		}
		
		int id = this.edgeList.size() - 1;
		for(int i = 0; i < serverList.size(); i++) {//增加服务器虚拟节点,定为所有节点的倒数第二个，只有出没有入
			int start = struct.length - 2;
			int end = serverList.get(i);
			id++;//边的id自增1
			int band = serverBand[i];//带宽表示为该点的输出能力
			int price = 0;//虚拟节点，单价为0
			Edge edge = new Edge(id, start, end, band, price);
			this.edgeList.add(edge);
			struct[struct.length - 2].put(end, id);
		}
		
		for(int i = 0; i < consumerList.size();i++) {//增加消费总结点,定为所有节点的最后一个,只有入没有出
			id++;
			int start = consumerList.get(i).connectedVertex;
			int end = struct.length - 1;
			int band = consumerList.get(i).demand;//带宽表示为需求
			int price = 0;//虚拟节点，单价为0
			Edge edge = new Edge(id, start, end, band, price);
			this.edgeList.add(edge);
			struct[start].put(end, id);
		}
		
		while(true) {//删除单支
			boolean flag = true;
			for(int i = 0; i < struct.length; i++) {
				if(struct[i].size() == 1) {//存在单支，将该点的邻接表置空，删除所有与该点的连接
					flag = false;
					//System.out.println("ONE");
					for(Integer x : struct[i].keySet()) {
						struct[x].remove(i);
					}
					struct[i].clear();
				}
			}
			if(flag) {
				break;
			}
		}
		
		Map<Integer, ConsumerVertex> m = new HashMap<Integer, ConsumerVertex>();
		int demand = 0;
    	for(int i = 0;i < consumerList.size(); i++) {
    		m.put(consumerList.get(i).connectedVertex, consumerList.get(i));
    		demand += consumerList.get(i).demand;//计算总需求
    	}
    	
		List<Integer> eachVertex = new ArrayList<Integer>();
    	for(int i = 0; i < struct.length - 2; i++) {
    		int temp = getOuputAbility(i);
    		eachVertex.add(temp);
    	}
    	
    	Collections.sort(eachVertex);
    	Collections.reverse(eachVertex);
    	for(int t = 0; t < demand; minCount++) {
    		t += eachVertex.get(minCount);
    	}
    	minCount++; //确定服务器个数下限
		
	}

	public void setServerList(List<Integer> serverList){
		Set<Integer> server=new HashSet<Integer>();
		for(Integer i:serverList){
			server.add(i);
		}
		List<Integer> newList=new ArrayList<Integer>();
		int count=0;
		for(Integer i:server){
			newList.add(count, i);
			count++;
		}
		serverList=newList;
		this.serverList=newList;
		Set<Integer> set = new HashSet<Integer>();//新建一个Set，利用contains函数查找与消费节点相连的网络节点
		for(int i = 0; i < consumerList.size(); i++) {
			set.add(consumerList.get(i).connectedVertex);
		}
		
		int [] serverBand = new int [serverList.size()];//服务器节点的输出能力,若该点和消费节点连接，输出能力会加入消费节点的需求

		for(int i = 0; i < serverList.size(); i++) {
			int temp = getOuputAbility(i);
			if(set.contains(serverList.get(i))) {
				for(int j = 0; j < consumerList.size(); j++) {
					if(consumerList.get(j).connectedVertex == serverList.get(i)) {
						temp += consumerList.get(j).demand;
						break;
					}
				}
			}
			serverBand[i] = temp;
		}
		
		int id = this.edgeList.size() - 1;
		for(int i = 0; i < serverList.size(); i++) {//增加服务器虚拟节点,定为所有节点的倒数第二个，只有出没有入
			int start = struct.length - 2;
			int end = serverList.get(i);
			id++;//边的id自增1
			int band = serverBand[i];//带宽表示为该点的输出能力
			int price = 0;//虚拟节点，单价为0
			Edge edge = new Edge(id, start, end, band, price);
			this.edgeList.add(edge);
			struct[struct.length - 2].put(end, id);
		}
		
		for(int i = 0; i < consumerList.size();i++) {//增加消费总结点,定为所有节点的最后一个,只有入没有出
			id++;
			int start = consumerList.get(i).connectedVertex;
			int end = struct.length - 1;
			int band = consumerList.get(i).demand;//带宽表示为需求
			int price = 0;//虚拟节点，单价为0
			Edge edge = new Edge(id, start, end, band, price);
			this.edgeList.add(edge);
			struct[start].put(end, id);
		}
		
		while(true) {//删除单支
			boolean flag = true;
			for(int i = 0; i < struct.length; i++) {
				if(struct[i].size() == 1) {//存在单支，将该点的邻接表置空，删除所有与该点的连接
					flag = false;
					//System.out.println("ONE");
					for(Integer x : struct[i].keySet()) {
						struct[x].remove(i);
					}
					struct[i].clear();
				}
			}
			if(flag) {
				break;
			}
		}
		
		Map<Integer, ConsumerVertex> m = new HashMap<Integer, ConsumerVertex>();
		int demand = 0;
    	for(int i = 0;i < consumerList.size(); i++) {
    		m.put(consumerList.get(i).connectedVertex, consumerList.get(i));
    		demand += consumerList.get(i).demand;//计算总需求
    	}
    	
		List<Integer> eachVertex = new ArrayList<Integer>();
    	for(int i = 0; i < struct.length - 2; i++) {
    		int temp = getOuputAbility(i);
    		eachVertex.add(temp);
    	}
    	
    	Collections.sort(eachVertex);
    	Collections.reverse(eachVertex);
//    	for(int t = 0; t < demand; minCount++) {
//    		t += eachVertex.get(minCount);
//    	}
//    	minCount++; //确定服务器个数下限
		//return minCount;
	}
	
	private int getOuputAbility(int i) {
		/**
		 * 获取节点的输出能力
		 * @param i 节点编号
		 * @return 输出能力
		 */
		int result = 0;
		for(Integer key: struct[i].keySet()) {
			result += edgeList.get(struct[i].get(key)).band;
		}
		return result;
	}
	
	public List<Integer> mustServer() {
		/**
		 * 确定绝对服务器
		 * 绝对服务器的情况分为两种：1.延伸的单支中存在一条边无法满足需求
		 * 					2.多支总和无法满足需求
		 * @return 绝对服务器列表
		 */
		List<Integer> output = new ArrayList<Integer>();
		for (int i = 0; i < consumerList.size(); i++) {
			int vertex = consumerList.get(i).connectedVertex;
			int demand = consumerList.get(i).demand;
//			if(demand>100){  //大于100的直接布置服务器
//				output.add(vertex);
//				continue;
//			}
			Map<Integer, Integer> link = struct[vertex];
			if (link.size() > 2) {//多支情况
				if (getOuputAbility(vertex) - demand < demand) {
					output.add(vertex);
				}
			} else if (link.size() == 2) {//单支情况
				int linkedVertex = 0;
				for (Integer key : link.keySet()) {
					int temp = link.get(key);
					if (temp != netVertexNum - 1) {
						linkedVertex = temp;
						vertex = key;
						break;
					}
				}
				boolean flag = true;
				int band = edgeList.get(linkedVertex).band;
				while (flag) {						
					Map<Integer, Integer> link2 = struct[vertex];
					if (link2.size() == 2) {
						if (getOuputAbility(vertex) - band < demand) {
							output.add(vertex);
							flag = false;
						} else {
							for (Integer key : link2.keySet()) {
								int temp = link2.get(key);
								if (temp != netVertexNum - 1) {
									linkedVertex = temp;
									vertex=key;
									band = edgeList.get(linkedVertex).band;
								}
							}

						}
					} else {
						if (getOuputAbility(vertex) - band < demand) {
							output.add(vertex);
							flag = false;
						} else {
							flag = false;
						}

					}

				}
			}
		}
		return output;
	}
	
	public Graph myclone() {
		/**
		 * 使用序列化的方法进行深复制，深复制对象的成员必须实现Serializable接口
		 * @return 返回副本
		 */
		Graph graph = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			graph = (Graph) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return graph;
	}
}
