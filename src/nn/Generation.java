package nn;

import space.Board;
import space.Commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generation {
    private final long generationID = Math.abs(new Random().nextLong());
    private int id; //Generation ID
    private List<Board> individualList; //list of individuals
    private final int size; //size of the generation
    private final int inputDim;
    private final int hiddenDim;
    private final int outputDim;
    //private List<String> forTesting = new ArrayList<>();

    public Generation(int size, int hiddenDim) {
        this.id = 0;
        this.size = size;
        this.hiddenDim = hiddenDim;
        this.inputDim = Commons.STATE_SIZE;
        this.outputDim = Commons.NUM_ACTIONS;
        individualList = new ArrayList<>(); //initializes variable
        for (int i = 0; i < size; i++) { //Still not removing duplicates
            NeuralNetwork nn = new NeuralNetwork(inputDim, hiddenDim, outputDim);
            nn.initializeWeights(); //inicializes nn
            Board board = new Board(nn); //temporary individual, will later be inserted
            board.run();
            if (!individualList.contains(board)) {
                //if generated individual is not on the list
                individualList.add(board);
            } else {
                i--; //tries again in case the generated individual alredy existed
            }
        }
        this.sortIndividuals();
    }
    //Updated
    public List<Board> getIndividualList() {
        return individualList;
    }

    public int getSize() {
        return size;
    }

    public int getId() {
        return id;
    }

    public long getGenerationID() {
        return generationID;
    }

    //Generates mutations on a generation given a list of fathers, it will try to create sets of
    //children of the same size each from their parent
    public void mutate(double chanceOfMutation, float fathersPercentage) {
        this.id += 1;
        Random random = new Random();
        List<Board> fatherList = this.getFathers(fathersPercentage); //Not sure
        //Testing
        int index = 0;
        for (Board board:fatherList) {
            if (index > 4){
                board.getController().mutateGenes(0.05);
                board.run();
            }
            index++;
        }
        //Testing
        int splitter = ((Math.round((float) this.getSize() / fatherList.size())) - 1) / 2;
        int fatherIndex = 0; int splitterIndex = 0; boolean duplicates;
        this.getIndividualList().removeAll(this.getIndividualList()); //eliminates all list
        this.getIndividualList().addAll(fatherList);

        int inicio = this.getIndividualList().size();
        int fim = this.getSize();
        int meio = inicio * splitter + inicio;

        for (int i = inicio; i < meio; i++){
            if (splitterIndex == splitter){
                splitterIndex = 0;
                fatherIndex++;
                if (fatherIndex == fatherList.size()){
                    fatherIndex--;
                }
            }

            Board board = new Board(new NeuralNetwork(inputDim, hiddenDim, outputDim, fatherList.get(fatherIndex).getController().mutateGenes(chanceOfMutation)));
            board.run();
            while (board.getFitness() == 0){
                board = new Board(new NeuralNetwork(inputDim, hiddenDim, outputDim, fatherList.get(fatherIndex).getController().mutateGenes(chanceOfMutation)));
                board.run();
            }
            this.getIndividualList().add(board);
            duplicates = this.numberOfDuplicates(this.getIndividualList().get(i)) > 1; //Just in Case
            while (duplicates){
                board = new Board(new NeuralNetwork(inputDim, hiddenDim, outputDim, fatherList.get(fatherIndex).getController().mutateGenes(chanceOfMutation)));
                board.run();
                while (board.getFitness() == 0){
                    board = new Board(new NeuralNetwork(inputDim, hiddenDim, outputDim, fatherList.get(fatherIndex).getController().mutateGenes(chanceOfMutation)));
                    board.run();
                }
                this.getIndividualList().set(i,board);
                duplicates = this.numberOfDuplicates(this.getIndividualList().get(i)) > 1;
            }
            splitterIndex++;
        }
        for (int i = meio; i < fim; i++) {
            //Get 2 random different fathers
            Board fatherA = getRandomFather(fatherList);
            Board fatherB = getRandomFather(fatherList);
            while (fatherA.equals(fatherB)){
                fatherA = getRandomFather(fatherList);
            }
            Board board;
            board = crossover(fatherA, fatherB);
            board.run();
            this.getIndividualList().add(board);

            duplicates = this.numberOfDuplicates(this.getIndividualList().get(i)) > 1; //Just in case
            while (duplicates){
                //Get 2 random different fathers
                fatherA = getRandomFather(fatherList);
                fatherB = getRandomFather(fatherList);
                while (fatherA.equals(fatherB)){
                    fatherA = getRandomFather(fatherList);
                }
                board = crossover(fatherA, fatherB);
                board.run();
                this.getIndividualList().set(i,board);
                duplicates = this.numberOfDuplicates(this.getIndividualList().get(i)) > 1;
            }
        }
        this.sortIndividuals();
    }

    private Board getRandomFather(List<Board> fatherList){
        return fatherList.get((int)(new Random().nextDouble() * fatherList.size()));
    }

    private Board crossover(Board a, Board b){ //Makes it easier to test different crossover functions
        return crossover4(a,b);
    }

    private Board crossover1(Board a, Board b){
        double[] genesA = a.getController().getChromossome();
        double[] genesB = b.getController().getChromossome();
        for (int i = 0; i < genesA.length; i++){
            if (Math.random() < 0.5){
                genesA[i] = genesB[i];
            }
        }
        return new Board(new NeuralNetwork(inputDim, hiddenDim, outputDim, genesA));
    }

    private Board crossover2(Board a, Board b){
        double[] genesA = a.getController().getChromossome();
        double[] genesB = b.getController().getChromossome();
        for (int i = 0; i < genesA.length; i++){
            genesA[i] = (double)((genesA[i] + genesB[i])/2);
        }
        return new Board(new NeuralNetwork(inputDim, hiddenDim, outputDim, genesA));
    }

    private Board crossover3(Board a, Board b){
        return new Board(new NeuralNetwork(inputDim, hiddenDim, outputDim, crossover2(a,b).getController().mutateGenes(0.05)));
    }

    private Board crossover4(Board a, Board b){
        return new Board(new NeuralNetwork(inputDim, hiddenDim, outputDim, crossover1(a,b).getController().mutateGenes(0.05)));
    }

    //Updated
    public int numberOfDuplicates(Board individual) {
        int output = 0;
        for (Board board : this.getIndividualList()) {
            if (board.equals(individual)) {
                output++;
            }
        }
        return output;
    }
    //Updated
    public void sortIndividuals() {
        this.getIndividualList().sort((o1, o2) -> o2.getFitness().intValue() - o1.getFitness().intValue());
    }
    //Updated
    public List<Board> getFathers(float fathersPercentage) {
        Random random = new Random();
        List<Board> fatherList = new ArrayList<>();
        this.sortIndividuals();
        List<Board> genIndividuals = this.getIndividualList();
        int numberOfFathers = (int) (this.getSize() * fathersPercentage);
        for (int i = 0; i < numberOfFathers; i++) {
            fatherList.add(new Board(individualList.get(i).getController(), random.nextLong()));
        }
        return fatherList;
    }

    public String generationAverage(){
        double output = 0;
        for (Board board : this.getIndividualList()){
            output += board.getFitness();
        }
        return String.format("%.2f", output/size);
    }

    public String deathsAverage(){
        double output = 0;
        for (Board board : this.getIndividualList()){
            output += board.getDeaths();
        }
        return String.format("%.2f", output/size);
    }

    public Board getFittestBoard(){
        return this.getIndividualList().get(0);
    }

    //Updated
    public String printGenerationAverage(){
        return "Generation " + id + " - " + "Average Score: " + generationAverage() + " | Best Score: " + String.format("%.2f",getFittestBoard().getFitness()) +
                " | Average Kills: " + deathsAverage() + " |\n Best Kills: " + getFittestBoard().getDeaths() + " | " +
                " Best Time: " + getFittestBoard().getTime() + " | Best samePositionTime: " + getFittestBoard().getIsOnSamePosition() + " |\n";
    }
    //Updated
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("Generation " + id + "\n");
        for (int i = 0; i < this.size; i++) {
            output.append("Board ").append(i).append(" - ").append(individualList.get(i).getFitness()).append("\n");
        }
        return output + "\nGeneration Fitness Average: " + generationAverage() + "\n";
    }

}
