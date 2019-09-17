package com.bingo.qiniu.component;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

public class QMenuItem extends JMenuItem {

	/**
	 * 功能描述：<br>
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String uiClassID = "QMenuItemUI";

	private Color mouseHoverColor;

	private Color bottomColor;

	private int bottomHeight;

	public QMenuItem() {
		super();
	}

	public QMenuItem(Action a) {
		super(a);
	}

	public QMenuItem(Icon icon) {
		super(icon);
	}

	public QMenuItem(String text, Icon icon) {
		super(text, icon);
	}

	public QMenuItem(String text, int mnemonic) {
		super(text, mnemonic);
	}

	public QMenuItem(String text) {
		super(text);
	}

	public Color getBottomColor() {
		return bottomColor;
	}

	public void setBottomColor(Color bottomColor) {
		this.bottomColor = bottomColor;
	}

	public int getBottomHeight() {
		return bottomHeight;
	}

	public void setBottomHeight(int bottomHeight) {
		this.bottomHeight = bottomHeight;
	}

	@Override
	protected void init(String text, Icon icon) {
		super.init(text, icon);
		this.setMargin(new Insets(10, 5, 10, 5));
	}

	public Color getMouseHoverColor() {
		if (mouseHoverColor == null) {
			return new Color(0, 255, 0, 50);
		}
		return mouseHoverColor;
	}

	public void setMouseHoverColor(Color mouseHoverColor) {
		this.mouseHoverColor = mouseHoverColor;
	}

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

}
