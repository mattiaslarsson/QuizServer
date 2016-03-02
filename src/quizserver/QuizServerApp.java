package quizserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/** Startar programmet, startar klient-tr�d
 * och inneh�ller statiska metoder f�r att skicka
 * och ta emot meddelanden
 *  
 * @author Mattias Larsson
 *
 */
public class QuizServerApp {
	private static List<Client> clientList = new ArrayList<>();
	
	public static void main(String[] args) {
		/* Startar fr�getr�d. Denna k�rs hela tiden
		 * �ven n�r ingen klient �r uppkopplad
		 */
		Thread questionThread = new Thread(new QuestionThread());
		questionThread.start();
		
		// Initierar en socket och v�ntar p� uppkoppling
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(3000);
			System.out.println("Server online och v�ntar p� anslutning...");
			while(true) {
				Socket socket = serverSocket.accept();
				// Uppkoppling har skett och en ny klient initieras
				Client client = new Client(socket);
				Thread clientThread = new Thread(client);
				clientThread.start();
			}
		} catch(IOException ioex) {
			System.out.println("Kunde inte initiera servern\nTesta att starta om");
			System.exit(0);
		}
	}
	
	/** L�gger till klienten i en lista och skickar meddelande
	 * till alla uppkopplade klienter om detta
	 * 
	 * @param Client
	 */
	public static void addClient(Client client) {
		clientList.add(client);
		for (int i = 0 ; i<clientList.indexOf(client); i++) {
			client.broadCast("@userName@" + clientList.get(i).getName() + "-" + clientList.get(i).getScore());
			clientList.get(i).broadCast("@userName@" + client.getName() + "-" + client.getScore());
		}
	}
	
	/** S�tter r�tt svar p� nuvarande fr�ga f�r alla klienter
	 * 
	 * @param String
	 */
	public static void setAnswer(String answer) {
		clientList.forEach(client -> {
			client.setAnswer(answer);
		});
	}
	
	/** Tar bort en klient ur listan
	 * 
	 * @param Client
	 */
	public static void removeClient(Client client) {
		clientList.remove(client);
	}
	
	/** Skickar ut meddelande till alla klienter
	 * 
	 * @param String
	 */
	public static void broadCast(String msg) {
		clientList.forEach(client -> {
			client.broadCast(msg);
		});
	}
}