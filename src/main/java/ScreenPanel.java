import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
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


@SuppressWarnings("serial")
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
			
			if(screenCapture.size() > 1) {
				
				JFileChooser fileChooser = new JFileChooser();
				
				if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				
					
					try {
						
						Workbook workBook = new XSSFWorkbook();
						Sheet sheet = workBook.createSheet("SnapTest");
						int row = 0;
						
						for(int i=1; i<screenCapture.size(); i++) {
							
							File temp = new File("" + Integer.toString(i) + ".png");
							
							ImageIO.write(screenCapture.getImage(i), "png", temp);
							
							InputStream inputstream = new FileInputStream(temp);
							byte[] bytes = IOUtils.toByteArray(inputstream);
							int pictureIdx = workBook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
							inputstream.close();
							
							//Returns an object that handles instantiating concrete classes
							CreationHelper helper = workBook.getCreationHelper();
							
							//Creates the top-level drawing patriarch
							@SuppressWarnings("rawtypes")
							Drawing drawing  = sheet.createDrawingPatriarch();
							
							//Create an anchor that is attached to the worksheet
							ClientAnchor anchor = helper.createClientAnchor();
							anchor.setRow1(row*55);
							anchor.setCol1(0);
							
							row++;
							
							//Creates a picture
							Picture picture = drawing.createPicture(anchor, pictureIdx);
							picture.resize();
							
							//Write out Excel file
							FileOutputStream outputStream = new FileOutputStream(fileChooser.getSelectedFile().getAbsolutePath() + ".xlsx");
							workBook.write(outputStream);
							outputStream.close();
							
							temp.delete();
						}
						
						workBook.close();
						
						JOptionPane.showMessageDialog(null, "Your test has been snapped.");
					}
					catch(FileNotFoundException exp) {}
					catch(IOException exp) {}
					
					//Delete images from app after saving screenshot
					for(int i=0; i<screenCapture.size(); i++) {
						screenCapture.delete();
					}
				}
			}
		}
	}

	public void nativeKeyReleased(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}