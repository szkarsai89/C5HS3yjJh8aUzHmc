package hu.wup.bt.challenge.emulator.existence.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/exist")
public class ExistenceEmulatorController {

	@PostMapping
	public ResponseEntity<Boolean> existence(@RequestParam MultiValueMap<String,String> paramMap) {
		if (paramMap.containsKey("idCard") && paramMap.getFirst("idCard").startsWith("E"))
		{
			return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
		}
		return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK);
	}
}
