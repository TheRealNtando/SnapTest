import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;


public class ScreenPanel extends JPanel implements NativeKeyListener{
	
	private ScreenCapture screenCapture = new ScreenCapture();
	
	public ScreenPanel(){
		try{
			GlobalScreen.registerNativeHook();
		}
		catch(NativeHookException exp){}
		
		GlobalScreen.addNativeKeyListener(this);
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(screenCapture.latestScreenShot(), 0, 0, getWidth(), getHeight(), null);
		repaint();
	}

	public void nativeKeyPressed(NativeKeyEvent arg0) {
		
		if(arg0.getKeyCode() == NativeKeyEvent.VC_PRINTSCREEN){
			System.out.println("ScreenShot Captured");
			screenCapture.capture();
		}
		
		else if(arg0.getKeyCode() == NativeKeyEvent.VC_DELETE){
			System.out.println("Screenshot deleted");
			screenCapture.delete();
		}
		
		else if(arg0.getKeyCode() == NativeKeyEvent.VC_ESCAPE){
			System.out.println("Escape");
			
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("SnapTest");
			int row=0;
			
			for(int i=1; i<screenCapture.size(); i++){
				
				File temp = new File("" + Integer.toString(i) + ".png");
				
				try{
					ImageIO.write(screenCapture.getImage(i), "png", temp);
					
					InputStream inputstream = new FileInputStream(temp);
					byte[] bytes = IOUtils.toByteArray(inputstream);
					int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
					inputstream.close();
					
					//Returns an object that handles instantiating concrete classes
					CreationHelper helper = workbook.getCreationHelper();
					
					//Creates the top-level drawing patriarch
					Drawing drawing  = sheet.createDrawingPatriarch();
					
					//Create an anchor that is attached to the worksheet
					ClientAnchor anchor = helper.createClientAnchor();
					anchor.setRow1(row*42);
					anchor.setCol1(0);
					
					row++;
					
					//Creates a picture
					Picture picture = drawing.createPicture(anchor, pictureIdx);
					picture.resize();
					
					//Write out Excel file
					FileOutputStream outputStream = null;
					outputStream = new FileOutputStream("ScreenShot.xlsx");
					workbook.write(outputStream);
					outputStream.close();
					
					temp.delete();
				}
				catch(IOException exp){}
				
			}
			
			try{
				workbook.close();
			}
			catch(IOException exp){}
		}
	}

	public void nativeKeyReleased(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

