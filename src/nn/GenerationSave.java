package nn;

import space.Board;
import space.SpaceInvaders;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

public class GenerationSave {

    public static void main(String[] args) {

        NeuralNetwork nn = GenerationSave.loadBoard( "Boards//3//2798846002012556257_357.dat");
        Board board = new Board(nn);
        SpaceInvaders.showControllerPlaying(board.getController(), new Random().nextLong());
    }


    public static void saveBestIndividual(Generation generation){

        String path = encodePath(generation);
        try {
            FileOutputStream fileOutput = new FileOutputStream(path);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOutput);
            NeuralNetwork nn = generation.getFittestBoard().getController();
            nn.setBoardSeed(generation.getFittestBoard().getSeed());
            objOut.writeObject(nn);
        }catch (Exception e){
            System.out.println("Something might have went wrong with saving the board");
        }
    }

    public static NeuralNetwork loadBoard(String path){
        try {
            FileInputStream inFile = new FileInputStream(path);
            ObjectInputStream inObj = new ObjectInputStream(inFile);
            NeuralNetwork nn = (NeuralNetwork)inObj.readObject();
            return nn;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String encodePath(Generation generation){
        int generationNum = generation.getId();
        long generationId = generation.getGenerationID();
        return "Boards//" + generationId + "_" + generationNum + ".dat";
    }

}
