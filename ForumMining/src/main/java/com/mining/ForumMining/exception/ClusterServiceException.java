package com.mining.ForumMining.exception;

import com.crawl.web.util.exception.ServiceException;
import com.crawl.web.util.messages.ErrorMessage;
import com.crawl.web.util.messages.ErrorMessages;

/*
 * ##############################$History Card$###################################
 * ### Latest changes description should be on the top of the history card list###
 * ###############################################################################
 *  Created Date	Updated Date	Author			Change Description
 *  ============	============	============	===================
 *  28/05/2015		28/05/2015		chandu-atina 	initial creation
 */

public class ClusterServiceException extends ServiceException
{
	/**
	 * This is used in serialization
	 */
	private static final long serialVersionUID = 1L;	
	
	private ErrorMessages errors = new ErrorMessages();
	
	/**
	 * default constructor and calls super class default constructor
	 */
	public ClusterServiceException() {
		super();
	}
	
	/**
	 * this builds the cluster service exception
	 * with supplied customized message and exception
	 * 
	 * @param message
	 * @param cause
	 */
	public ClusterServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * This builds the cluster service exception using supplied
	 * customized error message
	 * 
	 * @param message
	 */
	public ClusterServiceException(String message) {
		super(message);
	}

	/**
	 * this method is used to build the validation failure 
	 * exception using the cause 
	 * 
	 * @param cause
	 */
	public ClusterServiceException(Throwable cause) {
		super(cause);
	}
	
	public ClusterServiceException(ErrorMessages errors) {
		super(errors);
		if(errors != null) {
			this.errors = errors;
		}
	}
	
	/**
	 * this builds the cluster service exception using error
	 * message
	 * 
	 * @param error
	 */
	public ClusterServiceException(ErrorMessage error) {
		super(error.toString());
		this.getErrors().addError(error);
	}
		
	
	/**
	 * This overrides the Object toString() method
	 * for uniqueness of this object
	 * @return string representation of Object
	 */
	@Override
	public String toString() {
		//return super.toString()+"\n"+this.getErrors().toString();
		return super.toString();
	}
	
}
