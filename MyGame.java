// CS 2510 Fall 2013

// Assignment 3
// pair Christopher Clark & Amanda Fode
// Clark, Christopher
// cclark07
// Fode, Amanda
// afode11

import java.awt.Color;
import java.util.Random;

import tester.*;
import javalib.funworld.*;
import javalib.colors.*;
import javalib.worldimages.*;

// represents a location in cartesian points
// extends the Posn class to allow for custom methods
class CartPt extends Posn {

    // CartPt class constructor
    CartPt(int x, int y) {
        super(x, y);
    }

    // move this CartPt down by the given integer
    // once it reaches the bottom it is placed back at -10
    CartPt moveDown(int s) {
        if (this.y + s > 170) {
            return new CartPt(this.randomInt(300), this.y = -5);
        }
        else {
            return new CartPt(this.x, this.y + s);
        }
    }

    // compute the distance from this point to the given one
    double distTo(CartPt that) {
        return Math.sqrt((this.x - that.x) * (this.x - that.x) + 
                (this.y - that.y) * (this.y - that.y));
    }

    // generates a random number in the range 0 to n
    int randomInt(int n) {
        return (new Random().nextInt(n));
    }
}

// class representing a movable Player
class Player {
    CartPt loc;
    IColor color;
    int size;

    // Player class constructor
    Player(CartPt loc, IColor color, int size) {
        this.loc = loc;
        this.color = color;
        this.size = size;
    }
    
    /* TEMPLATE
      FIELDS:
      ... this.loc ...                   -- CartPt
      ... this.color ...                 -- IColor
      ... this.size ...                  -- int
      METHODS:
      ... this.playerImage() ...         -- WorldImage
      ... this.playerMove(String) ...    -- Player
     */

    // draws this Player's image
    WorldImage playerImage() {
        return new RectangleImage(this.loc, this.size, this.size, this.color);
    }

    // moves this player according to the given key event
    public Player playerMove(String ke) {
        if ((ke.equals("left")) && (this.loc.x > 20)) {
            return new Player(
                    new CartPt(this.loc.x - 20, this.loc.y), 
                    this.color, this.size);
        }
        else if ((ke.equals("right")) && (this.loc.x < 280)) {
            return new Player(
                    new CartPt(this.loc.x + 20, this.loc.y), 
                    this.color, this.size);
        }
        else {
            return this;
        }
    }
}

//to represent a list of Fireballs
interface ILoFireball {
 
    // create image of fireball
    WorldImage listImages();
 
    // move this list of Fireballs
    ILoFireball moveFireballs();
 
    // determine if any Fireballs in this list
    // collides with the given player
    boolean collision(Player p);
}

//to represent an empty list of Fireballs
class MtLoF implements ILoFireball {
    MtLoF() {
        //MtLoF Constructor
    }

    // returns a black dot that is off screen
    public WorldImage listImages() {
        return new CircleImage(new Posn(-30, -30), 1, new Black());
    }

    // move empty Fireball list
    public ILoFireball moveFireballs() {
        return this;
    }

    // determine if any Fireballs in this list collide with the given player
    public boolean collision(Player p) {
        return false;
    }
}

//to represent a non-empty list of Fireballs
class ConsLoF implements ILoFireball {
    Fireball first;
    ILoFireball rest;

    // ConsLoF constructor
    ConsLoF(Fireball first, ILoFireball rest) {
        this.first = first;
        this.rest = rest;
    }
    
    /* TEMPLATE
    FIELDS:
    ... this.first ...                      -- Fireball
    ... this.rest ...                       -- ILoFireBall
    METHODS:
    ... this.listImages() ...               -- WorldImage
    ... this.moveFireballs() ...            -- ILoFireball
    ... this.collision(Player) ...          -- boolean
    METHODS FOR FIELDS:
    ... this.first.fireballImage() ...      -- WorldImage
    ... this.first.moveFireball() ...       -- Fireball
    ... this.first.hitsPlayer(Player) ...   -- boolean
    ... this.rest.listImages() ...          -- WorldImage
    ... this.rest.moveFireballs() ...       -- ILoFireball
    ... this.rest.collision(Player) ...     -- boolean
   */

    // overlay this list of fireball images onto background
    public WorldImage listImages() {
        return new OverlayImages(this.first.fireballImage(), 
                this.rest.listImages());
    }

