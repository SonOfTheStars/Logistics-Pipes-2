package com.sots.routing.router;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.Queue;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import com.sots.routing.NetworkNode;
import com.sots.routing.WeightedNetworkNode;
import com.sots.util.data.Tuple;
import com.sots.util.data.Triple;

import net.minecraft.crash.CrashReport;
import net.minecraft.util.EnumFacing;

public class DijkstraRouter extends Router {
	WeightedNetworkNode start, target;
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private Queue<WeightedNetworkNode> unvisited = new LinkedBlockingQueue<WeightedNetworkNode>();
	private Queue<WeightedNetworkNode> visited = new LinkedBlockingQueue<WeightedNetworkNode>();
	private Triple<NetworkNode, NetworkNode, Stack<Tuple<UUID, EnumFacing>>> routingInfo;

	public Triple<NetworkNode, NetworkNode, Stack<Tuple<UUID, EnumFacing>>> route(NetworkNode s, NetworkNode t) {
		if (s instanceof WeightedNetworkNode)
			start = (WeightedNetworkNode) s;
		else
			// You tried routing between two nodes, which were not destinations or junctions.
			return null;

		if (t instanceof WeightedNetworkNode)
			target = (WeightedNetworkNode) t;
		else
			// You tried routing between two nodes, which were not destinations or junctions.
			return null;

		FutureTask<Triple<NetworkNode, NetworkNode, Stack<Tuple<UUID, EnumFacing>>>> routingTask =
			new FutureTask<Triple<NetworkNode, NetworkNode, Stack<Tuple<UUID, EnumFacing>>>>(
					new Callable<Triple<NetworkNode, NetworkNode, Stack<Tuple<UUID, EnumFacing>>>>() {
						@Override
						public Triple<NetworkNode, NetworkNode, Stack<Tuple<UUID, EnumFacing>>> call() 
								throws Exception {
							WeightedNetworkNode startW;
							start.p_cost=0;
							start.parent=null;
							unvisited.add(start);
							while(!unvisited.isEmpty()) {
								WeightedNetworkNode current = unvisited.poll();

								if (current.getId() == target.getId()) {
									//path found
									//Not sure how to get a route out of the path though
								}


								for (int i = 0; i < 6; i++) {
									Tuple<WeightedNetworkNode, Integer> neighborT = current.weightedNeighbors[i];
									WeightedNetworkNode neighbor = neighborT.getKey();
									int distance = neighborT.getVal();
									if (!unvisited.contains(neighbor) && !visited.contains(neighbor))
										unvisited.add(neighbor);
									if (current.p_cost + distance < neighbor.p_cost) {
										neighbor.p_cost = current.p_cost + distance;
										neighbor.parent = new Tuple<NetworkNode, EnumFacing>(current, EnumFacing.getFront(i));
									}
								}
								visited.add(current);
							}
							return null;
						}

					});
		executor.execute(routingTask);
		try {
			routingInfo = routingTask.get();
			executor.shutdownNow();
		}
		catch (Exception e) {
			CrashReport.makeCrashReport(e, "A logistics Pipes router was interrupted!");
		}
		return routingInfo;
	}

	public void clean() {
		unvisited.clear();
		visited.clear();
		executor = Executors.newSingleThreadExecutor();
	}

	public void shutdown() {
		executor.shutdownNow();
		executor = Executors.newSingleThreadExecutor();
	}
}
