package com.bingo.qiniu.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.bingo.qiniu.component.QTextField;
import com.sun.java.swing.plaf.windows.WindowsTextFieldUI;

/**
 * 文本框ui
 *
 * qiniu_tool ： com.bingo.qiniu.ui.QTextFieldUI 功能描述：
 *
 * 修改记录：
 *
 */
public class QTextFieldUI extends WindowsTextFieldUI {

	private QTextField tf = null;

	public QTextFieldUI(QTextField c) {
		this.tf = c;
	}

	public static ComponentUI createUI(JComponent c) {
		return new QTextFieldUI((QTextField) c);
	}

	@Override
	protected void paintBackground(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(tf.getBackground());
		g.fillRoundRect(0, 0, tf.getWidth(), tf.getHeight(), tf.getRoundSize(), tf.getRoundSize());
		g.setColor(tf.getBorderColor());
		g.drawRoundRect(0, 0, tf.getWidth() - 1, tf.getHeight() - 1, tf.getRoundSize(), tf.getRoundSize());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

	}

}
