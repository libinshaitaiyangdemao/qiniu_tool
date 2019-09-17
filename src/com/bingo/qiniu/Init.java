package com.bingo.qiniu;

import javax.swing.UIManager;

import com.bingo.qiniu.controller.QUploadController;
import com.bingo.qiniu.model.QUploadModel;
import com.bingo.qiniu.utils.WidgetUtils;
import com.bingo.qiniu.view.ConfigurePane;
import com.bingo.qiniu.view.ContentPanel;
import com.bingo.qiniu.view.Frame;
import com.bingo.qiniu.view.WorkPane;

public class Init {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.bingo.qiniu.lookandfeel.CustomLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Frame frame = new Frame("七牛文件上传工具", WidgetUtils.getImage("/com/bingo/qiniu/imgs/logo.jpg"));
		ContentPanel contentPanel = new ContentPanel();
		frame.setContentPane(contentPanel);
		ConfigurePane configurePane = new ConfigurePane();
		contentPanel.addConfigPane(configurePane);
		WorkPane workPane = new WorkPane();
		contentPanel.addWordPane(workPane);
		QUploadModel model = new QUploadModel();
		QUploadController controller = new QUploadController(frame, model);
		contentPanel.addActionListener(controller);
		controller.setConfigurePane(configurePane);
		controller.setWorkPane(workPane);

		controller.start();
	}
}
