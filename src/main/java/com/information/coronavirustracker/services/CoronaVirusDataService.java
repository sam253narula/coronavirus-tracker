package com.information.coronavirustracker.services;

import java.io.IOException;
import java.io.StringReader;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.information.coronavirustracker.models.LocationStats;

@Service
public class CoronaVirusDataService {

	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	private List<LocationStats> allStats = new ArrayList<>();
	private List<LocationStats> indianStats = new ArrayList<>();

	public List<LocationStats> getAllStats() {
		return allStats;
	}

	public List<LocationStats> getIndianStats() {
		return indianStats;
	}

	// HTTP GET request for world
	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchVirusData() throws IOException, InterruptedException {
		// URL call using RestTemplate Below
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		ResponseEntity<String> response = restTemplate.exchange(VIRUS_DATA_URL, HttpMethod.GET, entity, String.class);
		List<LocationStats> newStats = new ArrayList<>();
		StringReader csvBodyReader = new StringReader(response.toString());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

		for (CSVRecord record : records) {
			if (record.getRecordNumber() < 248) {
				LocationStats locationStat = new LocationStats();
				locationStat.setState(record.get("Province/State"));
				locationStat.setCountry(record.get("Country/Region"));
				int latestCases = Integer.parseInt(record.get(record.size() - 1));
				int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
				locationStat.setLatestTotalCases(latestCases);
				locationStat.setDiffFromPrevDay(latestCases - prevDayCases);
				newStats.add(locationStat);
			}
		}
		this.allStats = newStats;
	}

	// HTTP GET request for india
	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchIndianVirusData() throws IOException, InterruptedException {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		ResponseEntity<String> response = restTemplate.exchange(VIRUS_DATA_URL, HttpMethod.GET, entity, String.class);
		List<LocationStats> newIndiaStats = new ArrayList<>();
		StringReader csvBodyReader = new StringReader(response.toString().trim());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
//		int counter = 0;
//		for (CSVRecord record : records) {
//			counter++;
//		}
		for (CSVRecord record : records) {
			if (record.getRecordNumber() == 132) {
				LocationStats indianLocationStat = new LocationStats();
				indianLocationStat.setState(record.get("Province/State"));
				indianLocationStat.setCountry(record.get("Country/Region"));
				int latestCases = Integer.parseInt(record.get(record.size() - 1));
				int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
				indianLocationStat.setLatestTotalCases(latestCases);
				indianLocationStat.setDiffFromPrevDay(latestCases - prevDayCases);
				newIndiaStats.add(indianLocationStat);
			}
		}
		this.indianStats = newIndiaStats;
	}

}