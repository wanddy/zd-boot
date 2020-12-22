package auth.discard.demo.test.vo;

import java.util.List;

import auth.discard.demo.test.entity.JeecgOrderCustomer;
import auth.discard.demo.test.entity.JeecgOrderTicket;

import lombok.Data;

@Data
public class JeecgOrderMainPage {

    /**
     * 主键
     */
    private String id;
    /**
     * 订单号
     */
    private String orderCode;
    /**
     * 订单类型
     */
    private String ctype;
    /**
     * 订单日期
     */
    private java.util.Date orderDate;
    /**
     * 订单金额
     */
    private Double orderMoney;
    /**
     * 订单备注
     */
    private String content;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private java.util.Date createTime;
    /**
     * 修改人
     */
    private String updateBy;
    /**
     * 修改时间
     */
    private java.util.Date updateTime;

    private List<JeecgOrderCustomer> jeecgOrderCustomerList;
    private List<JeecgOrderTicket> jeecgOrderTicketList;

}
