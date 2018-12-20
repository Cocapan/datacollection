package cn.gz.rd.datacollection.model;

import java.util.List;

public class ShowUser extends SysUser{

    private String menuAuthority;

    private List<Integer> menuIds;

    public String getMenuAuthority() {
        return menuAuthority;
    }

    public void setMenuAuthority(String menuAuthority) {
        this.menuAuthority = menuAuthority;
    }

    public List<Integer> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(List<Integer> menuIds) {
        this.menuIds = menuIds;
    }
}
