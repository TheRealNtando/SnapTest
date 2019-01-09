import java.awt.AWTException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class ScreenCapture {
	private ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
	
	public ScreenCapture(){
		
		try{
			Image image = ImageIO.read(new File("SnapTest.png"));
			BufferedImage buffered = (BufferedImage) image;
			
			images.add(buffered);
		}
		catch(IOException exp){}
	}
	
	public void capture(){
		try{
			Robot robot = new Robot();
			Rectangle rect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage screenShot = robot.createScreenCapture(rect);
			images.add(screenShot);
		}
		catch(AWTException exp){}
	}
	
	public void delete(){
		if(images.size() > 1)
			images.remove(images.size()-1);
	}
	
	public ArrayList<BufferedImage> getScreenshots(){
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

