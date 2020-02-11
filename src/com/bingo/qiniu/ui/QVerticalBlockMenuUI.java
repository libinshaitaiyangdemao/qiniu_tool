package com.bingo.qiniu.ui;

import java.awt.AlphaComposite;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

import com.bingo.qiniu.component.QVerticalBlockMenu;

import sun.swing.SwingUtilities2;

public class QVerticalBlockMenuUI extends BasicPanelUI {

	private QVerticalBlockMenu menu;

	private int bw;

	private int ts;

	private QVerticalBlockMenuUI(QVerticalBlockMenu menu) {
		this.menu = menu;
	}

	public static ComponentUI createUI(JComponent c) {
		return new QVerticalBlockMenuUI((QVerticalBlockMenu) c);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		bw = (int) (menu.getWidth() * menu.getBlockWidthScale());

		ts = bw / 4;
		if (menu.getElements() != null && !menu.getElements().isEmpty()) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			g2d.setFont(menu.getFont());
			FontMetrics fm = SwingUtilities2.getFontMetrics(menu, menu.getFont());
			int th = (menu.getBlockHeight() + fm.getAscent() - fm.getDescent()) / 2;
			int y = 0;
			int start = (-menu.getContentHeight()) / (menu.getBlockHeight() + menu.getSpace());
			int drawCount = menu.getHeight() / (menu.getBlockHeight() + menu.getSpace()) + 1;
			while (start < menu.getElements().size() && drawCount > 0){
				QVerticalBlockMenu.QMenuElement ele = menu.getElements().get(start);
				start ++;
				drawCount --;
//				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, menu.getAlphaf()));
				if (ele.equals(menu.getSelectedElement())) {
					g2d.setColor(menu.getItemSelectedColor());
				} else {
					g2d.setColor(menu.getItemNormalColor());
				}
				g2d.fillRect(0, y, bw, menu.getBlockHeight());
				if (menu.getBottomColor() != null) {
					g2d.setColor(menu.getBottomColor());
					g2d.fillRect(0, y + menu.getBlockHeight() - menu.getBottomHeight(), bw, menu.getBottomHeight());
				}
//				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));

				g2d.setColor(menu.getForeground());
				g2d.drawString(ele.getText(), ts, y + th);
				y += menu.getBlockHeight();
				y += menu.getSpace();
			}
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

		}
	}

}
