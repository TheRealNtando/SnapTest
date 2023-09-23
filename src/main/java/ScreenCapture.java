import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;


public class ScreenCapture {

	private final Logger log;
	private final ArrayList<BufferedImage> images = new ArrayList<>();
	
	public ScreenCapture(){

		log = LogManager.getLogger(ScreenCapture.class);
		try{
			BufferedImage image = ImageIO.read(new File("SnapTest.png"));

            images.add(image);
		}
		catch(IOException exp){
			log.error(exp.getMessage());
		}
	}
	
	public void captureScreen(int monitorNumber){
		try{
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] monitors = ge.getScreenDevices();

			Rectangle screenBounds = monitors[monitorNumber-1].getDefaultConfiguration().getBounds();
			BufferedImage screenShot = new Robot().createScreenCapture(screenBounds);
			images.add(screenShot);
		}
		catch(AWTException exp){
			log.error(exp.getMessage());
		}
	}
	
	public void delete(){
		if(images.size() > 1)
			images.remove(images.size()-1);
	}

	public List<BufferedImage> getScreenshots(){
		if(images.size()==1)
			System.exit(0);
		return images;
	}

	public BufferedImage latestScreenShot(){
		return images.get(images.size()-1);
	}
	
	public BufferedImage getImage(int k){
		BufferedImage image = null;
		
		for(int i=0; i<images.size(); i++){
			if(k==i)
				image = images.get(i);
		}
		
		return image;
	}
	
	public int size(){
		return images.size();
	}

}

