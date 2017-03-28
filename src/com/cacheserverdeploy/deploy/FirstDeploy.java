package com.cacheserverdeploy.deploy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

//import java.util.Map;

public class FirstDeploy implements Serializable {
	/**
	 * 你需要完成的入口 <功能详细描述>
	 * 
	 * @param graphContent
	 *            用例信息文件
	 * @return [参数说明] 输出结果信息
	 * @see [类、类#方法、类#成员]
	 */
	private static int INFINITY = 9999; // 静态变量,用于表示无穷
	private static int serverCost;
	private static int[][] haveGone;

	public static List<Map<Integer, Integer>> Firstdeploy(String[] graphContent) {
		/** do your work here **/

		String[] num = graphContent[0].split(" ");
		int netVertexNum = Integer.parseInt(num[0]); // 网络节点数目
		int edgeNum = Integer.parseInt(num[1]); // 链路数目
		int consumerVertexNum = Integer.parseInt(num[2]); // 消费节点数目
		haveGone = new int[netVertexNum][netVertexNum];// 静态数组用于记录两两之间是否已经尝试过合并
		serverCost = Integer.parseInt(graphContent[2]); // 服务器价格

		FirstEdge[] edgeList = new FirstEdge[edgeNum]; // Edge数组
		for (int i = 0; i < edgeNum; i++) {
			edgeList[i] = new FirstEdge(i, graphContent[4 + i]); // 存储链路信息
		}

		ConsumerVertex[] consumerVertexList = new ConsumerVertex[consumerVertexNum]; // 消费节点数组
		for (int i = 0; i < consumerVertexNum; i++) {
			consumerVertexList[i] = new ConsumerVertex(graphContent[5 + edgeNum + i]); // 存储消费节点信息
		}
			
		Set<Integer> connected = new HashSet<Integer>();
		for (int i = 0; i < consumerVertexList.length; i++) {
			connected.add(consumerVertexList[i].connectedVertex);
		}
		FirstGraph firstgraph = new FirstGraph(netVertexNum, edgeList, connected); // 将图转化为邻接表的结构
		
//		Map<Integer,Integer> Dcount=new LinkedHashMap<Integer,Integer>();		
//	    for(int i=0;i<consumerVertexList.length-1;i++){
//			int startPoint = consumerVertexList[i].connectedVertex;
//			for(int j=i+1;j<consumerVertexList.length;j++){
//				int endPoint=consumerVertexList[j].connectedVertex;
//				List<Integer> temp = dijkstra(graph, startPoint, endPoint);				
////				if(temp.size()>4){					
////					temp.remove(1);
////					temp.remove(temp.size()-2);					
////				}
////				temp.remove(0);
////				temp.remove(temp.size()-1);
// 				for(int t:temp){
//					if(Dcount.containsKey(t)){
//						Dcount.put(t, Dcount.get(t)+1);
//					}else{
//						Dcount.put(t, 1);
//					}
//				}
//			}
//		}
//	   sortMap(Dcount);	    	   
//		for(Integer i:Dcount.keySet()){
//			System.out.println(i+":"+Dcount.get(i));
//			//System.out.println(key[i]+":"+value[i]);
//		}
//		System.out.println("num:"+Dcount.size());
		
		
		List<ServiceVertex> serviceVertexsList = new ArrayList<>();// 服务器节点链表
		List<ServiceVertex> mayServiceVertexsList = new ArrayList<>();// 可能是服务器点的链表，通过递归得到最后的服务器节点
		List<ServiceVertex> sPServiceVertexsList = new ArrayList<>();
		// 遍历图的array数组，判断是否存在正方形
		List<Integer> deleteList = new ArrayList<>();
		for (int i = 0; i < firstgraph.array.length - 4; i++) {
			int size = 0;
			int start = -1;
			for (int j = i + 1; j < firstgraph.array[i].length; j++) {
				if (firstgraph.array[i][j] == 1) {
					if (start == -1) {
						start = j;
					}
					size++;
				} else {
					if (size <= 3) {
						start = -1;
						size = 0;
					} else {
						int count = 0;
						for (int k = 0; k < size; k++) {
							for (int z = i + k + 1; z < j; z++) {
								count += firstgraph.array[i + k][z];
							}
						}
						if (count == size * (size + 1) / 2) {
							for (int k = i; k < size + i + 1; k++) {
								if (!deleteList.contains(k)) {
									deleteList.add(k);
								}
							}
						} else {
							start = -1;
							size = 0;
						}
					}
				}
			}
			if (size > 3) {
				int count = 0;
				for (int k = 0; k < size; k++) {
					for (int z = i + k + 1; z < firstgraph.array[i].length; z++) {
						count += firstgraph.array[i + k][z];
					}
				}
				if (count == size * (size + 1) / 2) {
					for (int k = i; k < size + i + 1; k++) {
						if (!deleteList.contains(k)) {
							deleteList.add(k);
						}
					}
				} else {
					start = -1;
					size = 0;
				}
			}
		}
		// 总需求
//		int ALL_DEMAND = 0;
//		for (int i = 0; i < consumerVertexList.length; i++) {
//			ALL_DEMAND += consumerVertexList[i].demand;
//		}

		// 遍历消费节点，
		for (int i = 0; i < consumerVertexList.length; i++) {
			/*
			 * 判断该消费节点连接的是否为单一路径。 如果是单一路径，且该路径最小值小于需求，
			 * 则在与消费节点相连的网络节点直接部署服务器，并且删除整条单一路径。
			 * 如果消费节点所连接的网络节点不是单一路径，但是其能接受的最大输入带宽小于需求值， 则也将该网络节点设置为服务器节点。
			 * 
			 */
			int minBand = 9999;
			boolean first = true;
			int pre = -1;
			int vertexNum = consumerVertexList[i].connectedVertex;
			// 首选在与消费节点相连的网络节点上部署服务器
			ServiceVertex serviceVertex = new ServiceVertex(firstgraph, vertexNum, consumerVertexList[i].demand);
			/*
			 * 消费节点的需求大于100的时候直接布置服务器
			 */
			if (consumerVertexList[i].demand >= 100) {
				System.out.println("------------" + vertexNum);
				// sPServiceVertexsList.add(serviceVertex);
				serviceVertexsList.add(serviceVertex);
				continue;
			}
			// 判断是否为单条路径。如果是first点，它的边的数目为1.如果不是first点，则应该为2(也有可能是3，4，5.。).
			while (((firstgraph.struct[vertexNum].size() == 1) && (first))
					|| ((firstgraph.struct[vertexNum].size() == 2) && (!first))) {
				// 如果这条路径的剩余带宽小于路径现有最小带宽，则改变路径最小带宽

				if (first) {
					first = false;
					int edgeId1 = firstgraph.struct[vertexNum].get(0)[1];
					if (getEdge(firstgraph, edgeId1).maxBand < minBand) {
						minBand = getEdge(firstgraph, firstgraph.struct[vertexNum].get(0)[1]).maxBand;
					}
					pre = vertexNum;
					vertexNum = firstgraph.struct[vertexNum].get(0)[0];// 将顶点号传递给边的另一个顶点继续判断是否还是单条路径
				} else {
					for (int j = 0; j < firstgraph.struct[vertexNum].size(); j++) {
						if (firstgraph.struct[vertexNum].get(j)[0] != pre) {
							int edgeId2 = firstgraph.struct[vertexNum].get(j)[1];
							if (getEdge(firstgraph, edgeId2).maxBand < minBand) {
								minBand = getEdge(firstgraph, firstgraph.struct[vertexNum].get(j)[1]).maxBand;
							}
							pre = vertexNum;
							vertexNum = firstgraph.struct[vertexNum].get(j)[0];
							break;
						}
					}
				}
			}
			// 如果单一路径最小带宽小于消费点需求，则直接将与消费节点相连的服务器加入到确定服务器集合中。
			// 并删除这条路径
			if (minBand < consumerVertexList[i].demand) {
				// System.out.println("1111111111" + serviceVertex.location);
				// System.out.println(pre);
				serviceVertexsList.add(serviceVertex);
			}
			// 如果与消费节点相连的网络节点有多条路径，但是路径带宽总和无法达到消费点需求
			// 则直接将与消费节点相连的服务器加入到确定服务器集合中。
			else if (computeVertexInBand(firstgraph, vertexNum, pre) < consumerVertexList[i].demand) {
				serviceVertexsList.add(serviceVertex);
			}
			// 如果上两个条件都不符合，则将建立在与消费节点相连的网络节点上的服务器加入候选服务器队
			// 列进行下一轮筛选
			else {
				mayServiceVertexsList.add(serviceVertex);
			}
		}
		// System.out.println(getServiceVertex(11, mayServiceVertexsList,
		// mayServiceVertexsList) == null);
		sort(mayServiceVertexsList);
		for (ServiceVertex serviceVertex : mayServiceVertexsList) {
			System.out.println(serviceVertex.location + ":" + serviceVertex.demand);
		}
		System.out.println("-----------------------------------");
		for (ServiceVertex serviceVertex : serviceVertexsList) {
			System.out.println(serviceVertex.location);
		}
		System.out.println("-----------------------------------");
		// 在进行合并前先对graph和两个list进行深复制
		FirstGraph graph2 = firstgraph.deepClone(firstgraph);
		List<ServiceVertex> mayServiceVertexsList2 = new ArrayList<ServiceVertex>(mayServiceVertexsList);
		List<ServiceVertex> serviceVertexsList2 = new ArrayList<ServiceVertex>(serviceVertexsList);

		System.out.println("-----------------------------------");
		for (int i = 0; i < firstgraph.weights.length; i++) {
			System.out.println(i + ":" + firstgraph.weights[i]);
		}

		System.out.println("-----------------------------------");

		int time = 4;
		while (time > 0) {
			time--;
			int count =0;
			while (true) {
				int result = combineService(firstgraph, mayServiceVertexsList, serviceVertexsList);
				if(count>5){//失败连续超过100次则退出来。
					break;
				}
				
				if (result == 1) {
					System.out.println("合并成功");
					//count--;
					graph2 = firstgraph.deepClone(firstgraph);
					mayServiceVertexsList2 = new ArrayList<ServiceVertex>(mayServiceVertexsList);
					serviceVertexsList2 = new ArrayList<ServiceVertex>(serviceVertexsList);
					int sum = 0;
//					for (ServiceVertex serviceVertex2 : mayServiceVertexsList) {
//						sum += serviceVertex2.demand;
//						System.out.print(serviceVertex2.location + ":" + serviceVertex2.demand + " , ");
//					}
//					System.out.println("sum == " + sum);
				}

				if (result == 2) {
					count++;
					System.out.println("合并失败返回2");
					firstgraph = graph2.deepClone(graph2);
					edgeList = firstgraph.edgeList;
					mayServiceVertexsList = new ArrayList<>(mayServiceVertexsList2);
					serviceVertexsList = new ArrayList<>(serviceVertexsList2);
//					for (ServiceVertex serviceVertex2 : mayServiceVertexsList) {
//						System.out.print(serviceVertex2.location + ":" + serviceVertex2.demand + " , ");
//					}
//					System.out.println("");
				}

				if (result == 3) {
					count++;
					System.out.println("合并失败返回3");
//					for (ServiceVertex serviceVertex2 : mayServiceVertexsList) {
//						System.out.print(serviceVertex2.location + ":" + serviceVertex2.demand + " , ");
//					}
//					System.out.println("");
				}

				if (result == 4) {
					break;
				}

			}
			for (ServiceVertex serviceVertex : mayServiceVertexsList) {
				refresh(firstgraph, serviceVertex, mayServiceVertexsList, serviceVertexsList);
			}

		}
		//////////////
		System.out.println("-----------------------------------");
		/*
		 * for (ServiceVertex serviceVertex : mayServiceVertexsList) {
		 * deleteImportToServiceVertex(graph, serviceVertex ,
		 * mayServiceVertexsList , serviceVertexsList); }
		 * System.out.println("-----------------------------------");
		 */
		int sum = 0;
		for (FirstEdge edge : edgeList) {
			sum += Math.abs(edge.usedBand) * edge.price;
			// System.out.println(
			// edge.id + "号边 ， 起点为" + edge.startPoint + "，终点为 " + edge.endPoint
			// + "，使用带宽为" + edge.usedBand);
		}
		System.out.println(sum);

		System.out.println("-----------------------------------");
		// System.out.println(computeVertexBand(graph, 1));
		// dijkstra(graph, 29, 31);
		System.out.println(mayServiceVertexsList.size());
		System.out.println(serviceVertexsList.size());
		System.out.println(sPServiceVertexsList.size());
		int count = 0;
		int count2 = 0;	
		List<Map<Integer,Integer>> returnOut= new ArrayList<Map<Integer,Integer>>();
		Map<Integer,Integer> serMap1=new LinkedHashMap<Integer,Integer>();  //可能
		Map<Integer,Integer> serMap2=new LinkedHashMap<Integer,Integer>(); //一定是服务器
		for (ServiceVertex serviceVertex : mayServiceVertexsList) {
			count += serviceVertex.demand;
			serMap1.put(serviceVertex.location, serviceVertex.demand);
		}
		for (ServiceVertex serviceVertex : sPServiceVertexsList) {
			count += serviceVertex.demand;
			serMap2.put(serviceVertex.location, serviceVertex.demand);
		}
		for (ServiceVertex serviceVertex : serviceVertexsList) {
			count += serviceVertex.demand;
			serMap2.put(serviceVertex.location, serviceVertex.demand);
		}
		for (ConsumerVertex cv : consumerVertexList) {
			count2 += cv.demand;
		}
		System.out.println(count);
		System.out.println(count2);
		
		sortMap(serMap1);
		returnOut.add(serMap1);
		returnOut.add(serMap2);
		return returnOut;
	}
	
