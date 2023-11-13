import gui.Window;

public class PongGui {
    
    public static void main(String[] args) {
    	int scale = 900;
        int width = 2*scale;
        int height = scale;
        PongGame game = new PongGame(width, height, menuscreen());
        
        Window window = new Window("Pong", width, height);
        window.open();
        window.setResizable(true);
        while(window.isOpen()) {
        	
        	double WR = window.getWidth() / width; // Width ratio
        	double HR = window.getHeight() / height; // Height ratio
            
        	// Update game state
        	game.step(window);
        	
        	// Fill background
        	window.setColor(PongGame.black[0], PongGame.black[1], PongGame.black[2]);
        	window.fillRect(0, 0, window.getWidth(), window.getHeight());
        	window.setColor(PongGame.darkBlack[0], PongGame.darkBlack[1], PongGame.darkBlack[2]);
        	window.setStrokeWidth(WR*( width/128.0 ));
        	window.drawLine(window.getWidth()/2, 0, window.getWidth()/2, window.getHeight());

        	// Draw effects
        	for (Effect effect : game.effects) {
        		window.setColor(effect.color[0], effect.color[1], effect.color[2]);
        		window.fillOval(WR*( effect.x - effect.radius ), HR*( height-(effect.y + effect.radius) ), // Why is this plus not a minus???????
        				WR*( 2*effect.radius ), HR*( 2*effect.radius ));
        		window.setColor(effect.color2[0], effect.color2[1], effect.color2[2]); // Black middle
        		window.fillOval(WR*( effect.x - (1 - effect.ringRatio) * effect.radius ), HR*( height-(effect.y + (1 - effect.ringRatio) * effect.radius) ), // ???????
        				WR*( (1 - effect.ringRatio) * 2*effect.radius ), HR*( (1 - effect.ringRatio) * 2*effect.radius ));
        		
        	}
        	
        	// Draw event fields
        	for (EventField event : game.events) {
        		window.setColor(event.color[0], event.color[1], event.color[2]);
        		window.fillOval(WR*( event.x - event.radius ), HR*( height-(event.y + event.radius) ), // Why is this plus not a minus???????
        				WR*( 2*event.radius ), HR*( 2*event.radius ));
        	}
        	for (EventField event : game.events) {
        		window.setColor(event.color2[0], event.color2[1], event.color2[2]); // Black middle
        		window.fillOval(WR*( event.x - (1 - event.ringRatio) * event.radius ), HR*( height-(event.y + (1 - event.ringRatio) * event.radius) ), // ???????
        				WR*( (1 - event.ringRatio) * 2*event.radius ), HR*( (1 - event.ringRatio) * 2*event.radius ));
        	}

        	// Draw balls
        	for (Pongball ball : game.balls) {
        		window.setColor(ball.color[0], ball.color[1], ball.color[2]); // Custom ring color
        		window.fillOval(WR*( ball.x - ball.radius ), HR*( height-(ball.y + ball.radius) ), // Why is this plus not a minus???????
        				WR*( 2*ball.radius ), HR*( 2*ball.radius ));
        	}
        	for (Pongball ball : game.balls) {
        		window.setColor(ball.color2[0], ball.color2[1], ball.color2[2]); // Custom middle color
        		window.fillOval(WR*( ball.x - (1 - ball.ringRatio) * ball.radius ), HR*( height-(ball.y + (1 - ball.ringRatio) * ball.radius) ), // ???????
        				WR*( (1 - ball.ringRatio) * 2*ball.radius ), HR*( (1 - ball.ringRatio) * 2*ball.radius ));
        	}
        	
        	// Draw players
        	for (Player player : game.players) {
        		window.setColor(player.color[0], player.color[1], player.color[2]);
        		window.setStrokeWidth(WR*( player.thickness ));
            	window.drawLine(WR*( player.x ), HR*( height-(player.y - player.length/2.0) ),
            			WR*( player.x ), HR*( height-(player.y + player.length/2.0) ));
        		window.setColor(player.color2[0], player.color2[1], player.color2[2]);
        		window.setStrokeWidth(WR*( (1-player.borderRatio)*player.thickness ));
            	window.drawLine(WR*( player.x ), HR*( height-(player.y - player.length/2.0 + player.borderRatio/2*player.thickness) ),
            			WR*( player.x ), HR*( height-(player.y + player.length/2.0 - player.borderRatio/2*player.thickness) ));
        	}
        	
        	// Draw score label
        	window.setColor(PongGame.white[0], PongGame.white[1], PongGame.white[2]);
        	window.setFontSize((int)(WR*HR*( Math.min(width, height)/25 * (4.0/3.0) )));
        	window.drawStringCentered(game.players[0].points+" | "+game.players[1].points,
        			WR*( width/2.0 ), HR*( height-(Math.min(width, height)/25) )); // Point display
        	
        	// Draw Loading icon
        	if (game.balls.isEmpty() && !game.over()) {
            	window.setColor(PongGame.white[0], PongGame.white[1], PongGame.white[2]);
            	window.setFontSize((int)(WR*HR*( Math.min(width, height)/25 * (4.0/3.0) )));
            	String loading = " ▘▀▜█▟▄▖".substring((int)game.time%40/5,(int)game.time%40/5+1);
            	window.drawStringCentered(loading,
            			WR*( width/2.0 ), HR*( height/2.0 ));
            	window.setBold(false);
        	}
        	
        	// Draw titles
        	for (Title title : game.titles) {
        		double offset = title.size/20.0;
        		window.setFontSize((int)(title.size * (4.0/3.0)));
        		window.setColor(title.color2[0], title.color2[1], title.color2[2]);
        		window.drawStringCentered(title.content, WR*( title.x+offset ), HR*( title.y+offset ));
        		window.drawStringCentered(title.content, WR*( title.x-offset ), HR*( title.y+offset ));
        		window.drawStringCentered(title.content, WR*( title.x-offset ), HR*( title.y-offset ));
        		window.drawStringCentered(title.content, WR*( title.x+offset ), HR*( title.y-offset ));
        		window.setColor(title.color[0], title.color[1], title.color[2]);
        		window.drawStringCentered(title.content, WR*( title.x ), HR*( title.y ));
        	}
        	
        	// Draw refresh button
        	if (WR!=1 || HR!=1 || game.over()) { // If resolution has changed or game is over
        		int fontSize = 12;
        		int margin = 4;
        		window.setColor(255, 127, 127);
        		window.fillRect(0, 0, 6*fontSize, 2*margin+fontSize);
        		window.setColor(255, 255, 255);
        		window.setFontSize((int)(14 * (4.0/3.0)));
        		window.drawString("Restart", margin, margin+fontSize);
        		
        		if (window.isLeftMouseButtonPressed()
        				&& 0 <= window.getMouseX() && window.getMouseX() <= 8*fontSize 
        				&& 0 <= window.getMouseY() && window.getMouseY() <= 2*margin+fontSize) { // If widget is clicked
        			width = (int) window.getWidth();
        	        height = (int) window.getHeight();
        	        game = new PongGame(width, height, game.players[1].isBot()); // Make new game
        		}
        	}

        	int refreshBuffer = 20;
//        	refreshBuffer = 0;
            window.refreshAndClear(refreshBuffer);
        }
        // Game Over
    }
    
