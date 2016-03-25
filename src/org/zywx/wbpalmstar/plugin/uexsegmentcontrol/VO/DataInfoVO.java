/*
 *  Copyright (C) 2014 The AppCan Open Source Project.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.zywx.wbpalmstar.plugin.uexsegmentcontrol.VO;

import java.io.Serializable;
import java.util.List;

public class DataInfoVO implements Serializable{
    private static final long serialVersionUID = 8286156871688257537L;
    private int isExpand = 1;
    private List<String> allData;
    private List<String> showData;
    private int maxShow = -1;
    private String expandOpenIcon = null;
    private String expandCloseIcon = null;
    private String showedLable = null;
    private String addLable = null;

    public int isExpand() {
        return isExpand;
    }

    public void setExpand(int isExpand) {
        this.isExpand = isExpand;
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

	public String getExpandOpenIcon() {
		return expandOpenIcon;
	}

	public void setExpandOpenIcon(String expandOpenIcon) {
		this.expandOpenIcon = expandOpenIcon;
	}

	public String getExpandCloseIcon() {
		return expandCloseIcon;
	}

	public void setExpandCloseIcon(String expandCloseIcon) {
		this.expandCloseIcon = expandCloseIcon;
	}

	public String getShowedLable() {
		return showedLable;
	}

	public void setShowedLable(String showedLable) {
		this.showedLable = showedLable;
	}

	public String getAddLable() {
		return addLable;
	}

	public void setAddLable(String addLable) {
		this.addLable = addLable;
	}

}
