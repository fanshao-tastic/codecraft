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
	 * ����Ҫ��ɵ���� <������ϸ����>
	 * 
	 * @param graphContent
	 *            ������Ϣ�ļ�
	 * @return [����˵��] ��������Ϣ
	 * @see [�ࡢ��#��������#��Ա]
	 */
	private static int INFINITY = 9999; // ��̬����,���ڱ�ʾ����
	private static int serverCost;
	private static int[][] haveGone;

	public static List<Map<Integer, Integer>> Firstdeploy(String[] graphContent) {
		/** do your work here **/

		String[] num = graphContent[0].split(" ");
		int netVertexNum = Integer.parseInt(num[0]); // ����ڵ���Ŀ
		int edgeNum = Integer.parseInt(num[1]); // ��·��Ŀ
		int consumerVertexNum = Integer.parseInt(num[2]); // ���ѽڵ���Ŀ
		haveGone = new int[netVertexNum][netVertexNum];// ��̬�������ڼ�¼����֮���Ƿ��Ѿ����Թ��ϲ�
		serverCost = Integer.parseInt(graphContent[2]); // �������۸�

		FirstEdge[] edgeList = new FirstEdge[edgeNum]; // Edge����
		for (int i = 0; i < edgeNum; i++) {
			edgeList[i] = new FirstEdge(i, graphContent[4 + i]); // �洢��·��Ϣ
		}

		ConsumerVertex[] consumerVertexList = new ConsumerVertex[consumerVertexNum]; // ���ѽڵ�����
		for (int i = 0; i < consumerVertexNum; i++) {
			consumerVertexList[i] = new ConsumerVertex(graphContent[5 + edgeNum + i]); // �洢���ѽڵ���Ϣ
		}
			
		Set<Integer> connected = new HashSet<Integer>();
		for (int i = 0; i < consumerVertexList.length; i++) {
			connected.add(consumerVertexList[i].connectedVertex);
		}
		FirstGraph firstgraph = new FirstGraph(netVertexNum, edgeList, connected); // ��ͼת��Ϊ�ڽӱ�Ľṹ
		
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
		
		
		List<ServiceVertex> serviceVertexsList = new ArrayList<>();// �������ڵ�����
		List<ServiceVertex> mayServiceVertexsList = new ArrayList<>();// �����Ƿ������������ͨ���ݹ�õ����ķ������ڵ�
		List<ServiceVertex> sPServiceVertexsList = new ArrayList<>();
		// ����ͼ��array���飬�ж��Ƿ����������
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
		// ������
//		int ALL_DEMAND = 0;
//		for (int i = 0; i < consumerVertexList.length; i++) {
//			ALL_DEMAND += consumerVertexList[i].demand;
//		}

		// �������ѽڵ㣬
		for (int i = 0; i < consumerVertexList.length; i++) {
			/*
			 * �жϸ����ѽڵ����ӵ��Ƿ�Ϊ��һ·���� ����ǵ�һ·�����Ҹ�·����СֵС������
			 * ���������ѽڵ�����������ڵ�ֱ�Ӳ��������������ɾ��������һ·����
			 * ������ѽڵ������ӵ�����ڵ㲻�ǵ�һ·�����������ܽ��ܵ�����������С������ֵ�� ��Ҳ��������ڵ�����Ϊ�������ڵ㡣
			 * 
			 */
			int minBand = 9999;
			boolean first = true;
			int pre = -1;
			int vertexNum = consumerVertexList[i].connectedVertex;
			// ��ѡ�������ѽڵ�����������ڵ��ϲ��������
			ServiceVertex serviceVertex = new ServiceVertex(firstgraph, vertexNum, consumerVertexList[i].demand);
			/*
			 * ���ѽڵ���������100��ʱ��ֱ�Ӳ��÷�����
			 */
			if (consumerVertexList[i].demand >= 100) {
				System.out.println("------------" + vertexNum);
				// sPServiceVertexsList.add(serviceVertex);
				serviceVertexsList.add(serviceVertex);
				continue;
			}
			// �ж��Ƿ�Ϊ����·���������first�㣬���ıߵ���ĿΪ1.�������first�㣬��Ӧ��Ϊ2(Ҳ�п�����3��4��5.��).
			while (((firstgraph.struct[vertexNum].size() == 1) && (first))
					|| ((firstgraph.struct[vertexNum].size() == 2) && (!first))) {
				// �������·����ʣ�����С��·��������С������ı�·����С����

				if (first) {
					first = false;
					int edgeId1 = firstgraph.struct[vertexNum].get(0)[1];
					if (getEdge(firstgraph, edgeId1).maxBand < minBand) {
						minBand = getEdge(firstgraph, firstgraph.struct[vertexNum].get(0)[1]).maxBand;
					}
					pre = vertexNum;
					vertexNum = firstgraph.struct[vertexNum].get(0)[0];// ������Ŵ��ݸ��ߵ���һ����������ж��Ƿ��ǵ���·��
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
			// �����һ·����С����С�����ѵ�������ֱ�ӽ������ѽڵ������ķ��������뵽ȷ�������������С�
			// ��ɾ������·��
			if (minBand < consumerVertexList[i].demand) {
				// System.out.println("1111111111" + serviceVertex.location);
				// System.out.println(pre);
				serviceVertexsList.add(serviceVertex);
			}
			// ��������ѽڵ�����������ڵ��ж���·��������·�������ܺ��޷��ﵽ���ѵ�����
			// ��ֱ�ӽ������ѽڵ������ķ��������뵽ȷ�������������С�
			else if (computeVertexInBand(firstgraph, vertexNum, pre) < consumerVertexList[i].demand) {
				serviceVertexsList.add(serviceVertex);
			}
			// ��������������������ϣ��򽫽����������ѽڵ�����������ڵ��ϵķ����������ѡ��������
			// �н�����һ��ɸѡ
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
		// �ڽ��кϲ�ǰ�ȶ�graph������list�������
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
				if(count>5){//ʧ����������100�����˳�����
					break;
				}
				
				if (result == 1) {
					System.out.println("�ϲ��ɹ�");
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
					System.out.println("�ϲ�ʧ�ܷ���2");
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
					System.out.println("�ϲ�ʧ�ܷ���3");
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
			// edge.id + "�ű� �� ���Ϊ" + edge.startPoint + "���յ�Ϊ " + edge.endPoint
			// + "��ʹ�ô���Ϊ" + edge.usedBand);
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
		Map<Integer,Integer> serMap1=new LinkedHashMap<Integer,Integer>();  //����
		Map<Integer,Integer> serMap2=new LinkedHashMap<Integer,Integer>(); //һ���Ƿ�����
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
		 * �������Ӧ�÷���ֵΪint��Ϊ���״̬�� 1���ϲ��ɹ� 2���ϲ�ʧ�ܣ����ҹ����и�����һЩ��Ϣ������֮����Ҫ��������ԭΪ���л�������Ĳ���
		 * 3���ϲ�ʧ�ܣ����ǲ�û�и����κ���Ϣ�� 4: ���б�ѡ������֮�䶼�Ѿ�����������ϲ�
		 *
		 * �����ϲ������� �Ա�ѡ�������б��еķ�����ʹ�����·���㷨��ѡ�������̵ķ����������ϲ�
		 * ���һ��·���ڱȶ�ʱ���뵱ǰ���·��������·���ϵ���·���Ѵ���һ̨�������ĳɱ��� �������·������ǰ���·�����ֲ���
		 * �µķ�����ѡ������֮�����������������ڵ���,�Ҹýڵ������������Ӧ�ô���ԭ������������������֮��
		 * �ϲ�������֮�󣬴ӱ�ѡ�������б���ɾ���ϲ�����̨������������ϲ����·����� ����ͼ�бߵ�ʣ�����ע��Ҫ�������У�
		 * ���֮�󣬷���true��ʾ�ҵ������ҵ���С·������ͼ������� ����޷��ҵ���С·�����򷵻�false
		 *
		 * @param graph ͼ
		 * 
		 * @param mayServiceVertexsList ��ѡ����������
		 * 
		 * @param serviceVertexsList ȷ������������
		 * 
		 */
		sort(mayServiceVertexsList);
		int result = 0;
		// �½�һ���������ڴ洢��С·�������������30��0��Ϊ��ʼֵ����Ƚ�
		List<Integer> shortestPath = new ArrayList<Integer>();
		int shortestPathCost = 9999;
		ServiceVertex startServiceVertex = null;
		ServiceVertex endServiceVertex = null;
		for (int i = 0; i < 50; i++) {
			shortestPath.add(0);
		}
		// ʹ��ѡ�������б��еĵ�����Ѱ����С·��
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

		// �������֮�䶼�޷��ҵ�·������ϲ�����������ֵΪ4
		if (startServiceVertex == null) {
			return 4;
		}
		// ȷ��Ҫ�ϲ�����̨������֮������haveGone�����м�¼����Ϊ���ܺϲ��ɹ�����ʧ�ܶ����Ա���¼
//		System.out.println("****************************************" + startServiceVertex.location);
//		System.out.println("****************************************" + endServiceVertex.location);
		for (Integer integer : shortestPath) {
			System.out.print(integer + "->");
		}
		haveGone[startServiceVertex.location][endServiceVertex.location] = 1;

		int maxBand = 0;
		int maxBandId = 0;
		// int id = 0; ����һ��id��¼�½���������λ��
		// 1��������·����ѡ�����������ǿ�ĵ���Ϊ�µķ������ı�ѡλ��
		for (Integer integer : shortestPath) {
			if (graph.weights[integer] > maxBand) {
				maxBand = graph.weights[integer];
				maxBandId = integer.intValue();
				// id = maxBandId;
			}
		}
		System.out.println(maxBandId);
		// 2�������
		int cost = pathCost(graph, shortestPath, maxBandId, startServiceVertex, endServiceVertex);

		// 3����ϲ�֮����·����С��һ���������ļ۸�,�����·�����������ڵ������������ԭ������������������֮��
		// ���´����޸ı�ѡ����������
		// ���´���ʱ�����ѡ������·���ϵı߲����������������������������ߣ�����ͼ��ɾ�������ߡ�
		// ��Ѱ�����������벻��������ڵ������Ľڵ㲼�÷������������δ��������Ľڵ�������ʹ���
		// ��������С������Ĳ��ܲ��÷�������������ȨֵС������������

		if (cost <= serverCost * 1.1) {
			int place = -1;
			// �����������·�����ǵڼ�����
			for (int i = 0; i < shortestPath.size(); i++) {
				if (maxBandId == shortestPath.get(i)) {
					place = i;
					break;
				}
			}
			// �������·������С���رߵĸ���
			int minBand = 9999;
			int newBand = 0;
			// ��ѡ����㵽�������İ���·���кϲ�
			for (int i = 0; i < place; i++) {
				if (canGive(graph, shortestPath.get(i), shortestPath.get(i + 1)) < minBand) {
					minBand = canGive(graph, shortestPath.get(i), shortestPath.get(i + 1));
				}
			}
			if (minBand != 9999) {// ��ѡ�ж�����ǲ��Ƿ������㡣����Ƿ�����������Ҫ������һ��
				// �������·���ܹ������������������������·�ϵĴ�������result����Ϊ1
				if (minBand >= startServiceVertex.demand) {
					newBand = startServiceVertex.demand;
					for (int i = 0; i < place; i++) {
						updataBand(graph, shortestPath.get(i), shortestPath.get(i + 1), startServiceVertex.demand);
					}
					result = 1;
				} else {
					// ������·�����������������ʱ���������ж�·���յ��ܷ������ҵ��㲹��ʣ�µĴ���
					// ������ԣ����ǽ�����������·���ٵ��յ㴦Ѱ�ҵ㲹��ʣ���������
					// ��������ԣ���ϲ�������ʧ�ܣ�ֱ�ӷ���result = 2��
					newBand = minBand;
					int stillNeed = startServiceVertex.demand - minBand;
					// ��ȡlist��������ת
					List<Integer> list = shortestPath.subList(0, place + 1);
					List<Integer> list1 = new ArrayList<>(list);
					Collections.reverse(list1);
					if (findOtherService(graph, list1, stillNeed, mayServiceVertexsList, serviceVertexsList)) {
						// �����ҵ��㲹ȫ�������󣬸�������·�Ĵ���
						for (int i = 0; i < place; i++) {
							updataBand(graph, shortestPath.get(i), shortestPath.get(i + 1), minBand);
						}
						result = 1;
					} else {
						// �޷��ҵ������������ֱ�ӷ���result = 2��
						result = 2;
						return result;
					}
				}
			}
			// ���ڶԺ���·�����кϲ�
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
				// �������·���ܹ������������������������·�ϵĴ�������result����Ϊ1
				if (minBand2 >= endServiceVertex.demand) {
					newBand2 = endServiceVertex.demand;
					for (int i = place; i < shortestPath.size() - 1; i++) {
						updataBand(graph, shortestPath.get(i + 1), shortestPath.get(i), endServiceVertex.demand);
					}
					result = 1;
				} else {
					// ������·�����������������ʱ���������ж�·���յ��ܷ������ҵ��㲹��ʣ�µĴ���
					// ������ԣ����ǽ�����������·���ٵ��յ㴦Ѱ�ҵ㲹��ʣ���������
					// ��������ԣ���ϲ�������ʧ�ܣ�ֱ�ӷ���result = 2��
					newBand2 = minBand2;
					int stillNeed = endServiceVertex.demand - minBand2;
					// ��ȡlist
					List<Integer> list2 = shortestPath.subList(place, shortestPath.size());
					if (findOtherService(graph, list2, stillNeed, mayServiceVertexsList, serviceVertexsList)) {
						// �����ҵ��㲹ȫ�������󣬸�������·�Ĵ���
						for (int i = place; i < shortestPath.size() - 1; i++) {
							updataBand(graph, shortestPath.get(i + 1), shortestPath.get(i), minBand2);
							result = 1;
						}
					} else {
						// �޷��ҵ������������ֱ�ӷ���result = 2��
						result = 2;
						return result;
					}
				}
			}
			/*
			 * if(minBand == 9999){ minBand = 0; } if(minBand2 == 9999){
			 * minBand2 = 0; }
			 */
			// ����·�����ϲ��ɹ�֮����Ҫ���µķ������㽨�������������������뵽��ѡ������������ȥ
			// ����õ��Ѿ��Ƿ���������ֱ������������������֮���½���������������Ϊ������·����minBand֮��
			if (getServiceVertex(maxBandId, mayServiceVertexsList, serviceVertexsList) == null) {
				ServiceVertex serviceVertex = new ServiceVertex(graph, maxBandId, newBand + newBand2);
				mayServiceVertexsList.add(serviceVertex);
				// ���¼�¼����
				// refresh(graph, serviceVertex, mayServiceVertexsList,
				// serviceVertexsList);
				// ɾ���Ѿ��ϲ��ķ�����
				for (int i = mayServiceVertexsList.size() - 1; i >= 0; i--) {
					if (mayServiceVertexsList.get(i).location == startServiceVertex.location
							|| mayServiceVertexsList.get(i).location == endServiceVertex.location) {
						mayServiceVertexsList.remove(i);
					}
				}
			} else {
				// ����µķ����������λ��
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
		 * ����·���ϵ����бߵĵ�λ�۸�֮��
		 * 
		 * @param graph ͼ
		 * 
		 * @param shortestPath ��Ҫ��������·��
		 * 
		 * @param demand ��Ҫ���յ㲹��Ĵ���
		 * 
		 * @param mayServiceVertexsList ��ѡ����������
		 * 
		 * @param serviceVertexsList ȷ������������
		 * 
		 */
		boolean result = false;
		FirstEdge cheapestEdge = null;
		int nowDemand = demand;
		int temp = shortestPath.get(shortestPath.size() - 1);
		int pre = shortestPath.get(shortestPath.size() - 2);
		List<Integer> list = new ArrayList<>();// ����һ�����ϴ������յ������Ľڵ���
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
			// ��ʼ����list���ҵ���ýڵ����������Ȩֵ�ĵ�
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
				// �жϸõ��Ƿ��Ѿ���Ϊ�������ڶ����С��������ֻ��Ҫ���·�����������
				// ��������ڸ÷���������Ҫ�½�һ���������ӵ�������ȥ��
				if (getServiceVertex(newServiceVertexId, mayServiceVertexsList, serviceVertexsList) == null) {
					ServiceVertex serviceVertex = new ServiceVertex(graph, newServiceVertexId, nowDemand);
					updataBand(graph, temp, newServiceVertexId, nowDemand);
					nowDemand = 0;
					// System.out.println("����"+ newServiceVertexId);
					mayServiceVertexsList.add(serviceVertex);
					// refresh(graph, serviceVertex, mayServiceVertexsList,
					// serviceVertexsList);
				} else {
					// System.out.println("ѡ��"+ newServiceVertexId);
					getServiceVertex(newServiceVertexId, mayServiceVertexsList, serviceVertexsList).demand += nowDemand;
					// refresh(graph, getServiceVertex(newServiceVertexId,
					// mayServiceVertexsList, serviceVertexsList),
					// mayServiceVertexsList, serviceVertexsList);
					updataBand(graph, temp, newServiceVertexId, nowDemand);
					nowDemand = 0;
				}
			} else {
				// System.out.println("�ܸ�" + canGive(graph, temp,
				// newServiceVertexId));
				if (getServiceVertex(newServiceVertexId, mayServiceVertexsList, serviceVertexsList) == null) {
					ServiceVertex serviceVertex = new ServiceVertex(graph, newServiceVertexId,
							canGive(graph, temp, newServiceVertexId));
					mayServiceVertexsList.add(serviceVertex);
					// refresh(graph, serviceVertex, mayServiceVertexsList,
					// serviceVertexsList);
					nowDemand -= canGive(graph, temp, newServiceVertexId);
					updataBand(graph, temp, newServiceVertexId, canGive(graph, temp, newServiceVertexId));
					// System.out.println("*����"+ newServiceVertexId);
					// System.out.println("ʣ��" + nowDemand);
				} else {
					// System.out.println("�ܸ�" + canGive(graph, temp,
					// newServiceVertexId));
					// System.out.println("*ѡ��"+ newServiceVertexId);
					getServiceVertex(newServiceVertexId, mayServiceVertexsList, serviceVertexsList).demand += canGive(
							graph, temp, newServiceVertexId);
					// refresh(graph, getServiceVertex(newServiceVertexId,
					// mayServiceVertexsList, serviceVertexsList),
					// mayServiceVertexsList, serviceVertexsList);
					nowDemand -= canGive(graph, temp, newServiceVertexId);
					updataBand(graph, temp, newServiceVertexId, canGive(graph, temp, newServiceVertexId));
					// System.out.println("ʣ��" + nowDemand);
				}
			}
		}
		return result;
	}

	public static void sort(List<ServiceVertex> list) {
		// �Է���������������
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

	// ˢ�¶�ά���飬�����һ�β��ܺϲ��������ĵڶ���ȴ���Ժϲ������
	public static void refresh(FirstGraph graph, ServiceVertex serviceVertex, List<ServiceVertex> mayServiceVertexsList,
			List<ServiceVertex> serviceVertexs) {
		// ˢ������
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
		// �������������Ĵ���Ϊ0
		int location = serviceVertex.location;
		for (int i = 0; i < graph.struct[serviceVertex.location].size(); i++) {
			if (location < graph.struct[location].get(i)[0]) {
				if (getEdge(graph, graph.struct[location].get(i)[1]).usedBand < 0) {
					// ������õ�����������һ������������÷������������СusedBand�ľ���ֵ
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
		// ����һ���ڵ㵽��һ���ڵ���������
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
		 * �÷���ʹ��ʱ���뱣֤giveBandС�ڵ�������֮��Ĺ�������
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
		 * ����·���ϵ����бߵĵ�λ�۸�֮��
		 * 
		 * @param graph ͼ
		 * 
		 * @param pathList ��Ҫ�����·��
		 * 
		 * @param ServiceVertexId ����·��ѡ����µı�ѡ������
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
		 * ����·���ϵ����бߵĵ�λ�۸�֮��
		 * 
		 * @param graph ͼ
		 * 
		 * @param pathList ��Ҫ�����·��
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
		 * ����һ���ڵ��ʣ����������
		 * 
		 * @param graph ͼ
		 * 
		 * @param vertexNum ������Ľڵ��Id��
		 * 
		 * @param connectedVertexNum ��Ҫ�Ľڵ��������Ľڵ�Id��
		 * �����ýڵ�ıߡ�����ߵ���һ���ڵ�IdС��vertexNum ����ʣ������Ϊʣ�����д��� ��֮Ϊʣ�����д���
		 * ע����������ߵ�����������vertexNum��connectedVertexNum����������ߵ��������
		 * ��Ϊ�������������ڵ��б�����������û���κ������
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
		 * ����һ���ڵ��ʣ����������
		 * 
		 * @param graph ͼ
		 * 
		 * @param vertexNum ������Ľڵ��Id�� �����ýڵ�ıߡ�����ߵ���һ���ڵ�IdС��vertexNum
		 * ����ʣ������Ϊʣ�����д��� ��֮Ϊʣ�����д���
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
		 * ����һ���ڵ��ʣ���������
		 * 
		 * @param graph ͼ
		 * 
		 * @param vertexNum ������Ľڵ��Id�� �����ýڵ�ıߡ�����ߵ���һ���ڵ�IdС��vertexNum
		 * ����ʣ�����Ϊʣ�����д��� ��֮Ϊʣ�����д���
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
		 * ����һ������ͼ�ͱߵ�ID�Ż�ȡ�ߵķ���
		 * 
		 * @param graph ��Ҫ���صı����ڵ�ͼ
		 * 
		 * @param edgeNum �ߵ�ID��
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
		 * ����һ������ͼ�ͱߵ����������ȡ�ߵķ���
		 * 
		 * @param graph ��Ҫ���صı����ڵ�ͼ
		 * 
		 * @param startNum ��㡣�����ǻ���graph.struct[startNum]�в����Ƿ����������
		 * 
		 * @param endNum �ߵ���һ������
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
		 * �������ڵ�֮������·������������Ϊ��·Ȩֵ ԭ����dijkstra�㷨,ʱ�临�Ӷ�O(VlogV),V��ʾ�ڵ���Ŀ
		 * 
		 * @param graph ͼ
		 * 
		 * @param startPoint ���
		 * 
		 * @param endPoint �յ�
		 * 
		 * @return List<Integer> ·���еĽڵ����꼯��ע���Ƿ���ģ������յ㵽���
		 */
		class Heap {
			/*
			 * Heap�࣬��ʾ��С�ѣ�����Ѱ�ҵ�ǰ���·����Ӧ�Ľڵ�
			 */

			public Vector<FirstVertex> vertexList; // �ڵ�����

			Heap() { // ���캯��1
				this.vertexList = new Vector<FirstVertex>();
			}

			Heap(FirstVertex[] vertexList) {
				/*
				 * ���캯��2
				 * 
				 * @vertexList �ڵ��б� �������������û������
				 */
				this.vertexList = new Vector<FirstVertex>();
				for (FirstVertex vertex : vertexList) {
					insert(vertex);
				}
			}

			public void insert(FirstVertex ver) {
				/*
				 * ��Ԫ�ؼ��뵽��С��
				 * 
				 * @param ver �������Ԫ��
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
				 * ɾ����С��Ԫ�ز�����
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
