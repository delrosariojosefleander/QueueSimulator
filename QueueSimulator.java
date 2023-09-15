package edu.ics211.h09;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;



public class QueueSimulator {

	private class Customer implements Comparable{
		int arrivalTime;
		int serviceTime;

		public Customer() {
		Random rand = new Random();
			arrivalTime = rand.nextInt(86101); // arrivalTime between 0 and 86099 seconds
			serviceTime = 30 + rand.nextInt(271); // serviceTime between 30 and 300 seconds
		}
		
		public int compareTo(Object obj) {
			Customer customer = (Customer)obj;
			return this.arrivalTime - customer.arrivalTime;
		}
	}
	
	

	private class Server {
		int finishTime;

		public Server() {
			finishTime = 0;
		}

		public boolean isIdle(int currentTime) {
			return currentTime >= finishTime;
		}
	}

	private boolean isSorted(Customer[] array) {
		for (int i = 0; i < array.length - 1; i++) {
			if (array[i].arrivalTime > array[i+1].arrivalTime) {
				return false;
			}
		}
		return true;
	}

	private int shortestQueueIndex(Queue[] queues) {
	    int minIndex = 0;
	    for (int i = 1; i < queues.length; i++) {
	        if (queues[i].size() < queues[minIndex].size()) {
	            minIndex = i;
	        }
	    }
	    return minIndex;
	}

	/**
	 * This method will simulate the servicing of the customers specified by
	 * numCustomers using either a single queue if singleQueue is true, or 10 queues
	 * if singleQueue is false. This method assumes this servicing will take place
	 * over a day, or 86,400 seconds.
	 * 
	 * @param singleQueue  a boolean that if true will cause the simulator to use a
	 *                     single queue, or use ten queues if false.
	 * @param numCustomers an int that specifies the number of customers to be
	 *                     served.
	 * @return the total wait time for all the customers served for that day.
	 */
	public int simulator(boolean singleQueue, int numCustomers) {

		Customer[] customers = new Customer[numCustomers];
		for(int i = 0; i < customers.length; i++) {
			customers[i] = new Customer();
		}
		Arrays.sort(customers);
		System.out.println("Are the customers sorted?" + isSorted(customers));
		
		Server[] servers = new Server[10];
		for(int i = 0; i < servers.length; i++) {
			servers[i] = new Server();
		}
		
		Queue<Customer>[] queues;
		if (singleQueue) {
			queues = new LinkedList[1];
		}
		else {
			queues = new LinkedList[10];
		}
		// construct the queue(s)
		for (int i = 0; i < queues.length; i++) {
			queues[i] = new LinkedList<Customer>();
		}
		int totalWaitingTime = 0; // initial waiting time 
		int custIndex = 0; // keep track of customers being added to queue(s) and servers
		for (int currentTime = 0; currentTime < 86400; currentTime++) {
			while(custIndex < numCustomers && currentTime >= customers[custIndex].arrivalTime) {
				Customer nextCustomer = customers[custIndex];
				int shortestIndex = shortestQueueIndex(queues);
				queues[shortestIndex].add(nextCustomer);
				custIndex++;
			}
			for (int i = 0; i < servers.length; i++) {
                if (servers[i].isIdle(currentTime)) {
                    if (singleQueue) {
                        if (!queues[0].isEmpty()) {
                            Customer cust = queues[0].poll();
                            int waitingTime = currentTime - cust.arrivalTime;
                            totalWaitingTime += waitingTime;
                            servers[i].finishTime = currentTime + cust.serviceTime;
                        }
                    }
                    else {
                    	if (!queues[i].isEmpty()) {
                            Customer cust = queues[i].poll();
                            int waitingTime = currentTime - cust.arrivalTime;
                            totalWaitingTime += waitingTime;
                            servers[i].finishTime = currentTime + cust.serviceTime;
                        }
                    }
                }
            }

		}	
		return totalWaitingTime;
	}	
	
	
	public static void main(String[] args) {
		int numCustomers = 5000;
		QueueSimulator qSim = new QueueSimulator();
		int singleQueueTime = qSim.simulator(true,  numCustomers);
		int multipleQueueTime = qSim.simulator(false, numCustomers);
		System.out.println("singleQueueTime is " + singleQueueTime + " seconds");
		System.out.println("multipleQueueTime is " + multipleQueueTime + " seconds");
	}

}