	public static void sortMap(Map<Integer,Integer> Dcount) {  
		 int[] value=new int[Dcount.size()];
		    int[] key=new int[Dcount.size()];
		    int a=0;
		    for(int i : Dcount.keySet()){
		    	key[a]=i;
		    	value[a]=Dcount.get(i);
		    	a++;
		    }
//		    for(int i=0;i<Dcount.size();i++){
//				System.out.println(value[i]);
//			}
		    for(int i=0;i<Dcount.size()-1;i++){
		    	int temp=value[i];
		    	int k=key[i];
		    	for(int j=i+1;j<Dcount.size();j++){
		    		if(temp<value[j]){
		    			value[i]=value[j];
		    			value[j]=temp;	    			
		    			temp=value[i];
		    			key[i] =key[j];
		    			key[j] =k;
		    			k=key[i];
		    		}
		    	}
		    }  
      //  Map<Integer,Integer> newMap = new LinkedHashMap<Integer,Integer>(); 
		    Dcount.clear();
        for(int i=0;i<key.length;i++){
        	 Dcount.put(key[i], value[i]);
        }  
       // return newMap;  
    }  

	public static int combineService(FirstGraph graph, List<ServiceVertex> mayServiceVertexsList,
			List<ServiceVertex> serviceVertexsList) {
		/*
		 * 这个方法应该返回值为int作为结果状态码 1：合并成功 2：合并失败，并且过程中更新了一些信息。返回之后需要将参数还原为序列化所保存的参数
		 * 3：合并失败，但是并没有更新任何信息。 4: 所有备选服务器之间都已经完成了两两合并
		 *
		 * 两两合并服务器 对备选服务器列表中的服务器使用最短路径算法，选择距离最短的服务器两两合并
		 * 如果一套路径在比对时短与当前最短路径，但其路径上的链路花费大于一台服务器的成本， 则放弃该路径，当前最短路径保持不变
		 * 新的服务器选在两点之间输出能力最大的网络节点上,且该节点的最大输出能力应该大于原先两个服务器的需求之和
		 * 合并服务器之后，从备选服务器列表中删除合并的两台服务器。加入合并的新服务器 更新图中边的剩余带宽（注意要分上下行）
		 * 完成之后，返回true表示找到还能找到最小路径并且图更新完毕 如果无法找到最小路径，则返回false
		 *
		 * @param graph 图
		 * 
		 * @param mayServiceVertexsList 待选服务器集合
		 * 
		 * @param serviceVertexsList 确定服务器集合
		 * 
		 */
		sort(mayServiceVertexsList);
		int result = 0;
		// 新建一个集合用于存储最小路径。给集合添加30个0作为初始值方便比较
		List<Integer> shortestPath = new ArrayList<Integer>();
		int shortestPathCost = 9999;
		ServiceVertex startServiceVertex = null;
		ServiceVertex endServiceVertex = null;
		for (int i = 0; i < 50; i++) {
			shortestPath.add(0);
		}
		// 使备选服务器列表中的点两两寻找最小路径
		for (int i = 1; i < mayServiceVertexsList.size(); i++) {

			if ((dijkstra(graph, mayServiceVertexsList.get(0).location, mayServiceVertexsList.get(i).location) != null
					&& pathCost(graph,
							dijkstra(graph, mayServiceVertexsList.get(0).location,
									mayServiceVertexsList.get(i).location)) < shortestPathCost)
					&& haveGone[mayServiceVertexsList.get(0).location][mayServiceVertexsList.get(i).location] != 1) {
				shortestPath = dijkstra(graph, mayServiceVertexsList.get(0).location,
						mayServiceVertexsList.get(i).location);
				shortestPathCost = pathCost(graph, shortestPath);
				startServiceVertex = mayServiceVertexsList.get(0);
				// removeId = i;
				endServiceVertex = mayServiceVertexsList.get(i);
			}
		}

		// 如果两两之间都无法找到路径，则合并结束。返回值为4
		if (startServiceVertex == null) {
			return 4;
		}
		// 确定要合并哪两台服务器之后先在haveGone数组中记录。因为不管合并成功还是失败都可以被记录
//		System.out.println("****************************************" + startServiceVertex.location);
//		System.out.println("****************************************" + endServiceVertex.location);
		for (Integer integer : shortestPath) {
			System.out.print(integer + "->");
		}
		haveGone[startServiceVertex.location][endServiceVertex.location] = 1;

		int maxBand = 0;
		int maxBandId = 0;
		// int id = 0; 设置一个id记录新建服务器的位置
		// 1遍历整条路径，选择输出能力最强的点作为新的服务器的备选位置
		for (Integer integer : shortestPath) {
			if (graph.weights[integer] > maxBand) {
				maxBand = graph.weights[integer];
				maxBandId = integer.intValue();
				// id = maxBandId;
			}
		}
		System.out.println(maxBandId);
		// 2计算费用
		int cost = pathCost(graph, shortestPath, maxBandId, startServiceVertex, endServiceVertex);

		// 3如果合并之后链路花费小于一个服务器的价格,并且新服务器的网络节点输出能力大于原来两个服务器的需求之和
		// 更新带宽，修改备选服务器集合
		// 更新带宽时，如果选择的最短路径上的边不能满足消费需求，则先跑满这条边，并在图中删除这条边。
		// 再寻找其他与输入不够的网络节点相连的节点布置服务器，向这个未满足需求的节点继续输送带宽
		// 输入能力小于需求的不能布置服务器，相连边权值小的优先跑满。

		if (cost <= serverCost * 1.1) {
			int place = -1;
			// 计算服务器在路径中是第几个点
			for (int i = 0; i < shortestPath.size(); i++) {
				if (maxBandId == shortestPath.get(i)) {
					place = i;
					break;
				}
			}
			// 计算该条路径上最小负载边的负载
			int minBand = 9999;
			int newBand = 0;
			// 首选对起点到服务器的半条路进行合并
			for (int i = 0; i < place; i++) {
				if (canGive(graph, shortestPath.get(i), shortestPath.get(i + 1)) < minBand) {
					minBand = canGive(graph, shortestPath.get(i), shortestPath.get(i + 1));
				}
			}
			if (minBand != 9999) {// 首选判断起点是不是服务器点。如果是服务器点则不需要进行这一步
				// 如果这条路径能够满足起点带宽需求，则更新整条路上的带宽，并将result设置为1
				if (minBand >= startServiceVertex.demand) {
					newBand = startServiceVertex.demand;
					for (int i = 0; i < place; i++) {
						updataBand(graph, shortestPath.get(i), shortestPath.get(i + 1), startServiceVertex.demand);
					}
					result = 1;
				} else {
					// 当这条路径不能满足带宽需求时，我们先判断路的终点能否满足找到点补足剩下的带宽。
					// 如果可以，我们将先跑满这条路，再到终点处寻找点补足剩余所需带宽
					// 如果不可以，则合并服务器失败，直接返回result = 2；
					newBand = minBand;
					int stillNeed = startServiceVertex.demand - minBand;
					// 截取list并将它逆转
					List<Integer> list = shortestPath.subList(0, place + 1);
					List<Integer> list1 = new ArrayList<>(list);
					Collections.reverse(list1);
					if (findOtherService(graph, list1, stillNeed, mayServiceVertexsList, serviceVertexsList)) {
						// 可以找到点补全带宽需求，更新这条路的带宽
						for (int i = 0; i < place; i++) {
							updataBand(graph, shortestPath.get(i), shortestPath.get(i + 1), minBand);
						}
						result = 1;
					} else {
						// 无法找到点来补足带宽，直接返回result = 2；
						result = 2;
						return result;
					}
				}
			}
			// 现在对后半段路径进行合并
			int minBand2 = 9999;
			int newBand2 = 0;
			for (int i = place; i < shortestPath.size() - 1; i++) {
				// System.out.println(shortestPath.get(i+1) +" + "+
				// shortestPath.get(i));
				if (canGive(graph, shortestPath.get(i + 1), shortestPath.get(i)) < minBand2) {
					minBand2 = canGive(graph, shortestPath.get(i + 1), shortestPath.get(i));
				}
			}
			if (minBand2 != 9999) {
				// 如果这条路径能够满足起点带宽需求，则更新整条路上的带宽，并将result设置为1
				if (minBand2 >= endServiceVertex.demand) {
					newBand2 = endServiceVertex.demand;
					for (int i = place; i < shortestPath.size() - 1; i++) {
						updataBand(graph, shortestPath.get(i + 1), shortestPath.get(i), endServiceVertex.demand);
					}
					result = 1;
				} else {
					// 当这条路径不能满足带宽需求时，我们先判断路的终点能否满足找到点补足剩下的带宽。
					// 如果可以，我们将先跑满这条路，再到终点处寻找点补足剩余所需带宽
					// 如果不可以，则合并服务器失败，直接返回result = 2；
					newBand2 = minBand2;
					int stillNeed = endServiceVertex.demand - minBand2;
					// 截取list
					List<Integer> list2 = shortestPath.subList(place, shortestPath.size());
					if (findOtherService(graph, list2, stillNeed, mayServiceVertexsList, serviceVertexsList)) {
						// 可以找到点补全带宽需求，更新这条路的带宽
						for (int i = place; i < shortestPath.size() - 1; i++) {
							updataBand(graph, shortestPath.get(i + 1), shortestPath.get(i), minBand2);
							result = 1;
						}
					} else {
						// 无法找到点来补足带宽，直接返回result = 2；
						result = 2;
						return result;
					}
				}
			}
			/*
			 * if(minBand == 9999){ minBand = 0; } if(minBand2 == 9999){
			 * minBand2 = 0; }
			 */
			// 两条路径都合并成功之后需要在新的服务器点建立服务器，并把它加入到备选服务器队列中去
			// 如果该点已经是服务器，则直接增加其需求量。反之则新建服务器，需求量为两条子路径的minBand之和
			if (getServiceVertex(maxBandId, mayServiceVertexsList, serviceVertexsList) == null) {
				ServiceVertex serviceVertex = new ServiceVertex(graph, maxBandId, newBand + newBand2);
				mayServiceVertexsList.add(serviceVertex);
				// 更新记录数组
				// refresh(graph, serviceVertex, mayServiceVertexsList,
				// serviceVertexsList);
				// 删除已经合并的服务器
				for (int i = mayServiceVertexsList.size() - 1; i >= 0; i--) {
					if (mayServiceVertexsList.get(i).location == startServiceVertex.location
							|| mayServiceVertexsList.get(i).location == endServiceVertex.location) {
						mayServiceVertexsList.remove(i);
					}
				}
			} else {
				// 如果新的服务器在起点位置
				if (maxBandId == startServiceVertex.location) {
					startServiceVertex.demand += (newBand2);
					for (int i = mayServiceVertexsList.size() - 1; i >= 0; i--) {
						if (mayServiceVertexsList.get(i).location == endServiceVertex.location) {
							mayServiceVertexsList.remove(i);
						}
					}
				} else if (maxBandId == endServiceVertex.location) {
					endServiceVertex.demand += (newBand);
					for (int i = mayServiceVertexsList.size() - 1; i >= 0; i--) {
						if (mayServiceVertexsList.get(i).location == startServiceVertex.location) {
							mayServiceVertexsList.remove(i);
						}
					}
				} else {
					getServiceVertex(maxBandId, mayServiceVertexsList, serviceVertexsList).demand += (newBand
							+ newBand2);
					// refresh(graph, getServiceVertex(maxBandId,
					// mayServiceVertexsList, serviceVertexsList),
					// mayServiceVertexsList, serviceVertexsList);
					for (int i = mayServiceVertexsList.size() - 1; i >= 0; i--) {
						if (mayServiceVertexsList.get(i).location == startServiceVertex.location
								|| mayServiceVertexsList.get(i).location == endServiceVertex.location) {
							mayServiceVertexsList.remove(i);
						}
					}
				}
				// System.out.println(getServiceVertex(maxBandId,
				// mayServiceVertexsList, serviceVertexsList).demand + "+" +
				// (minBand+minBand2));
			}
		} else {
			result = 3;
			System.out.println(cost);
			return result;
		}
		return result;
	}

