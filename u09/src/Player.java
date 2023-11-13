public class Player {
	public double x, y; // Position coordinates
	public double speedY; // Current speed (in pixel/step)
	public double maxAbsSpeedY; // Absolute value of the maximum speed player is allowed to have
	
	public double valAccelY; // Acceleration (pixel/step^2)
	public double valDecelY; // Deceleration (friction)
	public double wallDamp;
	
	public int points; // Player points
	public boolean gotPoint; // Whether player just got a point

	public double baseLength, length; // Paddle length
	public double thickness; // Paddle thickness
	public double borderRatio;
	public double drag; // how strong
	
	public int[] baseColor, color; // Player color in RGB
	public int[] color2; // Secondary color of the ball
	public String upKey, downKey; // Keybinds for player
	public int lastPressed;
	
	int powerupLifetime;
	
	
	Player(int width, int height, double startingX,
			int[] color, String upKey, String downKey) {
		
		double commonSize = Math.min(width, height)/28.0; // Common measuring unit used for a few things
        
        this.x = startingX; // Margin left and right
        this.y = height/2.0; // Centered position of players
        this.maxAbsSpeedY = height/36.0; // Maximum movement speed
        
        this.valAccelY = this.maxAbsSpeedY/7.0; // Acceleration
        this.valDecelY = this.valAccelY * 0.7; // Friction
        this.wallDamp = 0.5; // Wall elasticity
        
        this.points = 0;
        this.gotPoint = false;
        
        this.baseLength = height/5.0; // Paddle size
        this.length = this.baseLength;
        this.thickness = commonSize; // Paddle thickness
        this.borderRatio = 0.35;
		this.drag = 0.05;

		this.baseColor = color.clone();
		this.color = this.baseColor;
		this.color2 = new int[] {220, 221, 222};
		this.upKey = upKey;
		this.downKey = downKey;
		
		this.lastPressed = 0;
		
		this.powerupLifetime = 0;
	}
	
	void updateColor(long time) {
		if (powerupLifetime > 0) {
			int[] lightColor = {154, 71, 237};
			int[] darkColor = { 88, 53,185};
			int halfAnimationTime = 50;
			int currentFrame = Math.abs((int) time % (2*halfAnimationTime) - halfAnimationTime);
			
			this.color = new int[] {
					( lightColor[0] * currentFrame + darkColor[0] * (halfAnimationTime-currentFrame) ) / halfAnimationTime,
					( lightColor[1] * currentFrame + darkColor[1] * (halfAnimationTime-currentFrame) ) / halfAnimationTime,
					( lightColor[2] * currentFrame + darkColor[2] * (halfAnimationTime-currentFrame) ) / halfAnimationTime};
		} else {
			this.color = this.baseColor;
		}
	}
	
	boolean isBot() {
		return upKey==null && downKey==null;
	}
}
