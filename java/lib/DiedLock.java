/**
 * java  死锁
 */
public class DiedLock {

    /**
     * 死锁定义：
     * 死锁是指两个或两个以上的进程在执行过程中，
     * 由于竞争资源或者由于彼此通信而造成的一种阻塞的现象，
     * 若无外力作用，它们都将无法推进下去。
     *
     * 简单理解：
     * 如果此时有一个线程A，按照先锁a再获得锁b的的顺序获得锁，
     * 而在此同时又有另外一个线程B，按照先锁b再锁a的顺序获得锁
     *
     * 参考： https://juejin.im/post/5aaf6ee76fb9a028d3753534
     */

    private static final Object a = new Object();
    private static final Object b = new Object();

    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            synchronized (a){
               try {
                   System.out.println("t1 lock a");
                   Thread.sleep(1000L);
                   synchronized (b){
                       System.out.println("t1 lock b");
                   }
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (b){
                try {
                    System.out.println("t2 lock b");
                    Thread.sleep(2000L);
                    synchronized (a){
                        System.out.println("t2 lock a");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread1.start();
        thread2.start();
    }
}
