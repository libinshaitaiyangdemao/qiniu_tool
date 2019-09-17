package com.bingo.qiniu.component;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JComponent;

import com.sun.imageio.plugins.gif.GIFImageMetadata;

public class QDynamicImage extends JComponent {

	/**
	 * 功能描述：<br>
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ImageReader reader;

	private int count = 0;

	private DynamicFrame[] frames;

	private Map<Integer, Integer[]> frameMap;

	private int index = 0;

	private int delayFactor;

	private Integer imageWidth;

	private Integer imageHeight;

	/**
	 * 是否允许缩放
	 */
	private boolean zoom;

	@Override
	protected void paintComponent(Graphics g) {
		if (zoom) {
			paintComponentZoom(g);
		} else {
			paintComponentUnZoom(g);
		}
	}

	private void paintComponentZoom(Graphics g) {
		g.drawImage(frames[0].image, getVZoomSize(frames[0].x), getHZoomSize(frames[0].y), getVZoomSize(frames[0].width),
				getHZoomSize(frames[0].height), this);
		if (index > 0) {
			Integer[] array = frameMap.get(index);
			for (Integer i : array) {
				g.drawImage(frames[i].image, getVZoomSize(frames[i].x), getHZoomSize(frames[i].y), getVZoomSize(frames[i].width),
						getHZoomSize(frames[i].height), this);
			}
		}
	}

	private int getVZoomSize(int source) {
		float p = getWidth() * 1f / getImageWidth();
		int result = Math.round(source * p);
		return result;
	}

	private int getHZoomSize(int source) {
		float p = getHeight() * 1f / getImageHeight();
		int result = Math.round(source * p);
		return result;
	}

	private void paintComponentUnZoom(Graphics g) {
		g.drawImage(frames[0].image, frames[0].x, frames[0].y, this);
		if (index > 0) {
			Integer[] array = frameMap.get(index);
			for (Integer i : array) {
				g.drawImage(frames[i].image, frames[i].x, frames[i].y, this);
			}
		}
	}

	private int getFirstIndex(int index) {
		int tempIndex = index;
		while (tempIndex > 1) {
			if (tempIndex - 1 > 0 && frames[tempIndex - 1].disposalMethod == 2) {
				return index;
			}
			tempIndex--;
		}
		return tempIndex;
	}

	public QDynamicImage(InputStream inputStream, int delayFactor) {
		this(inputStream, delayFactor, false);
	}

	public QDynamicImage(InputStream inputStream, int delayFactor, boolean zoom) {
		setOpaque(false);
		this.zoom = zoom;
		this.delayFactor = delayFactor;
		frameMap = new HashMap<Integer, Integer[]>();
		ImageInputStream imageIn = null;
		try {
			imageIn = ImageIO.createImageInputStream(inputStream);
			Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("gif");
			if (iter.hasNext()) {
				reader = iter.next();
			}
			reader.setInput(imageIn, false);
			count = reader.getNumImages(true);
			frames = new DynamicFrame[count];
			for (int i = 0; i < count; i++) {
				frames[i] = new DynamicFrame();
				frames[i].image = reader.read(i);
				frames[i].x = ((GIFImageMetadata) reader.getImageMetadata(i)).imageLeftPosition;
				frames[i].y = ((GIFImageMetadata) reader.getImageMetadata(i)).imageTopPosition;
				frames[i].width = ((GIFImageMetadata) reader.getImageMetadata(i)).imageWidth;
				frames[i].height = ((GIFImageMetadata) reader.getImageMetadata(i)).imageHeight;
				frames[i].disposalMethod = ((GIFImageMetadata) reader.getImageMetadata(i)).disposalMethod;
				frames[i].delayTime = ((GIFImageMetadata) reader.getImageMetadata(i)).delayTime;
				if (frames[i].delayTime == 0) {
					frames[i].delayTime = 1;
				}
			}
			for (int i = 1; i < count; i++) {
				if (frames[i].disposalMethod == 2) {
					// restoreToBackgroundColor
					frameMap.put(new Integer(i), new Integer[] { i });
					continue;
				}
				// doNotDispose
				int firstIndex = getFirstIndex(i);
				List<Integer> l = new ArrayList<Integer>();
				for (int j = firstIndex; j <= i; j++) {
					l.add(j);
				}
				frameMap.put(new Integer(i), l.toArray(new Integer[] {}));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (imageIn != null) {
				try {
					imageIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Thread t = new Thread(new Delay());
		t.start();
	}

	public int getImageWidth() {
		if (imageWidth == null) {
			imageWidth = 0;
			for (DynamicFrame gf : frames) {
				if (gf.width + gf.x > imageWidth) {
					imageWidth = gf.width + gf.x;
				}
			}
		}
		return imageWidth;
	}

	public int getImageHeight() {
		if (imageHeight == null) {
			imageHeight = 0;
			for (DynamicFrame gf : frames) {
				if (gf.height + gf.y > imageHeight) {
					imageHeight = gf.height + gf.y;
				}
			}
		}
		return imageHeight;
	}

	private class Delay implements Runnable {

		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(frames[index].delayTime * delayFactor);
				} catch (InterruptedException e) {
				}

				index++;
				if (index >= count) {
					index = 0;
				}
			}
		}
	}

	public class DynamicFrame {

		public BufferedImage image;

		public int x;

		public int y;

		public int width;

		public int height;

		public int disposalMethod;

		public int delayTime;
	}

	public DynamicFrame[] getFrames() {
		return frames;
	}

	public void setFrames(DynamicFrame[] frames) {
		this.frames = frames;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isZoom() {
		return zoom;
	}

	public void setZoom(boolean zoom) {
		this.zoom = zoom;
	}

}
