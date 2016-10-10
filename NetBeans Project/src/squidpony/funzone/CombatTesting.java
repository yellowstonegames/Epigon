package squidpony.funzone;

import squidpony.math.RLMath;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Eben
 */
public class CombatTesting {

    FileWriter out;

    public static void main(String[] args) throws IOException {
        new CombatTesting();
    }

    public CombatTesting() throws IOException {
        out = new FileWriter("Battle Test Output.csv");

        base90();
        out.close();
    }

    private void base90() {
        double baseChance = 0.9;
        double modChance;
        double hits;
        int aim, myAgility;
        myAgility = 400;//lock this at mid point

        for (aim = 0; aim <= 1000; aim++) {
            hits = 0.0;
            for (int i = 0; i < 1000; i++) {
                modChance = (aim - myAgility) / 10000.0;//makes the difference a percentage
            }
            if (hits / 1000.0 > .20) {//found average
                System.out.println("Requires aim " + aim + " to hit at least 20%");
                
                break;
            }
        }
    }

    private void find90() {
        double hits;
        int aim, agility;
        double hitRoll, dodgeRoll;
        agility = 400;//lock this at mid point

        for (aim = 0; aim <= 1000; aim++) {
            hits = 0.0;
            for (int i = 0; i < 1000; i++) {
                hitRoll = RLMath.getModifiedRandom(aim, 20);
                dodgeRoll = RLMath.getModifiedRandom(agility, 20);
                if (hitRoll - dodgeRoll > 0) {//counts as a hit
                    hits++;
                }
            }
            if (hits / 1000.0 > .20) {//found average
                System.out.println("Requires aim " + aim + " to hit at least 20%");
                break;
            }
        }
    }

    private void testBasicAttack() throws IOException {
        out.append("aim,agility,average difference, max difference, min difference\n");


        StatBlock p1 = new StatBlock();
        StatBlock p2 = new StatBlock();

        double hitRoll, dodgeRoll, difference;
        double max, min, average;
        int numRuns = 1000;
        for (int run = 0; run < 1000; run++) {//check all values
            p1.aim = run;
            max = Double.NEGATIVE_INFINITY;
            min = Double.POSITIVE_INFINITY;
            average = 0.0;
            for (int i = 0; i < numRuns; i++) {
                hitRoll = RLMath.getModifiedRandom(p1.aim, 20);
                dodgeRoll = RLMath.getModifiedRandom(p2.agility, 20);
                difference = hitRoll - dodgeRoll;
                average += difference;
                max = Math.max(max, difference);
                min = Math.min(min, difference);
            }
            out.append("" + p1.aim + "," + p2.agility + "," + average / numRuns + "," + max + "," + min + "\n");
        }
    }
}
