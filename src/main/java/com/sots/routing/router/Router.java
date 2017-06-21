package com.sots.routing.router;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import com.sots.routing.NetworkNode;
import com.sots.util.data.Triple;
import com.sots.util.data.Tuple;

import net.minecraft.crash.CrashReport;
import net.minecraft.util.EnumFacing;

public class Router {
	NetworkNode start, target;
	private Comparator<NetworkNode> comp = new WeightComparator();
	private PriorityQueue<NetworkNode> open = new PriorityQueue<NetworkNode>(16, comp);
	private Set<NetworkNode> closed = new HashSet<NetworkNode>();
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private Triple<NetworkNode, NetworkNode, ArrayList<Tuple<UUID, EnumFacing>>> routingInfo;
	
	public Triple<NetworkNode, NetworkNode, ArrayList<Tuple<UUID, EnumFacing>>> route(NetworkNode s, NetworkNode t) {
		start = s;
		
		target = t;
		FutureTask<Triple<NetworkNode, NetworkNode, ArrayList<Tuple<UUID, EnumFacing>>>> routingTask =
				new FutureTask<Triple<NetworkNode, NetworkNode, ArrayList<Tuple<UUID, EnumFacing>>>>(
						new Callable<Triple<NetworkNode, NetworkNode, ArrayList<Tuple<UUID, EnumFacing>>>>() {
							@Override
							public Triple<NetworkNode, NetworkNode, ArrayList<Tuple<UUID, EnumFacing>>> call()
									throws Exception {
								start.p_cost=0;
								start.h_Cost(target);
								start.parent=null;
								open.add(start);
								while(!open.isEmpty()) {
									NetworkNode current = open.poll();
									closed.add(current);
									current.getMember().spawnParticle(0f, 1.000f, 0f);
									Thread.sleep(60);
									
									if(current.getId() == target.getId()) {
										//path found
										NetworkNode help = current;
										
										ArrayList<Tuple<UUID, EnumFacing>> route = new ArrayList<Tuple<UUID, EnumFacing>>();
										while(help.parent != null) {
											route.add(0,new Tuple<UUID, EnumFacing>(help.parent.getKey().getId(), help.parent.getVal()));
											help.getMember().spawnParticle(1.0f, 0.549f, 0.0f);
											Thread.sleep(60);
											help = help.parent.getKey();
										}
										
										return  new Triple<NetworkNode, NetworkNode, ArrayList<Tuple<UUID, EnumFacing>>>(start, target, route);
									}
									
									for(int i = 0; i<6; i++) {
										NetworkNode _node =current.getNeighborAt(i);
										if(_node == null || closed.contains(_node)) {
											continue;
										}
										_node.h_Cost(target);
										
										_node.parent = new Tuple<NetworkNode, EnumFacing>(current, EnumFacing.getFront(i));
										_node.p_cost=current.p_cost + _node.t_cost;
										if(!open.contains(_node) && !closed.contains(_node)) {
											open.offer(_node);
											_node.getMember().spawnParticle(0.502f, 0.000f, 0.502f);
										}
											
									}
									Thread.sleep(50);
								}
								return null;
							}
				});
		executor.execute(routingTask);
		try {
			routingInfo = routingTask.get();
			executor.shutdownNow();
		}
		catch(Exception e) {
			CrashReport.makeCrashReport(e, "A logistics Pipes router was interrupted!");
		}
		return routingInfo;
	}
	
	public void clean() {
		open.clear();
		closed.clear();
		executor = Executors.newSingleThreadExecutor();
	}
	
	public void shutdown() {
		executor.shutdownNow();
		executor = Executors.newSingleThreadExecutor();
	}
	
}
