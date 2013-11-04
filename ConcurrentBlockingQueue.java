import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentBlockingQueue<T extends Exchengeable> {
	public long p1, p2, p3, p4, p5, p6, p7; // cache line padding
	private volatile long proCursor = 0;
	public long p8, p9, p10, p11, p12, p13, p14; // cache line padding
	private volatile long comsumeCursor = 0;
	private final AtomicLong produceSequence = new AtomicLong(0);
	private final AtomicLong comsumeSequence = new AtomicLong(0);

	private final Exchengeable[] entries;
	private final int ringModMask;

	private final int nextHighestPowerOfTwo(int v) {
		v--;
		v |= v >> 1;
		v |= v >> 2;
		v |= v >> 4;
		v |= v >> 8;
		v |= v >> 16;
		v++;
		return v;
	}

	public ConcurrentBlockingQueue(final int size) {
		int sizeAsPowerOfTwo = Math.max(nextHighestPowerOfTwo(size), 1);
		ringModMask = sizeAsPowerOfTwo - 1;
		entries = new Exchengeable[sizeAsPowerOfTwo];
	}

	private T getEntry(final long sequence) {
		return (T) entries[(int) (sequence & ringModMask)];
	}

	private void setEntry(final long sequence, T t) {
		final int index = (int) (sequence & ringModMask);
		if (entries[index] == null) {
			entries[index] = t;
		} else {
			entries[index].exchange(t);
		}
	}

	private final long waitForNextProduce() {
		while (true) {
			if (produceSequence.get() - comsumeCursor < entries.length) {
				return produceSequence.incrementAndGet();
			} else {
				try {
					Thread.yield();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void put(T t) {
		long sequence = waitForNextProduce();
		setEntry(sequence - 1, t);
		proCursor = sequence;
	}

	private final long waitForComsume() {
		while (true) {
			long comsume = proCursor - comsumeSequence.get();
			if (comsume > 0 && comsume <= entries.length) {
				return comsumeSequence.incrementAndGet();
			} else {
				try {
					Thread.yield();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public T take() {
		long nextSequence = waitForComsume();
		comsumeCursor = nextSequence;
		return getEntry(nextSequence - 1);
	}

	public boolean put(T t, long time, TimeUnit unit) throws IOException {
		put(t);
		return true;
	}

	public T take(long time, TimeUnit unit) throws IOException {
		return take();
	}

}
