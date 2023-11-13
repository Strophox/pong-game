class Effect {
    	double x,y;
    	double speedX,speedY;
    	
    	double radius;
    	double ringRatio;
    	int[] color;
    	int[] color2;
    	
    	Effect(double x, double y, double speedX, double speedY,
    			double radius, int[] color, int[] color2) {
    		this.x = x;
    		this.y = y;
    		this.speedX = speedX;
    		this.speedY = speedY;
    		
    		this.radius = radius;
    		this.ringRatio = 0.1;
    		this.color = new int[] {color[0], color[1], color[2]};
    		this.color2 = new int[] {color2[0], color2[1], color2[2]};
    	}
}