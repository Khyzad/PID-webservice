package com.hida.model;

/**
 * An exception that was created to display what parameter was incorrect and the
 * value that caused an error.
 *
 * @author lruffin
 */
public class BadParameterException extends Exception {

    private Object parameter_;
    private String parameterType_;

    /**
     * Creates a new instance of <code>BadParameterException</code> without
     * detail message.
     */
    public BadParameterException() {
    }

    public BadParameterException(Object parameter, String parameterType) {
        parameter_ = parameter;
        parameterType_ = parameterType;
    }

    @Override
    public String getMessage() {
        return String.format("An invalid '%s' was detected: %s", parameterType_, parameter_);
    }
}
