package com.bingo.qiniu.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.bingo.qiniu.utils.WidgetUtils;

public class QDownTextField extends JPanel implements ActionListener {

	/**
	 * 功能描述：<br>
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private QTextField textField;

	private QPopupMenu popupMenu;

	private JButton button;

	public QDownTextField() {
		textField = new QTextField();
		textField.setRoundSize(6);
		textField.setBorderColor(Color.GRAY);
		popupMenu = new QPopupMenu();
		popupMenu.setSpace(5);
		popupMenu.setBackground(Color.WHITE);
		add(textField);
		button = new JButton(new ImageIcon(WidgetUtils.getImage("/com/bingo/qiniu/imgs/down.png")));
		add(button);
		button.addActionListener(this);
		button.setActionCommand("BUTTON");
		setLayout(null);
	}

	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
		textField.setEditable(enabled);
	}

	public String getText() {
		return textField.getText();
	}

	public void setText(String text) {
		textField.setText(text);
	}

	@Override
	public void setFont(Font font) {
		if (textField != null) {
			textField.setFont(font);
		}
	}

	public QDownTextField addDownItem(String item) {
		QMenuItem menuItem = new QMenuItem(item);
		menuItem.setFont(WidgetUtils.SMALL_FONT);
		popupMenu.add(menuItem);
		menuItem.addActionListener(this);
		return this;
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		textField.setBounds(0, 0, width - 36, height);
		button.setBounds(width - 35, 0, 35, height);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("BUTTON".equals(e.getActionCommand())) {
			popupMenu.show(this, 0, this.getHeight());
		} else {
			QMenuItem item = (QMenuItem) e.getSource();
			textField.setText(item.getText());
		}
	}
}
