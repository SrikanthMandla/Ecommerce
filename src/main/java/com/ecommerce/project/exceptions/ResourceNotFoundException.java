package com.ecommerce.project.exceptions;

public class ResourceNotFoundException  extends RuntimeException{
    String ResourceName;
    String fieldName;
    String field;
    Long fieldId;



    public ResourceNotFoundException(String resourceName, String fieldName, String field) {
        super(String.format("%s not found with %s: %s ", resourceName, field, fieldName));
        ResourceName = resourceName;
        this.fieldName = fieldName;
        this.field = field;
    }

    public ResourceNotFoundException(String resourceName,String field,Long fieldId) {
        super(String.format("%s not found with %s: %d ", resourceName, field, fieldId));
        this.fieldId = fieldId;
        ResourceName = resourceName;
        this.field = field;
    }

}
