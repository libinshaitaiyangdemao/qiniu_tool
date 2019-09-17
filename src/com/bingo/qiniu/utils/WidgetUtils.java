/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bingo.qiniu.utils;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 *
 * @author liumm
 */
public class WidgetUtils {

	private static double standardWidth = 1920.0;

	private static double standardHeight = 1080.0;

	public static final Font MIDDLE_FONT = createFont("MIDDLE_FONT", Font.PLAIN, 24);

	public static final Font SMALL_FONT = createFont("SMALL_FONT", Font.PLAIN, 18);

	public static final Font BIG_FONT = createFont("BIG_FONT", Font.PLAIN, 36);

	/*
	 * 获取宽比例系数
	 */
	public static double getWidthScale() {
		double scale = (double) java.awt.Toolkit.getDefaultToolkit().getScreenSize().width / standardWidth;
		return scale;
	}

	/*
	 * 获取高比例系数
	 */
	public static double getHeightScale() {
		double scale = (double) java.awt.Toolkit.getDefaultToolkit().getScreenSize().height / standardHeight;
		return scale;
	}

	/*
	 * 获取综合比例
	 */
	public static double getScale() {
		double scaleW = (double) java.awt.Toolkit.getDefaultToolkit().getScreenSize().width / standardWidth;
		double scaleH = (double) java.awt.Toolkit.getDefaultToolkit().getScreenSize().height / standardHeight;
		return Math.min(scaleW, scaleH);
	}

	/**
	 * 创建字体
	 */
	public static Font createFont(String name, int style, int size) {
		int newsize = (int) (size * getWidthScale());
		if (newsize % 2 != 0) {
			newsize++;
		}
		return new Font(name, style, newsize);
	}

	/**
	 * 获取图片
	 * 
	 * @param path
	 * @return
	 */
	public static BufferedImage getImage(String path) {
		try {
			BufferedImage img = ImageIO.read(WidgetUtils.class.getResourceAsStream(path));
			return img;
		} catch (IOException ex) {
			return null;
		}

	}
}
