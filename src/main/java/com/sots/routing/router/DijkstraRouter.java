package com.sots.routing.router;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

import com.sots.LogisticsPipes2;
import com.sots.routing.NetworkNode;
import com.sots.routing.WeightedNetworkNode;
import com.sots.util.References;
import com.sots.util.data.Triple;
import com.sots.util.data.Tuple;

import net.minecraft.crash.CrashReport;
import net.minecraft.util.EnumFacing;

public class DijkstraRouter extends Router {
	WeightedNetworkNode start, target;
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private Queue<WeightedNetworkNode> unvisited = new LinkedBlockingQueue<WeightedNetworkNode>();
	private Queue<WeightedNetworkNode> visited = new LinkedBlockingQueue<WeightedNetworkNode>();
	private Triple<NetworkNode, NetworkNode, ArrayDeque<Tuple<UUID, EnumFacing>>> routingInfo;

	protected volatile Map<UUID, WeightedNetworkNode> junctions;

	public DijkstraRouter(Map<UUID, WeightedNetworkNode> junctions) {
		this.junctions = junctions; //I am not sure if these two Maps will be kept updated with each other
	}


	public Triple<NetworkNode, NetworkNode, ArrayDeque<Tuple<UUID, EnumFacing>>> route(NetworkNode s, NetworkNode t) {
		if (junctions.containsKey(s.getId())) {
			start = junctions.get(s.getId());
		} else {
			LogisticsPipes2.logger.info("You tried routing from a node, which was not a destination or junction.");
			return null;
		}

		if (junctions.containsKey(t.getId())) {
			target = junctions.get(t.getId());
		} else {
			LogisticsPipes2.logger.info("You tried routing to a node, which was not a destination or junction.");
			return null;
		}

		FutureTask<Triple<NetworkNode, NetworkNode, ArrayDeque<Tuple<UUID, EnumFacing>>>> routingTask =
			new FutureTask<Triple<NetworkNode, NetworkNode, ArrayDeque<Tuple<UUID, EnumFacing>>>>(
					new Callable<Triple<NetworkNode, NetworkNode, ArrayDeque<Tuple<UUID, EnumFacing>>>>() {
						@Override
						public Triple<NetworkNode, NetworkNode, ArrayDeque<Tuple<UUID, EnumFacing>>> call() 
								throws Exception {
							start.p_cost=0;
							start.parent=null;
							unvisited.add(start);
							while(!unvisited.isEmpty()) {
								WeightedNetworkNode current = unvisited.poll();
								current.getMember().spawnParticle(References.RGB_GREEN);
								Thread.sleep(120);

								if (current.equals(target)) {
									//path found
									NetworkNode help = current;

									ArrayDeque<Tuple<UUID, EnumFacing>> route = new ArrayDeque<Tuple<UUID, EnumFacing>>();
									while(help.parent != null) {
										pushToRouteUntillParent(help, route);

										help.getMember().spawnParticle(References.RGB_ORANGE);
										help = help.parent.getKey();
									}
									return new Triple<NetworkNode, NetworkNode, ArrayDeque<Tuple<UUID, EnumFacing>>>(start, target, route);
								}



								for (int i = 0; i < 6; i++) {
									Tuple<WeightedNetworkNode, Integer> neighborT = current.weightedNeighbors[i];
									if (neighborT == null) {
										continue;
									}

									WeightedNetworkNode neighbor = neighborT.getKey();
									int distance = neighborT.getVal();
									if (!(unvisited.contains(neighbor) || visited.contains(neighbor))) {
										unvisited.add(neighbor);
										neighbor.getMember().spawnParticle(References.RGB_ORANGE);
									}
									if (current.p_cost + distance < neighbor.p_cost) {
										neighbor.p_cost = current.p_cost + distance;
										neighbor.parent = new Tuple<NetworkNode, EnumFacing>(current, EnumFacing.getFront(i));
									}
								}

								visited.add(current);
								Thread.sleep(120);
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

	private void pushToRouteUntillParent(NetworkNode current, ArrayDeque<Tuple<UUID, EnumFacing>> route) throws InterruptedException {
		NetworkNode parent = current.parent.getKey();
		EnumFacing direction = current.parent.getVal();
		int parentDirection = direction.getOpposite().getIndex();

		NetworkNode help = current;
		while(help.getId() != parent.getId()) {
			help = help.getNeighborAt(parentDirection);
			route.push(new Tuple<UUID, EnumFacing>(help.getId(), direction));
			Thread.sleep(120);
		}
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
