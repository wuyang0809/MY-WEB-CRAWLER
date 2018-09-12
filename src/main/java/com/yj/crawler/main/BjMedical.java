package com.yj.crawler.main;

/**
 * @author wuyang
 * @date 2018/8/20 10:37
 */
public class BjMedical {

    /** 名称 */
    private String name;
    /** 编号 */
    private String number;
    /** 类型 */
    private String type;
    /** 剂型 */
    private String formulation;
    /** 备注 */
    private String remark;
    /** 医保支付标准 */
    private String medicarePaymentStandard;

    public BjMedical() {

    }

    public BjMedical(String name, String number, String type, String formulation, String remark, String medicarePaymentStandard) {
        this.name = name;
        this.number = number;
        this.type = type;
        this.formulation = formulation;
        this.remark = remark;
        this.medicarePaymentStandard = medicarePaymentStandard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormulation() {
        return formulation;
    }

    public void setFormulation(String formulation) {
        this.formulation = formulation;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMedicarePaymentStandard() {
        return medicarePaymentStandard;
    }

    public void setMedicarePaymentStandard(String medicarePaymentStandard) {
        this.medicarePaymentStandard = medicarePaymentStandard;
    }
}
