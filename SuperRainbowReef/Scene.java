package SuperRainbowReef;

import SuperRainbowReef.GameObject.Movable.Katch;
import SuperRainbowReef.GameObject.Movable.Pop;
import SuperRainbowReef.GameObject.Unmovable.Breakable.BigLegs;
import SuperRainbowReef.GameObject.Unmovable.Breakable.CoralBlocks;
import SuperRainbowReef.GameObject.Unmovable.Breakable.PowerUp;
import SuperRainbowReef.GameObject.Unmovable.Unbreakable.SolidBlocks;
import SuperRainbowReef.GameObject.Unmovable.Unbreakable.Wall;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author monalimirel, motiveg
 */
public class Scene extends JPanel {

    private BufferedImage bgImg, screen, winScreen, gameOverScreen; // added BufferedImage to draw to
    private BufferedImage lifeIcon;

    private int mapWidth, mapHeight, windowWidth, windowHeight;
    private int score = 0;
    private int level = 1;

    private ArrayList<Wall> walls;
    private ArrayList<SolidBlocks> solidBlocks;
    private ArrayList<CoralBlocks> coralBlocks;
    private ArrayList<BigLegs> bigLegs;
    private ArrayList<PowerUp> powerUp;

    private static Pop pop; // sure?
    private static Katch katch; // sure?

    private boolean gameOver = false;
    private boolean winner = false;
    private boolean levelUp = false;

    private ArrayList<String> highscores;

    private Scores scores;

    public Scene(int mapWidth, int mapHeight, int windowWidth, int windowHeight, String backgroundPath, String[] imgPaths, String win_Screen, String game_over) {
        super();
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;

        this.setSize(mapWidth, mapHeight);
        this.setPreferredSize(new Dimension(mapWidth, mapHeight));
        this.bgImg = setImage(backgroundPath);
        this.winScreen = setImage(win_Screen);
        this.gameOverScreen = setImage(game_over);

        walls = new ArrayList<>();
        solidBlocks = new ArrayList<>();
        coralBlocks = new ArrayList<>();
        bigLegs = new ArrayList<>();
        powerUp = new ArrayList<>();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        getGameImage();
        g.drawImage(screen, 0, 0, this); // draw the final image here
    }

