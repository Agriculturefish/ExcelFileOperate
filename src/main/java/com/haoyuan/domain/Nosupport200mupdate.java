package com.haoyuan.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import org.springframework.context.annotation.Scope;
// import org.springframework.stereotype.Component;
//
// import com.dvte.boss.interf.card.util.StringSqlUtil;
// import com.dvte.boss.utils.Constants;
// import com.springjdbc.annotation.BaseDomain;
// import com.springjdbc.annotation.Entity;
// import com.springjdbc.annotation.Id;
// import com.springjdbc.annotation.Table;

// @Scope("prototype")
// @Component
// @Entity
// @Table(name = "t_nosupport200mupdate")
public class Nosupport200mupdate /*extends BaseDomain<Nosupport200mupdate>*/ {
    // @Id
    private String id;
    private String boxno;
    private String boxsn;
    private String mac;
    private String caid;
    private String cmmac;
    private String wifimac;
    private Date createdate;
    private String remark;

    public Nosupport200mupdate(){
        super();
        // this.t = Nosupport200mupdate.class;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBoxno() {
        return boxno;
    }

    public void setBoxno(String boxno) {
        this.boxno = boxno;
    }

    public String getBoxsn() {
        return boxsn;
    }

    public void setBoxsn(String boxsn) {
        this.boxsn = boxsn;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getCaid() {
        return caid;
    }

    public void setCaid(String caid) {
        this.caid = caid;
    }

    public String getCmmac() {
        return cmmac;
    }

    public void setCmmac(String cmmac) {
        this.cmmac = cmmac;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public String getWifimac() {
        return wifimac;
    }

    public void setWifimac(String wifimac) {
        this.wifimac = wifimac;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    /***
     * @param sql
     * @param
     */
    public <T> void bacthSave(String sql,List<T> nosupportList){
        List<Object> temp = new ArrayList<Object>();
        for(T nosupport200mupdate:nosupportList){
            temp.add(nosupport200mupdate);
        }
        // super.jdbcBatchUpdate(sql,temp);
    }


}
