package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.List;

public class Path {
	/**
	 * @param pathList ·��Ƭ��
	 * @param start ���
	 * @param end �յ�
	 */
	public List<OnePath> pathList;
	public int start;
	public int end;
	
	public Path(int start, int end) {
		/**
		 * ���캯��
		 */
		this.start = start;
		this.end =end;
		this.pathList = new ArrayList<OnePath>();
	}
	
	public Path(int start, int end, List<OnePath> pathList) {
		/**
		 * ���캯��
		 * @param pathList ·��Ƭ��
		 */
		this(start, end);
		for(OnePath onePath: pathList) {
			this.addPath(onePath);//���ν�·��Ƭ�μ���
		}
	}
	
	public void addPath(OnePath onepath) {
		/**
		 * ����һ��·��Ƭ��
		 */
		if(pathList.isEmpty()) {//���·��Ƭ����û��·����ֱ�Ӽ��뷵�ؼ���
			pathList.add(onepath);
			return;
		}
		int i = 0;//�����������е�ǰ·�������Ƿ���������߷������
		for(; i < pathList.size(); i++) {
			OnePath thisOne = pathList.get(i);
			if(onepath.startPoint == thisOne.startPoint && onepath.endPoint == thisOne.endPoint) {//����
				thisOne.band += onepath.band;
				return;
			} else if(onepath.startPoint == thisOne.endPoint && onepath.endPoint == thisOne.startPoint) {//����
				thisOne.band -= onepath.band;
				return;
			}
		}
		if(i == pathList.size()) {//δ����������߷��������ֱ�Ӽ��뷵��
			pathList.add(onepath);
		}
	}
	
	public int sumFlow() {
		/**
		 ** ��ȡ·����������
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
		 * ���ת���������ǳ��ؼ�
		 * @return ���ؽ���������ǿ�getAllPathʵ�ֵ�
		 */
		SimpleGraph simple = new SimpleGraph(this);
		return simple.getAllPath(start, end);
	}	
}
