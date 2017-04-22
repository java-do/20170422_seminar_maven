# Java Doでしょう #09

ビルドツールハンズオン Apache Maven編

## 練習

### Mavenプロジェクトを作成する

ターミナルやコマンドプロンプトから実行する。

うまくいかないときは、`\（改行）`を削って、1行でいれてみてください

```bash
mvn -B archetype:generate \
-DgroupId=com.example \
-DartifactId=mvn-app \
-Dversion=1.0 \
-DarchetypeArtifactId=maven-archetype-quickstart \
-DarchetypeVersion=1.1
```
### Java8のプロジェクトにする

pom.xmlを編集する。

```xml
<!-- /dependencies と /project の間に貼り付ける -->
<build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.1</version>
        <configuration>
          <source>1.8</source>
          <target>1.8</target>
        </configuration>
      </plugin>
    </plugins>
  </build>
```

### JUnitのライブラリを変更する

pom.xmlを編集する。

```xml
  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.12</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
```

### AppTest.javaを書き換える

```java
package com.example;

import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class AppTest {

	@Test
	public void testApp() {
		assertTrue(true);
	}

}
```

### テストを実行する

```
mvn test
```

## 実践

### mvn-appをパッケージングして、実行する

ターミナルやコマンドプロンプトから実行する。

```
mvn clean package

java -cp target/mvn-app-1.0.jar com.example.App
```

実行結果

```
Hello World!
```

### TwitterService.java を作る

`"****"` の部分は、自分のtwitterアカウントのAPIキーを使ってください。

参考サイト：https://syncer.jp/Web/API/Twitter/REST_API/

```java
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
```

### TwitterApp.java を作る

```java
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
```

### IDEからTwitterApp.javaを実行する

IntelliJ IDEA, Eclipse, NetBeansなどから直接 TwitterApp.java を実行して、Twitterのタイムラインが表示されることを確認してください。

### mvn-appをパッケージングして、再実行する

ターミナルやコマンドプロンプトから実行する。

TwitterApp を実行することに注意してください。

```
mvn clean package

java -cp target/mvn-app-1.0.jar com.example.TwitterApp
```

実行結果

```
Error: A JNI error has occurred, please check your installation and try again
Exception in thread "main" java.lang.NoClassDefFoundError: twitter4j/TwitterException
	at java.lang.Class.getDeclaredMethods0(Native Method)
	at java.lang.Class.privateGetDeclaredMethods(Class.java:2701)
	at java.lang.Class.privateGetMethodRecursive(Class.java:3048)
	at java.lang.Class.getMethod0(Class.java:3018)
	at java.lang.Class.getMethod(Class.java:1784)
	at sun.launcher.LauncherHelper.validateMainClass(LauncherHelper.java:544)
	at sun.launcher.LauncherHelper.checkAndLoadMain(LauncherHelper.java:526)
Caused by: java.lang.ClassNotFoundException: twitter4j.TwitterException
	at java.net.URLClassLoader.findClass(URLClassLoader.java:381)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:335)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
	... 7 more
```

### 実行可能jarを作る

pom.xmlを編集する。

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-assembly-plugin</artifactId>
  <configuration>
    <descriptorRefs>
      <descriptorRef>jar-with-dependencies</descriptorRef>
    </descriptorRefs>
    <archive>
      <manifest>
        <mainClass>com.example.TwitterApp</mainClass>
      </manifest>
    </archive>
  </configuration>
  <executions>
    <execution>
      <id>make-assembly</id>
      <phase>package</phase>
      <goals>
        <goal>single</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

## mvn-appの実行可能jarを動かす

mvn-app-1.0-jar-with-dependencies.jar を実行することに気をつけてください。

```
mvn clean package

java -cp target/mvn-app-1.0-jar-with-dependencies.jar com.example.TwitterApp
```

Twitterのタイムラインが表示されたら成功です！