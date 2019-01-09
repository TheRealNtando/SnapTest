import javax.swing.JFrame;


public class ScreenFrame extends JFrame{

	private ScreenPanel panel = new ScreenPanel();
	
	public ScreenFrame(){
		
		add(panel);
		setTitle("SnapTest");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		setVisible(true);
	}
}