    // move this list of fireballs
    public ILoFireball moveFireballs() {
        return new ConsLoF(this.first.moveFireball(), 
                this.rest.moveFireballs());
    }

    // determine if any objects in this list collide with given player
    // Fireball and player collision ends the game
    public boolean collision(Player p) {
        return (this.first.hitsPlayer(p) || this.rest.collision(p));
    }
}

//to represent a Fireball
class Fireball {
    CartPt loc;
    IColor color;
    int speed;

    // Fireball class constructor
    Fireball(CartPt loc, IColor color, int speed) {
        this.loc = loc;
        this.color = color;
        this.speed = speed;
    }
    
    /* TEMPLATE
      FIELDS:
      ... this.loc ...                  -- CartPt
      ... this.color ...                -- IColor
      ... this.speed ...                -- int
      METHODS:
      ... this.moveFireball() ...       -- Fireball
      ... this.fireballImage() ...      -- WorldImage
      ... this.hitsPlayer(Player) ...   -- boolean
    */

    // move fireball downwards
    public Fireball moveFireball() {
        return new Fireball(this.loc.moveDown(this.speed), 
                this.color, 
                this.speed);
    }

    // draws this Fireball's image
    WorldImage fireballImage() {
        return new DiskImage(this.loc, 5, this.color);
    }

    // determines if this Fireball collides with given player
    public boolean hitsPlayer(Player p) {
        return (this.loc.distTo(p.loc)) < 20;
    }
}

//represents the game world
class FireballStage extends World {
    int width = 300;
    int height = 200;
    Player player;
    ILoFireball fireballs;

    // FireballStage class constructor
    FireballStage(Player player, ILoFireball fireballs) {
        super();
        this.player = player;
        this.fireballs = fireballs;
    }
    
    /* TEMPLATE:
    FIELDS:
    ... this.width ...                                -- int
    ... this.height ...                               -- int
    ... this.player ...                               -- Player
    ... this.fireballs ...                            -- ILoFireball
    ... this.stage ...                                -- WorldImage
    METHODS:
    ... this.onKeyEvent(String) ...                   -- World
    ... this.endOfWorld(String) ...                   -- World
    ... this.onTick() ...                             -- FireballStage
    ... this.makeImage() ...                          -- WorldImage
    ... this.lastImage(String s) ...                  -- WorldImage
    ... this.worldEnds() ...                          -- WorldEnd
    METHODS FOR FIELDS:
    ... this.fireballs.listImages() ...               -- WorldImage
    ... this.fireballs.moveFireballs() ...            -- ILoFireball
    ... this.fireballs.collision(Player) ...          -- boolean
    ... this.player.playerImage() ...                 -- WorldImage
    ... this.playerMove(String) ...                   -- Player
    */

    // ends game if player pushes Shift + x
    // otherwise moves the player according to the input
    public World onKeyEvent(String ke) {
        if (ke.equals("X")) {
            return this.endOfWorld("Thanks for playing!");
        }
        else {
            return new FireballStage(this.player.playerMove(ke), 
                    this.fireballs.moveFireballs());
        }
    }
    
    // On tick move the Fireballs down this scene
    public FireballStage onTick() {
        return new FireballStage(this.player, this.fireballs.moveFireballs());
    }

    // The background for this world
    public WorldImage stage = 
            new OverlayImages(
                    new RectangleImage(new Posn(150, 100), 
                            this.width, this.height, new Black()),
                            new RectangleImage(new Posn(150, 185), 
                                    this.width, 30, new Blue()));

    // produces image of this world with the player and fireballs added
    public WorldImage makeImage() {
        return new OverlayImages(this.stage, 
                new OverlayImages(this.player.playerImage(),
                        this.fireballs.listImages()));
    }

    // returns this world's last image, with a line of final text added
    public WorldImage lastImage(String s) {
        return new OverlayImages(this.makeImage(),
                new TextImage(new Posn(150, 25), s, 
                        Color.red));
    }
 
    // checks whether player has been hit by a Fireball
    // ends game with a line of final text
    public WorldEnd worldEnds() {
        if (this.fireballs.collision(this.player)) {
            return 
                    new WorldEnd(true,
                            new OverlayImages(this.makeImage(),
                                    new TextImage(new Posn(150, 25), 
                                            "You got burned!", 
                                            Color.red)));
        }
        else {
            return new WorldEnd(false, this.makeImage());
        }
    }
}

