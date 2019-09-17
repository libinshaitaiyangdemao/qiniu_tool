package com.bingo.qiniu.lookandfeel;

import javax.swing.UIDefaults;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

/**
 * 继承自window的自定义皮肤
 *
 * qiniu_tool ： com.bingo.qiniu.lookandfeel.CustomLookAndFeel 功能描述：
 *
 * 修改记录：
 *
 */
public class CustomLookAndFeel extends WindowsLookAndFeel {

	/**
	 * 功能描述：<br>
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getDescription() {
		return "custom lookandfeel";
	}

	@Override
	public String getID() {
		return "custom lookandfeel";
	}

	@Override
	protected void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);
		String uiPackageName = "com.bingo.qiniu.ui.";
		Object[] uis = { "QTextFieldUI", uiPackageName + "QTextFieldUI", "QVerticalBlockMenuUI", uiPackageName + "QVerticalBlockMenuUI",
				"QPopupMenuUI", uiPackageName + "QPopupMenuUI", "QMenuItemUI", uiPackageName + "QMenuItemUI", "QUploadBlockUI",
				uiPackageName + "QUploadBlockUI" };
		table.putDefaults(uis);
	}

	/**
	 * 是否是底层平台的外观
	 *
	 * @return 是否是底层平台的外观
	 */
	@Override
	public boolean isNativeLookAndFeel() {
		return false;
	}

	/**
	 * 底层平台支持和/或允许此外观
	 *
	 * @return 是否支持此外观
	 */
	@Override
	public boolean isSupportedLookAndFeel() {
		return true;
	}
}
