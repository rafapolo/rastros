package rastros;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;

public class Traildentes extends PApplet {
	private static final long serialVersionUID = 1L;
	
	int rastroSize = 20;		
	float threshold = 60;
	boolean debug = true;

	Capture cam;
	PImage prevFrame;
	ArrayList<ArrayList<Ponto>> rastros = new ArrayList<ArrayList<Ponto>>();

	int t = 0;
	
	public void setup() {		 
		size(900, 700);
		textSize(20);
		background(0);
		frameRate(60);
		smooth();
	    
        cam = new Capture(this, width, height, 30);
        //cam.settings();
		prevFrame = createImage(cam.width, cam.height, RGB);

	}

	  public static void main(String args[]) {
		   PApplet.main(new String[] { "--present", Traildentes.class.getName() });
	  }
	
	public void draw() {		
		t++;
		if (cam.available()) {
			prevFrame.copy(cam, 0, 0, cam.width, cam.height, 0, 0, cam.width, cam.height);
			//prevFrame.updatePixels();
			cam.read();
		}

        //cam.loadPixels();
        //prevFrame.loadPixels();
        loadPixels();

		ArrayList<Ponto> motionAtual = new ArrayList<Ponto>();
		for (int x = 0; x < cam.width; x++) {
			for (int y = 0; y < cam.height; y++) {
				int loc = x + y * cam.width;
				int current = cam.pixels[loc];
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
					Ponto atual = new Ponto(current, loc);
					motionAtual.add(atual);
				} else {
					pixels[loc] = color(0);
				}
			}
		}
		
		if (t > rastroSize) { // after x draws
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

//                  Float R = (new Float(-x*x*10 +255) / rastroSize) * 255;
//					Float G = (new Float(-(x-rastroSize/2)*(x-rastroSize/2)*10+255) / rastroSize) * 105;
//					Float B = (new Float(-(x-rastroSize)*(x-rastroSize)*20+255) / rastroSize) * 55;
//					Float A = R;
//					Float cor = (new Float(x)/rastros.size())*255;

                    int a = 255 / (x+1) ;
                    //System.out.println(a);
                    
                    
					pixels[ponto.getPosition()] = color(0,0,156, a);//Math.round(ponto.getCor());
				}
			}
		}

		updatePixels();
			
		if (debug){
			text("rastros: " + rastroSize, 10, 30);
			text("contexto: " + threshold, 10, 60);
		}
	}
	
	boolean sketchFullScreen() {
		return true;
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

