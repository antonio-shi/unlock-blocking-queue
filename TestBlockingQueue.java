import java.net.Socket;
import java.util.TreeMap;


public class TestBlockingQueue {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final ConcurrentBlockingQueue<BeanTest> cache = new ConcurrentBlockingQueue<BeanTest>(
				100000);
		new Thread() {
			public void run() {
				int a = 0;
				while (true) {
					cache.put(new BeanTest("test"+a));
					a++;
//					if(a == 1000){
//						break;
//					}
				}
			}
		}.start();
		new Thread() {
			long start = System.currentTimeMillis();
			long count = 0;
			long temp = 0;

			public void run() {
				BeanTest bean = null;
				while (true) {
					bean = cache.take();
					temp++;
					long end = System.currentTimeMillis();
					if (end - start >= 1000) {
						System.out.println(Thread.currentThread());
						start = end;
						System.out.println(temp - count);
						count = temp;
						System.out.println(bean);
					}
//					System.out.println(temp);
				}
			}
		}.start();
	}

}
