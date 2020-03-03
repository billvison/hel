package com.xingkaichun.helloworldblockchain.node.dto.common;

import lombok.Data;

@Data
public class ServiceResult<T> {

    private ServiceCode serviceCode;
    private String message;
    private T result;

    public ServiceResult(ServiceCode serviceCode, String message, T result) {
        this.serviceCode = serviceCode;
        this.message = message;
        this.result = result;
    }

    public static<T> ServiceResult<T> createSuccessServiceResult(String message, T result){
        return new ServiceResult(ServiceCode.SUCCESS,message,result);
    }
    public static<T> ServiceResult createFailServiceResult(String message){
        return new ServiceResult(ServiceCode.FAIL,message,null);
    }
    public static boolean isSuccess(ServiceResult serviceResult){
        return serviceResult!=null && serviceResult.getServiceCode() != null && serviceResult.getServiceCode() == ServiceCode.SUCCESS;
    }
}
