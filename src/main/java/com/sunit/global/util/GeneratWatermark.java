package com.sunit.global.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class GeneratWatermark {

	/**
	 * 生成水印图片
	* @Title: GeneratWatermarkForUserName 
	* @Description: 
	* @param @param userName  水印文字  
	* @param @param fileName  水印文件名   
	* @param @param fileName  水印文件路径
	* @return void  
	* @throws 
	* @author joye 
	* Nov 20, 2013 3:04:22 PM
	 */
	public static void GeneratWatermarkForUserName(String userName,File file) {
		Random random = new Random(); 
		String sRand = userName; 
		int width = sRand.length() * 13 * 2, height = 20;
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics(); 
		g.setColor(new Color(255, 255, 255));
		g.fillRect(0, 0, width, height);
		g.setFont(new Font("宋体", Font.PLAIN, 18));
		g.setColor(new Color(200, 200, 200));
		g.drawString(sRand, 0, 16);
		// } 
		g.dispose(); 
		
		try { 
			ImageIO.write(image, "jpg", file);  
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
}
