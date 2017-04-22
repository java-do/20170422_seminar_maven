package com.example;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TwitterService {

	private static final String TL_BROCK_FORMAT = "-----------------------\n%s:%s\n%s";

	private Twitter twitter;

	public TwitterService() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
			.setOAuthConsumerKey("****")
			.setOAuthConsumerSecret("****")
			.setOAuthAccessToken("****")
			.setOAuthAccessTokenSecret("****");
		twitter = new TwitterFactory(cb.build()).getInstance();
	}

	public void printTimeline() throws TwitterException {
		String ldtStr = ZonedDateTime.now().toString();
		System.out.println("Checked at:" + ldtStr);
		twitter.getHomeTimeline().stream()
			.map(this::toTLBlock)
			.forEach(System.out::println);
	}

	private String toTLBlock(Status status) {
		String userName = status.getUser().getName();
		String text = status.getText();
		Instant instant = status.getCreatedAt().toInstant();
		ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
		return String.format(TL_BROCK_FORMAT, userName, text, zdt.toString());
	}
}
