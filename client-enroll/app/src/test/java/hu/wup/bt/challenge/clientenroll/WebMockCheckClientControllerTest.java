package hu.wup.bt.challenge.clientenroll;

import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hu.wup.bt.challenge.clientenroll.controller.CheckClientController;
import hu.wup.bt.challenge.clientenroll.service.CheckEnrollmentEligbilityService;
import hu.wup.bt.challenge.restapi.swagger.model.CheckClientRequest;
import hu.wup.bt.challenge.restapi.swagger.model.CheckClientResponse;
import hu.wup.bt.challenge.restapi.swagger.model.IdCard;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CheckClientController.class)
public class WebMockCheckClientControllerTest {
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CheckEnrollmentEligbilityService service;
	
	@Test
	public void validInputGenerateOK() throws Exception {
		CheckClientRequest request = new CheckClientRequest();
		request.setBirthDate(LocalDate.now().minusYears(30));
		request.setFirstName("Szabolcs");
		request.setLastName("Karsai");
		request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusYears(2)));
		
		CheckClientResponse response = new CheckClientResponse();
		response.setRiskLevel(12);
		response.setEnrollPossible(Boolean.TRUE);
		
		when(service.checkEligbility(request)).thenReturn(response);
		
		this.mockMvc.perform(post("/client/checkenroll").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andDo(print()).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.enrollPossible").value(true));
	}
	
	@Test
	public void checkRequiredFieldFirstName() throws Exception {
		CheckClientRequest request = new CheckClientRequest();
		request.setBirthDate(LocalDate.now().minusYears(30));
		//request.setFirstName("Szabolcs");
		request.setLastName("Karsai");
		request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusYears(2)));
		
		this.mockMvc.perform(post("/client/checkenroll").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.enrollPossible").doesNotExist())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(Matchers.hasSize(1)))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].code").value("NotNull"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].fieldId").value("firstName"));
	}
	
	@Test
	public void checkRequiredFieldLastName() throws Exception {
		CheckClientRequest request = new CheckClientRequest();
		request.setBirthDate(LocalDate.now().minusYears(30));
		request.setFirstName("Szabolcs");
		//request.setLastName("Karsai");
		request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusYears(2)));
		
		this.mockMvc.perform(post("/client/checkenroll").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.enrollPossible").doesNotExist())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(Matchers.hasSize(1)))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].code").value("NotNull"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].fieldId").value("lastName"));
	}
	
	@Test
	public void checkRequiredBirthDate() throws Exception {
		CheckClientRequest request = new CheckClientRequest();
		//request.setBirthDate(LocalDate.now().minusYears(30));
		request.setFirstName("Szabolcs");
		request.setLastName("Karsai");
		request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusYears(2)));
		
		this.mockMvc.perform(post("/client/checkenroll").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.enrollPossible").doesNotExist())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(Matchers.hasSize(1)))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].code").value("NotNull"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].fieldId").value("birthDate"));
	}
	
	@Test
	public void checkRequiredIdCardNumber() throws Exception {
		CheckClientRequest request = new CheckClientRequest();
		request.setBirthDate(LocalDate.now().minusYears(30));
		request.setFirstName("Szabolcs");
		request.setLastName("Karsai");
		request.setIdCardDetails(new IdCard().expiry(LocalDate.now().plusYears(2)));
		
		this.mockMvc.perform(post("/client/checkenroll").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.enrollPossible").doesNotExist())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(Matchers.hasSize(1)))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].code").value("NotNull"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].fieldId").value("idCardDetails.number"));
	}
	
	@Test
	public void checkRequiredIdCardExpiry() throws Exception {
		CheckClientRequest request = new CheckClientRequest();
		request.setBirthDate(LocalDate.now().minusYears(30));
		request.setFirstName("Szabolcs");
		request.setLastName("Karsai");
		request.setIdCardDetails(new IdCard().number("12345678AB"));
		
		this.mockMvc.perform(post("/client/checkenroll").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.enrollPossible").doesNotExist())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(Matchers.hasSize(1)))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].code").value("NotNull"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].fieldId").value("idCardDetails.expiry"));
	}
	
	@Test
	public void checkRequiredIdCardDetails() throws Exception {
		CheckClientRequest request = new CheckClientRequest();
		request.setBirthDate(LocalDate.now().minusYears(30));
		request.setFirstName("Szabolcs");
		request.setLastName("Karsai");
		//request.setIdCardDetails(new IdCard().number("12345678AB"));
		
		this.mockMvc.perform(post("/client/checkenroll").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.enrollPossible").doesNotExist())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(Matchers.hasSize(1)))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].code").value("NotNull"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].fieldId").value("idCardDetails"));
	}
	
	@Test
	public void checkFirstNameTooShort() throws Exception {
		CheckClientRequest request = new CheckClientRequest();
		request.setBirthDate(LocalDate.now().minusYears(30));
		request.setFirstName("S");
		request.setLastName("Karsai");
		request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusYears(2)));
		
		this.mockMvc.perform(post("/client/checkenroll").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.enrollPossible").doesNotExist())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(Matchers.hasSize(1)))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].code").value("Size"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].fieldId").value("firstName"));
	}
	
	@Test
	public void checkFirstNameTooLong() throws Exception {
		CheckClientRequest request = new CheckClientRequest();
		request.setBirthDate(LocalDate.now().minusYears(30));
		request.setFirstName("Ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
		request.setLastName("Karsai");
		request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusYears(2)));
		
		this.mockMvc.perform(post("/client/checkenroll").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.enrollPossible").doesNotExist())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(Matchers.hasSize(1)))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].code").value("Size"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].fieldId").value("firstName"));
	}
	
	@Test
	public void checkLastNameTooShort() throws Exception {
		CheckClientRequest request = new CheckClientRequest();
		request.setBirthDate(LocalDate.now().minusYears(30));
		request.setFirstName("Szabolcs");
		request.setLastName("K");
		request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusYears(2)));
		
		this.mockMvc.perform(post("/client/checkenroll").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.enrollPossible").doesNotExist())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(Matchers.hasSize(1)))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].code").value("Size"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].fieldId").value("lastName"));
	}
	
	@Test
	public void checkLastNameTooLong() throws Exception {
		CheckClientRequest request = new CheckClientRequest();
		request.setBirthDate(LocalDate.now().minusYears(30));
		request.setFirstName("Szabolcs");
		request.setLastName("Kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
		request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusYears(2)));
		
		this.mockMvc.perform(post("/client/checkenroll").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.enrollPossible").doesNotExist())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(Matchers.hasSize(1)))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].code").value("Size"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].fieldId").value("lastName"));
	}
	
	@Test
	public void checkIdCardNumberTooShort() throws Exception {
		CheckClientRequest request = new CheckClientRequest();
		request.setBirthDate(LocalDate.now().minusYears(30));
		request.setFirstName("Szabolcs");
		request.setLastName("Karsai");
		request.setIdCardDetails(new IdCard().number("1").expiry(LocalDate.now().plusYears(2)));
		
		this.mockMvc.perform(post("/client/checkenroll").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.enrollPossible").doesNotExist())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(Matchers.hasSize(1)))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].code").value("Size"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].fieldId").value("idCardDetails.number"));
	}
	
	@Test
	public void checkIdCardNumberTooLong() throws Exception {
		CheckClientRequest request = new CheckClientRequest();
		request.setBirthDate(LocalDate.now().minusYears(30));
		request.setFirstName("Szabolcs");
		request.setLastName("Karsai");
		request.setIdCardDetails(new IdCard().number("1kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk").expiry(LocalDate.now().plusYears(2)));
		
		this.mockMvc.perform(post("/client/checkenroll").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.enrollPossible").doesNotExist())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(Matchers.hasSize(1)))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].code").value("Size"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].fieldId").value("idCardDetails.number"));
	}
	
	@Test
	public void checkBirthDateInvalidFormat() throws Exception {
		CheckClientRequest request = new CheckClientRequest();
		request.setBirthDate(LocalDate.now().minusYears(30));
		request.setFirstName("Szabolcs");
		request.setLastName("Karsai");
		request.setIdCardDetails(new IdCard().number("12345678AB").expiry(LocalDate.now().plusYears(2)));
		
		String bodyContent = objectMapper.writeValueAsString(request);
		bodyContent = bodyContent.replace("1992-01-28", "INVALID");
		
		this.mockMvc.perform(post("/client/checkenroll").contentType(MediaType.APPLICATION_JSON).content(bodyContent)).andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.enrollPossible").doesNotExist())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(Matchers.hasSize(1)))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].code").value("InvalidFormat"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.error[0].fieldId").value("birthDate"));
	}
}
