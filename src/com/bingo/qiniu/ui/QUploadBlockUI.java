package com.bingo.qiniu.ui;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

import com.bingo.qiniu.component.QUploadBlock;
import com.bingo.qiniu.model.QUploadSubjectModel;

import sun.swing.SwingUtilities2;

public class QUploadBlockUI extends BasicPanelUI {

	public static ComponentUI createUI(JComponent c) {
		return new QUploadBlockUI();
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		QUploadBlock block = (QUploadBlock) c;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		switch (block.getSubjectModel().getStatus()) {
			case QUploadSubjectModel.UPLOAD_STATUS_ING:
				paintIng(g2d, block);
				break;
			case QUploadSubjectModel.UPLOAD_STATUS_FINISH_SUCCESS:
				paintSuccess(g2d, block);
				break;
			case QUploadSubjectModel.UPLOAD_STATUS_FINISH_FAILED:
				paintFailed(g2d, block);
				break;

			default:
				paintPre(g2d, block);
				break;
		}
		g2d.setColor(block.getBottomColor());
		g2d.fillRect(0, block.getHeight() - block.getBottomHeight(), block.getWidth(), block.getBottomHeight());

		if (block.getMouseStatus() != QUploadBlock.MOUSE_STATUS_NORMAL) {
			switch (block.getMouseStatus()) {
				case QUploadBlock.MOUSE_STATUS_HOVER:
					g2d.setColor(block.getMouseHoverColor());
					break;
				case QUploadBlock.MOUSE_STATUS_PRESS:
					g2d.setColor(block.getMousePressColor());
					break;
			}

			g2d.fillRect(0, 0, block.getWidth(), block.getHeight());
		}

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	private void paintPre(Graphics2D g, QUploadBlock block) {
		paintText(g, block, false);
	}

	private void paintIng(Graphics2D g, QUploadBlock block) {
		g.setColor(block.getProcessColor());
		int x = 0, y = block.getHeight() - block.getBottomHeight() - block.getProcessHeight(),
				w = (int) (block.getWidth() * block.getSubjectModel().getPersent());
		Area area = new Area(new Rectangle(x, y, w, block.getProcessHeight()));
		area.add(new Area(new RoundRectangle2D.Double(w - block.getProcessHeight() / 2, y, block.getProcessHeight(), block.getProcessHeight(),
				block.getProcessHeight(), block.getProcessHeight())));

		g.fill(area);

		paintText(g, block, false);
	}

	private void paintSuccess(Graphics2D g, QUploadBlock block) {
		g.setColor(block.getSuccessColor());
		g.fillRect(0, 0, block.getWidth(), block.getHeight() - block.getBottomHeight());

		int x = block.getWidth() - 20 - block.getSuccessImg().getWidth(block);
		int y = (block.getHeight() - block.getSuccessImg().getHeight(block)) / 2;
		g.drawImage(block.getSuccessImg(), x, y, block);

		paintText(g, block, true);
	}

	private void paintFailed(Graphics2D g, QUploadBlock block) {
		g.setColor(block.getFailedColor());
		g.fillRect(0, 0, block.getWidth(), block.getHeight() - block.getBottomHeight());

		int x = block.getWidth() - 20 - block.getFailedImg().getWidth(block);
		int y = (block.getHeight() - block.getFailedImg().getHeight(block)) / 2;
		g.drawImage(block.getFailedImg(), x, y, block);

		paintText(g, block, true);

	}

	private void paintText(Graphics2D g, QUploadBlock block, boolean finished) {
		FontMetrics fm = SwingUtilities2.getFontMetrics(block, block.getFont());
		g.setColor(block.getForeground());
		int y = (block.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
		int x = 20;
		g.drawString(block.getSubjectModel().getLocationUrl(), x, y);
		x = block.getWidth() / 3;
		if (block.getSubjectModel().getUrl() != null) {
			g.drawString(block.getSubjectModel().getUrl(), x, y);
		}
		x = block.getWidth() - block.getWidth() / 3;
		g.drawString(block.getSubjectModel().getFileSize(), x, y);
		if (!finished) {
			String pt = block.getSubjectModel().persentText();
			x = block.getWidth() - 20 - fm.stringWidth(pt);
			g.drawString(pt, x, y);
		}
	}
}
