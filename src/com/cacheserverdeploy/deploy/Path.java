package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.List;

public class Path {
	/**
	 * @param pathList 路径片段
	 * @param start 起点
	 * @param end 终点
	 */
	public List<OnePath> pathList;
	public int start;
	public int end;
	
	public Path(int start, int end) {
		/**
		 * 构造函数
		 */
		this.start = start;
		this.end =end;
		this.pathList = new ArrayList<OnePath>();
	}
	
	public Path(int start, int end, List<OnePath> pathList) {
		/**
		 * 构造函数
		 * @param pathList 路径片段
		 */
		this(start, end);
		for(OnePath onePath: pathList) {
			this.addPath(onePath);//依次将路径片段加入
		}
	}
	
	public void addPath(OnePath onepath) {
		/**
		 * 加入一个路径片段
		 */
		if(pathList.isEmpty()) {//如果路径片段中没有路径，直接加入返回即可
			pathList.add(onepath);
			return;
		}
		int i = 0;//否则搜索所有当前路径，看是否有正向或者反向情况
		for(; i < pathList.size(); i++) {
			OnePath thisOne = pathList.get(i);
			if(onepath.startPoint == thisOne.startPoint && onepath.endPoint == thisOne.endPoint) {//正向
				thisOne.band += onepath.band;
				return;
			} else if(onepath.startPoint == thisOne.endPoint && onepath.endPoint == thisOne.startPoint) {//反向
				thisOne.band -= onepath.band;
				return;
			}
		}
		if(i == pathList.size()) {//未出现正向或者反向情况，直接加入返回
			pathList.add(onepath);
		}
	}
	
	public int sumFlow() {
		/**
		 ** 获取路径的总流量
		 */
		int output = 0;
		for(OnePath o : pathList) {
			if(o.startPoint == start) {
				output += o.band;
			}
		}
		return output;
	}
	
	public List<List<Integer>> fromPathToString() {
		/**
		 * 结果转换函数，非常关键
		 * @return 返回结果，具体是靠getAllPath实现的
		 */
		SimpleGraph simple = new SimpleGraph(this);
		return simple.getAllPath(start, end);
	}	
}
