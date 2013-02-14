package traildentes;

import processing.core.*;
import processing.video.*;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Traildentes extends PApplet {
	private static final long serialVersionUID = 1L;
	
	int rastroSize = 30;		
	float threshold = 60;
	boolean debug = true;
	
	Capture video;
	PImage prevFrame;
	ArrayList<ArrayList<Ponto>> rastros = new ArrayList<ArrayList<Ponto>>();
	
	int t = 0;
	
	public void setup() {		 
		size(800, 600);
		textSize(20);
		video = new Capture(this, width, height, 30);
		video.settings();
		prevFrame = createImage(video.width, video.height, RGB);
	}

	public void draw() {		
		t++;
		if (video.available()) {
			prevFrame.copy(video, 0, 0, video.width, video.height, 0, 0, video.width, video.height);
			prevFrame.updatePixels();
			video.read();
		}

		loadPixels();
		video.loadPixels();
		prevFrame.loadPixels();

		ArrayList<Ponto> motionAtual = new ArrayList<Ponto>();
		for (int x = 0; x < video.width; x++) {
			for (int y = 0; y < video.height; y++) {
				int loc = x + y * video.width; 												
				int current = video.pixels[loc];												
				int previous = prevFrame.pixels[loc];

				float r1 = red(current);
				float g1 = green(current);
				float b1 = blue(current);
				float r2 = red(previous);
				float g2 = green(previous);
				float b2 = blue(previous);
				float diff = dist(r1, g1, b1, r2, g2, b2);

				// How different are the colors?
				if (diff > threshold) {									
					Ponto atual = new Ponto(previous, loc);
					motionAtual.add(atual);
				} else {
					pixels[loc] = color(0);
				}
			}
		}
		
		if (t > rastroSize) { // after 50 draws
			rastros.add(motionAtual);
			if(rastros.size()==rastroSize){ // array completo? excluir rastro 0
				rastros.remove(0);
			}			

			// aplicar rastro
			for (int x = 0; x < rastros.size(); x++) {
				ArrayList<Ponto> motion = rastros.get(x);
				// para cada motion
				for (int y = 0; y < motion.size(); y++) {
					// para cada ponto
					Ponto ponto = motion.get(y);
					Float R = (new Float(-x*x*10 +255) / rastroSize) * 255;
					Float G = (new Float(-(x-rastroSize/2)*(x-rastroSize/2)*10+255) / rastroSize) * 105;
					Float B = (new Float(-(x-rastroSize)*(x-rastroSize)*20+255) / rastroSize) * 55;
					Float A = R;
					Float cor = (new Float(x)/rastros.size())*255; 
					pixels[ponto.getPosition()] = Math.round(ponto.getCor());
				}
			}
		}

		updatePixels();
			
		if (debug){
			text("rastros: " + rastroSize, 10, 30);
			text("contexto: " + threshold, 10, 60);
		}
	}

	public void keyPressed() {		
		switch(key){ 
				case ' ':
					rastros.clear();
					break;
				case 'd':
					debug = !debug;
					break;
				case 's':
					Calendar cal = Calendar.getInstance();
			    	cal.getTime();
			    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
			    	String data = sdf.format(cal.getTime());
					String out = "/Users/rafaelpolo/Desktop/catete/rua "+data+".tiff";
					save(out);
					text("Salvo!", 400, 300);
					break;
		}
		
		switch (keyCode) {
		    case UP:
		    	threshold++;
		      break;
		    case DOWN:
		    	threshold--;
		      break; 
		    case RIGHT:
		    	rastroSize++;
		      break;
		    case LEFT:
		    	rastroSize--;
		    	break;
		  }		
	}
	
}

class Ponto{
	int cor;
	int position;
	
	public Ponto(int cor, int pos){
		this.cor = cor;
		this.position = pos;
	}
	
	public int getCor() {
		return cor;
	}
	public void setCor(int cor) {
		this.cor = cor;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	
}