	public static boolean findOtherService(FirstGraph graph, List<Integer> shortestPath, int demand,
			List<ServiceVertex> mayServiceVertexsList, List<ServiceVertex> serviceVertexsList) {
		/*
		 * 计算路径上的所有边的单位价格之和
		 * 
		 * @param graph 图
		 * 
		 * @param shortestPath 需要补足带宽的路径
		 * 
		 * @param demand 需要在终点补足的带宽
		 * 
		 * @param mayServiceVertexsList 备选服务器集合
		 * 
		 * @param serviceVertexsList 确定服务器集合
		 * 
		 */
		boolean result = false;
		FirstEdge cheapestEdge = null;
		int nowDemand = demand;
		int temp = shortestPath.get(shortestPath.size() - 1);
		int pre = shortestPath.get(shortestPath.size() - 2);
		List<Integer> list = new ArrayList<>();// 建立一个集合储存与终点相连的节点编号
		for (int i = 0; i < graph.struct[shortestPath.get(shortestPath.size() - 1)].size(); i++) {
			if (graph.struct[temp].get(i)[0] != pre) {
				list.add(graph.struct[temp].get(i)[0]);
			}
		}
		// System.out.println(list.size());
		// System.out.println(canGive(graph, 2, 3));
		if (list.size() == 0) {
			return result;
		}

		int time = 0;

		while (nowDemand != 0) {
			if (time == 3) {
				break;
			}
			time++;
			int maxWeight = 0;
			int newServiceVertexId = -1;
			// 开始遍历list，找到与该节点相连的最大权值的点
			for (int i = 0; i < list.size(); i++) {
				if (getEdge(graph, temp, list.get(i)) != null && graph.weights[list.get(i)] > maxWeight
						&& canGive(graph, temp, list.get(i)) != 0) {
					cheapestEdge = getEdge(graph, temp, list.get(i));
					maxWeight = graph.weights[list.get(i)];
					newServiceVertexId = list.get(i);
				}
			}
			if (newServiceVertexId == -1) {
				// System.out.println("1111111111");
				break;
			}
			if (canGive(graph, temp, newServiceVertexId) >= nowDemand) {
				result = true;
				// 判断该点是否已经作为服务器在队列中。如果是则只需要更新服务器的需求。
				// 如果不存在该服务器则需要新建一个服务器加到队列中去。
				if (getServiceVertex(newServiceVertexId, mayServiceVertexsList, serviceVertexsList) == null) {
					ServiceVertex serviceVertex = new ServiceVertex(graph, newServiceVertexId, nowDemand);
					updataBand(graph, temp, newServiceVertexId, nowDemand);
					nowDemand = 0;
					// System.out.println("加入"+ newServiceVertexId);
					mayServiceVertexsList.add(serviceVertex);
					// refresh(graph, serviceVertex, mayServiceVertexsList,
					// serviceVertexsList);
				} else {
					// System.out.println("选择"+ newServiceVertexId);
					getServiceVertex(newServiceVertexId, mayServiceVertexsList, serviceVertexsList).demand += nowDemand;
					// refresh(graph, getServiceVertex(newServiceVertexId,
					// mayServiceVertexsList, serviceVertexsList),
					// mayServiceVertexsList, serviceVertexsList);
					updataBand(graph, temp, newServiceVertexId, nowDemand);
					nowDemand = 0;
				}
			} else {
				// System.out.println("能给" + canGive(graph, temp,
				// newServiceVertexId));
				if (getServiceVertex(newServiceVertexId, mayServiceVertexsList, serviceVertexsList) == null) {
					ServiceVertex serviceVertex = new ServiceVertex(graph, newServiceVertexId,
							canGive(graph, temp, newServiceVertexId));
					mayServiceVertexsList.add(serviceVertex);
					// refresh(graph, serviceVertex, mayServiceVertexsList,
					// serviceVertexsList);
					nowDemand -= canGive(graph, temp, newServiceVertexId);
					updataBand(graph, temp, newServiceVertexId, canGive(graph, temp, newServiceVertexId));
					// System.out.println("*加入"+ newServiceVertexId);
					// System.out.println("剩余" + nowDemand);
				} else {
					// System.out.println("能给" + canGive(graph, temp,
					// newServiceVertexId));
					// System.out.println("*选择"+ newServiceVertexId);
					getServiceVertex(newServiceVertexId, mayServiceVertexsList, serviceVertexsList).demand += canGive(
							graph, temp, newServiceVertexId);
					// refresh(graph, getServiceVertex(newServiceVertexId,
					// mayServiceVertexsList, serviceVertexsList),
					// mayServiceVertexsList, serviceVertexsList);
					nowDemand -= canGive(graph, temp, newServiceVertexId);
					updataBand(graph, temp, newServiceVertexId, canGive(graph, temp, newServiceVertexId));
					// System.out.println("剩余" + nowDemand);
				}
			}
		}
		return result;
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

	// 刷新二维数组，解决第一次不能合并，新增的第二次却可以合并的情况
	public static void refresh(FirstGraph graph, ServiceVertex serviceVertex, List<ServiceVertex> mayServiceVertexsList,
			List<ServiceVertex> serviceVertexs) {
		// 刷新数组
		int location = serviceVertex.location;
		for (int i = 0; i < mayServiceVertexsList.size(); i++) {
			haveGone[location][mayServiceVertexsList.get(i).location] = 0;
			haveGone[mayServiceVertexsList.get(i).location][location] = 0;
		}
		for (int i = 0; i < serviceVertexs.size(); i++) {
			haveGone[location][serviceVertexs.get(i).location] = 0;
			haveGone[serviceVertexs.get(i).location][location] = 0;
		}
	}

	public static void deleteImportToServiceVertex(FirstGraph graph, ServiceVertex serviceVertex,
			List<ServiceVertex> mayServiceVertexsList, List<ServiceVertex> serviceVertexs) {
		// 让输入服务器点的带宽为0
		int location = serviceVertex.location;
		for (int i = 0; i < graph.struct[serviceVertex.location].size(); i++) {
			if (location < graph.struct[location].get(i)[0]) {
				if (getEdge(graph, graph.struct[location].get(i)[1]).usedBand < 0) {
					// 如果给该点输入带宽的是一个服务器，则该服务器的需求减小usedBand的绝对值
					if (getServiceVertex(graph.struct[location].get(i)[0], mayServiceVertexsList,
							serviceVertexs) != null) {
						getServiceVertex(graph.struct[location].get(i)[0], mayServiceVertexsList,
								serviceVertexs).demand += getEdge(graph, graph.struct[location].get(i)[1]).usedBand;
					}
					serviceVertex.demand -= getEdge(graph, graph.struct[location].get(i)[1]).usedBand;
					getEdge(graph, graph.struct[location].get(i)[1]).usedBand = 0;

				}
			} else {
				if (getEdge(graph, graph.struct[location].get(i)[1]).usedBand > 0) {
					if (getServiceVertex(graph.struct[location].get(i)[0], mayServiceVertexsList,
							serviceVertexs) != null) {
						getServiceVertex(graph.struct[location].get(i)[0], mayServiceVertexsList,
								serviceVertexs).demand -= getEdge(graph, graph.struct[location].get(i)[1]).usedBand;
					}
					serviceVertex.demand += getEdge(graph, graph.struct[location].get(i)[1]).usedBand;
					getEdge(graph, graph.struct[location].get(i)[1]).usedBand = 0;
				}
			}
		}
	}

	public static int canGive(FirstGraph graph, int needPoint, int givePoint) {
		// 计算一个节点到另一个节点的输出能力
		int result = 0;
		FirstEdge edge = null;
		if (needPoint < givePoint) {
			edge = getEdge(graph, needPoint, givePoint);
			result = edge.maxBand + edge.usedBand;
		} else {
			edge = getEdge(graph, givePoint, needPoint);
			result = edge.maxBand - edge.usedBand;
		}
		return result;
	}

	public static void updataBand(FirstGraph graph, int needPoint, int givePoint, int giveBand) {
		/*
		 * 该方法使用时必须保证giveBand小于等于两点之间的供给能力
		 */
		FirstEdge edge = null;
		if (needPoint < givePoint) {
			edge = getEdge(graph, needPoint, givePoint);
			edge.usedBand -= giveBand;
		} else {
			edge = getEdge(graph, givePoint, needPoint);
			edge.usedBand += giveBand;
		}
	}

	public static int pathCost(FirstGraph graph, List<Integer> pathList, int ServiceVertexId,
			ServiceVertex startServiceVertex, ServiceVertex endServiceVertex) {
		/*
		 * 计算路径上的所有边的单位价格之和
		 * 
		 * @param graph 图
		 * 
		 * @param pathList 需要计算的路径
		 * 
		 * @param ServiceVertexId 该线路上选择的新的备选服务器
		 * 
		 */
		int cost = 0;
		int currentDemand = startServiceVertex.demand;
		for (int i = 0; i < pathList.size() - 1; i++) {
			if (pathList.get(i) == ServiceVertexId) {
				currentDemand = endServiceVertex.demand;
			}
			for (int j = 0; j < graph.struct[pathList.get(i)].size(); j++) {
				if (pathList.get(i + 1) == graph.struct[pathList.get(i)].get(j)[0]) {
					cost += currentDemand * getEdge(graph, graph.struct[pathList.get(i)].get(j)[1]).price;
					break;
				}
			}
		}
		return cost;
	}

	public static int pathCost(FirstGraph graph, List<Integer> pathList) {
		/*
		 * 计算路径上的所有边的单位价格之和
		 * 
		 * @param graph 图
		 * 
		 * @param pathList 需要计算的路径
		 * 
		 */
		int cost = 0;

		for (int i = 0; i < pathList.size() - 1; i++) {
			for (int j = 0; j < graph.struct[pathList.get(i)].size(); j++) {
				if (graph.struct[pathList.get(i)].get(j)[0] == pathList.get(i + 1)) {
					cost += getEdge(graph, graph.struct[pathList.get(i)].get(j)[1]).price;
					break;
				}
			}
		}

		return cost;
	}

	public static ServiceVertex getServiceVertex(int location, List<ServiceVertex> mayServiceVertexsList,
			List<ServiceVertex> serviceVertexsList) {
		ServiceVertex result = null;
		for (ServiceVertex serviceVertex : serviceVertexsList) {
			if (serviceVertex.location == location) {
				return serviceVertex;
			}
		}

		for (ServiceVertex serviceVertex : mayServiceVertexsList) {
			if (serviceVertex.location == location) {
				return serviceVertex;
			}
		}
		return result;
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

	public static int computeVertexInBand(FirstGraph graph, int vertexNum) {
		/*
		 * 计算一个节点的剩余输入能力
		 * 
		 * @param graph 图
		 * 
		 * @param vertexNum 所计算的节点的Id号 遍历该节点的边。如果边的另一个节点Id小于vertexNum
		 * ，则剩余输入为剩余上行带宽。 反之为剩余下行带宽。
		 * 
		 */
		int vertexInBand = 0;
		for (int i = 0; i < graph.struct[vertexNum].size(); i++) {
			if (vertexNum < graph.struct[vertexNum].get(i)[0]) {
				vertexInBand += getEdge(graph, graph.struct[vertexNum].get(i)[1]).maxBand
						+ getEdge(graph, graph.struct[vertexNum].get(i)[1]).usedBand;
			} else {
				vertexInBand += getEdge(graph, graph.struct[vertexNum].get(i)[1]).maxBand
						- getEdge(graph, graph.struct[vertexNum].get(i)[1]).usedBand;
			}
		}
		return vertexInBand;
	}

	public static int computeVertexBand(FirstGraph graph, int vertexNum) {
		/*
		 * 计算一个节点的剩余输出能力
		 * 
		 * @param graph 图
		 * 
		 * @param vertexNum 所计算的节点的Id号 遍历该节点的边。如果边的另一个节点Id小于vertexNum
		 * ，则剩余输出为剩余下行带宽。 反之为剩余上行带宽。
		 * 
		 */
		int vertexBand = 0;
		for (int i = 0; i < graph.struct[vertexNum].size(); i++) {
			if (vertexNum < graph.struct[vertexNum].get(i)[0]) {
				vertexBand += getEdge(graph, graph.struct[vertexNum].get(i)[1]).maxBand
						- getEdge(graph, graph.struct[vertexNum].get(i)[1]).usedBand;
			} else {
				vertexBand += getEdge(graph, graph.struct[vertexNum].get(i)[1]).maxBand
						+ getEdge(graph, graph.struct[vertexNum].get(i)[1]).usedBand;
			}
		}
		return vertexBand;
	}

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

	public static List<Integer> dijkstra(FirstGraph graph, int startPoint, int endPoint) {
		/*
		 * 求两个节点之间的最短路径，将单价作为链路权值 原理是dijkstra算法,时间复杂度O(VlogV),V表示节点数目
		 * 
		 * @param graph 图
		 * 
		 * @param startPoint 起点
		 * 
		 * @param endPoint 终点
		 * 
		 * @return List<Integer> 路径中的节点坐标集，注意是反向的，即从终点到起点
		 */
		class Heap {
			/*
			 * Heap类，表示最小堆，用于寻找当前最短路径对应的节点
			 */

			public Vector<FirstVertex> vertexList; // 节点向量

			Heap() { // 构造函数1
				this.vertexList = new Vector<FirstVertex>();
			}

			Heap(FirstVertex[] vertexList) {
				/*
				 * 构造函数2
				 * 
				 * @vertexList 节点列表 这个函数在这里没有作用
				 */
				this.vertexList = new Vector<FirstVertex>();
				for (FirstVertex vertex : vertexList) {
					insert(vertex);
				}
			}

			public void insert(FirstVertex ver) {
				/*
				 * 将元素加入到最小堆
				 * 
				 * @param ver 待加入的元素
				 * 
				 */
				int hole = vertexList.size();
				vertexList.add(ver);
				for (; hole > 0 && ver.dv < vertexList.get((hole - 1) / 2).dv; hole = (hole - 1) / 2) {
					vertexList.set(hole, vertexList.get((hole - 1) / 2));
				}
				vertexList.set(hole, ver);
			}

			public FirstVertex deleteMin() {
				/*
				 * 删除最小的元素并返回
				 * 
				 */
				if (vertexList.isEmpty()) {
					return null;

				}
				FirstVertex forReturn = vertexList.get(0);
				FirstVertex tmp = vertexList.lastElement();
				vertexList.set(0, tmp);
				int hole = 0;
				int child;
				for (; hole * 2 + 1 < vertexList.size(); hole = child) {
					child = hole * 2 + 1;
					if (child != vertexList.size() - 1 && vertexList.get(child).dv > vertexList.get(child + 1).dv) {
						child++;
					}
					if (vertexList.get(child).dv < tmp.dv) {
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
		Deploy deploy = new Deploy();
		FirstVertex[] vertexList = new FirstVertex[graph.netVertexNum];
		for (int i = 0; i < graph.netVertexNum; i++) {
			vertexList[i] = new FirstVertex(i, false, INFINITY, -1);
		}
		vertexList[startPoint].dv = 0;
		Heap heap = new Heap();
		heap.insert(vertexList[startPoint]);

		while (true) {
			FirstVertex forProcess;
			while (true) {
				forProcess = heap.deleteMin();
				if (forProcess == null) {
					return null;
				}
				if (!forProcess.known) {
					break;
				}
			}
			forProcess.known = true;
			for (int i = 0; i < graph.struct[forProcess.id].size(); i++) {
				FirstVertex update = vertexList[graph.struct[forProcess.id].get(i)[0]];
				FirstEdge line = graph.edgeList[graph.struct[forProcess.id].get(i)[1]];
				if (!update.known && update.dv > forProcess.dv + line.price) {
					update.dv = forProcess.dv + line.price;
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
		// System.out.println(new StringBuilder(resultList.toString()));
		/*
		 * String resultString = "Shortest path from Vertex" +
		 * String.valueOf(startPoint) + " to Vertex" + String.valueOf(endPoint)
		 * + " :"; resultString += "[Vertex " + String.valueOf(startPoint) +
		 * "]"; for(int i = resultList.size() - 2; i >= 0 ; i--) { resultString
		 * += "[Vertex " + resultList.get(i) + "]"; } resultString += "[Vertex "
		 * + String.valueOf(endPoint) + "]"; System.out.println(resultString);
		 */
		// System.out.println("asdfg");
		return resultList;
	}
}
