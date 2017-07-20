package com.sots.routing.router;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.Queue;
import java.util.Stack;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import com.sots.LogisticsPipes2;
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

	private volatile Map<UUID, WeightedNetworkNode> junctions;

	public DijkstraRouter(Map<UUID, WeightedNetworkNode> junctions) {
		this.junctions = junctions; //I am not sure if these two Maps will be kept updated with each other
	}


	public Triple<NetworkNode, NetworkNode, Stack<Tuple<UUID, EnumFacing>>> route(NetworkNode s, NetworkNode t) {
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

		//if (s instanceof WeightedNetworkNode) {
			//start = (WeightedNetworkNode) s;
		//} else {
			//LogisticsPipes2.logger.info("You tried routing between two nodes, which were not destinations or junctions.");
			//return null;
		//}

		//if (t instanceof WeightedNetworkNode) {
			//target = (WeightedNetworkNode) t;
		//} else {
			//LogisticsPipes2.logger.info("You tried routing between two nodes, which were not destinations or junctions.");
			//return null;
		//}

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
								current.getMember().spawnParticle(0f, 1.000f, 0f);
								Thread.sleep(60);

								try { //DEBUG
								if (current.getId() == target.getId()) {
									//path found
									LogisticsPipes2.logger.info("Path found"); //DEBUG
									NetworkNode help = current;

									Stack<Tuple<UUID, EnumFacing>> route = new Stack<Tuple<UUID, EnumFacing>>();
									while(help.parent != null) {
										pushToRouteUntillParent(help, route);

										help.getMember().spawnParticle(1.0f, 0.549f, 0.0f);
										Thread.sleep(60);
										help = help.parent.getKey();
									}
									return new Triple<NetworkNode, NetworkNode, Stack<Tuple<UUID, EnumFacing>>>(start, target, route);
								}
								} catch (Exception e) {
									LogisticsPipes2.logger.info(e); //DEBUG
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
										neighbor.getMember().spawnParticle(0.502f, 0.000f, 0.502f);
									}
									if (current.p_cost + distance < neighbor.p_cost) {
										neighbor.p_cost = current.p_cost + distance;
										neighbor.parent = new Tuple<NetworkNode, EnumFacing>(current, EnumFacing.getFront(i));
									}
								}

								visited.add(current);
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
		catch (Exception e) {
			CrashReport.makeCrashReport(e, "A logistics Pipes router was interrupted!");
		}
		return routingInfo;
	}

	private void pushToRouteUntillParent(NetworkNode current, Stack<Tuple<UUID, EnumFacing>> route) throws InterruptedException {
		NetworkNode parent = current.parent.getKey();
		EnumFacing direction = current.parent.getVal();
		int parentDirection = direction.getOpposite().getIndex();

		LogisticsPipes2.logger.info("Test"); //DEBUG
		NetworkNode help = current;
		while(help.getId() != parent.getId()) {
			help = current.getNeighborAt(parentDirection);

			if (help == null) {
				LogisticsPipes2.logger.info("This should NOT happen"); //DEBUG
			}

			route.push(new Tuple<UUID, EnumFacing>(help.getId(), direction));
			help.getMember().spawnParticle(1.0f, 0.549f, 0.0f);
			Thread.sleep(60);
		}
		LogisticsPipes2.logger.info("Test2"); //DEBUG

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
