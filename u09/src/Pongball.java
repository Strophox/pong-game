
public class Pongball {
	public double x, y; // Position coordinates
	public double speedX, speedY; // Speed (pixel/step) parameter

	public double radius; // Ball size
	public double ringRatio;
	public int value;
	public int[] baseColor; // Color of the ball
	public int[] color; // Current color of the ball
	public int[] color2; // Secondary color of the ball
	
	Pongball (double x, double y, double speedX, double speedY,
			double radius, int[] color) {
		this.x = x;
		this.y = y;
		this.speedX = speedX;
		this.speedY = speedY;

		this.radius = radius;
		this.ringRatio = 0.25;
		this.value = 1;
		this.baseColor = color.clone();
		this.color2 = new int[] {220, 221, 222};
		updateColor();
	}
	
	public void updateColor() {
		int weight = 7;
		this.color = new int[] {
				(this.value*this.baseColor[0]+weight*this.color2[0]) / (this.value+weight),
				(this.value*this.baseColor[1]+weight*this.color2[1]) / (this.value+weight),
				(this.value*this.baseColor[2]+weight*this.color2[2]) / (this.value+weight)};
	}
}
