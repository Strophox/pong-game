class Title {
	double x,y;
	double baseSize, size;
	int baseLifetime, lifetime;

	String content;
	int[] color;
	int[] color2;

	Title(double x, double y, double size, String content, int[] color) {
		this.x = x;
		this.y = y;
		this.baseSize = size;
		this.size = baseSize;
		this.baseLifetime = 50 * 3 / 2;
		this.lifetime = this.baseLifetime;

		this.content = content;
		this.color = new int[] {color[0], color[1], color[2]};
		this.color2 = new int[] {47, 49, 54};
	}
}