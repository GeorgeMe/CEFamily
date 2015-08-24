package com.yunyan.toybricks.bean;

import java.io.Serializable;

/**
 * 下载文件实体类，实现序列化接口，这样可以直接在intent中传递该对象
 * @author gzc
 *
 */
public class FileInfo implements Serializable {

	private int id;
	// 下载该文件的网址
	private String url;
	private String fileName;
	// 已经下载了多少字节
	private int finished;
	// 文件的大小
	private int length;

	private String Name;
	private String Version;
	private String Displayname;
	private String Description;
	private String Icon;
	private String ToyBrick;

	public FileInfo(int id, String url, String fileName, int finished, int length, String name, String version, String displayname, String description, String icon, String toyBrick) {
		this.id = id;
		this.url = url;
		this.fileName = fileName;
		this.finished = finished;
		this.length = length;
		Name = name;
		Version = version;
		Displayname = displayname;
		Description = description;
		Icon = icon;
		ToyBrick = toyBrick;
	}

	@Override
	public String toString() {
		return "FileInfo{" +
				"id=" + id +
				", url='" + url + '\'' +
				", fileName='" + fileName + '\'' +
				", finished=" + finished +
				", length=" + length +
				", Name='" + Name + '\'' +
				", Version='" + Version + '\'' +
				", Displayname='" + Displayname + '\'' +
				", Description='" + Description + '\'' +
				", Icon='" + Icon + '\'' +
				", ToyBrick='" + ToyBrick + '\'' +
				'}';
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getVersion() {
		return Version;
	}

	public void setVersion(String version) {
		Version = version;
	}

	public String getDisplayname() {
		return Displayname;
	}

	public void setDisplayname(String displayname) {
		Displayname = displayname;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getIcon() {
		return Icon;
	}

	public void setIcon(String icon) {
		Icon = icon;
	}

	public String getToyBrick() {
		return ToyBrick;
	}

	public void setToyBrick(String toyBrick) {
		ToyBrick = toyBrick;
	}
}
