package com.example.panache.entity;

import com.example.dao.panache.PanacheEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * https://www.linkedin.com/in/mahdisharifi/
 * @since 28/11/2019
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "T_ACCOUNT")
@Data
@XmlRootElement
public class Account extends PanacheEntity<String> {
    @Id
    @Column(name = "PK_ACOUNT_NO", length = 100)
    @XmlAttribute(name = "account_no")
    private String accountNo;

    private Integer age;

    public Account(String accountNo) {
        this.accountNo = accountNo;
    }

    public Account() {
    }

    @Override
    public String getId() {
        return accountNo;
    }
}
