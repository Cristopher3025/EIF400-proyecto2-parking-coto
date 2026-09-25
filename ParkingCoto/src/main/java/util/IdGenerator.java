/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.util.concurrent.atomic.AtomicLong;

public final class IdGenerator {

	private static final AtomicLong SEQUENCE = new AtomicLong();

	private IdGenerator() {
	}

	public static String nextTicketId() {
		return "T-" + SEQUENCE.incrementAndGet();
	}
}
