package com.example.panache.entity;

import com.example.dao.panache.PanacheEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * https://www.linkedin.com/in/mahdisharifi/
 * @since 28/11/2019
 */
//https://quarkus.io/guides/hibernate-orm-panache
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "T_PERSON")
@Data
public class Person extends PanacheEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;
    public LocalDate birth;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

}