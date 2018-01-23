package com.sots.module;

import com.sots.tiles.TileGenericPipe;
import com.sots.tiles.TileRoutedPipe;

public class ModuleSink extends ModuleBase implements IModule{
	
	private boolean hasRegistered = false;
	
	@Override
	// TODO: 21-1-2018
	public boolean execute(TileRoutedPipe te) {
		/*if(!hasRegistered) {
			if(te.hasInventory() && te.hasNetwork()) {
				ArrayList<Item> types = te.getItemTypesInInventory(te.getNetwork().getDirectionForDestination(te.nodeID));
				if(!types.isEmpty()) {
					types.forEach(p -> te.getNetwork().registerItemStorage(new Tuple<>(te.nodeID, p)));
					hasRegistered=true;
				}
			}
		}
		return true;*/
		return false;
	}
	
	@Override
	public boolean canExecute() {return true;}
	
	@Override
	public boolean canInsert() {return true;}
	
	@Override
	public ModuleType modType() {return ModuleType.SINK;}
	
	@Override
	public void connect(TileRoutedPipe te) {
		
	}
	
	@Override
	public void disconnect(TileRoutedPipe te) {
		//has to be disconnected!
		hasRegistered=false;
		te.getNetwork().unregisterItemStorage(te.nodeID);
	}
	
	@Override
	public void onAdd(TileRoutedPipe te) {
		
	}
	
	@Override
	public void onRemoved(TileRoutedPipe te) {
		
	}
	

}
