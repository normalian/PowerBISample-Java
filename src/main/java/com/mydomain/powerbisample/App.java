package com.mydomain.powerbisample;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydomain.powerbisample.model.Report;
import com.mydomain.powerbisample.model.ReportList;

/**
 * This is a ample of Power BI Embedded with reference below article
 * https://blogs.msdn.microsoft.com/tsmatsuz/2016/07/20/power-bi-embedded-rest/
 * 
 */
public class App {
	private static String accessToken = "[Your access key]";

	// JavaでHMAC-SHA256を計算する
	// http://kaworu.jpn.org/java/Java%E3%81%A7HMAC-SHA256%E3%82%92%E8%A8%88%E7%AE%97%E3%81%99%E3%82%8B
	//
	// Convert a date format in epoch
	// http://stackoverflow.com/questions/6687433/convert-a-date-format-in-epoch
	//
	// Base64
	// https://gist.github.com/komiya-atsushi/d878e6e4bf9ba6dae8fa
	//
	// Reference for using PATCH requests using jersey
	// http://stackoverflow.com/questions/22355235/patch-request-using-jersey-client

	public static void main(String[] args)
			throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
		SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
		Encoder encoder = Base64.getMimeEncoder(0, new byte[0]);
		{
			String token1 = "{\"typ\":\"JWT\",\"alg\":\"HS256\"}";
			System.out.println("token1 = " + token1);

			String token2 = "{" + //
					"\"wid\":\"31dde159-7445-436b-bb96-f60f106b5982\"," + // workspaceid
					"\"rid\":\"ed051151-3a49-4cf3-9bfc-90cb1b25cda1\"," + // reportid
					"\"wcn\":\"mypowerbicollection01\"," + // workspace
															// collection name
					"\"iss\":\"PowerBISDK\"," + //
					"\"ver\":\"0.2.0\"," + //
					"\"aud\":\"https://analysis.windows.net/powerbi/api\"," + //
					"\"nbf\":" + df.format(new Date()) + ","//
					+ "\"exp\":" + df.format(new DateTime().plusHours(1).toDate()) + "}";//
			System.out.println("token2 = " + token2);

			String inputval = encoder.encodeToString(token1.getBytes("UTF-8")) + "."
					+ encoder.encodeToString(token2.getBytes("UTF-8"));
			inputval = inputval.replace("=", "");

			System.out.println("inputval = " + inputval);

			String algo = "HmacSHA256";
			SecretKeySpec sk = new SecretKeySpec(accessToken.getBytes("UTF-8"), algo);
			Mac mac = Mac.getInstance(algo);
			mac.init(sk);
			byte[] mac_bytes = mac.doFinal(inputval.getBytes("UTF-8"));
			String sig = encoder.encodeToString(mac_bytes);
			System.out.println("sig = " + sig);
			sig = sig.replace("=", "");

			String apptoken = inputval + "." + sig;
			System.out.println("apptoken = " + apptoken);
		}
	}

	public static void __main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		{
			String url = "https://api.powerbi.com/v1.0/collections/mypowerbicollection01/workspaces/31dde159-7445-436b-bb96-f60f106b5982/reports";

			Client client = ClientBuilder.newClient();
			MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
			headers.putSingle("Authorization", "AppKey " + accessToken);
			String src = client.target(url).request().headers(headers).get(String.class);
			ReportList list = new ObjectMapper().readValue(src, ReportList.class);

			for (Report report : list.getReports()) {
				System.out.println(report.getName() + ", " + report.getWebUrl() + ", " + report.getId());
			}
		}
		System.out.println("@@@@@@@@@@@@@@@");
		{
			// You can get workspaces info
			String url = "https://api.powerbi.com/v1.0/collections/mypowerbicollection01/workspaces/";

			Client client = ClientBuilder.newClient();
			MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
			headers.putSingle("Authorization", "AppKey " + accessToken);
			String src = client.target(url).request().headers(headers).get(String.class);
			System.out.println(src);
		}
		System.out.println("@@@@@@@@@@@@@@@");
		{
			// You can get an dataset info
			String url = "https://api.powerbi.com/v1.0/collections/mypowerbicollection01/workspaces/31dde159-7445-436b-bb96-f60f106b5982/imports";

			Client client = ClientBuilder.newClient();
			MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
			headers.putSingle("Authorization", "AppKey " + accessToken);
			String src = client.target(url).request().headers(headers).get(String.class);
			System.out.println(src);
		}
		System.out.println("@@@@@@@@@@@@@@@");
		{
			// You can get an dataset info
			String url = "https://api.powerbi.com/v1.0/collections/mypowerbicollection01/workspaces/31dde159-7445-436b-bb96-f60f106b5982/datasets/c279d59e-b189-4cac-9d62-ea679d276ea4/Default.GetBoundGatewayDatasources";

			Client client = ClientBuilder.newClient();
			MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
			headers.putSingle("Authorization", "AppKey " + accessToken);
			String src = client.target(url).request().headers(headers).get(String.class);
			System.out.println(src);
		}

		System.out.println("Hello World!");
	}
}
