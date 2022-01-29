package hu.wup.bt.challenge.clientenroll.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.wup.bt.challenge.clientenroll.service.CheckEnrollmentEligbilityService;
import hu.wup.bt.challenge.restapi.swagger.api.CheckenrollApi;
import hu.wup.bt.challenge.restapi.swagger.model.CheckClientRequest;
import hu.wup.bt.challenge.restapi.swagger.model.CheckClientResponse;

@RestController
@RequestMapping(value = "/client")
@Validated
public class CheckClientController implements CheckenrollApi {

	@Autowired
	private CheckEnrollmentEligbilityService checkService;
	
	@Override
	public ResponseEntity<CheckClientResponse> checkenrollment(CheckClientRequest body) {
		CheckClientResponse checkResult = checkService.checkEligbility(body);
		return new ResponseEntity<CheckClientResponse>(checkResult, HttpStatus.OK);
	}
}
