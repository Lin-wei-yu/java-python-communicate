package flappybird.environment;

public class State {
	public double birdY;
	public double birdVelocity;

	public double nextPillarDistToBird;
	public double nextPillarTopY;
	public double nextPillarBottomY;

	public double nextNextPillarDistToBird;
	public double nextNextPillarTopY;
	public double nextNextPillarBottomY;

	public State(Bird bird, Pillar nextPillar, Pillar nextNextPillar) {
		birdY = bird.posY();
		birdVelocity = bird.velocity();

		nextPillarDistToBird = nextPillar.posX() + nextPillar.width() / 2 - bird.posX();
		nextPillarTopY = nextPillar.gapStart();
		nextPillarBottomY = nextPillar.gapStart() + Environment.PILLAR_GAP;

		nextNextPillarDistToBird = nextNextPillar.posX() + nextNextPillar.width() / 2 - bird.posX();
		nextNextPillarTopY = nextNextPillar.gapStart();
		nextNextPillarBottomY = nextNextPillar.gapStart() + Environment.PILLAR_GAP;
	}

	@Override
	public String toString() {
		String str = String.format(
				"bird Y: %.2f\nbird velocity: %.2f\nnext pillar dist to player: %.2f\nnext pillar top y: %.2f\nnext pillar bottom y: %.2f\nnext next pillar dist to player: %.2f\nnext next pillar top y: %.2f\nnext next pillar bottom y: %.2f",
				birdY, birdVelocity, nextPillarDistToBird, nextPillarTopY, nextPillarBottomY, nextNextPillarDistToBird,
				nextNextPillarTopY, nextNextPillarBottomY);
		return str;
	}
	public String jsonString(){
		String str = String.format(
				"{\"player_y\":%.2f, \"player_vel\": %.2f, \"next_pipe_dist_to_player\": %.2f, \"next_pipe_top_y\": %.2f, \"next_pipe_bottom_y\": %.2f, \"next_next_pipe_dist_to_player\": %.2f, \"next_next_pipe_top_y\": %.2f, \"next_next_pipe_bottom_y\": %.2f}",
				birdY, birdVelocity, nextPillarDistToBird, nextPillarTopY, nextPillarBottomY, nextNextPillarDistToBird,
				nextNextPillarTopY, nextNextPillarBottomY);
		return str;
	}
}
