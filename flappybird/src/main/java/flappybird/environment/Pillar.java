package flappybird.environment;

public class Pillar {
	private final double PILLAR_WIDTH = 52;

	private double posX, posY;
	private Rec rec;

	private int gapStart;
	private double speed = 4.0;

	public Pillar(int gapStart) {
		init(gapStart, 0);
	}

	public Pillar(int gapStart, int offset) {
		init(gapStart, offset);
	}

	public void init(int gapStart, int offset) {
		this.gapStart = gapStart;

		posX = Environment.WIDTH + PILLAR_WIDTH + offset;

		rec = new Rec(PILLAR_WIDTH, Environment.HEIGHT);
		rec.setCenter(posX, Environment.HEIGHT / 2);
	}

	public void update() {
		posX -= speed;
		rec.setCenter(posX, Environment.HEIGHT / 2);
	}
	
	public Rec rec() {
		return rec;
	}
	
	public int gapStart() {
		return gapStart;
	}
	
	public double posX() {
		return posX;
	}
	
	public double posY() {
		return posY;
	}
	
	public double width() {
		return PILLAR_WIDTH;
	}
	
	@Override
	public String toString() {
		return "Pillar - x: " + posX + ", gapStart: " + gapStart;
	}
}
