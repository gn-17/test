package com.amiseq;

import java.util.Stack;

public class Main {
	public static void main(String[] args) {
		Stack<Integer> stack = new Stack<Integer>();
		Producer producer1 = new Producer(stack, 3);
		Producer producer2 = new Producer(stack, 5);
		Printer consumer1 = new Printer(stack);

		CustomThreadPool pool = CustomThreadPool.getInstance(3);
		pool.execute(producer1);
		pool.execute(producer2);
		pool.execute(consumer1);
	}
}
