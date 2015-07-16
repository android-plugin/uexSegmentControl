package org.zywx.wbpalmstar.plugin.uexsegmentcontrol.VO;

import java.io.Serializable;
import java.util.List;

public class DataInfoVO implements Serializable{
    private static final long serialVersionUID = 8286156871688257537L;
    private int flag;
    private List<String> allData;
    private List<String> showData;
    private int maxShow = -1;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public List<String> getAllData() {
        return allData;
    }

    public void setAllData(List<String> allData) {
        this.allData = allData;
    }

    public List<String> getShowData() {
        return showData;
    }

    public void setShowData(List<String> showData) {
        this.showData = showData;
    }

    public int getMaxShow() {
        return maxShow;
    }

    public void setMaxShow(int maxShow) {
        this.maxShow = maxShow;
    }
}