    static boolean menuscreen() {
    	int width = 600;
    	int height = 300;
    	Window window = new Window("Pong Menu", width, height);
    	window.setFontSize((int)(height/10.0 * (4.0/3.0)));
        window.open();
        while(window.isOpen()) {
        	boolean inWidget = 0 <= window.getMouseX() && window.getMouseX() <= width
        			&& 0 <= window.getMouseY() && window.getMouseY() <= height;
        	boolean onRight = width/2.0 <= window.getMouseX();
        	
        	window.setColor(47, 49, 54);
        	window.fillRect(0, 0, width, height);
        	
        	// Left side (Multiplayer)
        	if (inWidget && !onRight) {
            	window.setColor(216,63,96);
            	window.fillRect(0, 0, width/2.0, height);
        		window.setColor(224,101,96);
            	window.fillRect(0, 0, width/2.0, height*0.9);
            	window.setColor(255, 255, 255);
            	window.drawStringCentered("Multiplayer", width/4.0, height/2.05);
        	} else {
            	window.setColor(181,39,93);
            	window.fillRect(0, 0, width/2.0, height);
        		window.setColor(216,63,74);
            	window.fillRect(0, 0, width/2.0, height*0.9);
            	window.setColor(0, 0, 0);
            	window.drawStringCentered("Multiplayer", width/4.0, height/2.0);
        	}
        	
        	// Right side (Singleplayer)
        	if (inWidget && onRight) {
            	window.setColor(63,123,226);
            	window.fillRect(width/2.0, 0, width, height);
        		window.setColor(90,161,221);
            	window.fillRect(width/2.0, 0, width, height*0.9);
            	window.setColor(255, 255, 255);
            	window.drawStringCentered("Singleplayer", width/2.0+width/4.0, height/2.05);
        	} else {
            	window.setColor(38,85,204);
            	window.fillRect(width/2.0, 0, width, height);
        		window.setColor(63,123,226);
            	window.fillRect(width/2.0, 0, width, height*0.9);
            	window.setColor(0, 0, 0);
            	window.drawStringCentered("Singleplayer", width/2.0+width/4.0, height/2.0);
        	}
        	
        	if (window.isLeftMouseButtonPressed()) {
        		window.close();
        		return width/2.0 <= window.getMouseX();
        	}
        	
        	window.refreshAndClear(20);
        }
        return false;
    }
}

