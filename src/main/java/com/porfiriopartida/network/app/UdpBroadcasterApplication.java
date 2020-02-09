package com.porfiriopartida.network.app;

import com.porfiriopartida.network.ApplicationThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"com.porfiriopartida.network"
})
public class UdpBroadcasterApplication implements CommandLineRunner {
	@Autowired
	private ApplicationThread applicationThread;

	public static void main(String[] args) {
		SpringApplication.run(UdpBroadcasterApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		new Thread(applicationThread).run();
	}
}
