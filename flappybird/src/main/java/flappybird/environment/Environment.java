package flappybird.environment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

/*
 * Axis (0, 0) is at the upper left.
 * X will be positively increasing if the object is moving to the right.
 * Y will be positively increasing if the object is dropping down to the bottom.
 */

public class Environment {
	// This is the amount of time elapsed since the last frame in milliseconds.
//	public static final double DT = 1 / 30; // dt
	public static final int WIDTH = 288;
	public static final int HEIGHT = 512;
	public static final int PILLAR_GAP = 100;
	public static final Random r = new Random();

	private int lives;
	private double score;
	// The value would be 50.
	private int pillarMin = PILLAR_GAP / 4;
	// I don't know the meaning of the formula, but the value would be 192.
	private int pillarMax = (int) (HEIGHT * 0.79 * 0.6 - PILLAR_GAP / 2);

	private Position initPos = new Position((int) (WIDTH * 0.2), HEIGHT / 2);

	private Bird bird;
	private Pillar[] pillars;
	private int[] pillarOffset = { 0, (int) (WIDTH * 0.5), WIDTH };

	public Environment() {
		init();
	}

	public void init() {
		if (bird == null) {
			bird = new Bird(initPos);
		}

		if (pillars == null) {
			pillars = new Pillar[3];
			pillars[0] = generatePillar(-75);
			pillars[1] = generatePillar(-75 + WIDTH / 2);
			pillars[2] = generatePillar((int) (-75 + WIDTH * 1.5));

		} else {
			for (int i = 0; i < pillars.length; i++) {
				generatePillar(pillars[i], pillarOffset[i]);
			}
		}

		bird.init(initPos);
		lives = 1;
		score = 0;

		System.out.println("finished - init");
	}

	public double getScore() {
		return score;
	}

	public void generatePillar(Pillar p, int offset) {
		p.init(getGapStart(), offset);
	}

	public Pillar generatePillar(int offset) {
		return new Pillar(getGapStart(), offset);
	}

	private int getGapStart() {
		return r.nextInt(pillarMax + 1 - pillarMin) + pillarMin;
	}

	public void flap() {
		bird.flap();
	}

	public boolean gameOver() {
		return lives <= 0;
	}

	public State getGameState() {
		ArrayList<Pillar> ps = new ArrayList<Pillar>();

		for (Pillar p : pillars) {
			if (p.posX() + p.width() / 2 > bird.posX()) {
				ps.add(p);
			}
		}

		ps.sort(new PillarComparator());

		Pillar nextPillar = ps.get(0);
		Pillar nextNextPillar = ps.get(1);

		return new State(bird, nextPillar, nextNextPillar);
	}

	public double act(Action action) {
		step(action);
		return score;
	}

	private void step(Action action) {
		if (action == Action.FLAP) {
			bird.flap();
		}

		for (Pillar p : pillars) {
			ArrayList<Pillar> hits = Collision.collide(bird, pillars);
			for (Pillar hit : hits) {
				if ((bird.posY() - bird.height() / 2) <= hit.gapStart()) {
					lives -= 1;
				}
				if ((bird.posY() + bird.height() / 2) > hit.gapStart() + PILLAR_GAP) {
					lives -= 1;
				}
			}

			// player pass the pillar
			if ((p.posX() - p.width() / 2) <= bird.posX() && bird.posX() < (p.posX() - p.width() / 2 + 4)) {
				score += Reward.POSITIVE.value();
			}

			// is it out of the screen?
			if (p.posX() < -p.width()) {
				generatePillar(p, (int) (WIDTH * 0.2));
			}
		}

		// fell on the ground
		if (bird.posY() >= 0.79 * HEIGHT - bird.height()) {
			lives -= 1;
		}

		// went above the screen
		if (bird.posY() < -bird.height()) {
			lives -= 1;
		}

		bird.update();
		for (Pillar p : pillars) {
			p.update();
		}

		if (lives <= 0) {
			score += Reward.LOSS.value();
		}
	}

	public void printState() {
		System.out.println(bird);
		for (Pillar p : pillars) {
			System.out.println(p);
		}
	}
}

class PillarComparator implements Comparator<Pillar> {
	@Override
	public int compare(Pillar p1, Pillar p2) {
		if (p1.posX() > p2.posX()) {
			return 1;
		} else if (p2.posX() > p1.posX()) {
			return -1;
		} else {
			return 0;
		}
	}
}
