package com.bingo.qiniu.ui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;

import com.bingo.qiniu.component.QMenuItem;
import com.sun.java.swing.plaf.windows.WindowsMenuItemUI;

public class QMenuItemUI extends WindowsMenuItemUI {

	private QMenuItem menuItem;

	public static ComponentUI createUI(JComponent c) {
		QMenuItemUI ui = new QMenuItemUI();
		ui.menuItem = (QMenuItem) c;
		return ui;
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		super.selectionBackground = menuItem.getMouseHoverColor();
	}

	@Override
	protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
		ButtonModel model = menuItem.getModel();
		Color oldColor = g.getColor();
		int menuWidth = menuItem.getWidth();
		int menuHeight = menuItem.getHeight();

		if (menuItem.isOpaque()) {
			if (model.isArmed() || (menuItem instanceof JMenu && model.isSelected())) {
				g.setColor(((QMenuItem) menuItem).getMouseHoverColor());
				g.fillRect(0, 0, menuWidth, menuHeight);
			} else {
				g.setColor(menuItem.getBackground());
				g.fillRect(0, 0, menuWidth, menuHeight);
			}
			g.setColor(oldColor);
		} else if (model.isArmed() || (menuItem instanceof JMenu && model.isSelected())) {
			g.setColor(((QMenuItem) menuItem).getMouseHoverColor());
			g.fillRect(0, 0, menuWidth, menuHeight);
			g.setColor(oldColor);
		}
		g.setColor(this.menuItem.getBottomColor());

		g.fillRect(0, menuHeight - this.menuItem.getBottomHeight(), menuWidth, this.menuItem.getBottomHeight());
	}

}
