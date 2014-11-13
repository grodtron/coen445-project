package coen445.project.server.inventory;

import java.io.IOException;

public class Inventory {

	public Inventory(){
	}
	
	public int addItem(String description, int startingBid){
								
		Item item;
		try {
			item = new Item(description, startingBid);
		} catch (IOException e) {
			System.err.println("Couldn't create new Item: " + e);
			return -3;
		}
		
		final int itemId = item.getId();
		
		System.out.println("Adding new item with #" + itemId);
		
		new Thread(item).start();
		
		return itemId;
	}
	
}
