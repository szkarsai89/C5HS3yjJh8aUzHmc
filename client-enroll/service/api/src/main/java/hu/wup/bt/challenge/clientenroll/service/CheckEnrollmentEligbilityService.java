package hu.wup.bt.challenge.clientenroll.service;

import hu.wup.bt.challenge.restapi.swagger.model.CheckClientRequest;
import hu.wup.bt.challenge.restapi.swagger.model.CheckClientResponse;

/**
 * Purpose of provide data are required for decision the enrollment of customer is safe or not. 
 * @author Karsai
 *
 */
public interface CheckEnrollmentEligbilityService {

	/**
	 * Check and validate at first level of Customer Data.
	 * Furthermore made call of external system for sure customer is not added earlier.
	 * At least ask a risk level calculation of customer by call of configured service.
	 * 
	 * @param {@link CustomerData} data contains every data for check and calculation of risk
	 * @return {@link CheckClientResponse} the main format is the different severity lists about the check result
	 *    
	 */
	public CheckClientResponse checkEligbility(CheckClientRequest data);
}
