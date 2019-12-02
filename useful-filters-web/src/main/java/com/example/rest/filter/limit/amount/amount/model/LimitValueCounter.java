package com.example.rest.filter.limit.amount.amount.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Mahdi Sharifi
 * @since 17/11/2019
 * @version 1.0.0
 * https://www.linkedin.com/in/mahdisharifi/
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class LimitValueCounter {
    private long remain;
    private long expireAt;
}
