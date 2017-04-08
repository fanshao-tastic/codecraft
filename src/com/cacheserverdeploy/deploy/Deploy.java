package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class Deploy {
	/**
	 * 你需要完成的入口 <功能详细描述>
	 * 
	 * @param graphContent
	 *            用例信息文件
	 * @return [参数说明] 输出结果信息
	 * @see [类、类#方法、类#成员]
	 */
	private static int INFINITY = 999999999; // 静态变量,用于表示无穷
	static Boolean flag=true;

	public static String[] deployServer(String[] graphContent) {
		/** do your work here **/
		String[] num = graphContent[0].split(" ");
		int netVertexNum = Integer.parseInt(num[0]); // 网络节点数目
		int edgeNum = Integer.parseInt(num[1]); // 链路数目
		int consumerVertexNum = Integer.parseInt(num[2]); // 消费节点数目
		int serverCost = Integer.parseInt(graphContent[2]); // 服务器价格

		Edge[] edgeList = new Edge[edgeNum]; // Edge数组
		for (int i = 0; i < edgeNum; i++) {
			edgeList[i] = new Edge(i, graphContent[4 + i]); // 存储链路信息
		}

		List<ConsumerVertex> consumerVertexList = new ArrayList<ConsumerVertex>(); // 消费节点数组
		// List<Integer> serverList = new ArrayList<Integer>();
		int demand = 0;
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < consumerVertexNum; i++) {
			consumerVertexList.add(new ConsumerVertex(graphContent[5 + edgeNum + i])); // 存储消费节点信息
			demand += consumerVertexList.get(i).demand;
			map.put(consumerVertexList.get(i).connectedVertex, consumerVertexList.get(i).id); // 用网络节点搜索消费节点
			// serverList.add(consumerVertexList.get(i).connectedVertex);//直接直连
		}
		Graph graph = new Graph(netVertexNum, edgeList, consumerVertexList);// 构图

		// ----FirstStepStart
		// FirstDeploy firstdeploy = new FirstDeploy();
		//List<Map<Integer, Integer>> FirstStepOut = FirstDeploy.Firstdeploy(graphContent);

		// ----FirstStepEND		
		
		int minFee = INFINITY;
		List<List<Integer>> bestout = null;
		//List<Integer> serverList2 = new ArrayList<Integer>();
		Graph copy1 = graph.myclone();
		List<Integer> serverList1 = copy1.getTopServer(graph.maxCount);
		List<Integer> serverList2 = new ArrayList<Integer>();
		int count = serverList1.size();
		int count1=0;
		int count2=0;
		for(int i=0;i<count;i++){
			if(serverList1.get(i)>=80){
				count1++;
				//serverList2.add(serverList1.get(i));
			}
			if(serverList1.get(i)<=30){
				count2++;
			}
		}
		System.out.println(count1+"  "+count2);
		Timer timer = new Timer();
		  timer.schedule(new TimerTask() {
		      public void run() {		          
		    	  flag=false;
		    	  System.out.println("-------设定要指定任务--------");
		      }
		    }, 85000);// 设定指定的时间time,此处为85秒
		//flag =false;
		while (flag &&count>=0) {
			Graph copy = graph.myclone();
			List<Integer> serverList3 = new ArrayList<Integer>();			
			for(int i=0;i<count;i++){
				serverList3.add(serverList1.get(i));
			}
			for(Integer i:serverList2){
				serverList3.add(i);
			}			
			copy.addServerList(serverList3);			
			List<List<Integer>> out = getOutput(copy, demand);
			if (out.get(out.size() - 1).get(0) == -1) {
				System.out.println("warning!!!!!!!!!!!!!!!!!!!!!!!!!");
				//System.out.println(count);
				serverList2.add(serverList1.get(count));

			} else {
				out.remove(out.size() - 1);
//				if (judge(copy, out)) {
//					System.out.println("Successful!");
//				} else {
//					System.out.println("Fatal wrong");
//				}
				int fee = sumFee(copy, serverCost, out);// 计算总费用
				System.out.println(fee);
				if (fee < minFee) {
					minFee = fee;
					bestout = out;
				}
			}			
			//serverList.remove(serverList.size()-1);
			count--;
//			if(netVertexNum<255){
//				if(count==0){
//					break;
//				}
//			}else if(netVertexNum>255 && netVertexNum<400){
//				if(count<serverList1.size()*0.01){
//					break;
//				}
//			}else{
//				if(count<serverList1.size()*0.2){
//					break;
//				}
//			}					  			
		}
		//return getFinalStringArray(bestout, map); // 最终返回字符串数组
		 return new String[] {"\r\n", "0828"};
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
					theta = band;// 计算路径上的最小带宽
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
					if (i != 0 && i != output.size() - 2) {// point1
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
		 * 
		 * @param graph
		 *            当前的图
		 * @param demand
		 *            总需求
		 * @return 链路数据 该参数类似一个二维数组，每一行表示一条路径，第一个数一定是虚拟总服务器，倒数第二个数一定是虚拟消费节点，
		 *         最后一个数为链路带宽
		 */
		int netVertexNum = graph.netVertexNum;
		Graph clone = graph.myclone();// 备份 Significant!!!!!!!!!!!!
		Path path = maxFlowMinFee(clone, netVertexNum - 2, netVertexNum - 1, demand);
//		if (path == null) {
//			List<List<Integer>> warning = new ArrayList<List<Integer>>();
//			List<ConsumerVertex> list = clone.consumerList;
//			for (ConsumerVertex cv : list) {
//				int nv = cv.connectedVertex;
//				Map<Integer, Integer> map = clone.struct[nv];
//				if (map.containsKey(clone.netVertexNum - 1)) {
//					List<Integer> record = new ArrayList<Integer>();
//					record.add(0); // 0表示当前为消费节点信息
//					record.add(cv.id); // 消费节点id
//					record.add(cv.demand); // 消费节点需求
//					record.add(cv.demand - clone.edgeList.get(map.get(clone.netVertexNum - 1)).upBand); // 已经满足的需求
//					record.add(clone.edgeList.get(map.get(clone.netVertexNum - 1)).upBand); // 消费节点的剩余需求
//					warning.add(record);
//					String out = "Consumer Vertex NO." + cv.id + " is not fully servered ! ";
//					out += "demand: " + cv.demand + " left: "
//							+ clone.edgeList.get(map.get(clone.netVertexNum - 1)).upBand;
//				//	System.out.println(out);
//				}
//			}
//			for (Integer i : clone.struct[clone.netVertexNum - 2].keySet()) {
//				List<Integer> record = new ArrayList<Integer>();
//				record.add(1); // 1表示当前为服务器节点信息
//				record.add(i); // 服务器位置
//				record.add(clone.edgeList.get(clone.struct[clone.netVertexNum - 2].get(i)).band); // 服务器总输出能力
//				record.add(clone.edgeList.get(clone.struct[clone.netVertexNum - 2].get(i)).band
//						- clone.edgeList.get(clone.struct[clone.netVertexNum - 2].get(i)).upBand); // 服务器已输出的流量
//				record.add(clone.edgeList.get(clone.struct[clone.netVertexNum - 2].get(i)).upBand); // 服务器剩余输出能力
//				warning.add(record);
//				String out = "Server Vertex NO." + i + " is not fully used ! ";
//				out += "ability " + record.get(2) + " left: " + record.get(4);
//				//System.out.println(out);
//			}
//			List<Integer> t = new ArrayList<Integer>();
//			t.add(-1);// 最后一行为-1，代表异常
//			warning.add(t);
//			return warning;
//		}
		List<List<Integer>> out = path.fromPathToString();
		List<Integer> s = new ArrayList<Integer>();
		s.add(0); // 0表示正确输出
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
		Set<Integer> set = new HashSet<Integer>();
		for (int i = 0; i < out.size(); i++) {
			List<Integer> sub = out.get(i);
			set.add(sub.get(1));
			if (sub.size() == 4) {// 如果链路信息的长度为4，表示直连，不会产生费用
				continue;
			}
			int band = sub.get(sub.size() - 1);// 获取链路带宽
			for (int j = 0; j < sub.size() - 2; j++) {
				fee += graph.edgeList.get(graph.struct[sub.get(j)].get(sub.get(j + 1))).price * band;// 计算链路费用
			}
		}
		fee += set.size() * serverCost;// 加入服务器的费用
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

	public static boolean judge(Graph g, List<List<Integer>> out) {
		Graph graph = g.myclone();
		for (List<Integer> oneline : out) {
			int band = oneline.get(oneline.size() - 1);
			for (int i = 0; i < oneline.size() - 2; i++) {
				int start = oneline.get(i);
				int end = oneline.get(i + 1);
				if (!graph.struct[start].containsKey(end)) {
					System.out.println("Band Error!");
					return false;
				}
				Edge e = graph.edgeList.get(graph.struct[start].get(end));
				if (e.band < band) {
					System.out.println("Band Error!");
					return false;
				}
				e.band -= band;
				if (e.band == 0) {
					graph.struct[start].remove(end);
				}
			}
		}
		for (ConsumerVertex c : graph.consumerList) {
			int v = c.connectedVertex;
			if (graph.struct[v].containsKey(graph.netVertexNum - 1)) {
				System.out.println("Consumer Vertex Error!");
				return false;
			}
		}
		return true;
	}
}
