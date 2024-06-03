package com.talan.adminmodule;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@SpringBootTest
@EnableScheduling
class AdminModuleApplicationTests {
	@Autowired
	private  ApplicationContext context;
    @Test
	void contextLoads() {
		assertThat(context).isNotNull();

	}
}
