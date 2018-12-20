package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.DataCenterApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class JkwwDelDataTest extends DataCenterApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 删除某一季度下的所有业务数据（教科文卫相关的）
     */
    @Test
    public void testDelAllBoData() {
        String statPeriod = "'2018Q2'";
        jdbcTemplate.execute("DELETE FROM `jkwww_gyxghlafdjbqkmcb` WHERE tjzq = " + statPeriod);
        jdbcTemplate.execute("DELETE FROM `jkwww_gzscgjxxqqdjlsylwtgztzylb` WHERE tjzq = " + statPeriod);
        jdbcTemplate.execute("DELETE FROM `jkwww_jjlsylwtjdb` WHERE tjzq = " + statPeriod);
        jdbcTemplate.execute("DELETE FROM `jkwww_jzqptssjsyjlsylwttz` WHERE tjzq = " + statPeriod);
        jdbcTemplate.execute("DELETE FROM `jkwww_msjcssbjghjqssfazdssqkb` WHERE tjzq = " + statPeriod);
        jdbcTemplate.execute("DELETE FROM `jkwww_sjcj_ztztxxb` WHERE sjtjzq = " + statPeriod);
        jdbcTemplate.execute("DELETE FROM `jkwww_sswsqsbjshmsjcssjhzxqk` WHERE tjzq = " + statPeriod);
        jdbcTemplate.execute("DELETE FROM `jkwww_sswsqsbjshmsjcssjsxmjdzb` WHERE tjzq = " + statPeriod);
        jdbcTemplate.execute("DELETE FROM `jkwww_sswsqsbjshmsjcssjsxmjdzb_hzb` WHERE tjzq = " + statPeriod);
        jdbcTemplate.execute("DELETE FROM `jkwww_sswsqsbjshmsjcssndjhb` WHERE tjzq = " + statPeriod);
        jdbcTemplate.execute("DELETE FROM `jkwww_xyjybxtjjlsylwtxfwtyljgylb` WHERE tjzq = " + statPeriod);
    }

}
