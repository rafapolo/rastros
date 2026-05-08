package rastros;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;

public class Transmediale extends PApplet {
	private static final long serialVersionUID = 1L;

	int rastroSize = 20;
	float threshold = 60;
	boolean debug = true;

	Capture cam;
	PImage prevFrame;
	ArrayList<ArrayList<Ponto>> rastros = new ArrayList<ArrayList<Ponto>>();

	int t = 0;

	public void settings() {
		fullScreen();
	}

	public void setup() {
		textSize(20);
		background(0);
		frameRate(60);
		smooth();

        cam = new Capture(this, width, height);
        cam.start();
		prevFrame = createImage(cam.width, cam.height, RGB);
	}

	public static void main(String[] args) {
		PApplet.main(Transmediale.class.getName());
	}

	public void draw() {
		t++;
		if (cam.available()) {
			prevFrame.copy(cam, 0, 0, cam.width, cam.height, 0, 0, cam.width, cam.height);
			cam.next();
		}

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

				if (diff > threshold) {
					Ponto atual = new Ponto(current, loc);
					motionAtual.add(atual);
				} else {
					pixels[loc] = color(0);
				}
			}
		}

		if (t > rastroSize) {
			rastros.add(motionAtual);
			if (rastros.size() == rastroSize) {
				rastros.remove(0);
			}

			for (int x = 0; x < rastros.size(); x++) {
				ArrayList<Ponto> motion = rastros.get(x);
				for (int y = 0; y < motion.size(); y++) {
                    Ponto ponto = motion.get(y);
                    int a = 255 / (x + 1);
					pixels[ponto.getPosition()] = color(0, 0, 156, a);
				}
			}
		}

		updatePixels();

		if (debug) {
			text("rastros: " + rastroSize, 10, 30);
			text("contexto: " + threshold, 10, 60);
		}
	}

	public void keyPressed() {
		switch (key) {
			case ' ':
				rastros.clear();
				break;
			case 'd':
				debug = !debug;
				break;
			case 's':
				Calendar cal = Calendar.getInstance();
		    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
		    	String data = sdf.format(cal.getTime());
				String out = System.getProperty("user.home") + "/Desktop/rastro_" + data + ".tiff";
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
