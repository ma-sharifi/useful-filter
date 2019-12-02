package com.example.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.example.serializer.GsonModel;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * @since 11/11/2019
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResponseDto<T> extends GsonModel {
    @NotNull
    private long statusCode=-1;//Mandatory
    @NotNull
    private String message;//Mandatory
    private String title;
    private String informationLink;
    private String devMessage;
    private String parameters;
    @NotNull
    private MessageLevel messageLevel=MessageLevel.ERROR;//Mandatory
    private ErrorCodeSource source = ErrorCodeSource.THIS; //TODO MUST BE TRANSIENT
    private transient int httpStatus;
    private Long elapsedTime;
    private List<T> payload;//save service response to payload

    public ResponseDto() {
    }

    public ResponseDto(long statusCode) {
        this.statusCode = statusCode;
    }

    public ResponseDto(List<T> payload) {
        this.payload = payload;
    }
}
