package game.text;

import static org.junit.Assert.*;

import org.junit.Test;

import game.text.exceptions.ActionException;
import game.text.vampire.Vampire;
import game.text.vampire.items.Bookcase;
import game.text.vampire.items.BrickFireplace;
import game.text.vampire.places.Fireplace;



public class VampireTest {

	
	@Test
	public void testPlayerStartLocation() {
		Vampire vamp = start();
		assertEquals("Entrance Hall", vamp.getPlayer().getLocation().getName());
	}
	
	@Test
	public void testGoFromEntranceHallToStudy() {
		Vampire vamp = start();
		vamp.getPlayer().go(vamp.getDirection("west"));
		assertEquals("Study", vamp.getPlayer().getLocation().getName());
	}
	
	@Test
	public void testGoFromStudyToEntranceHall() {
		Vampire vamp = start();
		vamp.getPlayer().setLocation(vamp.getPlace("study"));
		vamp.getPlayer().go(vamp.getDirection("east"));
		assertEquals("Entrance Hall", vamp.getPlayer().getLocation().getName());
	}
	
	@Test
	public void testGoFromStudyNorthWhenNothingIsThere() {
		Vampire vamp = start();
		vamp.getPlayer().setLocation(vamp.getPlace("study"));
		assertFalse(vamp.getPlayer().go(vamp.getDirection("north")));
		
	}
	
	@Test(expected = ActionException.class )
	public void testGetObjectWhenNoneExists() throws ActionException {
		Vampire vamp = start();
		vamp.getPlayer().pickUp(vamp.getItem("wine"));
	}
	
	@Test
	public void testGetWineFromStudyAndCheckInventory() throws ActionException {
		Vampire vamp = start();
		vamp.getPlayer().setLocation(vamp.getPlace("study"));
		vamp.getPlayer().pickUp(vamp.getItem("wine"));
		assertEquals("Wine", vamp.getPlayer().getAll().iterator().next().getName());
	}
	
	@Test(expected = ActionException.class )
	public void testDropObjectThatYouDontHave() throws ActionException {
		Vampire vamp = start();
		vamp.getPlayer().drop(vamp.getItem("wine"));
	}
	
	
	@Test
	public void testDropWineInTheEntranceHall() throws ActionException {
		Vampire vamp = start();
		vamp.getPlayer().addOne(vamp.getItem("wine"));
		vamp.getPlayer().drop(vamp.getItem("wine"));
		assertTrue("Droped wine is not in the room", vamp.getPlayer().getLocation().getAll().contains(vamp.getItem("wine")));
	}
	
	@Test
	public void testPushBookcaseInTheLibrary() {
		Vampire vamp = start();
		Player player = vamp.getPlayer();
		player.setLocation(vamp.getPlace("library"));
		Item bookcase = new Bookcase(vamp);
		assertTrue(bookcase.executeAction("push", player).getMessage().contains("Aha! - You have revealed a Doorway"));
	}
	
	@Test
	public void testPushBookcaseIfItIsNotInTheRoom() {
		Vampire vamp = start();
		Player player = vamp.getPlayer();
		player.setLocation(vamp.getPlace("study"));
		Item bookcase = new Bookcase(vamp);
		assertFalse(bookcase.executeAction("push", player).getMessage().contains("Aha! - You have revealed a Doorway"));
	}
	
	@Test(expected = NullPointerException.class )
	public void testPushFireplace() {
		Vampire game = start();
		Player player = game.getPlayer();
		player.setLocation(game.getPlace("study"));
		Item fireplace = new BrickFireplace(game);
		fireplace.executeAction("push", player).getMessage();
	}
	
	
	

	@Test
	public void testStartTurnBeforeAndAfterWin() {
		Vampire game = start();
		assertEquals(true, game.startTurn());
		game.end(true);
		assertEquals(false, game.startTurn());
	}
	
	
	
	
	@Test
	public void testRoomConnections() {
		Vampire game = start();
		checkConnections("study", "entrance hall", "east", game, true);
		checkConnections("library", "entrance hall", "west", game, true);
		checkConnections("library", "armory", "east", game, true);
		checkConnections("armory", "tower", "east", game, true);
		checkConnections("hidden corridor", "alchemist's lab", "north", game, true);
		checkConnections("alchemist's lab", "storeroom", "north", game, true);
		checkConnections("lower tower", "chapel", "south", game, true);
		checkConnections("secret passage", "underground lake chamber", "north", game, true);
		checkConnections("secret passage", "torture chamber", "west", game, true);
		
		

		checkConnections("_fireplace", "study", "south", game, false);
		checkConnections("lower tower", "tower", "up", game, false);
		checkConnections("chapel", "armory", "up", game, false);
		checkConnections("secret passage", "_fireplace", "south", game, false);
		checkConnections("torture chamber", "alchemist's lab", "west", game, false);
		checkConnections("boat", "lake", "south", game, false);
		checkConnections("overhang", "gallery", "down", game, false);
		checkConnections("storeroom", "study", "up", game, false);
		
	}
	
	
	private Vampire start() {
		Vampire game = new Vampire();
		game.initialize();
		return game;
	}
	
	private void checkConnections(String fromKey, String toKey, String viaKey, Vampire game, boolean twoWay) {
		Place from = game.getPlace(fromKey);
		Place to = game.getPlace(toKey);
		Direction via = game.getDirection(viaKey);
		
		String errorMessage = "'" + from.getName() + "' is not connected to '" + to.getName() + "' via Direction '"+ via.getName() + "'";
		assertTrue(errorMessage,from.getConnection(via).equals(to));
		
		
			Place temp = from;
			from = to;
			to = temp;
			via = via.getOpposite();
			
			
			if(twoWay) {
				errorMessage = "Can not get back from '" + from.getName() + "' "
						+ "to '" + to.getName() + "' via Direction '"+ via.getName() + "'";
				assertTrue(errorMessage,from.getConnection(via).equals(to));
			}
			
			
//				else{
//				String dir = via.getName();
//				if(dir == null)
//					dir = "Down";
//				System.out.println(dir + " " + from.getName() + " " + to.getName());
//				errorMessage = "Should not be able to go back from '" + from.getName() 
//						+"' to '" + to.getName() + "' via Direction '"+ dir + "'";
//				assertFalse(errorMessage,from.getConnection(game.getDirection(dir.toLowerCase())).equals(to));	
//			}
		
	}
	
//	private String errorMessage(String from, String to, String via) {
//		return fromKey + " is not connected to " + toKey + " via Direction "+ viaKey;
//	}
}


