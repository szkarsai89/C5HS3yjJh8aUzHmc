package hu.wup.bt.challenge.clientenroll.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import hu.wup.bt.challenge.clientenroll.external.clients.CheckCustomerExistClient;
import hu.wup.bt.challenge.clientenroll.external.clients.RiskCalculatorClient;
import hu.wup.bt.challenge.clientenroll.service.impl.CheckEnrollmentEligbilityServiceImpl;
import hu.wup.bt.challenge.restapi.swagger.model.CheckClientRequest;
import hu.wup.bt.challenge.restapi.swagger.model.CheckClientResponse;
import hu.wup.bt.challenge.restapi.swagger.model.IdCard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = { "validation.client.idcard.pattern=20" })
public class CheckEnrollmentEligbilityServiceTest {

	    @InjectMocks
	    private CheckEnrollmentEligbilityServiceImpl service;

	    @Mock
	    private CheckCustomerExistClient customerExistClient;
	    
	    @Mock
	    private RiskCalculatorClient riskCalculatorClient;
	    
	    @Test
	    public void customerIsYoungerThan16() throws Exception
	    {
	    	//ReflectionTestUtils.setField(service, "idCardPatternString", "valami");
	    	CheckClientRequest request = new CheckClientRequest();
			request.setBirthDate(LocalDate.now().minusYears(10));
			request.setFirstName("Szabolcs");
			request.setLastName("Karsai");
			request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusYears(2)));
			
	    	CheckClientResponse result = service.checkEligbility(request);
	    	assertThat(result.getError()).isNotEmpty();
	    	assertThat(result.getError().size()).isEqualTo(1);
	    	assertThat(result.getError().get(0).getCode()).isEqualTo("NotEnoughOld");
	    	assertThat(result.getEnrollPossible()).isFalse();
	    }
	    
	    @Test
	    public void customerIsBetween16and18IdCardCheckSwitchedOff() throws Exception
	    {
	    	ReflectionTestUtils.setField(service, "idCardPatternString", ".*");
	    	CheckClientRequest request = new CheckClientRequest();
			request.setBirthDate(LocalDate.now().minusYears(17));
			request.setFirstName("Szabolcs");
			request.setLastName("Karsai");
			request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusYears(2)));
			
	    	CheckClientResponse result = service.checkEligbility(request);
	    	assertThat(result.getError()).isNullOrEmpty();
	    	assertThat(result.getWarn()).isNotEmpty();
	    	assertThat(result.getWarn().size()).isEqualTo(1);
	    	assertThat(result.getWarn().get(0).getCode()).isEqualTo("ParentAffirmation");
	    	assertThat(result.getEnrollPossible()).isTrue();
	    }
	    
	    @Test
	    public void customerIsOlderThan18IdCardExpiryIn6Months() throws Exception
	    {
	    	ReflectionTestUtils.setField(service, "idCardPatternString", ".*");
	    	CheckClientRequest request = new CheckClientRequest();
			request.setBirthDate(LocalDate.now().minusYears(20));
			request.setFirstName("Szabolcs");
			request.setLastName("Karsai");
			request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusMonths(2)));
			
	    	CheckClientResponse result = service.checkEligbility(request);
	    	assertThat(result.getError()).isNullOrEmpty();
	    	assertThat(result.getWarn()).isNotEmpty();
	    	assertThat(result.getWarn().size()).isEqualTo(1);
	    	assertThat(result.getWarn().get(0).getCode()).isEqualTo("IdCardExpiryClose");
	    	assertThat(result.getEnrollPossible()).isTrue();
	    }
	    
	    @Test
	    public void customerIsOlderThan18IdCardExpired() throws Exception
	    {
	    	ReflectionTestUtils.setField(service, "idCardPatternString", ".*");
	    	CheckClientRequest request = new CheckClientRequest();
			request.setBirthDate(LocalDate.now().minusYears(20));
			request.setFirstName("Szabolcs");
			request.setLastName("Karsai");
			request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().minusMonths(2)));
			
	    	CheckClientResponse result = service.checkEligbility(request);
	    	assertThat(result.getError()).isNullOrEmpty();
	    	assertThat(result.getWarn()).isNotEmpty();
	    	assertThat(result.getWarn().size()).isEqualTo(1);
	    	assertThat(result.getWarn().get(0).getCode()).isEqualTo("IdCardExpired");
	    	assertThat(result.getEnrollPossible()).isTrue();
	    }
	    
	    @Test
	    public void customerIsOlderThan18IdCardExpiredInvalidIdNumber() throws Exception
	    {
	    	ReflectionTestUtils.setField(service, "idCardPatternString", "^[0-9]*$");
	    	CheckClientRequest request = new CheckClientRequest();
			request.setBirthDate(LocalDate.now().minusYears(20));
			request.setFirstName("Szabolcs");
			request.setLastName("Karsai");
			request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusYears(2)));
			
	    	CheckClientResponse result = service.checkEligbility(request);
	    	assertThat(result.getError()).isNotEmpty();
	    	assertThat(result.getError().size()).isEqualTo(1);
	    	assertThat(result.getError().get(0).getCode()).isEqualTo("IdCardNumberInvalidFormat");
	    	assertThat(result.getEnrollPossible()).isFalse();
	    }
	    
	    @Test
	    public void customerIsOlderThan18IdCardValidCustomerExist() throws Exception
	    {
	    	ReflectionTestUtils.setField(service, "idCardPatternString", ".*");
	    	CheckClientRequest request = new CheckClientRequest();
			request.setBirthDate(LocalDate.now().minusYears(20));
			request.setFirstName("Szabolcs");
			request.setLastName("Karsai");
			request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusYears(2)));
			
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("idCard", request.getIdCardDetails().getNumber());
			map.add("firstName", request.getFirstName());
			map.add("lastName", request.getLastName());
			map.add("birthDate", request.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			
			when(customerExistClient.checkCustomerExistence(map)).thenReturn(Boolean.TRUE);
			
	    	CheckClientResponse result = service.checkEligbility(request);
	    	assertThat(result.getError()).isNotEmpty();
	    	assertThat(result.getError().size()).isEqualTo(1);
	    	assertThat(result.getError().get(0).getCode()).isEqualTo("CustomerAlreadyExist");
	    	assertThat(result.getEnrollPossible()).isFalse();
	    }
	    
	    @Test
	    public void customerIsOlderThan18IdCardValidCustomerNotExistLowRisk() throws Exception
	    {
	    	ReflectionTestUtils.setField(service, "idCardPatternString", ".*");
	    	CheckClientRequest request = new CheckClientRequest();
			request.setBirthDate(LocalDate.now().minusYears(20));
			request.setFirstName("Szabolcs");
			request.setLastName("Karsai");
			request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusYears(2)));
			
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("idCard", request.getIdCardDetails().getNumber());
			map.add("firstName", request.getFirstName());
			map.add("lastName", request.getLastName());
			map.add("birthDate", request.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			
			when(customerExistClient.checkCustomerExistence(map)).thenReturn(Boolean.FALSE);
			when(riskCalculatorClient.calculateRisk(map)).thenReturn(10);
			
	    	CheckClientResponse result = service.checkEligbility(request);
	    	assertThat(result.getError()).isNullOrEmpty();
	    	assertThat(result.getWarn()).isNullOrEmpty();
	    	assertThat(result.getInfo()).isNotEmpty();
	    	assertThat(result.getInfo().get(0).getCode()).isEqualTo("EnrollmentPossible");
	    	assertThat(result.getEnrollPossible()).isTrue();
	    }
	    
	    @Test
	    public void customerIsOlderThan18IdCardValidCustomerNotExistMidRisk() throws Exception
	    {
	    	ReflectionTestUtils.setField(service, "idCardPatternString", ".*");
	    	CheckClientRequest request = new CheckClientRequest();
			request.setBirthDate(LocalDate.now().minusYears(20));
			request.setFirstName("Szabolcs");
			request.setLastName("Karsai");
			request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusYears(2)));
			
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("idCard", request.getIdCardDetails().getNumber());
			map.add("firstName", request.getFirstName());
			map.add("lastName", request.getLastName());
			map.add("birthDate", request.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			
			when(customerExistClient.checkCustomerExistence(map)).thenReturn(Boolean.FALSE);
			when(riskCalculatorClient.calculateRisk(map)).thenReturn(50);
			
	    	CheckClientResponse result = service.checkEligbility(request);
	    	assertThat(result.getError()).isNullOrEmpty();
	    	assertThat(result.getWarn()).isNotEmpty();
	    	assertThat(result.getWarn().get(0).getCode()).isEqualTo("CustomerMidRisk");
	    	assertThat(result.getInfo()).isNotEmpty();
	    	assertThat(result.getInfo().get(0).getCode()).isEqualTo("EnrollmentPossible");
	    	assertThat(result.getEnrollPossible()).isTrue();
	    }
	    
	    @Test
	    public void customerIsOlderThan18IdCardValidCustomerNotExistHighRisk() throws Exception
	    {
	    	ReflectionTestUtils.setField(service, "idCardPatternString", ".*");
	    	CheckClientRequest request = new CheckClientRequest();
			request.setBirthDate(LocalDate.now().minusYears(20));
			request.setFirstName("Szabolcs");
			request.setLastName("Karsai");
			request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusYears(2)));
			
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("idCard", request.getIdCardDetails().getNumber());
			map.add("firstName", request.getFirstName());
			map.add("lastName", request.getLastName());
			map.add("birthDate", request.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			
			when(customerExistClient.checkCustomerExistence(map)).thenReturn(Boolean.FALSE);
			when(riskCalculatorClient.calculateRisk(map)).thenReturn(120);
			
	    	CheckClientResponse result = service.checkEligbility(request);
	    	assertThat(result.getInfo()).isNullOrEmpty();
	    	assertThat(result.getWarn()).isNullOrEmpty();
	    	assertThat(result.getError()).isNotEmpty();
	    	assertThat(result.getError().get(0).getCode()).isEqualTo("CustomerHighRisk");
	    	assertThat(result.getEnrollPossible()).isFalse();
	    }
}
