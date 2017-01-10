package com.sots.routing;

import net.minecraft.util.EnumFacing;

public class WeightedEdge {
	
	private int weight;
	
	private EnumFacing sideStart;
	
	private EnumFacing sideEnd;
	
	public WeightedEdge(int w, EnumFacing start, EnumFacing end) {
		weight=w;
		sideStart=start;
		sideEnd=end;
	}

	public int getWeight() {return weight;}

	public void setWeight(int weight) {this.weight = weight;}

	public EnumFacing getSideStart() {return sideStart;}

	public void setSideStart(EnumFacing sideStart) {this.sideStart = sideStart;}

	public EnumFacing getSideEnd() {return sideEnd;}

	public void setSideEnd(EnumFacing sideEnd) {this.sideEnd = sideEnd;}
	
}
