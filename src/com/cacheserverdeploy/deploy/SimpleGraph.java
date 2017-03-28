package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

class SimpleGraph {
	/**
	 * ��ʾһ���򵥵�ͼ����ʵ����Ӧ�ú�Graph���࣬���Կ�����Graph�̳д���
	 * @param INFINITY ��̬������ʾ�����
	 * @param struct ���ڽӱ��ʾͼ�ṹ����ʽΪ��ֵ�ԣ���Ϊ�ڵ�ı�ţ�ֵͬ���Ǽ�ֵ�Ե���ʽ������ʾ�õ�����Ľڵ㣬ֱֵ����������֮��ı�
	 * @param pointMap ��ʾͼ�еĽڵ㣬��Ϊ�ڵ��ţ�ֵΪ�ڵ㱾��
	 */
	
	private static final int INFINITY = 9999;
	Map<Integer, HashMap<Integer, OnePath>> struct;
	Map<Integer, Point> pointMap;
	
	class Point {
		/**
		 * �ڲ��࣬��ʾ�ڵ�
		 * @param id �ڵ�id
		 * @param dist ����
		 * @param path ·���ϸõ��ǰһ���ڵ��id
		 */
		public int id;
		public int dist;
		public int path;
		
		public Point(int id, int dist, int path) {
			this.id = id;
			this.dist = dist;
			this.path = path;
		}
	}

	public SimpleGraph(Path path) {
		/**
		 * ���캯��
		 * @param path ·����ͼ
		 */
		this.struct = new HashMap<Integer, HashMap<Integer, OnePath>>();//��ʼ��struct
		this.pointMap = new HashMap<Integer, Point>();//��ʼ��pointMap
		List<OnePath> pathList = path.pathList;//��ȡ·��
		for(int i = 0; i < pathList.size(); i++) {
			OnePath present = pathList.get(i);//ȡ������·��
			if(!struct.containsKey(present.startPoint)) {//��ʼ�����
				struct.put(present.startPoint, new HashMap<Integer, OnePath>());
				pointMap.put(present.startPoint, new Point(present.startPoint, INFINITY, -1));
			}
			
			if(!struct.containsKey(present.endPoint)) {//��ʼ���յ�
				struct.put(present.endPoint, new HashMap<Integer, OnePath>());
				pointMap.put(present.endPoint, new Point(present.endPoint, INFINITY, -1));
			}
			struct.get(present.startPoint).put(present.endPoint, present);//��ͼ
		}
	}
	
	public SimpleGraph(List<Edge> path) {
		/**
		 * ���캯����ԭ���֮ǰһ��
		 */
		this.struct = new HashMap<Integer, HashMap<Integer, OnePath>>();
		this.pointMap = new HashMap<Integer, Point>();
		for(int i = 0; i < path.size(); i++) {
			OnePath present = new OnePath(path.get(i).startPoint, path.get(i).endPoint, path.get(i).band);
			if(!struct.containsKey(present.startPoint)) {
				struct.put(present.startPoint, new HashMap<Integer, OnePath>());
				pointMap.put(present.startPoint, new Point(present.startPoint, INFINITY, -1));
			}
			
			if(!struct.containsKey(present.endPoint)) {
				struct.put(present.endPoint, new HashMap<Integer, OnePath>());
				pointMap.put(present.endPoint, new Point(present.endPoint, INFINITY, -1));
			}
			struct.get(present.startPoint).put(present.endPoint, present);
		}
	}
	
