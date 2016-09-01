package net.icelane.massband.core;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import net.icelane.massband.minecraft.HoloText;

public class Marker {

	private Player player;
	
	private ArrayList<HoloText> markerList = new ArrayList<>();
	private HoloText first;
	private HoloText last;

	public Marker(Player player) {
		this.player = player;
	}

	public void hideAll(){
		if (first != null) first.hide();
		if (last != null) last.hide();
		for (HoloText marker : markerList){
			marker.hide();
		}
	}
	
	public void showAll(){
		if (first != null) first.show();
		if (last != null) last.show();
		for (HoloText marker : markerList){
			marker.show();
		}
	}
	
	public void removeAll(){
		removeAllBetween();
		removeFirst();
		removeLast();
	}
	
	public void removeAllBetween(){
		for(HoloText marker : markerList){
			marker.remove();
		}
		markerList.clear();
	}
	
	public void removeFirst(){
		if (first != null) first.remove();
		first = null;
	}
	
	public void removeLast(){
		if (last != null) last.remove();
		last = null;
	}
	
	public void add(HoloText holotext){
		markerList.add(holotext);
	}
	
	public void add(int index, HoloText holotext){
		markerList.add(index, holotext);
	}
	
	public void add(Location location, String text){
		markerList.add(HoloText.create(location, text));
	}
	
	public void add(Block block, BlockFace face, String text){
		markerList.add(HoloText.create(player.getWorld(), block, face, text));
	}
	
	public HoloText addClone(HoloText holoText){
		HoloText clone = holoText.clone();
		add(clone);
		return clone;
	}
	
	public int getCount(){
		return markerList.size();
	}
	
	public HoloText get(int index){
		return getCount() > 0 ? markerList.get(index) : null;
	}
	
	public void set(int index, HoloText holoText){
		if (getCount() > 0) markerList.set(index, holoText);
		else add(holoText);
	}
	
	public HoloText getLastBetween(){
		return get(getCount() - 1);
	}
	
	public void setLastBetweent(HoloText holoText){
		if (getCount() > 0) set(getCount() - 1, holoText);
		else add(holoText);
	}
	
	public boolean hasAny(){
		return hasMore() || hasFirst() || hasLast();
	}
	
	public boolean hasMore(){
		return markerList.size() > 0;
	}
	
	public boolean hasFirst(){
		return first != null && first.isValid();
	}
	
	public boolean hasLast(){
		return last != null && last.isValid();
	}
		
	public HoloText getFirst(){
		return first;
	}
	
	public HoloText getLast(){
		return last;
	}

	public void setFirst(HoloText holotext){
		if (first != null) first.remove();
		first = holotext;
	}
	
	public void setLast(HoloText holotext){
		if (last != null) last.remove();
		last = holotext;
	}

	public void changeFirst(Block block, BlockFace face, String text){
		changeFirst(block, face, text, false);
	}
		
	public void changeFirst(Block block, BlockFace face, String text, boolean removeOther){	
		if (hasFirst()){
			getFirst().move(block, face);
			getFirst().setText(text);
			getFirst().show();
		}else{
			setFirst(HoloText.create(player.getWorld(), block, face, text));
		}
		
		if (removeOther) {
			removeAllBetween(); 
			if (hasLast()){
				getLast().hide();
			}
		}
	}
	
	public void changeLast(Block block, BlockFace face, String text){
		changeLast(block, face, text, false);
	}
	
	public void changeLast(Block block, BlockFace face, String text, boolean clone){
		if (hasLast()){
			if (clone) addClone(getLast());
		} else{
			if (hasMore()){
				// do not use "setLast(...)" here
				last  = getLastBetween();
				setLastBetweent(last.clone());
			}else if (hasFirst()){
				// do not use "setLast(...)" or "setFirst(...)" here
				last  = getFirst();
				first = last.clone();
			}else{
				setLast(HoloText.create(player.getWorld(), block, face, text));
			}
		}

		getLast().show();
		getLast().setText(text);
		getLast().move(block, face);
	}
	
}
