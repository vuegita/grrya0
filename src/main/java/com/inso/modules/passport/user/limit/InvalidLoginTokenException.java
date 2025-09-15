package com.inso.modules.passport.user.limit;

/**
 * 
 * @author Administrator
 *
 */
public class InvalidLoginTokenException extends RuntimeException
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    public static final InvalidLoginTokenException mException = new InvalidLoginTokenException();
}
