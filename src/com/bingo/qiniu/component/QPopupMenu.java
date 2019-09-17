package com.bingo.qiniu.component;

import java.awt.Component;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;

import com.sun.awt.AWTUtilities;

/**
 * 类描述注释
 *
 * @author Cydow
 * @version v0.1.0003
 * @since JDK1.6.0_27
 * @see
 */
public class QPopupMenu extends JPopupMenu {

	/**
	 * UI类名称
	 */
	private static final String uiClassID = "QPopupMenuUI";

	/**
	 * 序列版本号
	 */
	private static final long serialVersionUID = -6497888280847098839L;

	private int space;

	/**
	 * 空构造函数
	 */
	public QPopupMenu() {
		this(true);
	}

	/**
	 * 是否是轻格式构造函数
	 *
	 * @param isLightWeightPopu
	 *                轻量级全Java
	 */
	public QPopupMenu(Boolean isLightWeightPopu) {
		super(null);
		initComp(200, 200, isLightWeightPopu);
	}

	/**
	 * 设置高度和位置的构造函数
	 *
	 * @param width
	 *                宽度
	 * @param height
	 *                高度
	 */
	public QPopupMenu(int width, int height) {
		super(null);
		initComp(width, height, true);
	}

	/**
	 * 初始化组件
	 *
	 * @param width
	 *                宽度
	 * @param height
	 *                高度
	 * @param isLightWeightPopu
	 *                是否是全Java模式
	 */
	private void initComp(int width, int height, Boolean isLightWeightPopu) {

		if (isLightWeightPopu) {
			setOpaque(false);
		}
		setOpaque(false);
		setSize(width, height);
		this.setLightWeightPopupEnabled(isLightWeightPopu);
		setBorder(BorderFactory.createEmptyBorder(space, space, space, space));
	}

	/**
	 * 重写展示，目的对于重组件可以复制背景绘制阴影效果
	 *
	 * @param invoker
	 *                调用者
	 * @param x
	 *                显示X坐标
	 * @param y
	 *                显示Y坐标
	 */
	@Override
	public void show(Component invoker, int x, int y) {
		super.show(invoker, x, y);
		JWindow window = getPWindow();
		if (window != null) {
			AWTUtilities.setWindowOpaque(window, false);
		}
	}

	public JWindow getPWindow() {
		Container container = this.getParent();
		while (container != null) {
			if (container instanceof JWindow) {
				return (JWindow) container;
			} else {
				container = container.getParent();
			}
		}
		return null;
	}

	/**
	 * 是否接受焦点 -强制
	 *
	 * @return 是/否
	 */
	@Override
	public boolean isFocusTraversable() {
		return true;
	}

	/**
	 * 获取UI类ID
	 *
	 * @return UI类ID
	 */
	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	public int getSpace() {
		return space;
	}

	public void setSpace(int space) {
		this.space = space;
		setBorder(BorderFactory.createEmptyBorder(space, space, space, space));
	}

}
