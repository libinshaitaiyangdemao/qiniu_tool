package com.bingo.qiniu.component;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import com.bingo.qiniu.listener.QActionEvent;
import com.bingo.qiniu.listener.QActionListener;

/**
 * 垂直块状菜单
 *
 * qiniu_tool ： com.bingo.qiniu.component.QVerticalBlockMenu 功能描述：
 *
 * 修改记录：
 *
 */
public class QVerticalBlockMenu extends JPanel implements MouseListener,MouseWheelListener {

	private static final String uiClassID = "QVerticalBlockMenuUI";

	public static final String ACTION_SOURCE = "action_source";

	public static final String ACTION_SELECTED_TEXT = "action_selected_text";

	public static final String ACTION_SELECTED_KEY = "action_selected_key";

	private boolean enabled = true;

	private int blockHeight;

	private float blockWidthScale;

	private int space;

	private Color itemNormalColor;

	private Color itemSelectedColor;

	private float alphaf;

	private QMenuElement selectedElement;

	private Color bottomColor;

	private int bottomHeight;

	private int contentHeight;

	public int getContentHeight() {
		return contentHeight;
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

	public QMenuElement getSelectedElement() {
		return selectedElement;
	}

	private Set<QActionListener> listeners;

	public void addActionListener(QActionListener listener) {
		if (listeners == null) {
			listeners = new HashSet<>();
		}
		if (listener != null) {
			listeners.add(listener);
		}
	}

	public class QMenuElement {

		private String text;

		private String key;

		private Rectangle bounds;

		public QMenuElement(String text, String key) {
			super();
			this.text = text;
			this.key = key;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Rectangle getBounds() {
			return bounds;
		}

		public void setBounds(Rectangle bounds) {
			this.bounds = bounds;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((text == null) ? 0 : text.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			QMenuElement other = (QMenuElement) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			if (text == null) {
				if (other.text != null)
					return false;
			} else if (!text.equals(other.text))
				return false;
			return true;
		}

		private QVerticalBlockMenu getOuterType() {
			return QVerticalBlockMenu.this;
		}

	}

	public QVerticalBlockMenu() {
		super();
		setBackground(Color.WHITE);
		space = 10;
		itemNormalColor = Color.WHITE;
		itemSelectedColor = Color.GRAY;
		alphaf = 0.6f;
		blockHeight = 100;
		blockWidthScale = 0.98f;

		this.addMouseListener(this);
		this.addMouseWheelListener(this);
	}

	private List<QMenuElement> elements;

	public int getBlockHeight() {
		return blockHeight;
	}

	public void setBlockHeight(int blockHeight) {
		this.blockHeight = blockHeight;
	}

	public float getBlockWidthScale() {
		return blockWidthScale;
	}

	public void setBlockWidthScale(float blockWidthScale) {
		this.blockWidthScale = blockWidthScale;
	}

	public int getSpace() {
		return space;
	}

	public void setSpace(int space) {
		this.space = space;
	}

	public Color getItemNormalColor() {
		return itemNormalColor;
	}

	public void setItemNormalColor(Color itemNormalColor) {
		this.itemNormalColor = itemNormalColor;
	}

	public Color getItemSelectedColor() {
		return itemSelectedColor;
	}

	public void setItemSelectedColor(Color itemSelectedColor) {
		this.itemSelectedColor = itemSelectedColor;
	}

	public List<QMenuElement> getElements() {
		return elements;
	}

	/**
	 * 添加菜单
	 * 
	 * @param eles
	 */
	public void putElements(String[]... eles) {
		if (eles != null) {
			if (elements == null) {
				elements = new ArrayList<>();
			}
			for (String[] ele : eles) {
				QMenuElement element = new QMenuElement(ele[0], ele[1]);
				if (!elements.contains(element)) {
					elements.add(element);
				}
			}
		}
	}

	public void clear() {
		if (elements != null) {
			elements.clear();
		}
	}

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	/**
	 * 功能描述：<br>
	 * 
	 */
	private static final long serialVersionUID = 1017375000605525349L;

	public float getAlphaf() {
		return alphaf;
	}

	public void setAlphaf(float alphaf) {
		this.alphaf = alphaf;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (enabled && e.getButton() == MouseEvent.BUTTON1 && elements != null && !elements.isEmpty()) {
			Point point = e.getPoint();
			int index = (point.y + space) / (blockHeight + space);
			index += (- contentHeight) / (blockHeight + space);
			if (index < elements.size()) {
				selectedElement = elements.get(index);
				repaint();
				fireActionListeners();
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	private void fireActionListeners() {
		if (listeners != null && !listeners.isEmpty()) {
			Map<Object, Object> map = new HashMap<>();
			map.put(ACTION_SOURCE, this);
			map.put(ACTION_SELECTED_TEXT, selectedElement.getText());
			map.put(ACTION_SELECTED_KEY, selectedElement.getKey());

			QActionEvent event = new QActionEvent(map);
			for (QActionListener listener : listeners) {
				listener.action(event);
			}
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

		int min = (blockHeight + space) * (elements == null ? 0 : elements.size());
		if(min > getHeight()){
			contentHeight += (e.getWheelRotation() * 50);
			if(contentHeight > 0){
				contentHeight = 0;
			}
			if(contentHeight < getHeight() - min){
				contentHeight = getHeight() - min;
			}
			repaint();
		}
	}
}
