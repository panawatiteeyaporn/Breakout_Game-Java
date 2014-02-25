/*
 * File: Breakout.java
 * -------------------
 * Name: Panawat Iteeyaporn
 * A simple break-out game using acm graphic objects and 
 * basic animation loops.  
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1)
			* BRICK_SEP)
			/ NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;

	/** Rate of delay for animation */
	private static final int DELAY = 25;

	/** Velocity of the ball */
	private static final double yVel = 10.0;
	
	/*Init method set the size of the applet.*/
	public void init() {
		
		setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
	}

	/* Method: run() */
	/** Runs the Breakout program. */
	public void run() {
		
		setGame();
		playGame();
	}

	private void setGame() {

		setBricks();
		setPaddle();
		setBall();
		addMouseListeners();

	}

	/* This method set the bricks */
	private void setBricks() {

		for (int i = 0; i < NBRICK_ROWS; i++) {

			int x = (getWidth() - ((BRICK_WIDTH * NBRICKS_PER_ROW) + (BRICK_SEP * (NBRICKS_PER_ROW - 1)))) / 2;
			int y = (BRICK_Y_OFFSET + ((BRICK_HEIGHT + BRICK_SEP) * i));

			for (int j = 0; j < NBRICKS_PER_ROW; j++) {

				bricks = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				bricks.setFilled(true);

				if (i <= 1) { // Computer counts from 0!
					bricks.setFillColor(Color.RED);
				} else if (i <= 3 && i > 1) {
					bricks.setFillColor(Color.ORANGE);
				} else if (i <= 5 && i > 3) {
					bricks.setFillColor(Color.yellow);
				} else if (i <= 7 && i > 5) {
					bricks.setFillColor(Color.green);
				} else if (i <= 9 && i > 7) {
					bricks.setFillColor(Color.cyan);
				}

				add(bricks, x + (j * (BRICK_WIDTH + BRICK_SEP)), y);
			}

		}
	}

	/*This method set the paddle.*/
	private void setPaddle() {

		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		add(paddle, (getWidth() - PADDLE_WIDTH) / 2,
				(getHeight() - PADDLE_Y_OFFSET));

	}

	/*Add ball object and position.*/
	private void setBall() {

		ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball, (getWidth() - (BALL_RADIUS * 2)) / 2,
				(getHeight() - (BALL_RADIUS * 2)) / 2);

	}

	/*Track mouse as it moves and moves paddle according to the location.*/
	public void mouseMoved(MouseEvent t) {

		if (t.getX() < getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(t.getX(), getHeight() - PADDLE_Y_OFFSET);
		}
	}
	
	/*Set angle for the ball to bounce.*/
	private double setAngle() {

		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5))
			vx = -vx;

		return vx;
	}
	
	/*Create label to inform player how to play and set life variable.*/
	private void startNotice() {
		
		lifeUsed = 0;

		GLabel start = new GLabel("Please click mouse to start the game.");
		start.setColor(Color.RED);
		add(start, (getWidth() - start.getWidth()) / 2,
				(getHeight() + start.getAscent()) * 0.75);

		waitForClick();
		remove(start);

	}

	/*Play the game.*/
	private void playGame() {

		vx = setAngle();
		vy = yVel;

		startNotice();
		
		while (lifeUsed < NTURNS && counter != 0) {

			moveBall();
			checkForCollision();
			pause(DELAY);
			
		}
		checkResult();
	}

	/*Check if player win or lose and create label accordingly.*/
	private void checkResult() {
		
		if (counter > 0) {

			GLabel lost = new GLabel("Sorry! You have lost.");
			lost.setColor(Color.RED);
			add(lost, (getWidth() - lost.getWidth()) / 2,
					(getHeight() + lost.getAscent()) / 2);
		} else {

			GLabel won = new GLabel("Congratulations! You have won.");
			won.setColor(Color.RED);
			add(won, (getWidth() - won.getWidth()) / 2,
					(getHeight() + won.getAscent()) / 2);
		}

	}
	
	/*Move the ball with given vx, vy variables.*/
	private void moveBall() {

		ball.move(vx, vy);
	}

	/*Check for frame collision.*/
	private void checkForCollision() {

		if (ball.getY() >= getHeight() - (BALL_RADIUS * 2)) {
			resetGame();
			
		} else if (ball.getX() >= getWidth() - (BALL_RADIUS * 2)) {
			vx = -vx;

		} else if (ball.getY() <= 0) {
			vy = yVel;

		} else if (ball.getX() <= 0) {
			vx = Math.abs(vx);
		}
		checkObjectCollision();
	}
	
	/*Check for object collision, remove it and set ball velocity accordingly.*/
	private void checkObjectCollision() {
		
		collider = getCollidingObject();

		if (collider != null) {
			
			if (collider == paddle) {
				vy = -yVel;
		        bounceClip.play();
		        
			} else {
				remove(collider);
				counter--;
				
				if (vy == yVel) {
					vy = -yVel;
					
				} else {
					vy = yVel;
				}
				
				bounceClip.play();
			}
		}
	}

	/*Reset ball position and count life if ball lost.*/
	private void resetGame() {

		lifeUsed++;
		GLabel notice = new GLabel("You have " + (NTURNS - lifeUsed)
				+ " life left. Click to continue.");

		if (lifeUsed != NTURNS) {

			notice.setColor(Color.red);
			add(notice, (getWidth() - notice.getWidth()) / 2,
					(getHeight() + notice.getAscent()) / 2);
			waitForClick();
			remove(notice);

			vx = setAngle();
			
			ball.setLocation((getWidth() - (BALL_RADIUS * 2)) / 2,
					(getHeight() - (BALL_RADIUS * 2)) / 2);

		}
	}

	/*Check each corners of the ball and return object if any.*/
	private GObject getCollidingObject() {

		GObject conner1 = getElementAt(ball.getX(), ball.getY());
		GObject conner2 = getElementAt(ball.getX() + (BALL_RADIUS * 2),
				ball.getY());
		GObject conner3 = getElementAt(ball.getX(), ball.getY()
				+ (BALL_RADIUS * 2));
		GObject conner4 = getElementAt(ball.getX() + (BALL_RADIUS * 2),
				ball.getY() + (BALL_RADIUS * 2));

		if (conner1 != null) {
			return conner1;
		} else if (conner2 != null) {
			return conner2;
		} else if (conner3 != null) {
			return conner3;
		} else if (conner4 != null) {
			return conner4;
		} else {
			return null;
		}
	}

	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	private int counter = NBRICKS_PER_ROW * NBRICK_ROWS;
	private int lifeUsed;
	private GObject collider;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	private GRect bricks;
	private GRect paddle;
	private GOval ball;

}
