//import static java.lang.Math.PI;
import gui.Window;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class PongGame {
    long time; // Game clock
    int width, height; // Game field size, for non-degenerate values of width, height
    int toWin;
    
    Player[] players; // Players
    LinkedList<Pongball> balls; // Ball(s)
    PriorityQueue<Long> ballSchedule; // Scheduled events
    
    LinkedList<EventField> events; // Event fields
    LinkedList<Effect> effects;
    LinkedList<Title> titles;
    
    public static final int[] black = new int[] {54, 57, 63};
    public static final int[] darkBlack = new int[] {47, 49, 54};
    public static final int[] white = new int[] {220, 221, 222};
    
    public static final String[][] controlScheme = new String[][] {{"W","S"}, {"I","K"}, {"E","F"}, {"I","J"}};
    public static final int schemeP1 = 0;
    public static final int schemeP2 = 1;
    
    public static final boolean perfectAI = false;
    
	PongGame(int width, int height, boolean playWithBot) {
        this.time = 1;
        this.width = width;
        this.height = height;
        this.toWin = 10;
        
        double margin = Math.min(width, height)/28.0; // Common measuring unit used for a few things
        
        this.players = new Player[2]; // Store players
        
        players[0] = new Player(width,height, margin, 
        		new int[] {255, 75, 115}, controlScheme[schemeP1][0],controlScheme[schemeP1][1]); // Player 1
//        players[0].baseColor = new int[] {127,127,127};
//    	players[0].upKey = players[0].downKey = null;

        players[1] = new Player(width,height, width-margin,
    			new int[] {0, 185, 225}, controlScheme[schemeP2][0],controlScheme[schemeP2][1]); // Player 2
        if (playWithBot) {
        	players[1].baseColor = new int[] {127,127,127};
        	players[1].upKey = players[1].downKey = null;
        }
        
        this.balls = new LinkedList<Pongball>(); // Store balls
        
        this.ballSchedule = new PriorityQueue<Long>();
        
        this.events = new LinkedList<EventField>();
        
        this.effects = new LinkedList<Effect>();
        
        this.titles = new LinkedList<Title>();
        if (!players[0].isBot() && !players[1].isBot()) {
        	titles.add(new Title(width/2.0, height/2.0, height/15.0, "Use "+controlScheme[schemeP1][0]+"/"+controlScheme[schemeP1][1]+" and "+controlScheme[schemeP2][0]+"/"+controlScheme[schemeP2][1], white.clone()));
        } else if (!players[0].isBot()) {
        	titles.add(new Title(width/2.0, height/2.0, height/15.0, "Use "+controlScheme[schemeP1][0]+"/"+controlScheme[schemeP1][1], white.clone()));
        } else if (!players[1].isBot()) {
        	titles.add(new Title(width/2.0, height/2.0, height/15.0, "Use "+controlScheme[schemeP2][0]+"/"+controlScheme[schemeP2][1], white.clone()));
        }
    }
    
    boolean over() {
    	return Math.abs(players[0].points - players[1].points) >= toWin;
    }

    void step(Window window) {
    	// Update HACK
    	hax(window);
    	
    	// Update players
    	for (int p = players.length-1; 0 <= p; p--) {
    		players[p].updateColor(time);
    		stepPlayerPhysics(p, window);
    	}
    	int uncheckedScoreDiff = players[1].points - players[0].points;
    	int scoreDiff = (uncheckedScoreDiff > 0? 1 : -1) * Math.min(Math.abs(uncheckedScoreDiff), toWin);
    	double affectedSize = 3;
    	players[0].length = players[0].baseLength * (affectedSize*toWin + scoreDiff) / (affectedSize*toWin);
    	players[1].length = players[1].baseLength * (affectedSize*toWin - scoreDiff) / (affectedSize*toWin);
    	
    	// Update balls
    	if (balls.isEmpty()) {
    		if (ballSchedule.isEmpty()) {
    			ballSchedule.add(time + 50);
    		}
    	} else {
    		for (int b = balls.size()-1; 0 <= b; b--) {
    			balls.get(b).updateColor(); // TODO this is a mess; Why is this needed
    			stepBallPhysics(b);
    		}
    	}
    	
    	// Update ball schedule
    	while (!ballSchedule.isEmpty() && ballSchedule.peek() <= time) {
			if (ballSchedule.peek() == time) {
    			balls.add(createRandomPongball()); // A ball is made
    		}
			ballSchedule.poll();
		}
    	
    	// Update events
    	for (int e = events.size()-1; 0 <= e; e--) {
			EventField event = events.get(e);
			
			for (int b = balls.size()-1; 0 <= b; b--) { // Detect activations
				Pongball ball = balls.get(b);
				if (Math.pow(ball.x-event.x, 2)+Math.pow(ball.y-event.y, 2) < Math.pow(ball.radius+event.radius, 2)) {
    				event.typeEffect(this, b);
    			}
			}
			
			event.radius = event.baseRadius * ( (2.0*event.baseLifetime - event.lifetime) / event.baseLifetime);
			event.updateColor();
			event.lifetime--;
			
			if (event.lifetime<=0) {
		    	double angle = Math.random()*Math.PI/2;
		    	double speed = Math.sqrt((height*height+width*width)) / 200.0;
				effects.add(new Effect(event.x, event.y, Math.cos(angle+0*Math.PI/2)*speed, Math.sin(angle+0*Math.PI/2)*speed, event.radius/2.0, EventField.baseColors[event.type].clone(), event.color2.clone()));
				effects.add(new Effect(event.x, event.y, Math.cos(angle+1*Math.PI/2)*speed, Math.sin(angle+1*Math.PI/2)*speed, event.radius/2.0, EventField.baseColors[event.type].clone(), event.color2.clone()));
				effects.add(new Effect(event.x, event.y, Math.cos(angle+2*Math.PI/2)*speed, Math.sin(angle+2*Math.PI/2)*speed, event.radius/2.0, EventField.baseColors[event.type].clone(), event.color2.clone()));
				effects.add(new Effect(event.x, event.y, Math.cos(angle+3*Math.PI/2)*speed, Math.sin(angle+3*Math.PI/2)*speed, event.radius/2.0, EventField.baseColors[event.type].clone(), event.color2.clone()));
				events.remove(event);
			}
		}
    	int spawnInterval = 50 * 10;
    	double maxEvents = 5;
    	double probability = Math.pow( 1 - events.size()/maxEvents, 2);
    	if (time%spawnInterval==0 && Math.random() < probability * 0.70) {
			events.add(createRandomEventField());
		}
    	
    	// Update effects
    	for (int e = effects.size()-1; 0 <= e; e--) {
    		Effect effect = effects.get(e);
    		
    		effect.x += effect.speedX;
    		effect.y += effect.speedY;
    		effect.radius *= 0.9;
    		
    		if (effect.radius < 0.1) {
    			effects.remove(e);
    		}
    	}
    	
    	// Update titles
    	for (int t = titles.size()-1; 0 <= t; t--) {
    		Title title = titles.get(t);
    		
    		title.size = title.baseSize * ((double) title.lifetime/title.baseLifetime);
    		title.lifetime--;
    		
    		if (title.lifetime <= 0) {
    			titles.remove(t);
    		}
    	}
    	
    	if (over()) {
    		balls.clear();
			events.clear();
    		if (time%50==0) {
    			EventField event = createRandomEventField();
        		event.lifetime = 1;
        		events.add(event);
        		
        		int winner = players[0].points < players[1].points ? 1 : 0;
        		Title title = new Title(width/2.0, height/2.0, height/10.0, (players[winner].isBot()?"Computer":"Player")+" "+(winner+1)+" wins!", players[winner].baseColor);
        		titles.add(title);
    		}
    	}
    	
    	time++;
    }

    void stepPlayerPhysics(int p, Window window) {
    	Player player = players[p];
    	
		player.y += player.speedY; // Player gains distance according to his current speed
		
		boolean up = false;
		boolean down = false;
		if (!player.isBot()) { // Normal player
			up = window.isKeyPressed(player.upKey);
			down = window.isKeyPressed(player.downKey);
			if (!up && !down) {
				player.lastPressed = 0;
			} else if (up && !down) {
				player.lastPressed = 1;
			} else if (!up && down) {
				player.lastPressed = -1;
			} else if (up && down) {
				if (player.lastPressed==1) {
					up = false;
					down = true;
				} else if (player.lastPressed==-1) {
					up = true;
					down = false;
				}
			}
		} else { // Computer
			boolean idling = true;
			double closestDistance = Double.POSITIVE_INFINITY;
			for (Pongball ball : balls) {
				double distance = Math.abs(ball.x-player.x) / ball.value;
				if ((p==0? ball.speedX < 0 : ball.speedX > 0) && distance < closestDistance) { // React to ball coming its way 
	    			idling = false;
	    			closestDistance = distance;
	    			if (perfectAI) {
	    				up = false;
	    				down = false;
	    				player.y = ball.y;
	    			} else {
						up = ball.y > player.y + player.length/3.0;
		    			down = ball.y < player.y - player.length/3.0;
	    			}
				}
			}
			if (idling) { // Idle animation
				if (player.powerupLifetime > 0) {
					up = Math.random() > 0.5 ? player.speedY>0 : player.y < height/2.0;
					down = Math.random() > 0.5 ? player.speedY<0 : player.y > height/2.0;
				} else {
					up = player.y < height/2.0;
					down = player.y > height/2.0;
				}
			}
		}
		
		if (player.y + player.length/2.0 < height && up && !down) { // Accelerate up
    		player.speedY = Math.min(player.speedY+player.valAccelY, player.maxAbsSpeedY);
    	} else if (player.y-player.length/2.0 > 0 && !up && down) { // Accelerate down
    		player.speedY = Math.max(player.speedY-player.valAccelY, -player.maxAbsSpeedY);
    		
    	} else if (Math.abs(player.speedY) > player.valDecelY) { // No action - Friction
    		player.speedY -= (player.speedY > 0 ? 1 : -1) * player.valDecelY;
    	} else if (player.speedY != 0) { // Speed is halted
    		player.speedY = 0.0;
    	}

		if (player.y + player.length/2.0 > height) { // Hits upper wall
			player.y = height - player.length/2.0;
			player.speedY = player.wallDamp * (player.speedY<0 ? player.speedY : -player.speedY);
		} else if (player.y - player.length/2.0 < 0) { // Hits lower wall
			player.y = player.length/2.0;
			player.speedY = player.wallDamp * (player.speedY>0 ? player.speedY : -player.speedY);
		}
		
    	player.gotPoint = false;
    	if (player.powerupLifetime > 0) {
    		player.powerupLifetime--;
    	}
    }

    void stepBallPhysics(int b) {
    	Pongball ball = balls.get(b);
    	
    	if (!stepBallState(b)) {
    		return;
    	}
    	
    	double marginRatio = 0.1;
		if (players[ball.speedX<0?1:0].powerupLifetime > 0 && (ball.speedX<0 ? width*marginRatio<ball.x : ball.x<width*(1 - marginRatio))) {
			double distanceToPlayer = players[ball.speedX<0?1:0].y - ball.y;
			int weight = 10;
			double attraction = 0.07;
			ball.speedY = (weight*ball.speedY + distanceToPlayer*attraction) / (weight + 1);
		}
		
		ball.x += ball.speedX;
		ball.y += ball.speedY;

    	if (!stepBallState(b)) {
    		return;
    	}
    }

    boolean stepBallState(int b) { // Makes the ball be correctly placed and face the correct direction
    	Pongball ball = balls.get(b);
    	
    	// The ball dies
    	boolean scoreLeft = ball.x + ball.radius < 0.0;
		boolean scoreRight = ball.x - ball.radius > width;
		if (scoreLeft || scoreRight) { // Scored
			int scorer = scoreLeft ? 1 : 0;
			players[scorer].gotPoint = true;
			players[scorer].points += ball.value;
			balls.remove(b);
			return false;
		}
		
		// Ball - Player
		boolean collidesWithPlayer1 = ball.x-ball.radius < players[0].x+players[0].thickness/2.0
    			&& !(ball.x+ball.radius < players[0].x-players[0].thickness/2.0)
    			&& ball.y < players[0].y+players[0].length/2.0
    			&& ball.y > players[0].y-players[0].length/2.0;
		boolean collidesWithPlayer2 = ball.x+ball.radius > players[1].x-players[1].thickness/2.0
    			&& !(ball.x-ball.radius > players[1].x+players[1].thickness/2.0)
    			&& ball.y < players[1].y+players[1].length/2.0
    			&& ball.y > players[1].y-players[1].length/2.0;
		if (collidesWithPlayer1) { // Player 1 collision
			ball.x = players[0].x + players[0].thickness/2.0 + ball.radius;
    		ball.speedX = ball.speedX>0 ? ball.speedX : -ball.speedX;
    		ball.speedY += players[0].speedY * players[0].drag;
    	}
    	if (collidesWithPlayer2) { // Player 2 collision
    		ball.x = players[1].x - players[1].thickness/2.0 - ball.radius;
    		ball.speedX = ball.speedX<0 ? ball.speedX : -ball.speedX;
    		ball.speedY += players[1].speedY * players[1].drag;
    	}
    	
    	// Ball - Wall
    	double repellingCoefficient = ball.speedY > Math.sqrt((height*height+width*width)) / 100.0 ? 0.9 : 1;
		if (ball.y - ball.radius < 0) { // Wall collision
			ball.y = ball.radius;
			ball.speedY *= -repellingCoefficient;
		}
		
		if (height < ball.y + ball.radius) {
			ball.y = height - ball.radius;
			ball.speedY *= -repellingCoefficient;
		}
		
		return true;
    }
    
    void hax(Window window) {
    	for (Pongball ball : balls) { // Arrow keys to control balls on screen
        	double accel = 1.0;
    		ball.speedY += (window.isKeyPressed("UP") ? 1 : (window.isKeyPressed("DOWN") ? -1 : 0)) * accel; // Vertical control
    		ball.speedX += (window.isKeyPressed("LEFT") ? -1 : (window.isKeyPressed("RIGHT") ? 1 : 0)) * accel; // Horizontal control
    	}
    	if (window.isKeyPressed("X")) {
    		players[0].points = players[1].points = 0;
    	}
    	if (window.isKeyPressed("C")) { // C to create random ball - Create Chaos !
    		balls.add(createRandomPongball());
    	}
    	if (window.isKeyPressed("V")) { // X to stop all balls on screen
    		for (Pongball ball : balls) {
    			ball.speedX = ball.speedY = 0;
    		}
    		titles.add(new Title(width/2.0, height/2.0, height/15.0, "Time stopped!", white.clone()));
    	}
    	if (window.isKeyPressed("B")) {
    		if (players[0].upKey!=null || players[0].downKey!=null) {
    			players[0].baseColor = new int[] {127,127,127};
    	    	players[0].upKey = players[0].downKey = null;
    		} else {
    	        players[0].baseColor = new int[] {255, 75, 115};
    	        players[0].upKey = controlScheme[schemeP1][0];
    	        players[0].downKey = controlScheme[schemeP1][1];
    		}
    	}
    	if (window.isKeyPressed("N")) { // N to make game neverending
    		toWin = Integer.MAX_VALUE;
    		titles.add(new Title(width/2.0, height/2.0, height/15.0, "Neverending game", white.clone()));
    	}
    	if (window.isKeyPressed("M")) { // V to shoot ball veered in direction of mouse
        	double angle = Math.atan2(-(window.getMouseY()-height/2.0), window.getMouseX()-width/2.0);
        	double speed = Math.sqrt((height*height+width*width)) / 100.0; // Speed dependent on diagonal
        	double radius = Math.min(width, height)/50.0;
    		balls.add(new Pongball(width/2.0,height/2.0, speed*Math.cos(angle),speed*Math.sin(angle), 
    				radius, new int[] {255, 0, 0}));
    	}
    	if (window.isKeyPressed("0")) {
    		events.add(createRandomEventField());
    	}
    	if (window.isKeyPressed("1")) {
    		EventField event = createRandomEventField();
    		event.type = 1;
    		events.add(event);
    	}
    	if (window.isKeyPressed("2")) {
    		EventField event = createRandomEventField();
    		event.type = 2;
    		events.add(event);
    	}
    	if (window.isKeyPressed("3")) {
    		EventField event = createRandomEventField();
    		event.type = 3;
    		events.add(event);
    	}
    	if (window.isKeyPressed("4")) {
    		EventField event = createRandomEventField();
    		event.type = 4;
    		events.add(event);
    	}
    	if (window.isKeyPressed("5")) {
    		EventField event = createRandomEventField();
    		event.type = 5;
    		events.add(event);
    	}
    	if (window.isKeyPressed("6")) {
    		EventField event = createRandomEventField();
    		event.type = 6;
    		events.add(event);
    	}
    	if (window.isKeyPressed("7")) {
    		EventField event = createRandomEventField();
    		event.type = 7;
    		events.add(event);
    	}
    }
    
    Pongball createRandomPongball() {
    	double diagAngle = Math.atan((double)height / (width/2.0)); // Angle from the middle to a corner
//    	double randAngle =  + Math.random() * diagAngle + 0.5*diagAngle;
//    	randAngle = (3*randAngle + Math.PI/4.0) / (3 + 1); // Weighted average with 45°
    	
    	double speed = Math.sqrt((height*height+width*width)) / 100.0; // Speed dependent on diagonal
    	
    	double speedX = (Math.random() > 0.5 ? 1 : -1) * speed * Math.cos(diagAngle);
    	double speedY = (Math.random() > 0.5 ? 1 : -1) * speed * Math.sin(diagAngle);
    	
    	double radius = Math.min(width, height)/42.0;
    	
    	int p = (int)(Math.random() * 6);
    	int[] color = new int[3];
    	for (int i = 0; i < color.length; i++) {
    		int oix = (p%2+1) * (p + i) + (p%2*2);
    		color[i] = (int)( 255 * (oix%3 < 2 ? oix%3 : Math.random()) );
    	}
    	
    	return new Pongball(width/2.0,height/2.0, speedX,speedY, radius, color);
    }
    
    EventField createRandomEventField() {
    	double range = Math.random() * (-width/4.0 + width/2.0 - width/10);
    	double x = width/2.0 + (Math.random()>0.5?1:-1) * (width/10.0 + range);
    	double y = Math.random() * height;
    	
    	double radius = Math.min(width, height)/20.0;
    	
    	int type = (int) (Math.random() * 7) + 1;
    	
    	return new EventField(x, y, radius, type);
    }
}
