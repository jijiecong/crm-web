package com.meiren.tech.test;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class TestRest {

	public static void testRestGet() {
		String url = "http://localhost:8888/services/basic/1234.json";
		System.out.println("Getting user via " + url);
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(url);
		Response response = target.request().get();
		try {
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed with HTTP error code : "
						+ response.getStatus());
			}
			System.out.println("Successfully got result: "
					+ response.readEntity(String.class));
		} finally {
			response.close();
			client.close();
		}

	}

	public static void testRestPost() {
		String url = "http://localhost:8888/services/basic/count.json";
		System.out.println("Registering user via " + url);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userName", "William.jiang");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(url);
		Response response = target.request().post(
				Entity.entity(paramMap, MediaType.APPLICATION_JSON_TYPE));

		try {
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed with HTTP error code : "
						+ response.getStatus());
			}
			System.out.println("Successfully got result: "
					+ response.readEntity(String.class));
		} finally {
			response.close();
			client.close();
		}

	}

	public static void testRestMultiPost() {
		String url = "http://localhost:8888/services/basic/count.json";
		System.out.println("Registering user via " + url);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userName", "William.jiang");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(url);
		Response response = target.request().post(
				Entity.entity(paramMap, MediaType.APPLICATION_JSON_TYPE));

		try {
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed with HTTP error code : "
						+ response.getStatus());
			}
			System.out.println("Successfully got result: "
					+ response.readEntity(String.class));
		} finally {
			response.close();
			client.close();
		}

	}

	public static void main(String[] args) {
		testRestPost();
	}
}
