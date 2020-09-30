package flappybird.environment;

public class Rec {
	private double width, height;
	private double centerX, centerY;

	public Rec(double width, double height) {
		this.width = width;
		this.height = height;
	}

	public void setCenter(double x, double y) {
		centerX = x;
		centerY = y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public double getCenterX() {
		return centerX;
	}

	public double getCenterY() {
		return centerY;
	}

	public double getLeft() {
		return centerX - width / 2;
	}

	public double getRight() {
		return centerX + width / 2;
	}

	public double getTop() {
		return centerY + height / 2;
	}

	public double getBottom() {
		return centerY - height / 2;
	}
}
