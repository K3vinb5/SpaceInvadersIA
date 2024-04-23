package nn;
import space.*;

import java.util.Scanner;

public class GeneticAlgorithm {
    private final static int MAX_GENERATIONS = 5000;
    private final static int GENERATION_SIZE = 250;
    private final static int HIDDEN_SIZE = 150;
    private final static int SAVE_NUMBER = 20;

    public static void main(String[] args) {
        Generation generation = new Generation(GENERATION_SIZE, HIDDEN_SIZE);
        print(generation.printGenerationAverage());
        int saveSplitter = MAX_GENERATIONS / SAVE_NUMBER;
        int saveIndex = 0;
        System.out.println("A speciment will be saved every " + saveSplitter + " generations");
        for(int i = 0; i < MAX_GENERATIONS; i++){
            generation.mutate(0.2,0.2f);
            print(generation.printGenerationAverage());
            saveIndex++;
            if (saveIndex == saveSplitter || generation.getId() == MAX_GENERATIONS){
                GenerationSave.saveBestIndividual(generation);
                saveIndex = 0;
            }
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press enter to continue...");
        scanner.nextLine();
        scanner.close();
        SpaceInvaders.showControllerPlaying(generation.getFittestBoard().getController(), generation.getFittestBoard().getSeed());

    }

    private static void print(Object object){
        System.out.println(object);
    }

}
