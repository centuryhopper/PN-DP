import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Version1 {
    public static void main(String[] args) {
        System.out.println("Press 'Enter' to quit");
        int numPhilosophers, numChopsticks;
        numPhilosophers = numChopsticks = 5;

        // create thread pool, philosophers, and chopsticks
        // number of philosophers must match number of chopsticks

        ExecutorService threadPool = Executors.newCachedThreadPool();
        Philosopher[] philosophers = new Philosopher[numPhilosophers];
        Chopstick[] chopsticks = new Chopstick[numChopsticks];

        Arrays.fill(chopsticks, new Chopstick());

        for (int i = 0; i < numPhilosophers; i++) {
            threadPool.execute(
                    philosophers[i] = new Philosopher(chopsticks[i], chopsticks[(i + 1) % numPhilosophers], i));
        }

        try {
            System.in.read();
        } catch (IOException e) {
            System.out.println("invalid input");
        } finally {
            threadPool.shutdownNow();
        }

    }
}

class Chopstick {
    private boolean inUse = false;
    private int id;

    public synchronized void setId(int id) {
        this.id = id;
    }

    public synchronized void PickUp() throws InterruptedException {
        while (inUse) {
            wait();
        }

        inUse = true;
    }

    public synchronized void PutDown() {
        inUse = false;
    }

    public synchronized boolean InUse() {
        return inUse;
    }

    public synchronized int getId() {
        return id;
    }
}

// assume all philosophers wait and eat at indefinitely
class Philosopher implements Runnable {
    // 2 states: Thinking or Eating
    private Chopstick left, right;
    private int philId;
    private Random rnd;

    Philosopher(Chopstick left, Chopstick right, int philId) {
        this.left = left;
        this.right = right;
        this.philId = philId;
        rnd = new Random(47);
    }

    private void ImEitherWaitingOrEating() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(rnd.nextInt(1000));
    }

    // this is where all the magic happens with each philosopher
    @Override
    public void run() {
        try {
            while (true) {
                System.out.println(this + " is now thinking.");
                ImEitherWaitingOrEating();
                System.out.println(this + " is now hungry.");

                // pick up left and right chopsticks if available
                if (!left.InUse()) {
                    System.out.println(this + " grabbing left");
                }
                left.setId(philId);
                left.PickUp();

                if (!right.InUse()) {
                    System.out.println(this + " grabbing right");
                }
                right.setId((philId + 1) % 5);
                right.PickUp();

                System.out.println(this + " is now eating");
                ImEitherWaitingOrEating();

                // put down both left and right chopsticks
                left.PutDown();
                System.out.println(this + " has dropped left");
                right.PutDown();
                System.out.println(this + " has dropped right");
            }
        } catch (InterruptedException e) {
            System.out.println(this + " exited via interruption");
        }
    }

    public String toString() {
        return "Philosopher " + philId;
    }
}
