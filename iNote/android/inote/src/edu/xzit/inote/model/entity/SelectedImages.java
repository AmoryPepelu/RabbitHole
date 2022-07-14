package edu.xzit.inote.model.entity;

import java.util.LinkedList;

/**
 * 单例模式，储存image
 * 
 * @author John
 *
 */
public class SelectedImages {

	private static SelectedImages instance;
	private LinkedList<String> images;

	private SelectedImages() {
	}

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static SelectedImages getInstance() {
		if (instance == null) {
			synchronized (SelectedImages.class) {
				if (instance == null) {
					instance = new SelectedImages();
				}
			}
		}
		return instance;
	}

	/**
	 * 获得images LinkedList
	 * 
	 * @return
	 */
	public LinkedList<String> getImages() {
		if (images == null) {
			synchronized (SelectedImages.class) {
				if (images == null) {
					images = new LinkedList<String>();
				}
			}
		}
		return images;
	}

	public void clearImages() {
		images = null;
	}

}
