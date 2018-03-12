package com.sunit.global.util.poi;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;

public interface WriteActionInterface<T> {
	
	public boolean doAction(HSSFRow row, T rowObject);
}
