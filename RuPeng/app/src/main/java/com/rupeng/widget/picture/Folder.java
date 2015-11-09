package com.rupeng.widget.picture;



import java.util.List;

/**
 * Created by AMing on 15/10/16.
 * Company RongCloud
 * 文件夹的实体 bean
 */
public class Folder {
    public String name;
    public String path;
    public Image cover;
    public List<Image> images;

    @Override
    public boolean equals(Object o) {
        try {
            Folder other = (Folder) o;
            return this.path.equalsIgnoreCase(other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
