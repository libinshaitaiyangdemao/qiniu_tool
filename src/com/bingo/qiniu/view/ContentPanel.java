package com.bingo.qiniu.view;

import java.awt.CardLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.bingo.qiniu.listener.QActionEvent;
import com.bingo.qiniu.listener.QActionListener;
import com.bingo.qiniu.utils.QConstants;

public class ContentPanel extends JPanel {

	/**
	 * 功能描述：<br>
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CardLayout layout;

	private static final String CONFIGURE_PAGE = "configure_page";

	private static final String WORK_PAGE = "work_page";

	public ContentPanel() {
		setBackground(Color.WHITE);
		layout = new CardLayout();
		setLayout(layout);
	}

	public void addConfigPane(JComponent component) {
		add(CONFIGURE_PAGE, component);
	}

	public void addWordPane(JComponent component) {
		add(WORK_PAGE, component);
	}

	public void showConfigre() {
		layout.show(this, CONFIGURE_PAGE);
		fireActionListeners(QConstants.PAGE_CHANGE_CONFIG);
	}

	public void showWordPane() {
		layout.show(this, WORK_PAGE);
		fireActionListeners(QConstants.PAGE_CHANGE_WORK);
	}

	private Set<QActionListener> listeners;

	public static final String AK = "ak";

	public static final String SK = "sk";

	public void addActionListener(QActionListener listener) {
		if (listeners == null) {
			listeners = new HashSet<>();
		}
		if (listener != null) {
			listeners.add(listener);
		}
	}

	private void fireActionListeners(int key) {
		if (listeners != null && !listeners.isEmpty()) {
			Map<Object, Object> map = new HashMap<>();
			QActionEvent event = new QActionEvent(map);
			event.setKey(key);

			for (QActionListener listener : listeners) {
				listener.action(event);
			}
		}
	}
}
