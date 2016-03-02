package quizserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.util.Pair;

/** Tråd som ansvarar för att skicka ut frågor
 * 
 * @author Mattias Larsson
 *
 */
public class QuestionThread implements Runnable {
	private List<Pair<String, String>> questionList = new ArrayList<>();
	private Scanner questionReader;
	
	@Override
	public void run() {
		while(true) {
			while (!questionList.isEmpty()) {
				// Anger rätt svar
				QuizServerApp.setAnswer(questionList.get(0).getValue());
				// Skickar ut frågan till klienter
				QuizServerApp.broadCast("@question@"+questionList.get(0).getKey());
				questionList.remove(0);
				
				// Väntar så att klienter får chans att svara
				for (int timer = 15; timer >= 0; timer--) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
					QuizServerApp.broadCast("@timer@" + timer);
				}
			}
			readQuestions();
		}
	}
	
	private void readQuestions() {
		try {
			questionReader = new Scanner(new File("questions"));
			while (questionReader.hasNext()) {
				// Fråga och svar är separerade med radbrytning
				String q = questionReader.nextLine();
				String a = questionReader.nextLine();
				questionList.add(new Pair<String, String>(q, a));
			}
		} catch (FileNotFoundException fnfe) {
			System.out.println("Kunde inte läsa in frågor.\nSäkerställ att filen 'questions' finns i serverns root-directory\n"
						+"och att varje fråga och svar är separerade med en radbrytning.");
			System.exit(0);
		}
		
	}
}
