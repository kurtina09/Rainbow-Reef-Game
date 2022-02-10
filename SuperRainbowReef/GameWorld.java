/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperRainbowReef;

import SuperRainbowReef.GameObject.Movable.Katch;
import SuperRainbowReef.GameObject.Movable.Pop;
import SuperRainbowReef.GameObject.Unmovable.Breakable.BigLegs;
import SuperRainbowReef.GameObject.Unmovable.Breakable.CoralBlocks;
import SuperRainbowReef.GameObject.Unmovable.Breakable.PowerUp;
import SuperRainbowReef.GameObject.Unmovable.Unbreakable.SolidBlocks;
import SuperRainbowReef.GameObject.Unmovable.Unbreakable.Wall;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author monalimirel, motiveg
 */
public class GameWorld implements Runnable {

    // JFrame properties //
    private JFrame frame;
    private String frame_title;
    private int frame_width, frame_height;
    private int map_width, map_height;

    // Resource paths //
    private String background_path;
    private String wall_path; //1
    private String solid_block; //2
    private String blue_block; //3
    private String red_block; //4
    private String yellow_block; //5
    private String purple_block; //6
    private String green_block; //7
    private String life_block; //8
    private String split_block; //9
    private String bigLegs_path; //10
    private String smallLegs_path; //11
    private String pop_path;
    private String katch_path;
    private String life_icon;
    private String win_screen;
    private String game_over;
    private String img_paths[];

    private String music_path;
    private String sound_paths[];

    private String highscore_path;

    // Sound objects //
    private GameSounds music;
    private GameSounds sound_click;
    private GameSounds sound_katch;
    private GameSounds sound_wall;
    private GameSounds sound_block;
    private GameSounds sound_bigleg;
    private GameSounds sound_lost;

    // Map properties //
    private final int NUM_ROWS = 25, NUM_COLS = 25;
    private int[][] mapLayout;

    // Swing parts //
    private Scene scene;

    // Observables //
    private final GameObservable gobs;
    private KeyInput keyinput;

    // Active fields //
    private Thread thread;
    private boolean running = false;

    // Game objects //
    private ArrayList<Wall> walls;
    private ArrayList<SolidBlocks> solidBlocks;
    private ArrayList<CoralBlocks> coralBlocks;
    private ArrayList<BigLegs> bigLegs;
    private ArrayList<PowerUp> powerUp;
    private static Pop pop;
    private static Katch katch;

    public static void main(String args[]) {
        GameWorld rainbowWorld = new GameWorld();
        rainbowWorld.start();
    }

    private void initWorldProperties() {
        this.frame_title = "Super Rainbow Reef";
        this.frame_width = 615;
        this.frame_height = 470;
        this.map_width = 615;
        this.map_height = 470;

    }

    private void initResourcePaths() {
        // IMAGES
        background_path = "Resources/Background1.bmp";
        wall_path = "Resources/Wall.gif";
        solid_block = "Resources/Block_solid.gif";
        blue_block = "Resources/Block6.gif";
        red_block = "Resources/Block3.gif";
        yellow_block = "Resources/Block2.gif";
        purple_block = "Resources/Block1.gif";
        green_block = "Resources/Block4.gif";
        life_block = "Resources/Block_life.gif";
        split_block = "Resources/Block_split.gif";
        bigLegs_path = "Resources/Bigleg_transparent.gif";
        smallLegs_path = "Resources/Bigleg_small_transparent.gif";
        pop_path = "Resources/Pop_transparent.gif"; // transparent edit
        katch_path = "Resources/Katch_transparent.gif"; // transparent edit
        life_icon = "Resources/Katch_small.png";
        win_screen = "Resources/Congratulation.gif";
        game_over = "Resources/gameover.png";

        img_paths = new String[]{life_icon};

        music_path = "Resources/Music.wav";
        sound_paths = new String[]{"Resources/Sound_click.wav",
            "Resources/Sound_katch.wav",
            "Resources/Sound_wall.wav",
            "Resources/Sound_block.wav",
            "Resources/Sound_bigleg.wav",
            "Resources/Sound_lost.wav"};

        highscore_path = "/highscores";
    }

