package es.afm.microblog;

import java.io.IOException;

import es.afm.microblog.utils.IO;
import es.afm.microblog.utils.Redis;

/**
 * Main class
 * Simulates a microblog site with redis backend with teaching purposes
 *
 */
public class Microblog {
	
	private static final String menu = 
					"1. Nueva entrada \n" + 
					"2. Mostrar timeline \n" + 
					"3. Mostrar mi timeline \n" + 
					"4. Seguir al usuario... \n" + 
					"5. Mostrar timeline usuario... \n" +
					"6. Mostrar seguidores en común con usuario... \n" +
					"7. Logout \n"
					+ "------------------------------------\n" + 
					"Escoja opción: ";
	
	private static final String logInMenu = 
					"1. Log in \n" + 
					"2. Registrar nuevo usuario \n" + 
					"3. Salir \n" + 
					"------------------------------------\n" + 
					"Escoja opción: ";

	private String currentUser = null;
	private Redis redis;
	private IO io;

	private void run() throws IOException {
		redis = Redis.getInstance();
		io = IO.getIO();
		boolean salir = false;
		while (!salir) {
			if (currentUser == null) {
				IO.getIO().write(logInMenu);
				salir = parseLogInOption();
			} else {
				IO.getIO().write(menu);
				parseMenuOption();
			}
		}
		io.close();
		redis.close();
	}

	private void parseMenuOption() throws IOException {
		int option = Integer.parseInt(io.read());
		String userName;
		String userId;
		switch (option) {
		case 1: //nueva entrada
			io.write("Cuerpo del post: ");
			String body = io.read();
			redis.newPost(currentUser, body);
			break;
		case 2: //mostrar timeline
			redis.showTimeline();
			break;
		case 3: //mostrar mi timeline
			redis.showUserPosts(currentUser, 0, 10);
			break;
		case 4: //seguir al usuario...
			io.write("Username a seguir: ");
			userName = io.read();
			userId = redis.getUserIdFromUsername(userName);
			if(userId == null)
				io.write("No existe el usuario con nombre " + userName);
			else 
				redis.follow(currentUser, userId);
			
			break;
		case 5: //mostrar timeline usuario...
			io.write("Username: ");
			userName = io.read();
			userId = redis.getUserIdFromUsername(userName);
			if(userId == null)
				io.write("No existe el usuario con nombre " + userName);
			else
				redis.showUserPosts(userId, 0, 10);
			break;
		case 6: //mostrar seguidores en común con usuario...
			io.write("Username: ");
			userName = io.read();
			userId = redis.getUserIdFromUsername(userName);
			if(userId == null)
				io.write("No existe el usuario con nombre " + userName);
			else
				redis.showFollowersInCommon(currentUser, userId);
			break;
		case 7:
			currentUser = null;
			break;
		default: 
			io.write("Opción incorrecta");
		}
		
	}

	private boolean parseLogInOption() throws IOException {
		int option = Integer.parseInt(io.read());
		String userId;
		switch (option) {
		case 1:
			io.write("Username: ");
			userId = redis.getUserIdFromUsername(io.read());
			if(userId == null)
				io.write("No existe el nombre de usuario. Registrese");
			else 
				currentUser = userId;
			break;
		case 2:
			io.write("Username: ");
			String input = io.read();
			userId = redis.getUserIdFromUsername(input);
			if(userId != null)
				io.write("El usuario ya existe");
			else {
				userId = redis.newUser(input);
				currentUser = userId;
			}
			break;
		case 3:
			return true;
		default:
			io.write("Opción incorrecta");
		}
		return false;
	}

	public static void main(String[] args) throws IOException {
		new Microblog().run();
	}
}
