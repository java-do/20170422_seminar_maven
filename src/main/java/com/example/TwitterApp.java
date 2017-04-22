package com.example;

import twitter4j.TwitterException;

public class TwitterApp {

	public static void main(String[] args) {
		TwitterService twitterService = new TwitterService();
		try {
			twitterService.printTimeline();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

}
