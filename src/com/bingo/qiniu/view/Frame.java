package com.bingo.qiniu.view;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.bingo.qiniu.component.QGlassPane;
import com.bingo.qiniu.component.QMenuItem;
import com.bingo.qiniu.model.QiniuKey;
import com.bingo.qiniu.utils.Model;

public class Frame extends JFrame {

	/**
	 * 功能描述：<br>
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JMenu menu;

	private Map<JMenuItem, QiniuKey> itemMap;

	private ActionListener listener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem item = (JMenuItem) e.getSource();
			if (item.getActionCommand().equals("add_key")) {
				getQContentPane().showConfigre();
			} else {
				QiniuKey key = itemMap.get(item);
				Model.getInstance().setCurrentKey(key);
				getQContentPane().showWordPane();
			}
		}
	};

	public Frame(String title, Image icon) {
		itemMap = new HashMap<>();
		setTitle(title);
		setIconImage(icon);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		// setContentPane(new ContentPanel());
		setGlassPane(new QGlassPane());
		JMenuBar menuBar = new JMenuBar();
		menu = new JMenu("选项");
		QMenuItem item = new QMenuItem("添加七牛KEY");
		item.setActionCommand("add_key");
		item.addActionListener(listener);
		menu.add(item);
		menuBar.add(menu);
		setJMenuBar(menuBar);
	}

	public void reloadMenuBar() {
		Set<JMenuItem> set = itemMap.keySet();
		itemMap.clear();
		for (JMenuItem item : set) {
			item.removeActionListener(listener);
			menu.remove(item);
		}
		for (QiniuKey key : Model.getInstance().getKeys()) {
			QMenuItem menuItem = new QMenuItem(key.getName());
			itemMap.put(menuItem, key);
			menuItem.addActionListener(listener);
			menu.add(menuItem);
		}
	}

	public ContentPanel getQContentPane() {
		return (ContentPanel) getContentPane();
	}

	public void showGlassPane() {
		getGlassPane().setVisible(true);
	}

	public void hiddeGlassPane() {
		getGlassPane().setVisible(false);
	}

	public void menuEnabled(boolean enabled) {
		menu.setEnabled(enabled);
	}
}
