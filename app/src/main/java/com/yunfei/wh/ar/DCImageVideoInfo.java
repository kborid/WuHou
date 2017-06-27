package com.yunfei.wh.ar;

/**
 * @author kborid
 * @date 2016/7/22
 */
public class DCImageVideoInfo {
    public String image;
    public String name;
    public boolean isJson;
    public int type;
    public String path;

    public DCImageVideoInfo(String image, String name, boolean isJson, int type, String path) {
        this.image = image;
        this.name = name;
        this.isJson = isJson;
        this.type = type;
        this.path = path;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isJson() {
        return isJson;
    }

    public void setJson(boolean isJson) {
        this.isJson = isJson;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
