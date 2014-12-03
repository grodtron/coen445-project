package coen445.project.server.inventory;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Inventory {
	
	private Map<String, Set<Item>> pendingItems;

	public Inventory(){
		pendingItems = new HashMap<String, Set<Item>>();
	}
	
	public int addItem(String description, String user, int startingBid){
								
		Item item;
		try {
			item = new Item(description, user, startingBid, this);
		} catch (IOException e) {
			System.err.println("Couldn't create new Item: " + e);
			return -3;
		}
		
		final int itemId = item.getId();
		
		System.out.println("Adding new item with #" + itemId);
		
		new Thread(item).start();
		
		Set<Item> items = pendingItems.get(user);
		if(items == null){
			pendingItems.put(user, new HashSet<Item>());
		}
		items.add(item);
		
		return itemId;
	}

	public void done(Item item) {
		Set<Item> items = pendingItems.get(item.getUser());
		if(items != null){
			items.remove(item);
			if(items.size() == 0){
				pendingItems.remove(item.getUser());
			}
		}
	}
	
	public boolean canDeregister(String user){
		Set<Item> items = pendingItems.get(user);
		return items != null || items.size() == 0;
	}
	
}
