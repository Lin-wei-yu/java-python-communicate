package flappybird.environment;

import org.json.JSONException;
import org.json.JSONObject;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
//public class RabbitServer implements AutoCloseable{
//
//	private Channel channel;
//	private Connection connection;
//	
//	String exchange = "";
//	String routingKey = "rpc_queue";
//	public RabbitServer() throws IOException, TimeoutException{		
//		
//		ConnectionFactory factory = new ConnectionFactory();
//		factory.setHost("localhost");
//		connection = factory.newConnection();
//		channel = connection.createChannel();
//		
//	}
//	public void on_response() {
//		
//	}
////    public static void main(String[] argv) throws InterruptedException {
////        try (RabbitServer fibonacciRpc = new RabbitServer()) {
////            for (int i = 0; i < 32; i++) {
////                String i_str = Integer.toString(i);
////                System.out.println(" [x] Requesting fib(" + i_str + ")");
////                String response = fibonacciRpc.call(i_str);
////                System.out.println(" [.] Got '" + response + "'");
////            }
////        } catch (IOException | TimeoutException e) {
////            e.printStackTrace();
////        }
////    }
//	
//	
//    public int sendEnvInfo(State currentstate) throws IOException, InterruptedException {
//    	String jsonString = currentstate.jsonString();
//    	
//    	call(jsonString);
//		return 0;
//    }
//    
//	public String call(String message) throws IOException, InterruptedException {
//        final String corrId = UUID.randomUUID().toString();
//        String replyQueueName = channel.queueDeclare().getQueue();
//        
//        AMQP.BasicProperties props = new AMQP.BasicProperties
//                .Builder()
//                .correlationId(corrId)
//                .replyTo(replyQueueName)
//                .build();
//        
//        
//		channel.basicPublish(exchange, routingKey, props, message.getBytes("UTF-8"));
//		
//		final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
//		
//        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
//            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
//                response.offer(new String(delivery.getBody(), "UTF-8"));
//            }
//        }, consumerTag -> {
//        	
//        });
//
//        String result = response.take();
//        channel.basicCancel(ctag);
//        return result;
//		
//		
//	}
//    public void close() throws IOException {
//        connection.close();
//    }
//	
//}
public class RabbitServer {

    private static final String RPC_QUEUE_NAME = "flappy_bird";

	static Environment env = new Environment();
	static String response;
    
    public static void parseMessage(String jsonString) throws JSONException {
		JSONObject jsonobj = new JSONObject(jsonString);
    	String function = jsonobj.getString("function");

    	if(function.equals("game_over")) {
			response = String.format("{\"game_over\":%b}",env.gameOver() );

    		
    	}else if(function.equals("reset_game") ) {
    		env.init();
    		response = String.format("{\"reset\":%b}",true);
//    		response = env.getGameState().jsonString();

    		
    	}else if(function.equals("act")) {
    		
    		int action_idx = jsonobj.getInt("action");
    		
    		double reward;
    		String action = "";
    		
			if(action_idx == 0 ) {
				action = "flap";
				reward = env.act(Action.FLAP);
			}else {
				action = "no action";
				reward = env.act(Action.NONE);
			}
			
			response = String.format("{\"reward\":%.2f}",reward );
			
    	}else if(function.equals("getGameState")) {
    		response = env.getGameState().jsonString();
    		
    	}else {
    		System.out.println("This server dont have this function!");
    	}
    }
    
    
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(RPC_QUEUE_NAME);

            channel.basicQos(1);

            System.out.println(" [x] Awaiting RPC requests");

            Object monitor = new Object();
            
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                //String response = "";

                try {
                    String message = new String(delivery.getBody());
                    System.out.println(" [o] Get a  RPC requests: " + message);
                    
                    try {
						parseMessage(message);
					} catch (JSONException e) {
						e.printStackTrace();
					}
                
                    

                    //System.out.println(" [.] fib(" + message + ")");
                    //response ="ans";
                    
                    
                    
                    
                    
                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                	System.out.println(" [o] Sent the response: " + response);
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };

            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
            // Wait and be prepared to consume the message from RPC client.
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}