package com.bingo.qiniu.component;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JTextField;

/**
 * 自定义文本框
 *
 * qiniu_tool ： com.bingo.qiniu.QTextField 功能描述：
 *
 * 修改记录：
 *
 */
public class QTextField extends JTextField {

	private static final String uiClassID = "QTextFieldUI";

	/**
	 * 圆角大小
	 */
	private int roundSize;

	/**
	 * 边框颜色
	 */
	private Color borderColor;

	/**
	 * 功能描述：<br>
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	@Override
	protected void paintBorder(Graphics g) {
	}

	public int getRoundSize() {
		return roundSize;
	}

	public void setRoundSize(int roundSize) {
		this.roundSize = roundSize;
	}

	public Color getBorderColor() {
		if (borderColor == null) {
			return Color.BLACK;
		}
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

}
