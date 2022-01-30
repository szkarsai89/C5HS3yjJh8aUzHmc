package hu.wup.bt.challenge.emulator.riskcalculator.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/risk")
public class RiskCalculatorEmulatorController {

	@PostMapping
	public ResponseEntity<Integer> existence(@RequestParam MultiValueMap<String,String> paramMap) {
		if (paramMap.containsKey("idCard"))
		{
			try {
				String paramValue = paramMap.getFirst("idCard");
				int risk = Integer.parseInt(paramValue.substring(paramValue.length()-2));
				return new ResponseEntity<Integer>(risk, HttpStatus.OK);
			} catch (Exception ex)
			{
				
			}
		}
		return new ResponseEntity<Integer>(99, HttpStatus.OK);
	}
}
