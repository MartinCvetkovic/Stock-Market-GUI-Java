package stockmarket.exceptions;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


@SuppressWarnings("serial")
public class ErrorDialog extends Dialog {

	public ErrorDialog(Frame owner, String message) {
		super(owner, "Error", true);
		setBounds(owner.getX() + owner.getWidth() / 2, owner.getY() + owner.getHeight() / 2, 300, 150);
		add(new Label(message));
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		
		pack();
		setVisible(true);
	}
}
