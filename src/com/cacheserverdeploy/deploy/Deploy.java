package com.cacheserverdeploy.deploy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class Deploy implements Serializable {
	private static final long serialVersionUID = 3840820909564598408L;
	/**
	 * 你需要完成的入口 <功能详细描述>
	 * 
	 * @param graphContent
	 *            用例信息文件
	 * @return [参数说明] 输出结果信息
	 * @see [类、类#方法、类#成员]
	 */
	private static int INFINITY = 9999; // 静态变量,用于表示无穷

	public static String[] deployServer(String[] graphContent) {
		/** do your work here **/
		String[] num = graphContent[0].split(" ");
		int netVertexNum = Integer.parseInt(num[0]); // 网络节点数目
		int edgeNum = Integer.parseInt(num[1]); // 链路数目
		int consumerVertexNum = Integer.parseInt(num[2]); // 消费节点数目
		int serverCost = Integer.parseInt(graphContent[2]); // 服务器价格

		Edge[] edgeList = new Edge[edgeNum]; // Edge数组
		FirstEdge[] firstedgeList = new FirstEdge[edgeNum]; // Edge数组
		for (int i = 0; i < edgeNum; i++) {
			edgeList[i] = new Edge(i, graphContent[4 + i]); // 存储链路信息
			firstedgeList[i]=new FirstEdge(i, graphContent[4 + i]); // 存储链路信息
		}
		
		List<ConsumerVertex> consumerVertexList = new ArrayList<ConsumerVertex>(); // 消费节点数组
		List<Integer> serverList = new ArrayList<Integer>();
		int demand = 0;
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < consumerVertexNum; i++) {
			consumerVertexList.add(new ConsumerVertex(graphContent[5 + edgeNum + i])); // 存储消费节点信息
			demand += consumerVertexList.get(i).demand;
			map.put(consumerVertexList.get(i).connectedVertex, consumerVertexList.get(i).id); // 用网络节点搜索消费节点
			// serverList.add(consumerVertexList.get(i).connectedVertex);//直接直连
		}
		
		//----FirstStepStart
			 FirstDeploy firstdeploy=new FirstDeploy();	
			 firstdeploy.Firstdeploy(graphContent);
		//----FirstStepEND		
		
		Graph graph = new Graph(netVertexNum, edgeList, consumerVertexList, serverList);// 构图	
		// 得出每个网络节点的权值=节点数*all_band/all_cost,排序由高到低
		Map<Integer, Integer> nodeValue = new LinkedHashMap<Integer, Integer>();
		nodeValue =getNodeValue(netVertexNum,graph,edgeList);		
//		for (Integer i : nodeValue.keySet()) {
//			System.out.println(i + ":" + nodeValue.get(i));
//			if(count>0){
//			serverList.add(i);
//			}
//			count--;
//		}	
		serverList.add(0);
		//serverList.add(2);
		//serverList.add(15);
		serverList.add(24);
		//serverList.add(3);// 使用最开始图上的用例，节点定为0，3，22
		List<Integer> must = graph.mustServer();
		for (Integer i : must) {
			System.out.println(i);
			if (i != (netVertexNum + 1)) {
				serverList.add(i);
			}
		}
		graph.setServerList(serverList);
		int iterations = 1000;
		for (int i = 0; i < iterations; i++) {
			// 1.随机增加服务器
			// 2.随机减少服务器
			// 3.随机改变服务器的位置
			// to be continued.......本来准备采用模拟退火......
		}

		List<List<Integer>> out = getOutput(graph, demand);// 一定要使用副本
		//int fee = sumFee(graph, serverCost, out);// 计算总费用
		//System.out.println(fee);
		return null;
		//return getFinalStringArray(out, map); // 最终返回字符串数组
	}

	private static Map<Integer, Integer> getNodeValue(int netVertexNum, Graph graph, Edge[] edgeList) {
		// TODO Auto-generated method stub
		Map<Integer, Integer> nodeValue = new LinkedHashMap<Integer, Integer>();
		for (int i = 0; i < netVertexNum; i++) {
			int band = 0;
			int count = 0;
			int temp = graph.struct[i].size();
			//System.out.print(temp + " ");
			if (graph.struct[i].size() != 0) {
				for (Integer j : graph.struct[i].keySet()) {
					for (int k = 0; k < edgeList.length; k++) {
						if (edgeList[k].id == graph.struct[i].get(j)) {
							band += edgeList[k].getBand();
							count += edgeList[k].getBand() * edgeList[k].getPrice();
							break;
						}
					}
				}
				nodeValue.put(i, temp * band * 100 / count);
			}
		}
		sortMap(nodeValue);
		return nodeValue;
	}

	/**
	 * 对map，根据value的值来排序
	 * 
	 * @param Dcount
	 */
	public static void sortMap(Map<Integer, Integer> Dcount) {
		int[] value = new int[Dcount.size()];
		int[] key = new int[Dcount.size()];
		int a = 0;
		for (int i : Dcount.keySet()) {
			key[a] = i;
			value[a] = Dcount.get(i);
			a++;
		}
		for (int i = 0; i < Dcount.size() - 1; i++) {
			int temp = value[i];
			int k = key[i];
			for (int j = i + 1; j < Dcount.size(); j++) {
				if (temp < value[j]) {
					value[i] = value[j];
					value[j] = temp;
					temp = value[i];
					key[i] = key[j];
					key[j] = k;
					k = key[i];
				}
			}
		}
		// Map<Integer,Integer> newMap = new LinkedHashMap<Integer,Integer>();
		Dcount.clear();
		for (int i = 0; i < key.length; i++) {
			Dcount.put(key[i], value[i]);
		}
		// return newMap;
	}

	public static List<Integer> dijkstra(Graph graph, int startPoint, int endPoint) {
		/**
		 * 求两个节点之间的最短路径，将单价作为链路权值 原理是dijkstra算法,时间复杂度O(VlogV),V表示节点数目
		 * 
		 * @param graph
		 *            图
		 * @param startPoint
		 *            起点
		 * @param endPoint
		 *            终点
		 * @return List<Integer> 路径中的节点坐标集，注意是反向的，即从终点到起点
		 */
		class Heap {
			/**
			 * Heap类，表示最小堆，用于寻找当前最短路径对应的节点
			 */

			public Vector<Vertex> vertexList; // 节点向量

			Heap() { // 构造函数1
				this.vertexList = new Vector<Vertex>();
			}

			public void insert(Vertex ver) {
				/**
				 * 将元素加入到最小堆
				 * 
				 * @param ver
				 *            待加入的元素
				 * 
				 */
				int hole = vertexList.size();
				vertexList.add(ver);
				for (; hole > 0 && (ver.dv[0] < vertexList.get((hole - 1) / 2).dv[0]
						|| (ver.dv[0] == vertexList.get((hole - 1) / 2).dv[0])
								&& (ver.dv[1] > vertexList.get((hole - 1) / 2).dv[1])); hole = (hole - 1) / 2) {
					vertexList.set(hole, vertexList.get((hole - 1) / 2));
				}
				vertexList.set(hole, ver);
			}

			public Vertex deleteMin() {
				/**
				 * 删除最小的元素并返回
				 * 
				 */
				if (vertexList.isEmpty()) {
					return null;
				}
				Vertex forReturn = vertexList.get(0);
				Vertex tmp = vertexList.lastElement();
				vertexList.set(0, tmp);
				int hole = 0;
				int child;
				for (; hole * 2 + 1 < vertexList.size(); hole = child) {
					child = hole * 2 + 1;
					if (child != vertexList.size() - 1 && (vertexList.get(child).dv[0] > vertexList.get(child + 1).dv[0]
							|| (vertexList.get(child).dv[0] == vertexList.get(child + 1).dv[0]
									&& vertexList.get(child).dv[1] < vertexList.get(child + 1).dv[1]))) {
						child++;
					}
					if (vertexList.get(child).dv[0] < tmp.dv[0]
							|| (vertexList.get(child).dv[0] == tmp.dv[0] && vertexList.get(child).dv[1] > tmp.dv[1])) {
						vertexList.set(hole, vertexList.get(child));
					} else {
						break;
					}
				}
				vertexList.set(hole, tmp);
				vertexList.remove(vertexList.size() - 1);
				return forReturn;
			}

		}

		Vertex[] vertexList = new Vertex[graph.netVertexNum];
		for (int i = 0; i < graph.netVertexNum; i++) {
			vertexList[i] = new Vertex(i, false, INFINITY, -1);
		}
		vertexList[startPoint].dv[0] = 0;
		Heap heap = new Heap();
		heap.insert(vertexList[startPoint]);

		while (true) {
			Vertex forProcess;
			while (true) {
				forProcess = heap.deleteMin();
				if (forProcess == null) {
					// System.out.println("No path");
					return null;
				}
				if (!forProcess.known) {
					break;
				}
			}
			forProcess.known = true;
			Map<Integer, Integer> hashMap = graph.struct[forProcess.id];
			Iterator<Integer> it = hashMap.keySet().iterator();
			while (it.hasNext()) {
				Integer key = (Integer) it.next();
				Vertex update = vertexList[key];
				Edge line = graph.edgeList.get(hashMap.get(key));
				if (!update.known && (update.dv[0] > forProcess.dv[0] + line.price
						|| (update.dv[0] == forProcess.dv[0] + line.price
								&& update.dv[1] < min(forProcess.dv[1], line.band)))) {
					update.dv[1] = min(forProcess.dv[1], line.band);
					// 路径上的最小带宽
					update.dv[0] = forProcess.dv[0] + line.price;// to be
																	// continued...................................
					update.pv = forProcess.id;
					heap.insert(update);
				}
			}

			if (forProcess.id == endPoint) {
				break;
			}
		}
		List<Integer> resultList = new ArrayList<Integer>();
		int thisVertexId = vertexList[endPoint].pv;
		while (thisVertexId != -1) {
			resultList.add(thisVertexId);
			thisVertexId = vertexList[thisVertexId].pv;
		}

		Collections.reverse(resultList);
		resultList.add(endPoint);
		// System.out.println("----------------------------------------------------");
		// System.out.println(new StringBuilder(resultList.toString()));
		// System.out.println("----------------------------------------------------");
		return resultList;
	}

	public static Path maxFlowMinFee(Graph graph, int start, int end, int demand) {
		/**
		 * 最大流最小路径算法，具体实现过程我自己都看不懂了
		 * 
		 * @param graph
		 *            图
		 * @param start
		 *            起点
		 * @param end
		 *            终点
		 * @param demand
		 *            总需求
		 */
		int presentFlow = 0;
		int theta = INFINITY;
		List<Integer> output = dijkstra(graph, start, end);
		if (output == null) {
			return null;
		}
		Path path = new Path(start, end);
		while (output != null && presentFlow < demand) {
			for (int i = 0; i < output.size() - 1; i++) {
				int startPoint = output.get(i);
				int endPoint = output.get(i + 1);
				int band;
				Edge thisEdge = graph.edgeList.get((int) graph.struct[startPoint].get(endPoint));
				if (thisEdge.startPoint == startPoint) {
					band = thisEdge.upBand;
				} else {
					band = thisEdge.downBand;
				}
				if (band < theta) {
					theta = band;
				}
			}
			if (theta + presentFlow < demand) {
				presentFlow += theta;
			} else {
				theta = demand - presentFlow;
				presentFlow = demand;
			}
			for (int i = 0; i < output.size() - 1; i++) {
				int startPoint = output.get(i);
				int endPoint = output.get(i + 1);
				int band;
				Edge thisEdge = graph.edgeList.get((int) graph.struct[startPoint].get(endPoint));
				if (thisEdge.startPoint == startPoint) {
					band = thisEdge.upBand;
				} else {
					band = thisEdge.downBand;
				}
				path.addPath(new OnePath(startPoint, endPoint, theta));
				if (band == theta) {
					graph.struct[startPoint].remove(endPoint);
					if (i != 0 && i != output.size() - 1) {
						thisEdge.price = -thisEdge.price;
					}
				} else {
					if (thisEdge.startPoint == startPoint) {
						thisEdge.upBand -= theta;
					} else {
						thisEdge.downBand -= theta;
					}
				}
			}
			theta = INFINITY;
			output = dijkstra(graph, start, end);
			if (output == null && presentFlow < demand) {
				System.out.println("Fail");
				return null;
			}
		}
		return path;

	}

	public static int min(int x, int y) {
		if (x > y) {
			return y;
		} else {
			return x;
		}
	}

    public static List<List<Integer>> getOutput(Graph graph, int demand) {
    	/**
    	 * 使用最大流最小费用算法获取输出
    	 * @param graph 当前的图
    	 * @param demand 总需求
    	 * @return 链路数据 该参数类似一个二维数组，每一行表示一条路径，第一个数一定是虚拟总服务器，倒数第二个数一定是虚拟消费节点，最后一个数为链路带宽
    	 */
    	int netVertexNum = graph.netVertexNum;
//    	for(int i = 0; i < graph.struct.length; i++) {//打印图的结构
//    		String out = "Vertex " + String.valueOf(i) + ": ";
//    		for(Integer key: graph.struct[i].keySet()) {
//    			out += ("[Vertex " + String.valueOf(key) + " Edge " + String.valueOf(graph.struct[i].get(key)) + "]");
//    		}
//    		System.out.println(out);
//    	}
//    	System.out.println("-------------------------------------------------------------------------------");
    	Graph clone = graph.myclone();//备份 Significant!!!!!!!!!!!!
    	Path path = maxFlowMinFee(clone, netVertexNum-2, netVertexNum-1, demand);
    	if(path == null) {
    		List<List<Integer>> warning = new ArrayList<List<Integer>>();
    		List<ConsumerVertex> list = clone.consumerList;
			for(ConsumerVertex cv : list) {
				int nv = cv.connectedVertex;
				Map<Integer, Integer> map = clone.struct[nv];
				if(map.containsKey(clone.netVertexNum - 1)) {
					List<Integer> record = new ArrayList<Integer>();
					record.add(0); //0表示当前为消费节点信息
					record.add(cv.id); //消费节点id
					record.add(cv.demand); //消费节点需求
					record.add(cv.demand - clone.edgeList.get(map.get(clone.netVertexNum - 1)).upBand); //已经满足的需求
					record.add(clone.edgeList.get(map.get(clone.netVertexNum - 1)).upBand); //消费节点的剩余需求
					warning.add(record);
					String out = "Consumer Vertex NO." + cv.id + " is not fully servered ! ";
					out += "demand: " + cv.demand + " left: " + clone.edgeList.get(map.get(clone.netVertexNum - 1)).upBand;
					System.out.println(out);
				}
			}
			for(Integer i : clone.struct[clone.netVertexNum - 2].keySet()) {
				List<Integer> record = new ArrayList<Integer>();
				record.add(1); //1表示当前为服务器节点信息
				record.add(i); //服务器位置
				record.add(clone.edgeList.get(clone.struct[clone.netVertexNum - 2].get(i)).band); //服务器总输出能力
				record.add(clone.edgeList.get(clone.struct[clone.netVertexNum - 2].get(i)).band - clone.edgeList.get(clone.struct[clone.netVertexNum - 2].get(i)).upBand); //服务器已输出的流量
				record.add(clone.edgeList.get(clone.struct[clone.netVertexNum - 2].get(i)).upBand); //服务器剩余输出能力
				warning.add(record);
				String out = "Server Vertex NO." +  i + " is not fully used ! ";
				out += "ability " + record.get(2) + " left: " + record.get(4);
				System.out.println(out);
			}
			List<Integer> t = new ArrayList<Integer>();
			t.add(-1);//最后一行为-1，代表异常
			warning.add(t);
    		return warning;
    	}
    	List<List<Integer>> out = path.fromPathToString();
		List<Integer> s = new ArrayList<Integer>();
		s.add(0); //0表示正确输出
		out.add(s);
    	return out;
    }

	public static int sumFee(Graph graph, int serverCost, List<List<Integer>> out) {
		/**
		 * 计算总价
		 * 
		 * @param graph
		 *            图
		 * @param serverCost
		 *            服务器单价
		 * @param out
		 *            路径
		 * @return 总费用
		 */
		int fee = 0; // 总价
		for (int i = 0; i < out.size(); i++) {
			List<Integer> sub = out.get(i);
			if (sub.size() == 4) {// 如果链路信息的长度为4，表示直连，不会产生费用
				continue;
			}
			int band = sub.get(sub.size() - 1);// 获取链路带宽
			for (int j = 0; j < sub.size() - 2; j++) {
				fee += graph.edgeList.get(graph.struct[sub.get(j)].get(sub.get(j + 1))).price * band;// 计算链路费用
			}
		}
		fee += graph.serverList.size() * serverCost;// 加入服务器的费用
		return fee;
	}

	public static String[] getFinalStringArray(List<List<Integer>> out, Map<Integer, Integer> map) {
		/**
		 * 转换成最后写入文件的结果
		 * 
		 * @out 输出结果，待转换
		 * @map 网络节点到消费节点的转换表
		 */
		int lineCount = out.size();
		String[] output = new String[lineCount + 2];
		output[0] = String.valueOf(lineCount);// 第一行链路数目
		output[1] = "";// 第二行为空行
		for (int i = 0; i < lineCount; i++) {// 网络节点到消费节点 带宽
			List<Integer> line = out.get(i);
			StringBuffer s = new StringBuffer();
			for (int j = 1; j < line.size() - 2; j++) {
				int t = line.get(j);
				s.append(String.valueOf(t)).append(" ");
			}
			int c = map.get(line.get(line.size() - 3));
			s.append(String.valueOf(c)).append(" ");
			s.append(line.get(line.size() - 1));
			output[i + 2] = new String(s);
		}
		return output;
	}
	
	//FirstStep的方法
	public static FirstEdge getEdge(FirstGraph graph, int edgeNum) {
		/*
		 * 定义一个根据图和边的ID号获取边的方法
		 * 
		 * @param graph 想要返回的边所在的图
		 * 
		 * @param edgeNum 边的ID号
		 * 
		 */
		FirstEdge edge;
		for (int i = 0; i < graph.edgeList.length; i++) {
			if (graph.edgeList[i].id == edgeNum) {
				edge = graph.edgeList[i];
				return edge;
			}
		}
		return null;
	}

	public static FirstEdge getEdge(FirstGraph graph, int startNum, int endNum) {
		/*
		 * 定义一个根据图和边的两个顶点获取边的方法
		 * 
		 * @param graph 想要返回的边所在的图
		 * 
		 * @param startNum 起点。即我们会在graph.struct[startNum]中查找是否存在这条边
		 * 
		 * @param endNum 边的另一个顶点
		 * 
		 */
		FirstEdge edge;
		for (int i = 0; i < graph.struct[startNum].size(); i++) {
			if (graph.struct[startNum].get(i)[0] == endNum) {
				edge = getEdge(graph, graph.struct[startNum].get(i)[1]);
				return edge;
			}
		}
		return null;
	}
	public static int computeVertexInBand(FirstGraph graph, int vertexNum, int connectedVertexNum) {
		/*
		 * 计算一个节点的剩余输入能力
		 * 
		 * @param graph 图
		 * 
		 * @param vertexNum 所计算的节点的Id号
		 * 
		 * @param connectedVertexNum 需要改节点输入带宽的节点Id号
		 * 遍历该节点的边。如果边的另一个节点Id小于vertexNum ，则剩余输入为剩余上行带宽。 反之为剩余下行带宽。
		 * 注意如果那条边的两个顶点是vertexNum和connectedVertexNum则忽略这条边的输出能力
		 * 因为这条边在两个节点中边输入边输出是没有任何意义的
		 * 
		 */
		int vertexInBand = 0;
		for (int i = 0; i < graph.struct[vertexNum].size(); i++) {
			if (connectedVertexNum != graph.struct[vertexNum].get(i)[0]) {
				if (vertexNum < graph.struct[vertexNum].get(i)[0]) {
					vertexInBand += getEdge(graph, graph.struct[vertexNum].get(i)[1]).maxBand
							+ getEdge(graph, graph.struct[vertexNum].get(i)[1]).usedBand;
				} else {
					vertexInBand += getEdge(graph, graph.struct[vertexNum].get(i)[1]).maxBand
							- getEdge(graph, graph.struct[vertexNum].get(i)[1]).usedBand;
				}
			}
		}
		return vertexInBand;
	}
	public static void sort(List<ServiceVertex> list) {
		// 对服务器的需求排序
		ServiceVertex[] array = new ServiceVertex[list.size()];
		ServiceVertex temp = null;
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		for (int i = 0; i < array.length - 1; i++) {
			boolean flag = false;
			for (int j = 0; j < array.length - 1 - i; j++) {
				if (array[j].demand > array[j + 1].demand) {
					temp = array[j];
					array[j] = array[j + 1];
					array[j + 1] = temp;
					flag = true;
				}
			}
			if (!flag) {
				break;
			}
		}
		/*
		 * for (ServiceVertex serviceVertex : array) {
		 * System.out.println(serviceVertex.location + ":"
		 * +serviceVertex.demand); }
		 */
		// list = Arrays.asList(array);
		list.clear();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
	}

}
