package org.lucidTalents;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jdk8.*;
import discord4j.common.jackson.*;
import discord4j.core.*;
import discord4j.core.event.domain.lifecycle.*;
import discord4j.core.event.domain.message.*;
import discord4j.core.object.entity.*;
import discord4j.core.object.util.Snowflake;
import discord4j.rest.RestClient;
import discord4j.rest.http.*;
import discord4j.rest.http.client.DiscordWebClient;
import discord4j.rest.request.*;
import discord4j.rest.json.response.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.*;
import java.net.*;
import java.util.Objects;

public class ReaperBot{
    String token = getToken("src/main/resources/token.json");
    DiscordClient client;

    public ReaperBot(){
        // login, wait for !bump, send http request out, send information to discord via bot.
        // Imagine the bot is on discord, I am running this server, and the website is out there.
        this.token = token;
        this.client = this.login(this.token);
        //this.sendMessage("!d bump");
        //pingPong(this.client);
        //discordHttpRequest(this.client);
        // postmanHttpRequest();
    }


    protected void postmanHttpRequest(){
        // set connection and GET and then post. After this is completed, I should clean up my code a bit.
        // POSTMAN sets up a mock endpoint. Can simulate examples/example commands.
        // Add http requets get/post as an http interface to this bot.
        try {
            URL url = new URL("https://77d3ba3e-f9e2-4558-bff6-01656d90ea4a.mock.pstmn.io/test");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            System.out.println(status);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            StringBuffer content = new StringBuffer();

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
                System.out.println(inputLine);
            }

            in.close();
            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Finished GETTING");

        // Do Post.

    }
    /**
     * @param client
     */
    protected void discordHttpRequest(DiscordClient client){

        // Maps an object to JSON and vice versa
        ObjectMapper mapper = new ObjectMapper()
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .addHandler(new UnknownPropertyHandler(true))
                .registerModules(new PossibleModule(), new Jdk8Module());

        DiscordWebClient webClient = new DiscordWebClient(HttpClient.create().compress(true),
                ExchangeStrategies.jackson(mapper), this.token);

        RouterOptions rOptions = RouterOptions.create();
        Router router = new DefaultRouter(webClient, rOptions);

        RestClient restClient = new RestClient(router);

        restClient.getApplicationService().getCurrentApplicationInfo()
                .map(ApplicationInfoResponse::getName)
                .subscribe(name -> System.out.println("My name is " + name));
    }

    /*
    Currently overloaded with normal discordJ4 block.
     */
    protected DiscordClient login(String token){
        DiscordClientBuilder cBuilder = new DiscordClientBuilder(this.token);
        DiscordClient client = cBuilder.build();

        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> {
                    User self = event.getSelf();
                    System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
                });

        return client;
    }

    protected void pingPong(DiscordClient client){
        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().orElse("").equalsIgnoreCase("!ping"))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Pong!"))
                .subscribe(event -> {
                    System.out.println("Message Sent!");
                });
    }

    /**
     * send message to trigger another bot around the clock. Using class method instead of passing itself like other classes.
     * To clean up: make channelID secret and a parameter for better visibility to what is going on.
     */
    protected void sendMessage(String msg){
        //String channelID = "593282979845898240";
        String bumpID = "686401320231370838";
        //TextChannel channel = (TextChannel) this.client.getChannelById(Snowflake.of(channelID)).block();
        TextChannel channel = (TextChannel) this.client.getChannelById(Snowflake.of(bumpID)).block();
        channel.createMessage(msg).block();

    //subscribe(value -> System.out.println(value));
    }

    private void setToken(){
        this.token = getToken("src/main/resources/token.json");
    }

    private static String getToken(String fileName) {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(fileName)) {
            //Read JSON file
            JSONObject obj = (JSONObject) jsonParser.parse(reader);
            String value = (String) obj.get("reaperToken");
            return value;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
