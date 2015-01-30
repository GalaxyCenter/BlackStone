package com.dodola.model;

import java.util.List;

import apollo.data.model.Photo;

public class Infos {
	private String			newsLast	= "0";
	private int				type		= 0;
	private List<Photo>	newsInfos;

	public String getNewsLast() {
		return newsLast;
	}

	public void setNewsLast(String newsLast) {
		this.newsLast = newsLast;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<Photo> getNewsInfos() {
		return newsInfos;
	}

	public void setNewsInfos(List<Photo> newsInfos) {
		this.newsInfos = newsInfos;
	}

}
