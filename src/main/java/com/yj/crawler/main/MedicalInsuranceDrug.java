package com.yj.crawler.main;

/**
 * 基本医疗保险药品
 * @author wuyang
 * @date 2018/8/9 17:21
 */
public class MedicalInsuranceDrug {

    /**
     * 通用名
     */
    private String commonName;

    /**
     * 剂型
     */
    private String formulation;

    /**
     * 甲乙类
     */
    private String type;

    /**
     * 支付
     */
    private String pay;

    /**
     * 限制支付及备注
     */
    private String remark;


    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getFormulation() {
        return formulation;
    }

    public void setFormulation(String formulation) {
        this.formulation = formulation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
