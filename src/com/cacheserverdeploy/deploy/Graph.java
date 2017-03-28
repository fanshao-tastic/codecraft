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
	 * ͼ��
	 * @param struct ����ͼ�Ľṹ���洢��ʽΪ�ڽӱ�
	 * @param netVertexNum ����ڵ���Ŀ
	 * @param edgeList �߼�
	 * @param serverList ��������
	 * @param consumerList ���ѽڵ�
	 * @param maxCount ������������
	 * @param minCount ���ٷ������ĸ���
	 */
	private static final long serialVersionUID = 872390113109L;
	public Map<Integer, Integer> [] struct; //ArrayList���飬�±�Ϊ�ڵ�id��ArrayList�е�Ԫ��������,��һ����Ϊ�ڵ�id,�ڶ�����Ϊ��·id
	public int netVertexNum;
	public List<Edge> edgeList;
	public List<Integer> serverList;
	public List<ConsumerVertex> consumerList;
	//public Map<Integer,Integer> nodeValueList;
	public int maxCount;
	public int minCount;
	
	public Graph(int netVertexNum, Edge [] edgeList, List<ConsumerVertex> consumerList, List<Integer> serverList) { //���췽��
		this.struct = new HashMap [netVertexNum + 2];
		this.edgeList = new ArrayList<Edge>();
		this.serverList = serverList;
		this.consumerList = consumerList;
		this.maxCount = consumerList.size();
		//this.nodeValueList = setNodeValueList(netVertexNum,edgeList);
		
		this.minCount = 0;
		for(int i = 0; i < edgeList.length;i++) {
			this.edgeList.add(edgeList[i]);//����ԭʼ�ı�
		}
 		this.netVertexNum = netVertexNum + 2;//����ڵ���Ŀ+2���½��������������ڵ㣬һ����ʾ�ܷ�������һ����ʾ�����ѽڵ�
		//��ʼ������
		for(int i = 0; i < netVertexNum + 2; i++) {
			struct[i] = new LinkedHashMap<Integer, Integer>();
		}
		
		for(int i = 0; i < edgeList.length; i++) {
			Edge thisEdge = edgeList[i];
			struct[thisEdge.startPoint].put(thisEdge.endPoint, i);
			struct[thisEdge.endPoint].put(thisEdge.startPoint, i);
		}
		
		Set<Integer> set = new HashSet<Integer>();//�½�һ��Set������contains�������������ѽڵ�����������ڵ�
		for(int i = 0; i < consumerList.size(); i++) {
			set.add(consumerList.get(i).connectedVertex);
		}
		
		int [] serverBand = new int [serverList.size()];//�������ڵ���������,���õ�����ѽڵ����ӣ����������������ѽڵ������

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
		for(int i = 0; i < serverList.size(); i++) {//���ӷ���������ڵ�,��Ϊ���нڵ�ĵ����ڶ�����ֻ�г�û����
			int start = struct.length - 2;
			int end = serverList.get(i);
			id++;//�ߵ�id����1
			int band = serverBand[i];//�����ʾΪ�õ���������
			int price = 0;//����ڵ㣬����Ϊ0
			Edge edge = new Edge(id, start, end, band, price);
			this.edgeList.add(edge);
			struct[struct.length - 2].put(end, id);
		}
		
		for(int i = 0; i < consumerList.size();i++) {//���������ܽ��,��Ϊ���нڵ�����һ��,ֻ����û�г�
			id++;
			int start = consumerList.get(i).connectedVertex;
			int end = struct.length - 1;
			int band = consumerList.get(i).demand;//�����ʾΪ����
			int price = 0;//����ڵ㣬����Ϊ0
			Edge edge = new Edge(id, start, end, band, price);
			this.edgeList.add(edge);
			struct[start].put(end, id);
		}
		
		while(true) {//ɾ����֧
			boolean flag = true;
			for(int i = 0; i < struct.length; i++) {
				if(struct[i].size() == 1) {//���ڵ�֧�����õ���ڽӱ��ÿգ�ɾ��������õ������
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
    		demand += consumerList.get(i).demand;//����������
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
    	minCount++; //ȷ����������������
		
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
		Set<Integer> set = new HashSet<Integer>();//�½�һ��Set������contains�������������ѽڵ�����������ڵ�
		for(int i = 0; i < consumerList.size(); i++) {
			set.add(consumerList.get(i).connectedVertex);
		}
		
		int [] serverBand = new int [serverList.size()];//�������ڵ���������,���õ�����ѽڵ����ӣ����������������ѽڵ������

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
		for(int i = 0; i < serverList.size(); i++) {//���ӷ���������ڵ�,��Ϊ���нڵ�ĵ����ڶ�����ֻ�г�û����
			int start = struct.length - 2;
			int end = serverList.get(i);
			id++;//�ߵ�id����1
			int band = serverBand[i];//�����ʾΪ�õ���������
			int price = 0;//����ڵ㣬����Ϊ0
			Edge edge = new Edge(id, start, end, band, price);
			this.edgeList.add(edge);
			struct[struct.length - 2].put(end, id);
		}
		
		for(int i = 0; i < consumerList.size();i++) {//���������ܽ��,��Ϊ���нڵ�����һ��,ֻ����û�г�
			id++;
			int start = consumerList.get(i).connectedVertex;
			int end = struct.length - 1;
			int band = consumerList.get(i).demand;//�����ʾΪ����
			int price = 0;//����ڵ㣬����Ϊ0
			Edge edge = new Edge(id, start, end, band, price);
			this.edgeList.add(edge);
			struct[start].put(end, id);
		}
		
		while(true) {//ɾ����֧
			boolean flag = true;
			for(int i = 0; i < struct.length; i++) {
				if(struct[i].size() == 1) {//���ڵ�֧�����õ���ڽӱ��ÿգ�ɾ��������õ������
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
    		demand += consumerList.get(i).demand;//����������
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
//    	minCount++; //ȷ����������������
		//return minCount;
	}
	
	private int getOuputAbility(int i) {
		/**
		 * ��ȡ�ڵ���������
		 * @param i �ڵ���
		 * @return �������
		 */
		int result = 0;
		for(Integer key: struct[i].keySet()) {
			result += edgeList.get(struct[i].get(key)).band;
		}
		return result;
	}
	
	public List<Integer> mustServer() {
		/**
		 * ȷ�����Է�����
		 * ���Է������������Ϊ���֣�1.����ĵ�֧�д���һ�����޷���������
		 * 					2.��֧�ܺ��޷���������
		 * @return ���Է������б�
		 */
		List<Integer> output = new ArrayList<Integer>();
		for (int i = 0; i < consumerList.size(); i++) {
			int vertex = consumerList.get(i).connectedVertex;
			int demand = consumerList.get(i).demand;
//			if(demand>100){  //����100��ֱ�Ӳ��÷�����
//				output.add(vertex);
//				continue;
//			}
			Map<Integer, Integer> link = struct[vertex];
			if (link.size() > 2) {//��֧���
				if (getOuputAbility(vertex) - demand < demand) {
					output.add(vertex);
				}
			} else if (link.size() == 2) {//��֧���
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
		 * ʹ�����л��ķ���������ƣ���ƶ���ĳ�Ա����ʵ��Serializable�ӿ�
		 * @return ���ظ���
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
