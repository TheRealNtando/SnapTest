import javax.swing.*;

public class ScreenFrame extends JFrame{

	public ScreenFrame(){

		add(new MainPanel());
		setTitle("SnapTest");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(500, 500);
		setVisible(true);
	}
}
