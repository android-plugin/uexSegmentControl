package org.zywx.wbpalmstar.plugin.uexsegmentcontrol.VO;

import java.io.Serializable;

public class OpenDataVO implements Serializable{
    private static final long serialVersionUID = 2341754908606349258L;
    private double left = 0;
    private double top = 0;
    private double width = -1;
    private double height = -1;
    private DataInfoVO dataInfo;

    public int getLeft() {
        return (int)left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public int getTop() {
        return (int)top;
    }

    public void setTop(double top) {
        this.top = top;
    }

    public int getWidth() {
        return (int)width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public int getHeight() {
        return (int)height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public DataInfoVO getDataInfo() {
        return dataInfo;
    }

    public void setDataInfo(DataInfoVO dataInfo) {
        this.dataInfo = dataInfo;
    }
}