// Examples and Tests
class ExamplesGame {

    // examples of CartPt class
    CartPt p1 = new CartPt(100, 180);
    CartPt p2 = new CartPt(80, 80);
    CartPt p3 = new CartPt(120, 0);
    
    // example of MtLoF class
    ILoFireball mt = new MtLoF();

    // example of Player class
    Player player = (new Player(new CartPt(150, 160), new White(), 20));

    //examples of Fireball class
    Fireball f1 = new Fireball(new CartPt(10, -5), new Yellow(), 10);
    Fireball f2 = new Fireball(new CartPt(30, -5), new Red(), 12);
    Fireball f3 = new Fireball(new CartPt(40, -5), new Yellow(), 8);
    Fireball f4 = new Fireball(new CartPt(90, -5), new Red(), 14);
    Fireball f5 = new Fireball(new CartPt(120, -5), new Yellow(), 4);
    Fireball f6 = new Fireball(new CartPt(160, -5), new Red(), 12);
    Fireball f7 = new Fireball(new CartPt(190, -5), new Yellow(), 6);
    Fireball f8 = new Fireball(new CartPt(100, -5), new Yellow(), 2);
    Fireball f9 = new Fireball(new CartPt(195, -5), new Yellow(), 10);
    
    Fireball f0 = new Fireball(new CartPt(150, 150), new Yellow(), 10);

    // examples of ConsLoF class
    ILoFireball list0 = new ConsLoF(this.f1,
            new ConsLoF(this.f2,
                    new ConsLoF(this.f3,
                            new ConsLoF(this.f4, 
                                    new ConsLoF(this.f0, this.mt)))));
    
    ILoFireball fireballList = new ConsLoF(this.f1,
            new ConsLoF(this.f2,
                    new ConsLoF(this.f3,
                            new ConsLoF(this.f4,
                                    new ConsLoF(this.f5, 
                                            new ConsLoF(this.f6, 
                                                    new ConsLoF(this.f7, 
                                                            new ConsLoF(
                                                                    this.f8,
                                                                    new ConsLoF(
                                        this.f9, 
                                                                     this.mt)
                                ))))))));

    // examples of FireballStage class
    FireballStage w0 = 
            new FireballStage(this.player, this.list0);
    
    FireballStage w1 = 
            new FireballStage(this.player, this.fireballList);
    
    boolean runAnimation = this.w1.bigBang(300, 200, 0.1);
    
    
    // test the method playerMove in the Player class
    boolean testPlayerMove(Tester t) {
        return
        t.checkExpect(this.player.playerMove("left"), 
                new Player(
                        new CartPt(this.player.loc.x - 20, this.player.loc.y), 
                        this.player.color, this.player.size)) &&
        t.checkExpect(this.player.playerMove("right"), 
                new Player(
                        new CartPt(this.player.loc.x + 20, this.player.loc.y), 
                        this.player.color, this.player.size));
    }

    
    // test the method onKeyEvent in the FireballStage class
    boolean testOnKeyEvent(Tester t) {
        return
        t.checkExpect(this.w1.onKeyEvent("left"), 
                    new FireballStage(this.player.playerMove("left"), 
                            this.fireballList.moveFireballs())) &&
        t.checkExpect(this.w1.onKeyEvent("right"), 
                    new FireballStage(this.player.playerMove("right"), 
                            this.fireballList.moveFireballs())) &&

        // to test the world ending, verify the value of the lastWorld
        t.checkExpect(this.w0.onKeyEvent("X").lastWorld,
            new WorldEnd(true,
                new OverlayImages(this.w0.makeImage(),
                    new TextImage(new Posn(150, 25), "Thanks for playing!", 
                          Color.red))));
    }
    
    // test the method moveDown in the CartPt class
    boolean testMoveDown(Tester t) {
        return
        t.checkExpect(this.p2.moveDown(10),
                new CartPt(80, 90)) &&
        t.checkExpect(this.p3.moveDown(10),
                new CartPt(120, 10));
    }
    
