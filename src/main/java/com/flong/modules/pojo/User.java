package com.flong.modules.pojo;

import com.flong.commons.persistence.Entity;
import com.flong.commons.persistence.annotation.Column;
import com.flong.commons.persistence.annotation.Id;
import com.flong.commons.persistence.annotation.Relation;
import com.flong.commons.persistence.annotation.Table;

/**
 * @Author:liangjilong
 * @Date:2015年10月29日-下午3:45:04
 * @Email:jilonglinag@sina.com
 * @Version:1.0
 * @Description:
 */
@Relation(User.TABLE)
@SuppressWarnings("all")
public class User extends Entity {

    /** 表名常量 */
    public static final String TABLE = Table.USER;

    /**
     * 列名常量
     */
    public static final String COL_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    /**
     * 列属性
     */
    /**  */
    @Id
    @Column(COL_ID)
    private Long id;
    /**  */
    @Column(COL_USERNAME)
    private String username;
    /**  */
    @Column(COL_PASSWORD)
    private String describe;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
   
    public String getDescribe() {
        return describe;
    }
    public void setDescribe(String describe) {
        this.describe = describe;
    }
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
