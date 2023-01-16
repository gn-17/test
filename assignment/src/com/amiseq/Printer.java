package com.amiseq;

import java.util.Random;
import java.util.Stack;

public class Printer implements Runnable {
	private Integer rTime = 0;
	private Random rand = new Random();
	private Stack<Integer> stack = null;

	Printer(Stack<Integer> stack) {
		this.stack = stack;
	}

	public void printNumber() {
		synchronized (stack) {
			while (stack.isEmpty()) {
				try {
					System.out.println("Printer Buffer(stack) is empty, waiting for producer to publish");
					stack.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Number - " + stack.pop());
			stack.notifyAll();
		}
	}

	@Override
	public void run() {
		while (true) {
			rTime = (int) rand.nextInt(1000);
			try {
				Thread.sleep(rTime);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			System.out.println("Printer window acquired");
			for (int i = 0; i <= 10; i++)
				this.printNumber();
		}
	}

}
