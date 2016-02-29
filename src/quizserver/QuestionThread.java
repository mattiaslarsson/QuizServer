package quizserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/** Tr�d som ansvarar f�r att skicka ut fr�gor
 * 
 * @author Mattias Larsson
 *
 */
public class QuestionThread implements Runnable {
	Scanner questionReader;
	@Override
	public void run() {
		try {
			// L�ser in en fil med fr�gor
			questionReader = new Scanner(new File("questions"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(questionReader.hasNext()) {
			// Fr�ga och svar �r separerade med en radbrytning
			String q = questionReader.nextLine();
			String a = questionReader.nextLine();
			// Anger svaret
			QuizServerApp.setAnswer(a);
			// Skickar ut fr�gan till klienterna
			QuizServerApp.broadCast("@question@" + q);
			
			// V�ntar s� att klienter f�r chans att svara
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