    // test the method randomInt in the CartPt class
    boolean testRandomInt(Tester t) {
        return
        t.checkRange(this.player.loc.randomInt(300), 0, 300, 
                "To test randomInt") &&
        t.checkNoneOf("To test randomInt", 
                      this.player.loc.randomInt(300), -1, -100, 301, 400);
    }
    
    // test the method hitsPlayer in the Fireball class
    boolean testHitsPlayer(Tester t) {
        return
        t.checkExpect(this.f0.hitsPlayer(this.player), true) &&
        t.checkExpect(this.f1.hitsPlayer(this.player), false);
    }
    
    // test the method moveFireball in the Fireball class
    boolean testMoveFireball(Tester t) {
        return
        t.checkExpect(this.f1.moveFireball(),
                new Fireball(this.f1.loc.moveDown(this.f1.speed), 
                        this.f1.color, 
                        this.f1.speed)) &&
        t.checkExpect(this.f2.moveFireball(),
                new Fireball(this.f2.loc.moveDown(this.f2.speed), 
                        this.f2.color, 
                        this.f2.speed));
    }
    
    // test the method moveFireballs in the ILoF interface
    boolean testMoveFireballs(Tester t) {
        return
        t.checkExpect(this.list0.moveFireballs(),
                new ConsLoF(this.f1.moveFireball(),
                        new ConsLoF(this.f2.moveFireball(),
                                new ConsLoF(this.f3.moveFireball(),
                                        new ConsLoF(this.f4.moveFireball(),
                                                new ConsLoF(
                                                        this.f0.moveFireball(),
                                                        this.mt)))))) &&
        t.checkExpect(this.mt.moveFireballs(), this.mt);
    }

    // test the method listImages in the ILoF interface
    boolean testlistImages(Tester t) {
        return
        t.checkExpect(this.list0.listImages(), 
            new OverlayImages(this.f1.fireballImage(), 
                new OverlayImages(this.f2.fireballImage(), 
                    new OverlayImages(this.f3.fireballImage(), 
                        new OverlayImages(this.f4.fireballImage(), 
                            new OverlayImages(this.f0.fireballImage(), 
                                    this.mt.listImages())))))) &&
        t.checkExpect(this.mt.listImages(), new CircleImage(
                new Posn(-30, -30), 1, new Black()));
    }
    
    // test the method collision in the ILoF interface
    boolean testCollision(Tester t) {
        return
                t.checkExpect(this.mt.collision(this.player), false) &&
                t.checkExpect(this.fireballList.collision(this.player), 
                        false) &&
                t.checkExpect(this.list0.collision(this.player), true) &&
                t.checkExpect(new ConsLoF(new Fireball(new CartPt(150, 160),
                        new Red(), 10), this.mt).collision(this.player), 
                        true) &&
                t.checkExpect(new ConsLoF(this.f2,
                        new ConsLoF(this.f4,
                        new ConsLoF(this.f6, this.mt))).collision(
                                this.player), false);
    }
   
    // test the method distTo in the CartPt class
    boolean testDistTo(Tester t) {
        return
                t.checkInexact(new CartPt(10, 4).distTo(
                        new CartPt(7, 2)), 3.6, .01) &&
                t.checkInexact(new CartPt(6, 8).distTo(
                        new CartPt(3, 7)), 3.16, .01) &&
                t.checkInexact(new CartPt(20, 10).distTo(
                        new CartPt(16, 7)), 5.0, .01) &&
                t.checkInexact(new CartPt(12, 11).distTo(
                        new CartPt(11, 10)), 1.41, .01);
    }
                                
    // test the method onTick in the FireballStage class
    boolean testOnTick(Tester t) {
        return
        t.checkExpect(this.w1.onTick(),
                new FireballStage(this.w1.player, 
                        this.w1.fireballs.moveFireballs()));
    }
    
    // test the method worldEnds in the FireballStage class
    boolean testWorldEnds(Tester t) {
        return
        t.checkExpect(this.w1.worldEnds(),
                new WorldEnd(false, this.w1.makeImage())) &&
         
        t.checkExpect(this.w0.worldEnds(),
                new WorldEnd(true, 
                    new OverlayImages(this.w0.makeImage(),
                    new TextImage(new Posn(150, 25), "You got burned!", 13, 0,
                            Color.red))));
    }
}