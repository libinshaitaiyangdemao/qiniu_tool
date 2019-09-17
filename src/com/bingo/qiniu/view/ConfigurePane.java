package com.bingo.qiniu.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.bingo.qiniu.component.QTextField;
import com.bingo.qiniu.listener.QActionEvent;
import com.bingo.qiniu.listener.QActionListener;
import com.bingo.qiniu.utils.QConstants;
import com.bingo.qiniu.utils.WidgetUtils;

public class ConfigurePane extends JPanel {

	private QTextField ak;

	private QTextField sk;

	private QTextField nameTextField;

	private JLabel title;

	private JLabel akLabel;

	private JLabel nameLabel;

	private JLabel skLabel;

	private JButton button;

	private Set<QActionListener> listeners;

	public static final String AK = "ak";

	public static final String SK = "sk";

	public static final String NAME = "name";

	public void addActionListener(QActionListener listener) {
		if (listeners == null) {
			listeners = new HashSet<>();
		}
		if (listener != null) {
			listeners.add(listener);
		}
	}

	private void fireActionListeners(String akText, String skText, String name) {
		if (listeners != null && !listeners.isEmpty()) {
			Map<Object, Object> map = new HashMap<>();
			map.put(AK, akText);
			map.put(SK, skText);
			map.put(NAME, name);
			QActionEvent event = new QActionEvent(map);
			event.setKey(QConstants.CONFIG_KEY);

			for (QActionListener listener : listeners) {
				listener.action(event);
			}
		}
	}

	/**
	 * 功能描述：<br>
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConfigurePane() {
		this.setBackground(Color.WHITE);
		setLayout(null);
		ak = new QTextField();
		ak.setRoundSize(6);
		add(ak);
		ak.setFont(WidgetUtils.SMALL_FONT);
		sk = new QTextField();
		sk.setRoundSize(6);
		add(sk);
		sk.setFont(WidgetUtils.SMALL_FONT);
		title = new JLabel("配置七牛提供的秘钥");
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setFont(WidgetUtils.BIG_FONT);
		add(title);
		akLabel = new JLabel("AK:");
		akLabel.setHorizontalAlignment(JLabel.RIGHT);
		akLabel.setFont(WidgetUtils.SMALL_FONT);
		add(akLabel);
		skLabel = new JLabel("SK:");
		skLabel.setHorizontalAlignment(JLabel.RIGHT);
		skLabel.setFont(WidgetUtils.SMALL_FONT);
		add(skLabel);
		button = new JButton("保存");
		button.setFont(WidgetUtils.SMALL_FONT);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String akText = ak.getText();
				if (akText == null || akText.trim().isEmpty()) {
					return;
				}
				akText = akText.trim();
				String skText = sk.getText();
				if (skText == null || skText.trim().isEmpty()) {
					return;
				}
				String nameText = nameTextField.getText();
				if (nameText == null || nameText.trim().isEmpty()) {
					return;
				}
				fireActionListeners(akText, skText, nameText);
			}
		});
		add(button);

		nameTextField = new QTextField();
		nameTextField.setRoundSize(6);
		add(nameTextField);
		nameTextField.setFont(WidgetUtils.SMALL_FONT);
		nameLabel = new JLabel("NAME:");
		nameLabel.setHorizontalAlignment(JLabel.RIGHT);
		nameLabel.setFont(WidgetUtils.SMALL_FONT);
		add(nameLabel);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		title.setBounds(width / 4, height / 6, width / 2, height / 6);
		nameLabel.setBounds(width / 4 - 10, height / 3, width / 10, 40);
		akLabel.setBounds(width / 4 - 10, height / 3 + 70, width / 10, 40);
		skLabel.setBounds(width / 4 - 10, height / 3 + 140, width / 10, 40);
		nameTextField.setBounds(width / 4 + width / 10, height / 3, width / 10 * 4, 40);
		ak.setBounds(width / 4 + width / 10, height / 3 + 70, width / 10 * 4, 40);
		sk.setBounds(width / 4 + width / 10, height / 3 + 140, width / 10 * 4, 40);
		button.setBounds(width / 4 * 3 - 80, height / 3 + 210, 80, 40);
	}

}
