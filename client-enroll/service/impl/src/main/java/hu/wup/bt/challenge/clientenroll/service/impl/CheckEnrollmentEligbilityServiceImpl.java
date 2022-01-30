package hu.wup.bt.challenge.clientenroll.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import hu.wup.bt.challenge.clientenroll.external.clients.CheckCustomerExistClient;
import hu.wup.bt.challenge.clientenroll.external.clients.RiskCalculatorClient;
import hu.wup.bt.challenge.clientenroll.service.CheckEnrollmentEligbilityService;
import hu.wup.bt.challenge.restapi.swagger.model.CheckClientRequest;
import hu.wup.bt.challenge.restapi.swagger.model.CheckClientResponse;
import hu.wup.bt.challenge.restapi.swagger.model.IdCard;
import hu.wup.bt.challenge.restapi.swagger.model.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CheckEnrollmentEligbilityServiceImpl implements CheckEnrollmentEligbilityService{

	@Value("${validation.client.idcard.pattern}")
	private String idCardPatternString;
	
	private Integer HIGH_RISK = 99;
	private Integer MID_RISK = 21;
	
	@Autowired
	private CheckCustomerExistClient checkCustomerExistClient;
	
	@Autowired
	private RiskCalculatorClient riskCalculatorClient;
	
	@Override
	public CheckClientResponse checkEligbility(CheckClientRequest data) {
		final CheckClientResponse response = new CheckClientResponse();
		response.setEnrollPossible(Boolean.FALSE);
		
		if (!validateCustomerDataInternal(data, response))
		{
			return response;
		}
		
		try {
			if (isCustomerAlreadyExistInCore(data))
			{
				response.addErrorItem(new Message().code("CustomerAlreadyExist").message("This customer is already exist in core system."));
				return response;
			}
			
			determineRiskOfCustomer(data, response);
		} catch(RestClientException rce)
		{
			log.info("External system call is failed!", rce);
			response.addErrorItem(new Message().code("InternalError").message("The eligibility of customer cannot be done. Please try it later."));
			return response;
		}
		
		return response;
	}

	private boolean validateCustomerDataInternal(CheckClientRequest data, final CheckClientResponse response) {
		
		if(!isCustomerOldEnough(data.getBirthDate(), response))
		{
			return false;
		}
		
		if (!isIdCardValid(data.getIdCardDetails(), response))
		{
			return false;
		}
		
		return true;
	}

	private boolean isCustomerOldEnough(LocalDate birthDate, final CheckClientResponse response) {
		LocalDate sixteenYearsDate = LocalDate.now().minusYears(16);
		LocalDate eighteenYearsData = LocalDate.now().minusYears(18);
		
		if (birthDate.isAfter(sixteenYearsDate))
		{
			response.addErrorItem(new Message().code("NotEnoughOld").message("The new customer has to be at least 16 years old."));
			return false;
		}
		
		if (birthDate.isAfter(eighteenYearsData))
		{
			response.addWarnItem(new Message().code("ParentAffirmation").message("The new customer is not 18 years old. Parent is  approval?"));
		}
		return true;
	}

	private boolean isIdCardValid(IdCard idCardDetails, CheckClientResponse response) {
		LocalDate now = LocalDate.now();
		LocalDate sixMonthsDate = now.plusMonths(6);
		
		if (now.isAfter(idCardDetails.getExpiry()))
		{
			response.addWarnItem(new Message().code("IdCardExpired").message("The IdCard is expired. It can be risk."));
		} else if (idCardDetails.getExpiry().isBefore(sixMonthsDate) && idCardDetails.getExpiry().isAfter(now))
		{
			response.addWarnItem(new Message().code("IdCardExpiryClose").message("The IdCard expiry in 6 months. It can be risk."));
		}
		
		Pattern idCardPattern = Pattern.compile(idCardPatternString);
		if (!idCardPattern.matcher(idCardDetails.getNumber()).find())
		{
			response.addErrorItem(new Message().code("IdCardNumberInvalidFormat").message("The IdCard format is not matches with the pattern."));
			return false;
		}
		return true;
	}
	
	private boolean isCustomerAlreadyExistInCore(CheckClientRequest data) {
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("idCard", data.getIdCardDetails().getNumber());
		map.add("firstName", data.getFirstName());
		map.add("lastName", data.getLastName());
		map.add("birthDate", data.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		
		return checkCustomerExistClient.checkCustomerExistence(map);
	}
	
	private void determineRiskOfCustomer(CheckClientRequest data, final CheckClientResponse response) {
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("idCard", data.getIdCardDetails().getNumber());
		map.add("firstName", data.getFirstName());
		map.add("lastName", data.getLastName());
		map.add("birthDate", data.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		Integer risk = riskCalculatorClient.calculateRisk(map);
		
		response.setRiskLevel(risk);
		
		if (risk.compareTo(HIGH_RISK) != -1)
		{
			response.addErrorItem(new Message().code("CustomerHighRisk").message("Too high risk of enrollment."));
			return;
		}
		if (risk.compareTo(MID_RISK) != -1)
		{
			response.addWarnItem(new Message().code("CustomerMidRisk").message("Has a risk of enrollment. Need to think about enrollment."));
			response.setEnrollPossible(Boolean.TRUE);
		}
		response.addInfoItem(new Message().code("EnrollmentPossible").message("Everything find ok. Enrollment is startable."));
		response.setEnrollPossible(Boolean.TRUE);
	}
}
