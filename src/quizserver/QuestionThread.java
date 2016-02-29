package quizserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/** Tråd som ansvarar för att skicka ut frågor
 * 
 * @author Mattias Larsson
 *
 */
public class QuestionThread implements Runnable {
	Scanner questionReader;
	@Override
	public void run() {
		try {
			// Läser in en fil med frågor
			questionReader = new Scanner(new File("questions"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(questionReader.hasNext()) {
			// Fråga och svar är separerade med en radbrytning
			String q = questionReader.nextLine();
			String a = questionReader.nextLine();
			// Anger svaret
			QuizServerApp.setAnswer(a);
			// Skickar ut frågan till klienterna
			QuizServerApp.broadCast("@question@" + q);
			
			// Väntar så att klienter får chans att svara
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
