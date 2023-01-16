package com.amiseq;

import java.util.Stack;

public class Producer implements Runnable {
	private int generatedNumber = 0, progressionFactor = 0;
	private final int limit = 10;
	private Stack<Integer> stack = null;

	Producer(Stack<Integer> stack, Integer progressionBy) {
		this.stack = stack;
		this.progressionFactor = progressionBy;
	}

	public void generateNextNumber() {
		this.generatedNumber += this.progressionFactor;
		synchronized (stack) {
			while (stack.size() >= limit) {
				try {
					System.out.println("Printer Buffer(stack) is full, waiting for printer to consume");
					stack.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println(Thread.currentThread().getName() + " producing number - '" + generatedNumber + "'");
			stack.push(generatedNumber);
			stack.notifyAll();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				this.generateNextNumber();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