    public void getGameImage() {
        // create buffered image
        BufferedImage bimg = new BufferedImage(mapWidth, mapHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bimg.createGraphics();

        checkGameStatus();
        // still playing game
        if (!gameOver && !winner) {
            drawBackground(g2);
            drawMapLayout(g2);
            drawKatch(g2);
            drawPop(g2);
            drawScore(g2);
            drawLife(g2);
            drawLevelNum(g2);
            screen = bimg;
            // level change
        } else if (levelUp) {
            drawBackground(g2);
            drawMapLayout(g2);
            drawKatch(g2);
            drawPop(g2);
            drawScore(g2);
            drawLife(g2);
            drawLevelNum(g2);
            System.out.println("Level" + level);
            levelUp = false;
            screen = bimg;
            // player won the game
        } else if (winner) {
            drawWinScreen(g2);
            drawHighScores(g2);
            screen = bimg;
            // player lost the game
        } else if (gameOver) {
            drawBackground(g2);
            drawGameOver(g2);
            drawScore(g2);
            drawLife(g2);
            drawLevelNum(g2);
            drawHighScores(g2);
            screen = bimg;
        }

    }

    // SETTERS //
    private BufferedImage setImage(String filePath) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            System.out.println("Error getting image." + e.getMessage());
        }
        return img;
    }

    public void setBackgroundImage(BufferedImage img) {
        this.bgImg = img;
    }

    public void setMapObjects(ArrayList<Wall> w, ArrayList<SolidBlocks> sb, ArrayList<CoralBlocks> cb, ArrayList<PowerUp> p,
            ArrayList<BigLegs> bl) {
        this.walls = w;
        this.solidBlocks = sb;
        this.coralBlocks = cb;
        this.powerUp = p;
        this.bigLegs = bl;
    }

    public void setWalls(ArrayList<Wall> walls) {
        this.walls = walls;
    }

    public void setSolidBlocks(ArrayList<SolidBlocks> solidBlocks) {
        this.solidBlocks = solidBlocks;
    }

    public void setCoralBlocks(ArrayList<CoralBlocks> coralBlocks) {
        this.coralBlocks = coralBlocks;
    }

    public void setPowerUps(ArrayList<PowerUp> powerUp) {
        this.powerUp = powerUp;
    }

    public void setBigLegs(ArrayList<BigLegs> bigLegs) {
        this.bigLegs = bigLegs;
    }

    public void setKatch(Katch katch) {
        this.katch = katch;
    }

    public void setPop(Pop pop) {
        this.pop = pop;
    }

    public void setLifeIcon(BufferedImage img) {
        this.lifeIcon = img;
    }

    public void setLevelUpStatus(boolean bool) {
        this.levelUp = bool;
    }

    // GETTERS //
    public BufferedImage getBackgroundImage() {
        return this.bgImg;
    }

    public int getLevel() {
        return level;
    }

    public boolean getLevelUpStatus() {
        return levelUp;
    }

    public boolean getWinStatus() {
        return this.winner;
    }

    public boolean getGameOver() {
        return this.gameOver;
    }

    public void setGameOver(boolean bool) {
        this.gameOver = bool;
    }

    public void setScoresObject(Scores scores) {
        this.scores = scores;
    }

    // DRAW METHODS //
    private void drawBackground(Graphics2D g) {
        //g.drawImage(this.bgImg, this.bgImg.getWidth(), this.bgImg.getHeight(),this);
        g.drawImage(this.bgImg, 0, 0, this); // the 2nd and 3rd arg indicate position
    }

    private void drawWinScreen(Graphics2D g) {
        g.drawImage(this.winScreen, 0, 0, 615, 470, this);
    }

    private void drawGameOver(Graphics2D g) {
        g.drawImage(this.gameOverScreen, 180, 300, this);
    }

    private void drawMapLayout(Graphics2D g) {
        walls.forEach((curr) -> {
            curr.draw(g);
        });
        solidBlocks.forEach((curr) -> {
            curr.draw(g);
        });
        coralBlocks.forEach((curr) -> {
            curr.draw(g);
        });
        powerUp.forEach((curr) -> {
            curr.draw(g);
        });
        bigLegs.forEach((curr) -> {
            curr.draw(g);
        });
    }

    private void drawKatch(Graphics2D g) {
        katch.draw(g);
    }

    private void drawPop(Graphics2D g) {
        pop.draw(this, g);
    }

    private void drawScore(Graphics g) {
        int playerScore = pop.getScore();
        g.drawString("Score: " + playerScore, 500, 400);
    }

    private void drawLife(Graphics2D g) {
        int playerLives = pop.getLife();
        //int offset = 30;
        //for(int i = 0; i<playerLives;i++){
        g.drawString("Lives: " + playerLives, 50, 400);
        //g.drawImage(lifeIcon, 100, 400, this);
        //g.drawImage(lifeIcon, 50+(i*offset), 400, this);
        //}
    }

    private void drawLevelNum(Graphics2D g) {
        g.drawString("Level:" + this.getLevel(), 500, 380);
    }

    private void drawHighScores(Graphics g) {
        // get the high scores
        if (highscores == null) {
            highscores = scores.getHighScoreList();
        }

        // change font color for different screen
        if (gameOver) {
            g.setColor(Color.BLACK);
        }
        if (winner) {
            g.setColor(Color.GREEN);
        }

        // high score heading
        g.setFont(new Font(g.getFont().getFontName(), Font.CENTER_BASELINE, 48));
        g.drawString("HIGH SCORES", this.mapWidth / 4, this.mapHeight / 5);

        // high scores
        g.setFont(new Font(g.getFont().getFontName(), Font.CENTER_BASELINE, 36));
        int scores_x = this.mapWidth / 4;
        int scores_y = (this.mapHeight / 5) + 60;
        int y_increment = 48;
        for (int i = 0; i < highscores.size(); i++) {
            g.drawString(highscores.get(i), scores_x, scores_y + (i * y_increment));
        }
    }

    public void checkGameStatus() {
        if (bigLegs.isEmpty()) {
            pop.respawn();
            levelUp = true;
            //levelUp
        } else if (pop.getLife() <= 0) {
            gameOver = true;
            //lose
        } else if (level > 3) {
            winner = true;
            //win
        }
    }

    public void levelUp() {
        level++;
        System.out.println("Level: " + level);
    }

}
