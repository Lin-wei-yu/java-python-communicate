import numpy as np

import pika
import uuid
import json

class RpcClient:
    def __init__(self):
        self.connection = pika.BlockingConnection(
            pika.ConnectionParameters('localhost'))
        self.channel = self.connection.channel()

        result = self.channel.queue_declare('flappy_bird')
        self.callback_queue = result.method.queue

        self.channel.basic_consume(
            queue = self.callback_queue,
            on_message_callback = self.on_response,
            auto_ack = True
        )


    def on_response(self, channel, method, properties, body):
        #if self.corrId == properties.correlationId:
        self.response = json.loads(body)

        print("[o] rpc client get response ", self.response) 

    def call(self, jstring):
        
        self.response = None
        #self.corrId = uuid

        self.channel.basic_publish(
            exchange='', 
            routing_key='flappy_bird',
            properties = pika.BasicProperties(
                #correlation_id = self.corrId,
                reply_to = self.callback_queue
            ),
            body = jstring
        )
        print("success")
        while self.response == None :
            # keep waiting till obtain a response.
            self.connection.process_data_events()
        return self.response

    def reset_game(self):
        jstring = '{"function":"reset_game"}'
        self.call(jstring)
        return
    def act(self, action):
        jstring = '{"function":"act", "action":"'+ str(action) +'"}'
        response = self.call(jstring)
        return response['reward']
    def game_over(self):
        jstring = '{"function":"game_over"}'
        response = self.call(jstring)
        return response['game_over']
    def getGameState(self):
        jstring = '{"function":"getGameState"}'
        response = self.call(jstring)
        return response




env = RpcClient()



import math
import copy
from collections import defaultdict
MIN_EXPLORING_RATE = 0.01
MIN_LEARNING_RATE = 0.5
class Agent:

    def __init__(self,
                 bucket_range_per_feature,
                 num_action,
                 t=0,
                 discount_factor=0.99):
        self.update_parameters(t)  # init explore rate and learning rate
        self.q_table = defaultdict(lambda: np.zeros(num_action))
        self.discount_factor = discount_factor
        self.num_action = num_action

        # how to discretize each feature in a state
        # the higher each value, less time to train but with worser performance
        # e.g. if range = 2, feature with value 1 is equal to feature with value 0 bacause int(1/2) = int(0/2)
        self.bucket_range_per_feature = bucket_range_per_feature

    def select_action(self, state):
        # epsilon-greedy
        state_idx = self.get_state_idx(state)
        if np.random.rand() < self.exploring_rate:
            action = np.random.choice(num_action)  # Select a random action
        else:
            action = np.argmax(
                self.q_table[state_idx])  # Select the action with the highest q
        return action

    def update_policy(self, state, action, reward, state_prime, action_prime):
        state_idx = self.get_state_idx(state)
        state_prime_idx = self.get_state_idx(state_prime)
        action_prime_idx = action_prime
        
        # Update Q_value using Q-learning update rule
#         best_q = np.max(self.q_table[state_prime_idx])
#         self.q_table[state_idx][action] += self.learning_rate * (
#             reward + self.discount_factor * best_q - self.q_table[state_idx][action])
        
        # Update Q_value using SARSA update rule 
        
        
        predict_q = self.q_table[state_prime_idx][action_prime_idx]
        self.q_table[state_idx][action] += self.learning_rate * (
            reward + self.discount_factor * predict_q - self.q_table[state_idx][action])
        

    def get_state_idx(self, state):

        # instead of using absolute position of pipe, use relative position
        state = copy.deepcopy(state)
        state['next_next_pipe_bottom_y'] -= state['player_y']
        state['next_next_pipe_top_y'] -= state['player_y']
        state['next_pipe_bottom_y'] -= state['player_y']
        state['next_pipe_top_y'] -= state['player_y']

        # sort to make list converted from dict ordered in alphabet order
        state_key = [k for k, v in sorted(state.items())]

        # do bucketing to decrease state space to speed up training
        state_idx = []
        for key in state_key:
            state_idx.append(
                int(state[key] / self.bucket_range_per_feature[key]))
        return tuple(state_idx)

    def update_parameters(self, episode):
        self.exploring_rate = max(MIN_EXPLORING_RATE,
                                  min(0.5, 0.99**((episode) / 30)))
        self.learning_rate = max(MIN_LEARNING_RATE, min(0.5, 0.99
                                                        ** ((episode) / 30)))

    def shutdown_explore(self):
        # make action selection greedy
        self.exploring_rate = 0




num_action = 2 # flap & noaction

bucket_range_per_feature = {
  'next_next_pipe_bottom_y': 40,
  'next_next_pipe_dist_to_player': 512,
  'next_next_pipe_top_y': 40,
  'next_pipe_bottom_y': 20,
  'next_pipe_dist_to_player': 20,
  'next_pipe_top_y': 20,
  'player_vel': 4,
  'player_y': 16
}
# init agent
agent = Agent(bucket_range_per_feature, num_action)


# traning process
reward_per_epoch = []
lifetime_per_epoch = []
exploring_rates = []
learning_rates = []
print_every_episode = 500
show_gif_every_episode = 5000
NUM_EPISODE = 40000
for episode in range(0, NUM_EPISODE):

    # Reset the environment
    env.reset_game()

    # record frame
    # frames = [env.getScreenRGB()]

    # for every 500 episodes, shutdown exploration to see performance of greedy action
    if episode % print_every_episode == 0:
        agent.shutdown_explore()

    # the initial state
    # state = game.getGameState()
    state = env.getGameState()

    # get initial action add by me
    action = agent.select_action(state)
    
    # cumulate reward for this episode
    cum_reward = 0  
    t = 0

    while not env.game_over():

        # select an action
        action = agent.select_action(state) 

        # execute the action and get reward
        # reward = +1 when pass a pipe, -5 when die
#       reward = env.act(env.getActionSet()[action])  
        reward = env.act(action)  


#       frames.append(env.getScreenRGB())

        # cumulate reward
        cum_reward += reward

        # observe the result
        state_prime = env.getGameState()  # get next state
        
        # calculate action prime
        action_prime = agent.select_action(state_prime)
        
        # update agent
        agent.update_policy(state, action, reward, state_prime, action_prime)
        

        # Setting up for the next iteration
        state = state_prime
        action = action_prime
        
        t += 1

    # update exploring_rate and learning_rate
    agent.update_parameters(episode)

    if episode % print_every_episode == 0:
        print("Episode %d finished after %f time steps" % (episode, t))
        print("cumulated reward: %f" % cum_reward)
        print("exploring rate %f" % agent.exploring_rate)
        print("learning rate %f" % agent.learning_rate)
        reward_per_epoch.append(cum_reward)
        exploring_rates.append(agent.exploring_rate)
        learning_rates.append(agent.learning_rate)
        lifetime_per_epoch.append(t)