package com.seneau.tankchlore_service;

import com.seneau.tankflow.TankflowApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TankflowApplication.class)
class TankchloreServiceApplicationTests {

	@Test
	@Disabled("Requires PostgreSQL running locally")
	void contextLoads() {
	}

}
