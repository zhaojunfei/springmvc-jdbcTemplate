package com.flong.entity;

/**
 * 
 *
 * @created：2015-10-30
 * @author liangjilong
 */
@Relation(User.TABLE)
public class User extends Entity {

    /** 表名常量 */
    public static final String TABLE = Table.USER;

    /**
     * 列名常量
     */
    public static final String COL_ID = "ID";
    public static final String COL_USERNAME = "USERNAME";
    public static final String COL_PASSWORD = "PASSWORD";

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
    private String password;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
