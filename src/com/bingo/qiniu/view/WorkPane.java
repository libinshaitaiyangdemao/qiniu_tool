package com.bingo.qiniu.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileSystemView;

import com.bingo.qiniu.component.QDownTextField;
import com.bingo.qiniu.component.QDynamicImage;
import com.bingo.qiniu.component.QMenuItem;
import com.bingo.qiniu.component.QPopupMenu;
import com.bingo.qiniu.component.QTextField;
import com.bingo.qiniu.component.QUploadBlock;
import com.bingo.qiniu.component.QVerticalBlockMenu;
import com.bingo.qiniu.listener.QActionEvent;
import com.bingo.qiniu.listener.QActionListener;
import com.bingo.qiniu.listener.QFileInputstreamListener;
import com.bingo.qiniu.model.QUploadModel;
import com.bingo.qiniu.model.QUploadSubjectModel;
import com.bingo.qiniu.upload.CustomResumeUploader;
import com.bingo.qiniu.upload.CustomUploadManager;
import com.bingo.qiniu.utils.Model;
import com.bingo.qiniu.utils.QConstants;
import com.bingo.qiniu.utils.WidgetUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.util.Auth;

public class WorkPane extends JPanel {

	/**
	 * 功能描述：<br>
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ExecutorService executor = Executors.newFixedThreadPool(5);

	private static String MESSAGE_MODEL = "当前为%s 共有文件%d个 成功上传%d个 失败%d个";

	private QVerticalBlockMenu menu;

	private QUploadModel uploadModel;

	private Set<QActionListener> listeners;

	private int menuw = 300;

	private int space = 10;

	private JLabel hostLabel;

	private QTextField hostTextField;

	private JLabel keyLabel;

	private QDownTextField keyDownTextField;

	private JCheckBox coverCheckBox;

	private JButton addButton;

	private JButton cleanButton;

	private JButton uploadButton;

	private JScrollPane scrollPane;

	private JPanel scrollPanelView;

	private JFileChooser fileChooser = null;

	private int vw;

	private int uploadBlockHeight = 100;

	private List<QUploadBlock> uploadBlocks;

	private Set<String> filePaths;

	private DefaultActionListener listener;

	private QPopupMenu popupMenu;

	private JLabel messageLabel;

	private JPanel titleLine;

	private QDynamicImage loading;

	private class UploadInfo {
		boolean uploading;

		private String url;

		private String key;

		private int total;

		private int failed;

		private int success;

		private boolean cover;

		private String blacket;
	}

	private UploadInfo uploadInfo;

	private class DefaultActionListener implements ActionListener, QActionListener, MouseListener {
		private QUploadBlock block;

		@Override
		public void actionPerformed(ActionEvent e) {
			int key = Integer.parseInt(e.getActionCommand());
			switch (key) {
				case QConstants.BUTTON_ACTION_ADD:
					showSelectFileDialog();
					break;
				case QConstants.BUTTON_ACTION_CLEAN:
					if (uploadBlocks != null) {
						List<QUploadBlock> bs = new ArrayList<>();
						for (QUploadBlock b : uploadBlocks) {
							bs.add(b);
						}
						for (QUploadBlock b : bs) {
							removeUploadBlock(b);
						}
						reloadUploadBlocks();
						uploadInfo();
					}
					break;

				case QConstants.DELETE_UPLOAD_BLOCK:
					if (block != null) {
						removeUploadBlock(block);
						reloadUploadBlocks();
						uploadInfo();
					}
					break;
				case QConstants.BUTTON_ACTION_UPLOAD:
					if (preUploadCheck()) {
						enabled(false);
						setBlocksUrl();
						startUpload();
						fireActionListeners(QConstants.UPLOAD_START);
					}
					break;
				default:
					break;
			}
		}

		@Override
		public void action(QActionEvent event) {
			uploadInfo.blacket = (String) event.get(QVerticalBlockMenu.ACTION_SELECTED_KEY);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				block = (QUploadBlock) e.getSource();
				popupMenu.show(block, e.getX(), e.getY());
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

	}

	public void addActionListener(QActionListener listener) {
		if (listeners == null) {
			listeners = new HashSet<>();
		}
		if (listener != null) {
			listeners.add(listener);
		}
	}

	private void fireActionListeners(int key) {
		if (listeners != null && !listeners.isEmpty()) {
			Map<Object, Object> map = new HashMap<>();
			QActionEvent event = new QActionEvent(map);
			event.setKey(key);
			for (QActionListener listener : listeners) {
				listener.action(event);
			}
		}
	}

	public WorkPane() {
		setBackground(Color.WHITE);
		setLayout(null);
		uploadInfo = new UploadInfo();
		menu = new QVerticalBlockMenu();
		menu.setSpace(3);
		menu.setFont(WidgetUtils.MIDDLE_FONT);
		menu.setBottomColor(Color.BLACK);
		menu.setBottomHeight(1);
		menu.setBackground(new Color(234, 234, 234));
		add(menu);

		listener = new DefaultActionListener();
		menu.addActionListener(listener);

		hostLabel = new JLabel("HOST:");
		hostLabel.setFont(WidgetUtils.SMALL_FONT);
		add(hostLabel);
		hostTextField = new QTextField();
		hostTextField.setRoundSize(6);
		hostTextField.setFont(WidgetUtils.SMALL_FONT);
		add(hostTextField);

		keyLabel = new JLabel("KEY:");
		keyLabel.setFont(WidgetUtils.SMALL_FONT);
		add(keyLabel);
		keyDownTextField = new QDownTextField();
		keyDownTextField.setFont(WidgetUtils.SMALL_FONT);
		add(keyDownTextField);

		coverCheckBox = new JCheckBox("覆盖");
		coverCheckBox.setBackground(Color.WHITE);
		coverCheckBox.setSelected(true);
		coverCheckBox.setFont(WidgetUtils.SMALL_FONT);
		add(coverCheckBox);
		addButton = new JButton("添加");
		addButton.setFont(WidgetUtils.SMALL_FONT);
		addButton.setActionCommand(String.valueOf(QConstants.BUTTON_ACTION_ADD));
		add(addButton);
		addButton.addActionListener(listener);

		cleanButton = new JButton("清空");
		cleanButton.setFont(WidgetUtils.SMALL_FONT);
		cleanButton.setActionCommand(String.valueOf(QConstants.BUTTON_ACTION_CLEAN));
		add(cleanButton);
		cleanButton.addActionListener(listener);

		uploadButton = new JButton("上传");
		uploadButton.setFont(WidgetUtils.SMALL_FONT);
		uploadButton.setActionCommand(String.valueOf(QConstants.BUTTON_ACTION_UPLOAD));
		add(uploadButton);
		uploadButton.addActionListener(listener);

		scrollPanelView = new JPanel();
		scrollPanelView.setLayout(null);
		scrollPanelView.setBackground(Color.WHITE);
		scrollPane = new JScrollPane(scrollPanelView);
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setBorder(null);
		add(scrollPane);

		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());

		popupMenu = new QPopupMenu();
		popupMenu.setSpace(space);
		popupMenu.setBackground(Color.WHITE);
		QMenuItem menuItem = new QMenuItem("删除");
		menuItem.setFont(WidgetUtils.SMALL_FONT);
		menuItem.setMouseHoverColor(Color.GRAY);
		menuItem.setBottomColor(Color.BLACK);
		menuItem.setBottomHeight(1);
		menuItem.addActionListener(listener);
		menuItem.setActionCommand(String.valueOf(QConstants.DELETE_UPLOAD_BLOCK));
		popupMenu.add(menuItem);

		messageLabel = new JLabel("");
		messageLabel.setFont(WidgetUtils.SMALL_FONT);
		add(messageLabel);

		titleLine = new JPanel();
		titleLine.setBackground(Color.black);
		add(titleLine);

		loading = new QDynamicImage(QDynamicImage.class.getResourceAsStream("/com/bingo/qiniu/imgs/loading6.gif"), 10);
		add(loading);
		loading.setVisible(false);

		uploadInfo();
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		menu.setBounds(0, 0, menuw, height);
		hostLabel.setBounds(menuw + space, space, width / 30, 40);
		hostTextField.setBounds(hostLabel.getX() + hostLabel.getWidth() + space, space, width / 5, 40);
		keyLabel.setBounds(hostTextField.getX() + hostTextField.getWidth() + space, space, width / 40, 40);
		keyDownTextField.setBounds(keyLabel.getX() + keyLabel.getWidth() + space, space, width / 5, 40);
		messageLabel.setBounds(menuw + space, space * 2 + 40, width / 4, 40);
		uploadButton.setBounds(keyDownTextField.getX() + keyDownTextField.getWidth() - width / 20, space * 2 + 40, width / 20, 40);
		cleanButton.setBounds(uploadButton.getX() - space - width / 20, space * 2 + 40, width / 20, 40);
		addButton.setBounds(cleanButton.getX() - space - width / 20, space * 2 + 40, width / 20, 40);
		coverCheckBox.setBounds(addButton.getX() - space - width / 20, space * 2 + 40, width / 20, 40);
		titleLine.setBounds(menuw + space, space * 3 + 40 * 2 - 1, width - menuw - space, 1);
		scrollPane.setBounds(menuw + space, space * 3 + 40 * 2, width - menuw - space, height - space * 3 - 40 * 2);

		loading.setBounds(width - loading.getImageWidth() - space * 4, 0, loading.getImageWidth(), loading.getImageHeight());
		vw = width - menuw - space * 3;
	}

	public QUploadModel getUploadModel() {
		return uploadModel;
	}

	public void showSelectFileDialog() {
		final int flag = fileChooser.showDialog(this, "选择要上传的文件");
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (flag == JFileChooser.APPROVE_OPTION) {
					fireActionListeners(QConstants.BUTTON_ACTION_ADD);
					File[] files = fileChooser.getSelectedFiles();
					addFiles(files);
					reloadUploadBlocks();
					uploadInfo();
					fireActionListeners(QConstants.BUTTON_ACTION_ADD_OVER);
				}
			}
		}).start();
	}

	private void addFiles(File[] files) {
		if (files != null && files.length > 0) {
			for (File file : files) {
				if (file.isFile()) {
					if (uploadBlocks == null) {
						uploadBlocks = new ArrayList<>();
					}
					QUploadBlock block = createUploadBlock(file);
					if (block != null) {
						uploadBlocks.add(block);
					}
				} else if (file.isDirectory()) {
					addFiles(file.listFiles());
				}
			}
		}
	}

	private void removeUploadBlock(QUploadBlock block) {
		uploadBlocks.remove(block);
		block.removeMouseListener(listener);
		filePaths.remove(block.getSubjectModel().getLocationUrl());
		scrollPanelView.remove(block);
	}

	private QUploadBlock createUploadBlock(File file) {
		if (filePaths == null) {
			filePaths = new HashSet<>();
		}
		if (filePaths.add(file.getPath())) {
			QUploadBlock block = new QUploadBlock();
			QUploadSubjectModel model = new QUploadSubjectModel();
			model.setFileSize(String.format("%.2fM", file.length() * 1f / 1024 / 1024));
			model.setLocationUrl(file.getPath());
			model.setPersent(0f);
			block.setSubjectModel(model);
			block.addMouseListener(listener);
			scrollPanelView.add(block);
			return block;
		}
		return null;
	}

	private void reloadUploadBlocks() {
		int y = 0;
		if (uploadBlocks != null && !uploadBlocks.isEmpty()) {
			for (QUploadBlock b : uploadBlocks) {
				b.setBounds(0, y, vw, uploadBlockHeight);
				y += uploadBlockHeight;
				y += space;
			}
		}
		uploadInfo.total = uploadBlocks.size();
		scrollPanelView.setPreferredSize(new Dimension(vw, y));
		scrollPanelView.repaint();
	}

	public void setUploadModel(QUploadModel uploadModel) {
		this.uploadModel = uploadModel;

	}

	public void updateMenu(String[] menus) {
		menu.clear();
		if (menus != null) {
			for (int i = 0; i < menus.length; i++) {
				menu.putElements(new String[] { menus[i], menus[i] });
			}
		}
	}

	public void uploadInfo() {
		String key = null;
		if (Model.getInstance().getCurrentKey() != null) {
			key = Model.getInstance().getCurrentKey().getName();
		} else {
			key = "";
		}
		messageLabel.setText(String.format(MESSAGE_MODEL, key, uploadInfo.total, uploadInfo.success, uploadInfo.failed));
	}

	private boolean preUploadCheck() {
		if (uploadInfo.uploading) {
			return false;
		}
		if (uploadInfo.blacket == null || uploadInfo.blacket.isEmpty()) {
			return false;
		}
		if (uploadInfo.total <= 0) {
			return false;
		}
		uploadInfo.key = subPoint(keyDownTextField.getText(), true, true, "/");
		uploadInfo.url = subPoint(hostTextField.getText(), true, true, "/");
		uploadInfo.cover = coverCheckBox.isSelected();
		return true;
	}

	public void enabled(boolean enabled) {
		menu.setEnabled(enabled);
		hostTextField.setEditable(enabled);
		keyDownTextField.setEnabled(enabled);
		coverCheckBox.setEnabled(enabled);
		addButton.setEnabled(enabled);
		cleanButton.setEnabled(enabled);
		uploadButton.setEnabled(enabled);

		loading.setVisible(!enabled);
	}

	private void setBlocksUrl() {
		if (uploadBlocks != null) {
			StringBuilder builder = new StringBuilder();
			if (uploadInfo.url != null && !uploadInfo.url.isEmpty()) {
				builder.append(uploadInfo.url);
			}
			if (uploadInfo.key != null && !uploadInfo.key.isEmpty()) {
				builder.append("/").append(uploadInfo.key);
			}
			builder.append("/");
			for (QUploadBlock b : uploadBlocks) {
				File file = new File(b.getSubjectModel().getLocationUrl());
				b.getSubjectModel().setUrl(builder + file.getName());
			}
			scrollPanelView.repaint();
		}
	}

	private String subPoint(String text, boolean start, boolean end, String sp) {
		if (text == null || (!start && !end) || sp == null || sp.length() > text.length()) {
			return text;
		}
		if (start) {
			boolean flag = text.startsWith(sp);
			while (flag) {
				text = text.substring(sp.length());
				flag = text.startsWith(sp);
			}
		}
		if (end) {
			boolean flag = text.endsWith(sp);
			while (flag) {
				text = text.substring(0, text.length() - sp.length());
				flag = text.endsWith(sp);
			}
		}
		return text;

	}

	private void startUpload() {
		uploadInfo.uploading = true;
		new UIThread().start();
		UploadThread uploadThread = new UploadThread();
		final List<Future<?>> futures = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Future<?> future = executor.submit(uploadThread);
			futures.add(future);
		}
		new Thread() {
			public void run() {
				for (Future<?> f : futures) {
					try {
						f.get();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
				synchronized (uploadInfo) {
					uploadInfo.uploading = false;
					enabled(true);
					fireActionListeners(QConstants.UPLOAD_OVER);
				}
			};
		}.start();
	}

	private class UIThread extends Thread {

		@Override
		public void run() {
			while (uploadInfo.uploading) {
				uploadInfo();
				WorkPane.this.repaint();
				try {
					sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			uploadInfo();
			WorkPane.this.repaint();
		}
	}

	private class UploadThread implements Runnable {
		private Auth auth;

		private AtomicInteger index = new AtomicInteger(0);

		private String unCoverToken;

		@Override
		public void run() {
			int i = nextInt();
			while (i < uploadBlocks.size()) {
				final QUploadBlock qub = uploadBlocks.get(i);
				QUploadSubjectModel s = qub.getSubjectModel();
				if (s.getStatus() != QUploadSubjectModel.UPLOAD_STATUS_PRE) {
					continue;
				}
				s.setStatus(QUploadSubjectModel.UPLOAD_STATUS_ING);
				File file = new File(qub.getSubjectModel().getLocationUrl());
				String key = uploadInfo.key + "/" + file.getName();
				String token = auth(uploadInfo.blacket, key);
				CustomUploadManager uploadManager = new CustomUploadManager();
				try {
					// 调用put方法上传，这里指定的key和上传策略中的key要一致
					final long flength = file.length();
					CustomResumeUploader resumeUploader = uploadManager.buildResumeUploader(file, key, token, null, null, false);
					resumeUploader.addListener(new QFileInputstreamListener() {

						@Override
						public void read(long finishBit) {
							QUploadSubjectModel sm = qub.getSubjectModel();
							sm = qub.getSubjectModel();
							sm.setPersent(1f * finishBit / flength);
						}
					});
					Response res = resumeUploader.upload();
					// 打印返回的信息
					System.out.println(res.bodyString());
					s.setStatus(QUploadSubjectModel.UPLOAD_STATUS_FINISH_SUCCESS);
					synchronized (uploadInfo) {
						uploadInfo.success++;
					}
				} catch (QiniuException e) {
					s.setStatus(QUploadSubjectModel.UPLOAD_STATUS_FINISH_FAILED);
					synchronized (uploadInfo) {
						uploadInfo.failed++;
					}
					Response r = e.response;
					System.out.println(r.toString());
					e.printStackTrace();
					try {
						System.out.println(r.bodyString());
					} catch (QiniuException e1) {
					}
				}
				i = nextInt();
			}
		}

		private synchronized int nextInt() {
			return index.getAndIncrement();
		}

		private synchronized String auth(String blacket, String key) {
			if (auth == null) {
				auth = Auth.create(Model.getInstance().getCurrentKey().getAk(), Model.getInstance().getCurrentKey().getSk());
			}
			if (!uploadInfo.cover) {
				if (unCoverToken == null) {
					unCoverToken = auth.uploadToken(blacket);
				}
				return unCoverToken;
			}
			// System.out.println("--------------------");
			return auth.uploadToken(blacket, key, 36000, null);
		}
	}
}
