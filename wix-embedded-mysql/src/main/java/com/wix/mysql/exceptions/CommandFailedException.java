package com.wix.mysql.exceptions;

/**
 * @author viliusl
 * @since 13/02/15
 */
public class CommandFailedException extends RuntimeException {
    public CommandFailedException(String cmd, String schema, int errorCode, String errorMessage) {
        super(String.format("Command '%s' on schema '%s' failed with errCode '%s' and output '%s'",
                cmd, schema, errorCode, errorMessage));
    }

    public CommandFailedException(String cmd, String schema, String errorMessage, Throwable e) {
        super(String.format("Command '%s' on schema '%s' failed with message '%s'", cmd, schema, errorMessage), e);
    }

}
