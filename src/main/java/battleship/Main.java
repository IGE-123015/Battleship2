/**
 * 
 */
package battleship;


/**
 * The type Main.
 *
 * @author britoeabreu
 * @author adrianolopes
 * @author miguelgoulao
 */
public class Main {
	/**
	 * Main.
	 *
	 * @param args the args
	 */
	public static void main(String[] args) {
		System.out.println("***  Battleship  ***");

		if (args.length > 0 && args[0].equalsIgnoreCase("gui")) {
			BattleshipGUI.launchGUI();
		}

		Tasks.menu();
	}
}
