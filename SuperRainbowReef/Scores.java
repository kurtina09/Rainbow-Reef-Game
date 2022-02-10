package SuperRainbowReef;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author motiveg, monalimirel
 */
public class Scores {

    private ArrayList<String> highscores_str;
    private ArrayList<String> names;
    private ArrayList<Integer> scores;

    private String highscore_path;

    private String newName;
    private int newScore;

    private final int MAX_NUM_SCORES = 3;

    public Scores(String newName, int newScore, String highscore_path) {
        this.newName = newName;
        this.newScore = newScore;
        this.highscore_path = highscore_path;
    }

    public void updateHighScores() {
        getHighScores();
        parseHighScores();
        boolean updated = checkNewHighScore();
        if (updated) {
            rewriteHighScores();
            setLocalHighScoreList();
        }
    }

    private void getHighScores() {
        highscores_str = new ArrayList<>();
        URL url = getClass().getResource(highscore_path);
        File file = new File(url.getPath());

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String st;
            while ((st = br.readLine()) != null) {
                System.out.println(st);
                highscores_str.add(st);
            }

        } catch (IOException e) {
            System.out.println("High scores not found!");
        }

    }

    private void parseHighScores() {
        // tokenize each line of the high scores file into names and scores

        names = new ArrayList<>();
        scores = new ArrayList<>();

        String delimiters = " ";
        StringTokenizer st;
        String token;

        for (String line : highscores_str) {

            // initialize tokenizer
            st = new StringTokenizer(line, delimiters);

            token = st.nextToken();
            //System.out.println(token);
            names.add(token);

            token = st.nextToken();
            //System.out.println(token);
            scores.add(Integer.parseInt(token));
        }

    }

    // returns true if the new score is a high score
    private boolean checkNewHighScore() {
        // compare with each high score to see if it should be inserted/added
        for (int curr : scores) {
            if (newScore > curr) {

                // save the old name and score temporarily
                int curr_index = scores.indexOf(curr);

                String temp_name = names.get(curr_index);
                int temp_score = scores.get(curr_index);

                for (int i = curr_index; i < MAX_NUM_SCORES; i++) {
                    if (!newName.equals("") && newScore >= 0) {
                        names.set(i, newName);
                        scores.set(i, newScore);
                        newName = "";
                        newScore = -1;
                    } else {
                        names.set(i, temp_name);
                        scores.set(i, temp_score);
                        if (i + 1 < MAX_NUM_SCORES) {
                            temp_name = names.get(i + 1);
                            temp_score = scores.get(i + 1);
                        }
                    }
                } // end for loop

                // clear the arrays to size 3
                while (names.size() > 3) {
                    names.remove(names.size());
                }
                while (scores.size() > 3) {
                    scores.remove(scores.size());
                }

                System.out.println("New high score");
                return true;
            }
        } // end outer for loop
        return false;
    }

    private void setLocalHighScoreList() {
        // go through each name and each score
        this.highscores_str.clear();
        StringBuilder sb;
        for (int i = 0; i < names.size(); i++) {
            sb = new StringBuilder();
            sb.append(names.get(i));
            sb.append(" ");
            sb.append(scores.get(i));
            highscores_str.add(sb.toString());
        }
    }

    // NOTE: do not call unless updateHighScores() is called first
    public ArrayList<String> getHighScoreList() {
        return this.highscores_str;
    }

    private void rewriteHighScores() {
        // build a new string and write it to the highscores file
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MAX_NUM_SCORES; i++) {
            sb.append(names.get(i));
            sb.append(" ");
            sb.append(scores.get(i).toString());
            sb.append("\n");
        }

        // overwrite the existing highscores file with the new string
        URL url = getClass().getResource(highscore_path);
        File highscores = new File(url.getPath());
        highscores.delete();
        File newhighscores = new File(url.getPath());

        try {
            FileWriter fw = new FileWriter(newhighscores, false);
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            System.out.println("Failed to write to file for high scores!");
            e.printStackTrace();
        }
    }

}
