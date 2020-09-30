package flappybird.environment;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Run {
	public static void main(String[] args) throws IOException, TimeoutException {
		Environment env = new Environment();
		Double cumulativeReward = 0.0;
		
		//RabbitServer rabbitServer = new RabbitServer();

		int step = 1;
		String action;

		while (!env.gameOver()) {
			System.out.println(String.format("----------step %d----------", step));
			
			// print current state (For RL)
			System.out.println("game state(RL):");
			
			
			
			State currentState = env.getGameState();
			System.out.println(currentState);

			// print current state (For human readable)
			// don't train with this information
			System.out.println("--------------------");
			System.out.println("bird and pillars information(human):");
			env.printState();
			
			
			
			int action_idx = 0;
			action_idx = 1;//rabbitServer.sendEnvInfo(currentState);
			
			
			
			if(action_idx == 0 ) {
				action = "flap";
				cumulativeReward += env.act(Action.FLAP);
			}else {
				action = "no action";
				cumulativeReward += env.act(Action.NONE);
			}
			
//			if (Environment.r.nextDouble() < 0.15) {
//				action = "flap";
//				cumulativeReward += env.act(Action.FLAP);
//			} else {
//				action = "no action";
//				cumulativeReward += env.act(Action.NONE);
//			}
			
			
			
			System.out.println("--------------------");
			System.out.println(String.format("your action performed in this step: %s", action));

			System.out.println("--------------------");
			System.out.println("Cumulative Reward: " + cumulativeReward);
			System.out.println("--------------------\n");
			step += 1;
		}
	}

}
