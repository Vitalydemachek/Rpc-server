package com.rpc.services;

import java.time.LocalDate;


public class Service1 {
	private static final Service1 instance = new Service1();


	private Service1() {
	}

	public static Service1 getInstance() {
		return instance;
	}

	public void sleep(Long millis) throws InterruptedException {
		Thread.sleep(millis);
	}

	public LocalDate getCurrentDate() {
		return LocalDate.now();
	}
}
