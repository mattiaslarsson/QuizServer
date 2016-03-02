package quizserver;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/** Klassen definierar en unik klient
 * 
 * @author Mattias Larsson
 *
 */
public class Client implements Runnable{
	private Socket socket;
	private String answer=null;
	private PrintStream writer;
	private Scanner sc;
	private String name;
	private int score;
		
	public Client (Socket socket) {
		this.socket = socket;
		try {
			writer = new PrintStream(this.socket.getOutputStream());
			sc = new Scanner(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("Kunde inte öppna strömmar.\nTesta att starta om servern.");
		}
	}
	
	/** Returnerar denna klients socket
	 * 
	 * @return Socket
	 */
	public Socket getSocket() {
		return socket;
	}
	
	/** Sätter rätt svar på frågan
	 * 
	 * @param String
	 */
	public void setAnswer(String a) {
		answer = a;
	}
	
	/** Returnerar klientens namn
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/** Returnerar klientens poäng
	 * 
	 * @return int
	 */
	public int getScore() {
		return score;
	}
	
	/** Skickar ut meddelande till klienten
	 *  
	 * @param String
	 */
	public synchronized void broadCast(String msg) {
		writer.println(msg);
		writer.flush();
	}
	
	@Override
	public void run() {
		while (sc.hasNextLine()) {
			String input = sc.nextLine();
			// Om det klienten skriver matchar rätt svar
			if (input.equalsIgnoreCase(answer)) {
				score++;
				answer="";
				QuizServerApp.broadCast("@point@" + name + "-" + score);
				QuizServerApp.broadCast(name + " svarade rätt!");
				
				// Denna sträng skickas när man loggar in
			} else if (input.startsWith("@userName@")) {
				// Sätter namn och poäng
				name = input.substring(input.lastIndexOf("@")+1, input.length());
				score = 0;
				// Lägger till klienten i listan
				QuizServerApp.addClient(this);
				
				// Denna sträng skickas när man loggar ut
			} else if (input.startsWith("@drop@")) {
				// Tar bort klienten ur listan
				QuizServerApp.removeClient(this);
				QuizServerApp.broadCast("@drop@" + name);
				
				// Skickar ut chatmeddelande
			} else {
				QuizServerApp.broadCast(input);
			}
		}
	}
}