	public List<Integer> bfs(int start, int end) {
		/**
		 * ʹ�ù�����ȱ�������ȡ���·��
		 * @param start ���
		 * @param end �յ�
		 * @return ·����ţ���������յ�
		 */
		List<Integer> output = new ArrayList<Integer>();//��ʼ�����
		Queue<Point> queue = new LinkedList<Point>();//��ʼ������
		for(Integer key: pointMap.keySet()) {
			pointMap.put(key, new Point(key, INFINITY, -1));//��ʼ�����нڵ�Ĳ�����-1��ʾ�ýڵ�Ŀǰû��ǰ��
		}
		
		pointMap.get(start).dist = 0;//���ľ����ʼ��Ϊ0
		queue.offer(pointMap.get(start));//���������
		//���зǿջ����յ�δ����ʱ���е���
		while(!queue.isEmpty() && pointMap.get(end).dist == INFINITY) {
			Point p = queue.poll();	//���׳���
			HashMap<Integer, OnePath> linkedPoint = struct.get(p.id);//��ȡ�ýڵ���ڽӱ�
			for(Integer key: linkedPoint.keySet()) {
				if(pointMap.get(key).dist == INFINITY) {//���ýڵ�δ֪���õ����
					pointMap.get(key).dist = pointMap.get(p.id).dist + 1;//��������1
					pointMap.get(key).path = p.id;//���ǰ��
					queue.offer(pointMap.get(key));//���
				}
			}
		}
		int pv = end;
		while(pv != -1) { //��·��������output,����pvΪ-1,��������-1ʱ��ʾ����
			output.add(pv);
			pv = pointMap.get(pv).path;
		}
		if(output.size() == 1) {//���ֻ���յ㣬��ʾ������յ㲻����·��������null
			return null;
		}
		Collections.reverse(output);//�����Ϊ����
		return output;
	}
	
	public List<OnePath> getOneSetByBFS(int server) {
		/**
		 * ʹ�ù�����ȱ����ҵ���һ�����������йص�����·��
		 * @param server �������ڵ��Ӧ����ڵ�ı��
		 * @return ����·��
		 */
		List<OnePath> output = new ArrayList<OnePath>();
		Queue<Point> queue = new LinkedList<Point>();
		for(Integer key: pointMap.keySet()) {
			pointMap.put(key, new Point(key, INFINITY, -1));
		}
		
		pointMap.get(server).dist = 0;
		queue.offer(pointMap.get(server));
		
		while(!queue.isEmpty()) {
			Point p = queue.poll();
			HashMap<Integer, OnePath> linkedPoint = struct.get(p.id);
			for(Integer key: linkedPoint.keySet()) {//Ŀ��˴�Ҫ�޸�
				output.add(linkedPoint.get(key));
				queue.offer(pointMap.get(key));
//				if(pointMap.get(key).dist == INFINITY) {
//					pointMap.get(key).dist = pointMap.get(p.id).dist + 1;
//					output.add(linkedPoint.get(key));
//					queue.offer(pointMap.get(key));
//					System.out.println(pointMap.get(key).id);
//					/*if(!custom.contains(linkedPoint.get(key).endPoint)) {
//						queue.offer(pointMap.get(key));
//					}*/
//				}
			}
		}
		return new ArrayList<OnePath>(new HashSet<OnePath>(output));//ȥ��
	}
	
	public List<List<Integer>> getAllPath(int start, int end) {
		/**
		 * һ��һ��ȡ����·��������������С�����㷨������Ϲٷ���ʽ�Ľ��
		 * @param start ���
		 * @param end �յ�
		 * @return ����·�������ƶ�ά���飬ÿһ�б�ʾһ��·��
		 */
		int demand = 0;
		List<List<Integer>> output = new ArrayList<List<Integer>>();
		for(Integer i : struct.keySet()) {
			Map<Integer, OnePath> thisPoint = struct.get(i);
			for(Integer key : thisPoint.keySet()) {
				OnePath o = thisPoint.get(key);
				if(o.endPoint == end) {
					demand += o.band;
				}
			}
		}//��ȡ�յ��������
		
		while(demand > 0) {//δ�ﵽ����ʱ���е���
			List<Integer> path = bfs(start, end);//������ȱ�������·��
			if(path == null) {
				break;//��·����ʾ�����쳣��ֱ���˳����null
			}
			int band = INFINITY;//���Ĵ���ȡ·���ϵ���С����
			for(int i = 0; i < path.size() - 1; i++) {
				int one = path.get(i);
				int two = path.get(i + 1);
				if(band > struct.get(one).get(two).band) {
					band = struct.get(one).get(two).band;
				}
			}
			
			for(int i = 0; i < path.size() - 1; i++) {//�������ĵĴ������ͼ���Ա�����һ�ε���
				int one = path.get(i);
				int two = path.get(i + 1);
				struct.get(one).get(two).band -= band;
				if(struct.get(one).get(two).band == 0) {//��·���ϵĴ���������ֱ��ɾ��
					struct.get(one).remove(two);
				}
			}
			demand -= band;
			//System.out.println("path: " + path.toString() + " , band: " + String.valueOf(band));
			path.add(band);
			output.add(path);
		}
		return output;
		
	}
}
