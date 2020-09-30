package flappybird.environment;

public enum Reward {
	POSITIVE(1), NEGATIVE(-1), LOSS(-5), WIN(5);

	private int value;

	Reward(int d) {
		value = d;
	}

	public int value() {
		return value;
	}
}
