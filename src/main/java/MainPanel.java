import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
public class MainPanel extends JPanel implements NativeKeyListener{

	private final Logger log = Logger.getLogger(MainPanel.class);
	private final ScreenCapture screenCapture = new ScreenCapture();

	private JButton delBtn = new JButton("Del");
	private JButton saveBtn = new JButton("Save screenshots");

	JComboBox<String> jScreen = new JComboBox<>(new String[]{"1","2","3","4","5"});

	public MainPanel(){

		delBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("ScreenShot deleted");
				screenCapture.delete();
			}
		});

		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if(screenCapture.size() > 1) {

					JFileChooser fileChooser = new JFileChooser();

					if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

						try {

							Workbook workBook = new XSSFWorkbook();
							Sheet sheet = workBook.createSheet("SnapTest");
							int row = 0;

							for(int i=1; i<screenCapture.size(); i++) {

								File temp = new File(i + ".png");

								ImageIO.write(screenCapture.getImage(i), "png", temp);

								InputStream inputstream = Files.newInputStream(temp.toPath());
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
						catch(IOException exp) { log.error(exp.getMessage()); }


						//Delete images from app after saving screenshot
						for(int i=0; i<screenCapture.size(); i++) {
							screenCapture.delete();
						}
					}
				}
			}
		});

		jScreen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});


		add(jScreen);
		add(delBtn);
		add(saveBtn);

		try{
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(this);
		}
		catch(NativeHookException exp){
			log.error(exp.getMessage());
		}

	}

	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(screenCapture.latestScreenShot(), 0, 30, getWidth(), getHeight(), null);
		repaint();
	}

	public void nativeKeyPressed(NativeKeyEvent arg0) {

		if(arg0.getKeyCode() == NativeKeyEvent.VC_PRINTSCREEN){
			log.info("ScreenShot Captured");

			screenCapture.captureScreen(jScreen.getSelectedIndex()+1);
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) { /* TODO document why this method is empty */ }

	@Override
	public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) { /* TODO document why this method is empty */ }


}