    private void setupKatchAndPop() {
        BufferedImage katch_img = setImage(katch_path);
        BufferedImage pop_img = setImage(pop_path);
        //ImageIcon katch_icon = new ImageIcon(katch_path);
        this.katch = new Katch(this, katch_img,
                this.map_width / 2 - katch_img.getWidth() / 2, this.map_height - katch_img.getHeight() - pop_img.getHeight(),
                3, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
        //KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,KeyEvent.VK_SPACE,KeyEvent.VK_A, KeyEvent.VK_D
        //this.katch.setImageIcon(katch_icon);
        this.pop = new Pop(this, this.katch, pop_img, 1);
        this.katch.setPop(this.pop);
        this.keyinput = new KeyInput(katch);
        this.gobs.addObserver(katch);
        this.gobs.addObserver(pop);
        this.scene.setKatch(this.katch);
        this.scene.setPop(this.pop);
    }

    private void setupSounds() {
        music = new GameSounds(1, music_path);

        sound_click = new GameSounds(2, sound_paths[0]);
        sound_katch = new GameSounds(2, sound_paths[1]);
        sound_wall = new GameSounds(2, sound_paths[2]);
        sound_block = new GameSounds(2, sound_paths[3]);
        sound_bigleg = new GameSounds(2, sound_paths[4]);
        sound_lost = new GameSounds(2, sound_paths[5]);
    }

    private void setupMap() {
        setMapLayout(scene.getLevel());
        createMapObjects();
    }

    // 0:empty, 1:wall, 2:solid, 3:blue, 4:red, 5:yellow, 6:purple, 7:green
    // 8:life, 9:split, 10:bigLegs, 11:smallLegs
    private void setMapLayout(int level) {
        System.out.print("MAP LAYOUT");
        if (level == 1) {
            this.mapLayout = new int[][]{ // size: 24 x 32
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 7, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 3, 0, 1},
                    {1, 6, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 5, 0, 1},
                    {1, 3, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 4, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},};
        } else if (level == 2) {
            this.mapLayout = new int[][]{ // size: 24 x 32
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 11,0,11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,11, 0,11, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 3, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 5, 0, 1},
                {1, 4, 0, 7, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 6, 0, 4, 0, 1},
                {1, 6, 0, 6, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 7, 0, 7, 0, 1},
                {1, 5, 0, 3, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 5, 0, 6, 0, 1},
                {1, 3, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 3, 0, 1},
                {1, 7, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 5, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},};
        } else if (level == 3) {
            this.mapLayout = new int[][]{ // size: 24 x 32
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 10, 0, 0, 0, 2, 0, 11, 0, 7, 0, 2, 0, 6, 0, 10, 0, 0, 0, 2, 0, 7, 0, 11, 0, 2, 0, 8, 0, 8, 0, 1},
                {1, 0, 0, 0, 0, 2, 0, 0, 0, 7, 0, 2, 0, 6, 0, 0, 0, 0, 0, 2, 0, 7, 0, 0, 0, 2, 0, 4, 0, 3, 0, 1},
                {1, 0, 0, 0, 0, 2, 0, 5, 0, 7, 0, 2, 0, 6, 0, 0, 0, 0, 0, 2, 0, 7, 0, 5, 0, 2, 0, 11, 0, 3, 0, 1},
                {1, 0, 0, 0, 0, 2, 0, 8, 0, 8, 0, 2, 0, 6, 0, 0, 0, 0, 0, 2, 0, 7, 0, 5, 0, 2, 0, 0, 0, 3, 0, 1},
                {1, 3, 0, 4, 0, 2, 0, 5, 0, 7, 0, 2, 0, 6, 0, 4, 0, 6, 0, 2, 0, 7, 0, 5, 0, 2, 0, 4, 0, 3, 0, 1},
                {1, 3, 0, 4, 0, 2, 0, 5, 0, 7, 0, 2, 0, 6, 0, 4, 0, 6, 0, 2, 0, 7, 0, 5, 0, 2, 0, 4, 0, 3, 0, 1},
                {1, 3, 0, 11, 0, 2, 0, 5, 0, 8, 0, 2, 0, 6, 0, 4, 0, 6, 0, 2, 0, 8, 0, 5, 0, 2, 0, 4, 0, 3, 0, 1},
                {1, 3, 0, 0, 0, 2, 0, 5, 0, 7, 0, 2, 0, 11, 0, 4, 0, 6, 0, 2, 0, 7, 0, 5, 0, 2, 0, 4, 0, 3, 0, 1},
                {1, 3, 0, 4, 0, 2, 0, 5, 0, 7, 0, 2, 0, 0, 0, 4, 0, 6, 0, 2, 0, 7, 0, 5, 0, 2, 0, 4, 0, 11, 0, 1},
                {1, 3, 0, 4, 0, 2, 0, 8, 0, 7, 0, 2, 0, 6, 0, 4, 0, 6, 0, 2, 0, 7, 0, 8, 0, 2, 0, 4, 0, 0, 0, 1},
                {1, 3, 0, 4, 0, 2, 0, 5, 0, 7, 0, 2, 0, 6, 0, 4, 0, 6, 0, 2, 0, 7, 0, 5, 0, 2, 0, 4, 0, 3, 0, 1},
                {1, 8, 0, 8, 0, 2, 0, 5, 0, 7, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 4, 0, 3, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},};
        }

    }

    public void createMapObjects() {
        walls = new ArrayList<>();
        solidBlocks = new ArrayList<>();
        coralBlocks = new ArrayList<>();
        bigLegs = new ArrayList<>();
        powerUp = new ArrayList<>();
        BufferedImage objectImage;
        int cell_size = 19;
        for (int row = 0; row < 23; row++) {
            for (int column = 0; column < 32; column++) {
                //Wall
                if (mapLayout[row][column] == 1) {
                    objectImage = setImage(wall_path);
                    walls.add(new Wall(column * cell_size, row * cell_size,
                            objectImage.getWidth(), objectImage.getHeight(), objectImage));
                }
                //Solid blocks
                if (mapLayout[row][column] == 2) {
                    objectImage = setImage(solid_block);
                    solidBlocks.add(new SolidBlocks(column * cell_size, row * cell_size, objectImage.getWidth(), objectImage.getHeight(), objectImage));
                }
                //Coral blocks
                if (mapLayout[row][column] == 3) {
                    objectImage = setImage(blue_block);
                    coralBlocks.add(new CoralBlocks(column * cell_size, row * cell_size, objectImage.getWidth(), objectImage.getHeight(), objectImage, 10));
                }
                if (mapLayout[row][column] == 4) {
                    objectImage = setImage(red_block);
                    coralBlocks.add(new CoralBlocks(column * cell_size, row * cell_size, objectImage.getWidth(), objectImage.getHeight(), objectImage, 10));
                }
                if (mapLayout[row][column] == 5) {
                    objectImage = setImage(yellow_block);
                    coralBlocks.add(new CoralBlocks(column * cell_size, row * cell_size, objectImage.getWidth(), objectImage.getHeight(), objectImage, 10));
                }
                if (mapLayout[row][column] == 6) {
                    objectImage = setImage(purple_block);
                    coralBlocks.add(new CoralBlocks(column * cell_size, row * cell_size, objectImage.getWidth(), objectImage.getHeight(), objectImage, 10));
                }
                if (mapLayout[row][column] == 7) {
                    objectImage = setImage(green_block);
                    coralBlocks.add(new CoralBlocks(column * cell_size, row * cell_size, objectImage.getWidth(), objectImage.getHeight(), objectImage, 10));
                }
                if (mapLayout[row][column] == 8) {
                    objectImage = setImage(life_block);
                    powerUp.add(new PowerUp(column * cell_size, row * cell_size, objectImage.getWidth(), objectImage.getHeight(), objectImage, 50, "life"));
                }
                if (mapLayout[row][column] == 9) {
                    objectImage = setImage(split_block);
                    powerUp.add(new PowerUp(column * cell_size, row * cell_size, objectImage.getWidth(), objectImage.getHeight(), objectImage, 50, "split"));
                }
                if (mapLayout[row][column] == 10) {
                    objectImage = setImage(bigLegs_path);
                    bigLegs.add(new BigLegs(column * cell_size, row * cell_size, objectImage.getWidth(), objectImage.getHeight(), objectImage, 100));
                }
                if (mapLayout[row][column] == 11) {
                    objectImage = setImage(smallLegs_path);
                    bigLegs.add(new BigLegs(column * cell_size, row * cell_size, objectImage.getWidth(), objectImage.getHeight(), objectImage, 100));
                }

            }
        }
        this.scene.setWalls(walls);
        this.scene.setSolidBlocks(solidBlocks);
        this.scene.setCoralBlocks(coralBlocks);
        this.scene.setPowerUps(powerUp);
        this.scene.setBigLegs(bigLegs);

        walls.forEach((curr) -> {
            this.gobs.addObserver(curr);
        });
        solidBlocks.forEach((curr) -> {
            this.gobs.addObserver(curr);
        });
        coralBlocks.forEach((curr) -> {
            this.gobs.addObserver(curr);
        });
        powerUp.forEach((curr) -> {
            this.gobs.addObserver(curr);
        });
        bigLegs.forEach((curr) -> {
            this.gobs.addObserver(curr);
        });

    }

    private void setupFrame() {
        frame = new JFrame(frame_title);

        // GAME WINDOW
        this.frame.setSize(frame_width, frame_height);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setPreferredSize(new Dimension(frame_width, frame_height));//
        this.frame.setResizable(false);
        this.frame.setLocationRelativeTo(null);
        this.frame.add(this.scene); // add jpanel to the frame

        this.frame.addKeyListener(keyinput);

        this.frame.pack(); // pack the frame before displaying
        this.frame.setVisible(true);
    }

    private void init() {
        initWorldProperties();
        initResourcePaths();

        this.scene = new Scene(map_width, map_height, frame_width, frame_height, background_path, img_paths, win_screen, game_over);

        setupKatchAndPop();
        setupSounds();
        setupMap();
        setupFrame();

    }

    public GameWorld() {
        this.gobs = new GameObservable();
    }

    @Override
    public void run() {
        // input player name
        String name;
        Object message = "Enter your name";
        name = JOptionPane.showInputDialog(message);

        init();

        boolean alreadyRan = false; // helps lock win and gameover in game loop

        try {
            while (running) {
                this.gobs.setChanged();
                this.gobs.notifyObservers();
                render();

                // update high scores after game over
                if (this.scene.getGameOver() && !alreadyRan) {
                    alreadyRan = true;
                    Scores scores = new Scores(name, pop.getScore(), highscore_path);
                    scores.updateHighScores();
                    this.scene.setScoresObject(scores);
                }

                // update high scores after winning
                if (this.scene.getWinStatus() && !alreadyRan) {
                    alreadyRan = true;

                    Scores scores = new Scores(name, pop.getScore(), highscore_path);
                    scores.updateHighScores();
                    this.scene.setScoresObject(scores);
                }

                Thread.sleep(1000 / 144);

                // update level if previous level is completed
                if (scene.getLevelUpStatus()) {
                    scene.setLevelUpStatus(false);
                    scene.levelUp();
                    coralBlocks.removeAll(coralBlocks);
                    solidBlocks.removeAll(solidBlocks);
                    powerUp.removeAll(powerUp);
                    setMapLayout(scene.getLevel());
                    createMapObjects();
                    if (this.scene.getGameOver()) {
                        System.out.println("GAME OVER");
                    }
                }

            }
        } catch (InterruptedException e) {
            Logger.getLogger(GameWorld.class.getName()).log(Level.SEVERE, null, e);
        }

        stop();

    }

    private void render() {
        this.scene.repaint();
        //System.out.println(katch.toString());
    }

    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    private void stop() {
        if (!running) {
            return;
        }
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // SETTERS //
    public BufferedImage setImage(String filepath) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(filepath));
        } catch (IOException e) {
            System.out.println("Error getting Image" + e.getMessage());
        }
        return img;
    }

    // GETTERS //
    public int getMapWidth() {
        return this.map_width;
    }

    public int getMapHeight() {
        return this.map_height;
    }

    public ArrayList<Wall> getWalls() {
        return this.walls;
    }

    public ArrayList<SolidBlocks> getSolidBlocks() {
        return this.solidBlocks;
    }

    public ArrayList<CoralBlocks> getCoralBlocks() {
        return this.coralBlocks;
    }

    public ArrayList<BigLegs> getBigLegs() {
        return this.bigLegs;
    }

    public ArrayList<PowerUp> getPowerUp() {
        return this.powerUp;
    }

    public void playSound(int sound_index) {
        if (sound_index == 0) {
            sound_click.play();
        }
        if (sound_index == 1) {
            sound_katch.play();
        }
        if (sound_index == 2) {
            sound_wall.play();
        }
        if (sound_index == 3) {
            sound_block.play();
        }
        if (sound_index == 4) {
            sound_bigleg.play();
        }
        if (sound_index == 5) {
            sound_lost.play();
        }
    }

    public void resetSoundEffect(int sound_index) {
        if (sound_index == 0) {
            sound_click.resetClip();
        }
        if (sound_index == 1) {
            sound_katch.resetClip();
        }
        if (sound_index == 2) {
            sound_wall.resetClip();
        }
        if (sound_index == 3) {
            sound_block.resetClip();
        }
        if (sound_index == 4) {
            sound_bigleg.resetClip();
        }
        if (sound_index == 5) {
            sound_lost.resetClip();
        }
    }

}
