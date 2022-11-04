package com.trainingcamel.util;

import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class CodeGenerator {
	
	DecimalFormat df = new DecimalFormat("00000000");

	public String generateCodeByTime() {
		LocalTime currentTime = LocalTime.now();
		String currentTimeString = currentTime.format(DateTimeFormatter.ofPattern("HHmmssSS"));
		
		return df.format(Integer.parseInt(currentTimeString));
	}
	
	public String generateCodeByRandomInt() {
		Random random = new Random();
		int nextInt = random.nextInt(10000001);
		
		return df.format(nextInt);
	}
}
