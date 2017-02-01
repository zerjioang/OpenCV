import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import com.googlecode.javacv.CanvasFrame;
import java.awt.Font;
import javax.swing.SwingConstants;

public class MotionApp {

	private JFrame frmSensorDeMovimiento;
	private JLabel lblMov;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MotionApp window = new MotionApp();
					window.frmSensorDeMovimiento.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MotionApp() {
		initialize();
		initCanvas();
	}

	private void initCanvas() {
		CanvasFrame cv = new CanvasFrame("Camara");
		runCam cam = new runCam(cv, frmSensorDeMovimiento, lblMov);
		cam.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSensorDeMovimiento = new JFrame();
		frmSensorDeMovimiento.setTitle("Sensor de Movimiento");
		frmSensorDeMovimiento.setBounds(100, 100, 664, 354);
		frmSensorDeMovimiento.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		lblMov = new JLabel("      ");
		lblMov.setHorizontalTextPosition(SwingConstants.CENTER);
		lblMov.setHorizontalAlignment(SwingConstants.CENTER);
		lblMov.setFont(new Font("Tahoma", Font.BOLD, 15));
        
		GroupLayout groupLayout = new GroupLayout(frmSensorDeMovimiento.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(418, Short.MAX_VALUE)
					.addComponent(lblMov, GroupLayout.PREFERRED_SIZE, 228, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addGap(278)
					.addComponent(lblMov)
					.addContainerGap(23, Short.MAX_VALUE))
		);
		frmSensorDeMovimiento.getContentPane().setLayout(groupLayout);
	}
}
