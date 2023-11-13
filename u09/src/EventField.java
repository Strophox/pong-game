
class EventField {
	double x,y; // Position
	
	double baseRadius, radius; // Size
	double ringRatio;

	int baseLifetime, lifetime; // Expiration
	public int type;
	/*
	 * 0 - No type, black
	 *  
	 * 1 - Grant ball control, purple
	 * 2 - Player drag plus, blue
	 * 3 - Player movement plus, cyan
	 *  
	 * 4 - Spawn Twin ball, green
	 *  
	 * 5 - Increase ball value, yellow
	 * 6 - Increase ball speed, red
	 * 7 - Random ball size, magenta
	 */
	int[] color; // Current color
	int[] color2; // Secondary color
	static final int[][] baseColors = {
			{220,221,222}, // White
			{154, 71, 237}, // Purple
			{ 58,111,255}, // Blue
			{ 58,205,255}, // Cyan
			{ 68,229, 68}, // Green
			{255,207, 76}, // Yellow
			{255, 76, 76}, // Red
			{255, 76,255}, // Pink
	};

	static final int[][] darkColors = {
			{ 47, 49, 54}, // Black
			{ 88, 53,185}, // Dark Purple
			{ 51, 64,214}, // Dark Blue
			{ 27,115,137}, // Dark Cyan
			{ 27,151, 92}, // Dark Green
			{188,123, 54}, // Dark Yellow-orange
			{181, 39, 75}, // Dark Red
			{181, 19,143}, // Dark Magenta
	};
//	static final int[][] baseColors = {
//			{220,221,222}, // White
//			{229, 68,189}, // Pink
//			{ 68,149,229}, // Blue
//			{229,202, 68}, // Yellow
//			{229,135, 68}, // Orange
//			{226, 68, 81}, // Red
//			{149, 68,229}, // Purple
//			{ 68,229, 68}, // Green	
//	};
//	static final int[][] darkColors = {
//			{ 47, 49, 54}, // Black
//			{178, 53,178}, // Dark magenta
//			{ 35, 95,178}, // Dark blue
//			{191,124, 38}, // Dark yellow
//			{191, 76, 38}, // Dark orange
//			{178, 35, 83}, // Dark red
//			{ 96, 61,201}, // Dark purple
//			{ 35,178, 71}, // Dark green
//	};
	
	EventField(double x, double y, double radius, int type) {
		this.x = x;
		this.y = y;
		
		this.baseRadius = radius;
		this.radius = this.baseRadius;
		this.ringRatio = 0.1;
		
		this.baseLifetime = 50 * 30;
		this.lifetime = this.baseLifetime;
		this.type = type;
//		this.color = new int[] {baseColors[type][0], baseColors[type][1], baseColors[type][2]};
		updateColor();
		this.color2 = new int[] {54, 57, 63};//{47, 49, 54};
	}
	
	public void updateColor() {
		int halfAnimationTime = 50;
		int currentFrame = Math.abs((int) lifetime % (2*halfAnimationTime) - halfAnimationTime);
		
		this.color = new int[] {
				( baseColors[type][0] * currentFrame + darkColors[type][0] * (halfAnimationTime-currentFrame) ) / halfAnimationTime,
				( baseColors[type][1] * currentFrame + darkColors[type][1] * (halfAnimationTime-currentFrame) ) / halfAnimationTime,
				( baseColors[type][2] * currentFrame + darkColors[type][2] * (halfAnimationTime-currentFrame) ) / halfAnimationTime};
	}
	
	public void typeEffect(PongGame game, int b) {
		Pongball ball = game.balls.get(b);
		int ballOwnerID = ball.speedX < 0 ? 1 : 0;
		Player ballOwner = game.players[ballOwnerID];
		
		int weight = 1;
		int[] titleColor = new int[] {
				(weight*220 + baseColors[this.type][0]) / (weight+1),
				(weight*221 + baseColors[this.type][1]) / (weight+1),
				(weight*222 + baseColors[this.type][2]) / (weight+1)};
		Title title = new Title(game.width/2.0, game.height/2.0, game.height/15.0, null, titleColor);

		/*
		 * 0 - No type, black
		 *  
		 * 1 - Grant ball control, purple
		 * 2 - Player drag plus, blue
		 * 3 - Player movement plus, cyan
		 *  
		 * 4 - Spawn Twin ball, green
		 *  
		 * 5 - Increase ball value, yellow
		 * 6 - Increase ball speed, red
		 * 7 - Random ball size, magenta
		 */
		switch (type) {
		case 1: // Ball control!
			ballOwner.powerupLifetime += 50 * 10;
			title.content = (ballOwnerID==0?"Left":"Right")+" Control !";
			break;
		case 2: // Increased player drag
			ballOwner.drag += 0.05;
			if (ballOwner.drag > 1.0) {
				ballOwner.drag = 1.0;
			}
			title.content = "Drag "+(ballOwnerID==0?"Left":"Right")+" +";
			break;
		case 3: // Increased player velocity cap
			ballOwner.maxAbsSpeedY += game.height/500.0;
			ballOwner.valAccelY += game.height/500.0;
			ballOwner.valDecelY += game.height/500.0;
			title.content = "Speed "+(ballOwnerID==0?"Left":"Right")+" +";
			break;

		case 4: // Spawn twin ball
			game.balls.add(game.createRandomPongball());
			title.content = "More balls !";
			break;

		case 5: // Increased ball value
			ball.value += 1;
			ball.updateColor();
			title.content = "Precious !";
			break;
		case 6: // Increased ball speed
			ball.speedX *= 1.25;
			ball.speedY *= 1.25;
			title.content = "Speed up !";
			break;
		case 7: // Random ball size
			ball.radius *= Math.random() * 1.5 + 0.5;
			if (ball.radius > game.height/6.0) {
				ball.radius = game.height/6.0;
			}
			title.content = "Resized !";
			break;
		}
		
		game.titles.add(title);
		this.lifetime = 0; // Kil
	}
}
