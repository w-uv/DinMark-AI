import java.util.Random;

/* 
 * Riches2RagsMachine represents a slot machine in the Escobar Casino.
 * 
 * Its characteristics include:
 * - Each machine has predefined probabilities for winning different amounts
 * - The play() method simulates a spin and returns the payout
 */
class Riches2RagsMachine {
    final private String name;
    final private double[] probabilities;
    final private int[] payouts = { 0, 1, 5, 100 };
    final private Random random;

    public Riches2RagsMachine(String name, double[] probabilities) {
        this.name = name;
        this.probabilities = probabilities;
        this.random = new Random();
    }

    /*
     * Simulates playing the slot machine
     * - Rolls a random number and determines the payout based on probabilities
     */
    public int play() {
        double roll = random.nextDouble() * 100; // Generate a random number from 0 to 100
        double cumulative = 0;

        for (int i = 0; i < probabilities.length; i++) {
            cumulative += probabilities[i];
            if (roll <= cumulative) {
                return payouts[i]; // Return the corresponding payout
            }
        }
        return 0; // If somehow nothing matches, return Php 0 (shouldn't happen in the first
                  // place!!)
    }

    public String getName() {
        return name;
    }
}

/**
 * DinMarkAi is the AI that decides which slot machine to play
 * - It starts by testing both machines equally
 * - Then, it chooses the machine that has given the highest payout
 */
class DinMarkAi {
    private int money;
    final private int totalRounds;
    final private Riches2RagsMachine slotA;
    final private Riches2RagsMachine slotB;
    private int playsOnA = 0, playsOnB = 0;
    private int winningsA = 0, winningsB = 0;

    public DinMarkAi(int money, int totalRounds, Riches2RagsMachine slotA, Riches2RagsMachine slotB) {
        this.money = money;
        this.totalRounds = totalRounds;
        this.slotA = slotA;
        this.slotB = slotB;
    }

    /*
     * Plays the entire game, round by round
     * - Decides which machine to play based on past winnings
     * - Prints out what happens in each round
     */
    public void playGame() {
        System.out.println("\n It's Time to Gamble!!! ");

        for (int round = 1; round <= totalRounds; round++) {

            if (money < 1) { // Stop playing if money is zero
                System.out.println("\n Out of money! Can't Gamble anymore :(");
                break;
            }

            Riches2RagsMachine chosenSlot = chooseSlot();
            int payOff = chosenSlot.play();
            money += (payOff - 1); // Pay Php 1 to play, add winnings

            // Track results for decision-making
            if (chosenSlot == slotA) {
                playsOnA++;
                winningsA += payOff;
            } else {
                playsOnB++;
                winningsB += payOff;
            }

            // Print what happened this round
            System.out.printf("Round %d: Played %s -> Won Php %d (Total Money: Php %d)\n",
                    round, chosenSlot.getName(), payOff, money);
        }

        // Print the final earnings and summary of the game
        System.out.println("\n Final Results | Money: Php " + money);
        System.out.println(" Summary:");
        System.out.println("Slot A: Played " + playsOnA + " times, Total Winnings: Php " + winningsA);
        System.out.println("Slot B: Played " + playsOnB + " times, Total Winnings: Php " + winningsB);
    }

    /*
     * IMPLEMENTED BY MARK
     * Determines which slot machine to play
     * - First 6 rounds: Try both machines equally (3 rounds each)
     * - After that, choose the one with the highest average payout
     */

    /*
     * private Riches2RagsMachine chooseSlot() {
     * if (playsOnA + playsOnB < 6) {
     * return (playsOnA < 3) ? slotA : slotB; // Try A for 3 rounds, then B for 3
     * rounds
     * }
     * 
     * // Calculate average winnings per play
     * double avgA = (playsOnA == 0) ? 0 : winningsA / (double) playsOnA;
     * double avgB = (playsOnB == 0) ? 0 : winningsB / (double) playsOnB;
     * 
     * return (avgA > avgB) ? slotA : slotB; // Choose the more profitable machine
     * }
     */

    /*
     * Determines which slot machine to play
     * - Gets the possible totalRounds from the totalPlaytime
     * - Divide the total Round into 3/5
     * - The 3/5 will serve as the exploring phase
     * - The remaining 2/5 will be the educated guess
     * - Educated guess, choose the one with the highest average payout
     */

    private Riches2RagsMachine chooseSlot() {
        // 3/5 ratio of totalRounds for exploring slot machine
        int explorationRounds = (int) ((totalRounds * 3) / 5.0);

        if (playsOnA + playsOnB < explorationRounds) {
            // Try both slot machines equally during exploring segment
            return (playsOnA <= playsOnB) ? slotA : slotB;
        }

        // Calculate average winnings per play after exploring
        double avgA = (playsOnA == 0) ? 0 : winningsA / (double) playsOnA;
        double avgB = (playsOnB == 0) ? 0 : winningsB / (double) playsOnB;

        return (avgA > avgB) ? slotA : slotB; // Choose the more profitable machine
    }
}

/*
 * ShadyCasino manages the game and connects the DinMarkAi with the
 * Riches2RagsMachine
 * - Creates the slot machines and the agent
 * - Runs the game until the total playtime is used up
 */
class ShadyCasino {
    final private DinMarkAi agent;

    public ShadyCasino(int money, int totalRounds, double[] slotAChances, double[] slotBChances) {
        /**
         * Create two slot machines with different probability distributions
         * - The agent does NOT know these probabilities
         */
        Riches2RagsMachine slotA = new Riches2RagsMachine("Slot A", slotAChances);
        Riches2RagsMachine slotB = new Riches2RagsMachine("Slot B", slotBChances);

        /**
         * Create an agent that will play the game and decide the best slot
         * - It starts with the given amount of money
         * - It will learn which slot is better over time
         */
        this.agent = new DinMarkAi(money, totalRounds, slotA, slotB);
    }

    /**
     * Runs the game by letting the agent play
     */
    public void runGame() {
        agent.playGame();
    }
}

public class Main {
    public static void main(String[] args) {
        /*
         ***************
         * TESTING THE THE DinMarkAi agent with based on the defined user input
         * - NOTE: Each round is 10 seconds
         ***************
         */
        // SAMPLE INPUT
        // ***********************************/
        int money = 20;
        int totalPlayTime = 100;

        double[] slotAChances = { 50, 30, 15, 5 };
        double[] slotBChances = { 40, 35, 20, 5 };
        // ***********************************/
        /*
         * Create the instance of the ShadyCasino and start the game
         * The agent inside will make decisions based on winnings
         */
        ShadyCasino casino = new ShadyCasino(money, totalPlayTime / 10, slotAChances, slotBChances);
        casino.runGame();
    }
}