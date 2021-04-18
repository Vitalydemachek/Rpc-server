package com.rpc.services;

import java.time.LocalDate;


public class Service2 {
	private static final Service2 instance = new Service2();


	private Service2() {
	}

	public static Service2 getInstance() {
		return instance;
	}

	public void sleep(Long millis) throws InterruptedException {
		Thread.sleep(millis);
	}

	public String checkServiceName(String serviceName) {
		return "It's Service2: " + "Service2".equals(serviceName);
	}
}
