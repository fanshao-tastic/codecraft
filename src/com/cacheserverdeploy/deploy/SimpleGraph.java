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
	 * 表示一个简单的图，其实该类应该和Graph类差不多，可以考虑用Graph继承此类
	 * @param INFINITY 静态变量表示无穷大
	 * @param struct 用邻接表表示图结构，形式为键值对，键为节点的标号，值同样是键值对的形式，键表示该点延伸的节点，值直接是两个点之间的边
	 * @param pointMap 表示图中的节点，键为节点编号，值为节点本身
	 */
	
	private static final int INFINITY = 9999;
	Map<Integer, HashMap<Integer, OnePath>> struct;
	Map<Integer, Point> pointMap;
	
	class Point {
		/**
		 * 内部类，表示节点
		 * @param id 节点id
		 * @param dist 距离
		 * @param path 路径上该点的前一个节点的id
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
		 * 构造函数
		 * @param path 路径构图
		 */
		this.struct = new HashMap<Integer, HashMap<Integer, OnePath>>();//初始化struct
		this.pointMap = new HashMap<Integer, Point>();//初始化pointMap
		List<OnePath> pathList = path.pathList;//提取路径
		for(int i = 0; i < pathList.size(); i++) {
			OnePath present = pathList.get(i);//取出单条路径
			if(!struct.containsKey(present.startPoint)) {//初始化起点
				struct.put(present.startPoint, new HashMap<Integer, OnePath>());
				pointMap.put(present.startPoint, new Point(present.startPoint, INFINITY, -1));
			}
			
			if(!struct.containsKey(present.endPoint)) {//初始化终点
				struct.put(present.endPoint, new HashMap<Integer, OnePath>());
				pointMap.put(present.endPoint, new Point(present.endPoint, INFINITY, -1));
			}
			struct.get(present.startPoint).put(present.endPoint, present);//构图
		}
	}
	
	public SimpleGraph(List<Edge> path) {
		/**
		 * 构造函数，原理和之前一样
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
		 * 使用广度优先遍历法获取最短路径
		 * @param start 起点
		 * @param end 终点
		 * @return 路径编号，从起点至终点
		 */
		List<Integer> output = new ArrayList<Integer>();//初始化输出
		Queue<Point> queue = new LinkedList<Point>();//初始化队列
		for(Integer key: pointMap.keySet()) {
			pointMap.put(key, new Point(key, INFINITY, -1));//初始化所有节点的参数。-1表示该节点目前没有前驱
		}
		
		pointMap.get(start).dist = 0;//起点的距离初始化为0
		queue.offer(pointMap.get(start));//起点加入队列
		//队列非空或者终点未到达时进行迭代
		while(!queue.isEmpty() && pointMap.get(end).dist == INFINITY) {
			Point p = queue.poll();	//队首出队
			HashMap<Integer, OnePath> linkedPoint = struct.get(p.id);//获取该节点的邻接表
			for(Integer key: linkedPoint.keySet()) {
				if(pointMap.get(key).dist == INFINITY) {//若该节点未知将该点入队
					pointMap.get(key).dist = pointMap.get(p.id).dist + 1;//距离自增1
					pointMap.get(key).path = p.id;//标记前驱
					queue.offer(pointMap.get(key));//入队
				}
			}
		}
		int pv = end;
		while(pv != -1) { //将路径反向导入output,起点的pv为-1,当搜索到-1时表示结束
			output.add(pv);
			pv = pointMap.get(pv).path;
		}
		if(output.size() == 1) {//结果只有终点，表示起点与终点不存在路径，返回null
			return null;
		}
		Collections.reverse(output);//反向变为正向
		return output;
	}
	
	public List<OnePath> getOneSetByBFS(int server) {
		/**
		 * 使用广度优先遍历找到与一个服务器点有关的所有路径
		 * @param server 服务器节点对应网络节点的编号
		 * @return 所有路径
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
			for(Integer key: linkedPoint.keySet()) {//目测此处要修改
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
		return new ArrayList<OnePath>(new HashSet<OnePath>(output));//去重
	}
	
	public List<List<Integer>> getAllPath(int start, int end) {
		/**
		 * 一对一获取所有路径，针对最大流最小费用算法输出符合官方格式的结果
		 * @param start 起点
		 * @param end 终点
		 * @return 多条路径，类似二维数组，每一行表示一条路径
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
		}//获取终点的总需求
		
		while(demand > 0) {//未达到需求时进行迭代
			List<Integer> path = bfs(start, end);//广度优先遍历搜索路径
			if(path == null) {
				break;//无路径表示出现异常，直接退出输出null
			}
			int band = INFINITY;//消耗带宽取路径上的最小带宽
			for(int i = 0; i < path.size() - 1; i++) {
				int one = path.get(i);
				int two = path.get(i + 1);
				if(band > struct.get(one).get(two).band) {
					band = struct.get(one).get(two).band;
				}
			}
			
			for(int i = 0; i < path.size() - 1; i++) {//根据消耗的带宽更新图，以便于下一次迭代
				int one = path.get(i);
				int two = path.get(i + 1);
				struct.get(one).get(two).band -= band;
				if(struct.get(one).get(two).band == 0) {//若路径上的带宽消耗完直接删除
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
