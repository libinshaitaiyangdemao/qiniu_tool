package com.bingo.qiniu.component;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import com.bingo.qiniu.model.QUploadSubjectModel;
import com.bingo.qiniu.utils.WidgetUtils;

public class QUploadBlock extends JPanel implements MouseListener {

	public static final int MOUSE_STATUS_NORMAL = 0;

	public static final int MOUSE_STATUS_HOVER = 1;

	public static final int MOUSE_STATUS_PRESS = 2;

	public static final Image UPLOAD_SUCCESS_IMAGE = WidgetUtils.getImage("/com/bingo/qiniu/imgs/green_yes.png");

	public static final Image UPLOAD_FAILED_IMAGE = WidgetUtils.getImage("/com/bingo/qiniu/imgs/red_no.png");

	/**
	 * 功能描述：<br>
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String uiClassID = "QUploadBlockUI";

	private QUploadSubjectModel subjectModel;

	private Color bottomColor;

	private int bottomHeight;

	private int mouseStatus;

	private Color mouseHoverColor;

	private Color mousePressColor;

	private Color processColor;

	private Color successColor;

	private Color failedColor;

	private int processHeight;

	private int blockSpace;

	private Image successImg;

	private Image failedImg;

	public QUploadBlock() {
		setBackground(Color.WHITE);
		setFont(WidgetUtils.SMALL_FONT);
		this.addMouseListener(this);
		initDefaults();
	}

	private void initDefaults() {
		bottomHeight = 2;
		bottomColor = Color.GRAY;
		mouseHoverColor = new Color(0, 0, 0, 30);
		mousePressColor = new Color(0, 0, 0, 50);
		blockSpace = 3;
		processColor = new Color(0, 0, 255, 60);
		processHeight = 10;
		successColor = new Color(0, 255, 0, 30);
		failedColor = new Color(255, 0, 0, 30);
		successImg = UPLOAD_SUCCESS_IMAGE;
		failedImg = UPLOAD_FAILED_IMAGE;
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

	public int getMouseStatus() {
		return mouseStatus;
	}

	public void setMouseStatus(int mouseStatus) {
		this.mouseStatus = mouseStatus;
	}

	public Color getMouseHoverColor() {
		return mouseHoverColor;
	}

	public void setMouseHoverColor(Color mouseHoverColor) {
		this.mouseHoverColor = mouseHoverColor;
	}

	public Color getMousePressColor() {
		return mousePressColor;
	}

	public void setMousePressColor(Color mousePressColor) {
		this.mousePressColor = mousePressColor;
	}

	public Color getProcessColor() {
		return processColor;
	}

	public void setProcessColor(Color processColor) {
		this.processColor = processColor;
	}

	public Color getSuccessColor() {
		return successColor;
	}

	public void setSuccessColor(Color successColor) {
		this.successColor = successColor;
	}

	public Color getFailedColor() {
		return failedColor;
	}

	public void setFailedColor(Color failedColor) {
		this.failedColor = failedColor;
	}

	public int getProcessHeight() {
		return processHeight;
	}

	public void setProcessHeight(int processHeight) {
		this.processHeight = processHeight;
	}

	public int getBlockSpace() {
		return blockSpace;
	}

	public void setBlockSpace(int blockSpace) {
		this.blockSpace = blockSpace;
	}

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	public Image getSuccessImg() {
		return successImg;
	}

	public void setSuccessImg(Image successImg) {
		this.successImg = successImg;
	}

	public Image getFailedImg() {
		return failedImg;
	}

	public void setFailedImg(Image failedImg) {
		this.failedImg = failedImg;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(subjectModel.getStatus() == QUploadSubjectModel.UPLOAD_STATUS_FINISH_SUCCESS){
			//上传成功
			StringSelection selection = new StringSelection(subjectModel.getUrl());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, null);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseStatus = MOUSE_STATUS_PRESS;
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (MOUSE_STATUS_PRESS == mouseStatus) {
			mouseStatus = MOUSE_STATUS_HOVER;
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		mouseStatus = MOUSE_STATUS_HOVER;
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseStatus = MOUSE_STATUS_NORMAL;
		repaint();
	}

	public QUploadSubjectModel getSubjectModel() {
		return subjectModel;
	}

	public void setSubjectModel(QUploadSubjectModel subjectModel) {
		this.subjectModel = subjectModel;
	}

}
