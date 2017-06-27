package com.prj.sdk.util.concurrent;

public class LIFOLinkedBlockingQueue<E> extends LinkedBlockingDeque<E> {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	@Override
	public boolean offer(E e) {
		return super.offerFirst(e);
	}
}
