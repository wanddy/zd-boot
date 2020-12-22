package auth.discard.demo.test.entity;

import java.io.Serializable;

import commons.system.base.entity.JeecgEntity;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: jeecg 测试demo
 * @Author: jeecg-boot
 * @Date: 2018-12-29
 * @Version:V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("demo")
public class JeecgDemo extends JeecgEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 部门编码
     */
    private String sysOrgCode;
    /**
     * 姓名
     */
    private String name;
    /**
     * 关键词
     */
    private String keyWord;
    /**
     * 打卡时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date punchTime;
    /**
     * 工资
     */
    private java.math.BigDecimal salaryMoney;
    /**
     * 奖金
     */
    private Double bonusMoney;
    /**
     * 性别 {男:1,女:2}
     */
    private String sex;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 生日
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private java.util.Date birthday;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 个人简介
     */
    private String content;
}
