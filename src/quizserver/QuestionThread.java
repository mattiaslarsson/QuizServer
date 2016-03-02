package quizserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.util.Pair;

/** Tr�d som ansvarar f�r att skicka ut fr�gor
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
				// Anger r�tt svar
				QuizServerApp.setAnswer(questionList.get(0).getValue());
				// Skickar ut fr�gan till klienter
				QuizServerApp.broadCast("@question@"+questionList.get(0).getKey());
				questionList.remove(0);
				
				// V�ntar s� att klienter f�r chans att svara
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
				// Fr�ga och svar �r separerade med radbrytning
				String q = questionReader.nextLine();
				String a = questionReader.nextLine();
				questionList.add(new Pair<String, String>(q, a));
			}
		} catch (FileNotFoundException fnfe) {
			System.out.println("Kunde inte l�sa in fr�gor.\nS�kerst�ll att filen 'questions' finns i serverns root-directory\n"
						+"och att varje fr�ga och svar �r separerade med en radbrytning.");
			System.exit(0);
		}
		
	}
}
