package controllers;

import java.util.Random;

import controllers.GameController;
import controllers.RandomController;
import space.SpaceInvaders;

public class PlayRandomController {

	public static void main(String[] args) {
		GameController c = new RandomController(new Random());
		SpaceInvaders.showControllerPlaying(c,5);
	}
}
