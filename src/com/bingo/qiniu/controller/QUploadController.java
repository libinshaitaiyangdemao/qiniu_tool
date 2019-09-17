package com.bingo.qiniu.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.bingo.qiniu.listener.QActionEvent;
import com.bingo.qiniu.listener.QActionListener;
import com.bingo.qiniu.model.QUploadModel;
import com.bingo.qiniu.model.QiniuKey;
import com.bingo.qiniu.process.DBProcessor;
import com.bingo.qiniu.process.QiniuProcessor;
import com.bingo.qiniu.utils.Model;
import com.bingo.qiniu.utils.QConstants;
import com.bingo.qiniu.view.ConfigurePane;
import com.bingo.qiniu.view.ContentPanel;
import com.bingo.qiniu.view.Frame;
import com.bingo.qiniu.view.WorkPane;

public class QUploadController implements QActionListener {

	private Frame frame;

	private QUploadModel model;

	private DBProcessor processor;

	private QiniuProcessor qiniuProcessor;

	public QiniuProcessor getQiniuProcessor() {
		if (qiniuProcessor == null) {
			qiniuProcessor = new QiniuProcessor();
		}
		return qiniuProcessor;
	}

	private WorkPane workPane;

	private ConfigurePane configurePane;

	public WorkPane getWorkPane() {
		return workPane;
	}

	public void setWorkPane(WorkPane workPane) {
		this.workPane = workPane;
		if (this.workPane != null) {
			this.workPane.addActionListener(this);
		}
	}

	public ConfigurePane getConfigurePane() {
		return configurePane;
	}

	public void setConfigurePane(ConfigurePane configurePane) {
		this.configurePane = configurePane;
		if (this.configurePane != null) {
			this.configurePane.addActionListener(this);
		}
	}

	public QUploadController(Frame frame, QUploadModel model) {
		super();
		this.frame = frame;
		this.model = model;
		processor = new DBProcessor(System.getProperty("user.dir") + File.separator + "sys.db");
	}

	public void start() {
		invokeWithUIThread(new Runnable() {

			@Override
			public void run() {
				frame.setVisible(true);
				frame.getContentPane().setVisible(false);
				frame.getGlassPane().setVisible(true);
			}
		});

		startCheck();
	}

	private void invokeWithUIThread(Runnable runnable) {
		SwingUtilities.invokeLater(runnable);
	}

	private void invokerWithBackThread(Runnable runnable) {
		new Thread(runnable).start();
	}

	private void startCheck() {
		if (!processor.connected()) {
			try {
				processor.connect();
			} catch (Exception e) {
				e.printStackTrace();
				invokeWithUIThread(new Runnable() {

					@Override
					public void run() {
						frame.getContentPane().setVisible(true);
						((ContentPanel) frame.getContentPane()).showConfigre();
						frame.getGlassPane().setVisible(false);
					}
				});
			}
		}
		List<QiniuKey> keys = processor.getAllQiniuKeys();
		if (keys != null && !keys.isEmpty()) {
			Model.getInstance().setKeys(keys);
			invokeWithUIThread(new Runnable() {

				@Override
				public void run() {
					frame.getContentPane().setVisible(true);
					frame.reloadMenuBar();
					((ContentPanel) frame.getContentPane()).showWordPane();
					frame.getGlassPane().setVisible(false);
				}
			});
		} else {
			invokeWithUIThread(new Runnable() {

				@Override
				public void run() {
					frame.getContentPane().setVisible(true);
					((ContentPanel) frame.getContentPane()).showConfigre();
					frame.getGlassPane().setVisible(false);
				}
			});
		}

	}

	@Override
	public void action(QActionEvent event) {
		int key = event.getKey();
		switch (key) {
			case QConstants.CONFIG_KEY:
				configAction(event);
				break;
			case QConstants.PAGE_CHANGE_WORK:
				changeWorkPageAction(event);
				break;
			case QConstants.BUTTON_ACTION_ADD:
				chooseFiles(event);
				break;
			case QConstants.BUTTON_ACTION_ADD_OVER:
				frame.getGlassPane().setVisible(false);
				break;

			case QConstants.UPLOAD_START:
				frame.menuEnabled(false);
				break;
			case QConstants.UPLOAD_OVER:
				frame.menuEnabled(true);
				break;
			default:
				break;
		}
	}

	private void chooseFiles(final QActionEvent event) {
		frame.getGlassPane().setVisible(true);
	}

	private void changeWorkPageAction(final QActionEvent event) {
		invokerWithBackThread(new Runnable() {

			@Override
			public void run() {
				if (Model.getInstance().getKeys() != null && !Model.getInstance().getKeys().isEmpty()) {
					List<String> blackets = getQiniuProcessor().queryBlackets(Model.getInstance().getCurrentKey());
					if (blackets != null && !blackets.isEmpty()) {
						workPane.updateMenu(blackets.toArray(new String[0]));
						workPane.uploadInfo();
					}
					invokeWithUIThread(new Runnable() {

						@Override
						public void run() {
							workPane.repaint();
							frame.getGlassPane().setVisible(false);
						}
					});
				}
			}
		});
	}

	private void configAction(final QActionEvent event) {
		frame.getGlassPane().setVisible(true);
		invokerWithBackThread(new Runnable() {

			@Override
			public void run() {

				if (Model.getInstance().getKeys() == null) {
					Model.getInstance().setKeys(new ArrayList<QiniuKey>());
				}
				QiniuKey key = new QiniuKey();
				key.setName((String) event.get(ConfigurePane.NAME));
				key.setAk((String) event.get(ConfigurePane.AK));
				key.setSk((String) event.get(ConfigurePane.SK));
				if (!Model.getInstance().getKeys().contains(key)) {
					processor.saveQiniuKey(key);
					Model.getInstance().setKeys(processor.getAllQiniuKeys());
					frame.reloadMenuBar();
				}

				invokeWithUIThread(new Runnable() {

					@Override
					public void run() {
						((ContentPanel) frame.getContentPane()).showWordPane();
					}
				});
				// frame.getGlassPane().setVisible(false);
			}
		});
	}
}
