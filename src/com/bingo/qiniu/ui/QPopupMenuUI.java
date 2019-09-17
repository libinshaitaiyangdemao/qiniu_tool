package com.bingo.qiniu.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;

import com.bingo.qiniu.component.QPopupMenu;

public class QPopupMenuUI extends BasicPopupMenuUI {
	private QPopupMenu menu;

	/**
	 * 创建UI
	 *
	 * @param c
	 *                组件
	 * @return UI对象
	 */
	public static ComponentUI createUI(JComponent c) {
		return new QPopupMenuUI(c);
	}

	/**
	 * 默认构造函数
	 *
	 * @param com
	 *                组件
	 */
	public QPopupMenuUI(JComponent com) {
		this.menu = (QPopupMenu) com;
	}

	/**
	 * 重写绘制，以实现必要的效果
	 *
	 * @param g
	 *                画布
	 * @param c
	 *                组件
	 */
	@Override
	public void update(Graphics g, JComponent c) {
		int width = (int) menu.getPreferredSize().getWidth();
		int height = (int) menu.getPreferredSize().getHeight();

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(menu.getBackground());
		g2d.fillRoundRect(0, 0, width, height, 15, 15);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

}
