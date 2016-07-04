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

public class OpenDataVO implements Serializable {
    private static final long serialVersionUID = 2341754908606349258L;
    private double left = 0;
    private double top = 0;
    private double width = -1;
    private double height = -1;
    private DataInfoVO dataInfo;

    public int getLeft() {
        return (int) left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public int getTop() {
        return (int) top;
    }

    public void setTop(double top) {
        this.top = top;
    }

    public int getWidth() {
        return (int) width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public int getHeight() {
        return (int) height;
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
