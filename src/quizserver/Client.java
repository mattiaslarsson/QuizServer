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
			System.out.println("Kunde inte �ppna str�mmar");
		}
	}
	
	/** Returnerar denna klients socket
	 * 
	 * @return Socket
	 */
	public Socket getSocket() {
		return socket;
	}
	
	/** S�tter r�tt svar p� fr�gan
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
	
	/** Returnerar klientens po�ng
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
		writer.println("V�lkommen!");
		while (sc.hasNextLine()) {
			String input = sc.nextLine();
			// Om det klienten skriver matchar r�tt svar
			if (input.equalsIgnoreCase(answer)) {
				score++;
				answer="";
				QuizServerApp.broadCast("@point@" + name + "-" + score);
				QuizServerApp.broadCast(name + " svarade r�tt!");
				
				// Denna str�ng skickas n�r man loggar in
			} else if (input.startsWith("@userName@")) {
				// S�tter namn och po�ng
				name = input.substring(input.lastIndexOf("@")+1, input.length());
				score = 0;
				// L�gger till klienten i listan
				QuizServerApp.addClient(this);
				
				// Denna str�ng skickas n�r man loggar ut
			} else if (input.startsWith("/drop")) {
				// Tar bort klienten ur listan
				QuizServerApp.removeClient(this);
				
				// Skickar ut chatmeddelande
			} else {
				QuizServerApp.broadCast(input);
			}
		}
	}
}
