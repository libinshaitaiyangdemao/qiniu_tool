package com.bingo.qiniu.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

public class QGlassPane extends JPanel {

	/**
	 * 功能描述：<br>
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Color background;

	private QDynamicImage loading;

	public QGlassPane() {
		setOpaque(false);
		setLayout(null);
		background = new Color(0, 0, 0, 90);
		setBackground(background);
		loading = new QDynamicImage(QDynamicImage.class.getResourceAsStream("/com/bingo/qiniu/imgs/loading5.gif"), 10);
		add(loading);
		addMouseListener(new MouseAdapter() {
		});
		addMouseMotionListener(new MouseMotionAdapter() {
		});
		addKeyListener(new KeyAdapter() {
		});
		setFocusTraversalKeysEnabled(false);
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent evt) {
				requestFocusInWindow();
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		int x = 0, y = 0, w = getWidth(), h = getHeight();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(background);
		g2d.fillRect(x, y, w, h);

		// g2d.setColor(Color.gray);
		// g2d.fillRect(300, h / 2 - 10, w - 600, 20);
		// g2d.setColor(waitColor);
		// g2d.fillRect(start, h / 2 - 10, 5, 20);
		g2d.dispose();
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		loading.setBounds(x + (width - loading.getImageWidth()) / 2, y + (height - loading.getImageWidth()) / 2, loading.getImageWidth(),
				loading.getImageHeight());
	}
}
