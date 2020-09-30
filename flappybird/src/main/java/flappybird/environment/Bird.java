package flappybird.environment;

public class Bird {
	private final double MAX_DROP_SPEED = 10.0;
	private final double FLAP_UPPER_BOUND = -48.0;
	private final double BIRD_WIDTH = 34;
	private final double BIRD_HEIGHT = 24;

	private double posX, posY;
	private Rec rec;

	// all in terms of Y
	private double velocity = 0.0;
	private double gravity = 1.0;
	private double flapPower = 9.0;
	private boolean flapped = true;

	public Bird(Position initPos) {
		rec = new Rec(BIRD_WIDTH, BIRD_HEIGHT);
		init(initPos);
		oscillateStartPosY();
		rec.setCenter(posX, posY);
	}

	public void init(Position initPos) {
		posX = initPos.x;
		posY = initPos.y;
		flapped = true;
	}

	private void oscillateStartPosY() {
		double offset = 8 * Math.sin(Environment.r.nextDouble() * Math.PI);
		posY += offset;
	}

	public void flap() {
		/*
		 * Original code in Python is self.pos_y > -2.0*self.image.get_height(). The
		 * height of a flappybird image is 24.
		 */
		if (posY > FLAP_UPPER_BOUND) {
			velocity = 0.0;
			flapped = true;
		}
	}

	public void update() {
		if (velocity < MAX_DROP_SPEED) {
			velocity += gravity;
		}

		if (flapped) {
			velocity += -1.0 * flapPower;
			flapped = false;
		}

		posY += velocity;
		rec.setCenter(posX, posY);
	}

	public Position position() {
		return new Position(posX, posY);
	}

	public double posX() {
		return posX;
	}

	public double posY() {
		return posY;
	}
	
	public double velocity() {
		return velocity;
	}
	
	public double height() {
		return BIRD_HEIGHT;
	}

	public Rec rec() {
		return rec;
	}
	
	@Override
	public String toString() {
		return "Bird - x: " + posX + ", y: " + posY;
	}
}
