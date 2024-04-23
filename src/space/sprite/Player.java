package space.sprite;

import javax.swing.ImageIcon;

import space.Commons;

import java.awt.event.KeyEvent;

public class Player extends Sprite {

	private int width;
	public int X_MIN = 2;
	public int X_MAX;

	public Player() {

		initPlayer();
	}

	private void initPlayer() {

		String playerImg = "src/images/player.png";
		ImageIcon ii = new ImageIcon(playerImg);

		width = ii.getImage().getWidth(null);
		setImage(ii.getImage());

		X_MAX = Commons.BOARD_WIDTH - 2 * width;

		int START_X = 270;
		setX(START_X);

		int START_Y = 280;
		setY(START_Y);
	}

	public int getX_MIN() {
		return X_MIN;
	}

	public int getX_MAX(){
		return X_MAX;
	}

	private int maxIndex(double[] output) {
		double max = output[0];
		int maxI = 0;
		for (int i = 1; i < output.length; i++) {
			if (max < output[i]) {
				maxI = i;
				max = output[i];
			}
		}
		return maxI;
	}

	private void applyOrder(double[] output) {
		int key = maxIndex(output);
		if (key == 1) {
			dx = -2;
		}
		if (key == 2) {
			dx = 2;
		}
		if (key == 0) {
			dx = 0;
		}
	}

	public void act(double[] output) { //move o player baseado na sua velocidade atual

		applyOrder(output);

		x += dx;

		if (x <= 2) { //Parede para a esquerda

			x = 2;
		}

		if (x >= Commons.BOARD_WIDTH - 2 * width) { //Parede para a direita

			x = Commons.BOARD_WIDTH - 2 * width;
		}

	}

//	public void keyPressed(KeyEvent e) {
//
//		int key = e.getKeyCode();
//
//		if (key == KeyEvent.VK_LEFT) {
//
//			dx = -2;
//		}
//
//		if (key == KeyEvent.VK_RIGHT) {
//
//			dx = 2;
//		}
//	}
//
//	public void keyReleased(KeyEvent e) {
//
//		int key = e.getKeyCode();
//
//		if (key == KeyEvent.VK_LEFT) {
//
//			dx = 0;
//		}
//
//		if (key == KeyEvent.VK_RIGHT) {
//
//			dx = 0;
//		}
//	}

	public void moveLeft() { //altera a velocidade do player para -2 (esquerda)
		dx = -2;
	}

	public void moveRight() { //altera a velocidade do player para 2 (direita)
		dx = 2;
	}

	public void stop() { //altara a velocidade do player para 0 (eventualmente vai ficar parado)
		dx = 0;
	}
}
