package flappybird.environment;

import java.util.ArrayList;

public class Collision {
	public static ArrayList<Pillar> collide(Bird bird, Pillar[] pillars) {
		ArrayList<Pillar> hits = new ArrayList<Pillar>();
		for (Pillar p : pillars) {
			if (isCollided(bird.rec(), p.rec())) {
				hits.add(p);
			}
		}

		return hits;
	}

	public static boolean isCollided(Rec a, Rec b) {
		return (a.getLeft() < b.getRight()) && (a.getBottom() < b.getTop()) && (a.getRight() > b.getLeft())
				&& (a.getTop() > b.getBottom());
	}
}
